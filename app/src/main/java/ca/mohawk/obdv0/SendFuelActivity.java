package ca.mohawk.obdv0;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SendFuelActivity extends AppCompatActivity {

    private static final String TAG = "OBD_FuelCmd";
    private static final String AUTH_CREDENTIALS = "MohawkProd:tJ4U!L7*q.Dc@2hLNQz6";

    // The standard OBD-II UUID for Bluetooth connections
    private static final UUID OBD_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // UI Elements
    private TextView connectionStatus, fuelDisplay;
    private Button getFuelButton;

    // Bluetooth and I/O
    private BluetoothManager bluetoothManager;
    private BluetoothSocket bluetoothSocket;
    private InputStream inStream;
    private OutputStream outStream;
    private Thread readThread;
    private volatile boolean stopReading = false;

    // Execution
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String deviceAddr;
    private String receivedMessage = "";
    private String msgTemp = "";

    // These track OBD responses, e.g. for debugging
    private final ArrayList<String> obdResponseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        // Reuse your existing layout or create a new one.

        connectionStatus = findViewById(R.id.connectionStatus);
        fuelDisplay = findViewById(R.id.vinNumber);
        // If you have a different TextView, rename accordingly.
        getFuelButton = findViewById(R.id.getButton);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        // Ensure we have a device address from Intent
        deviceAddr = getIntent().getStringExtra("device_address");
        if (deviceAddr == null) {
            Toast.makeText(this, "No device address provided!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getFuelButton.setEnabled(false);
        connectToDevice(deviceAddr);

        // On button click, retrieve fuel data via Odomatic
        getFuelButton.setOnClickListener(view -> {
            getFuelButton.setEnabled(false);
            new Thread(() -> {
                // 1) (Optional) Send local OBD commands to get raw data if needed
                // For demonstration, we do the standard 11-bit requests for fuel
                // but you can customize as needed:
                requestFuelOBD();

                // 2) Hit the Odomatic endpoint with requestType=52 to decode
                String fuelInfo = performNetworkRequest(deviceAddr /*or VIN*/, 52);

                // 3) Parse or display the returned data
                // For a more advanced approach, parse the JSON to find a result array
                String displayString = parseFuelResponse(fuelInfo);

                runOnUiThread(() -> {
                    fuelDisplay.setText("Fuel Info: " + displayString);
                    getFuelButton.setEnabled(true);
                });
            }).start();
        });
    }

    private void connectToDevice(String address) {
        new Thread(() -> {
            BluetoothAdapter adapter = bluetoothManager.getAdapter();
            if (adapter == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Bluetooth not supported on this device",
                            Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }

            // For Android 12+, check BLUETOOTH_CONNECT permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
                ) {
                    runOnUiThread(() ->
                            Toast.makeText(this,
                                    "Missing BLUETOOTH_CONNECT permission!",
                                    Toast.LENGTH_SHORT).show()
                    );
                    return;
                }
            }

            BluetoothDevice device = adapter.getRemoteDevice(address);
            if (device == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Device not found!", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(OBD_UUID);
                bluetoothSocket.connect();
                outStream = bluetoothSocket.getOutputStream();
                inStream = bluetoothSocket.getInputStream();

                runOnUiThread(() -> {
                    connectionStatus.setText("Connected to: " + device.getName());
                    Toast.makeText(this,
                            "Connected to " + device.getName(),
                            Toast.LENGTH_SHORT).show();
                });

                startReading();
                getFuelButton.setEnabled(true);

            } catch (IOException e) {
                Log.e(TAG, "Error connecting", e);
                runOnUiThread(() -> {
                    connectionStatus.setText("Connection failed");
                    Toast.makeText(this,
                            "Connection failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void startReading() {
        stopReading = false;
        readThread = new Thread(() -> {
            byte[] buffer = new byte[1024];
            int bytes;
            while (!stopReading) {
                try {
                    if (inStream != null && (bytes = inStream.read(buffer)) > 0) {
                        final String msg = new String(buffer, 0, bytes);
                        msgTemp += msg;
                        if (msg.contains(">")) {
                            receivedMessage = msgTemp;
                            msgTemp = "";
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Reading error", e);
                    break;
                }
            }
        });
        readThread.start();
    }

    // Example function to locally request the fuel parameter via OBD
//    {
//        "result": [
//        1,
//                0,
//                0,
//                7,        7
//                223,      DF
//                2,        no of bytes to transmit
//                1,        transmit byte 1
//                8,        transmit byte 2
//                1,
//                10,
//                1,
//                47,
//                0,
//                0,
//                0,
//                0,
//                0,
//                0,
//                0
//    ]
//    }
    private void requestFuelOBD() {
        // Standard 11-bit approach
        // 1) Reset and set protocol
        sendOBDCommand("ATZ");
        sendOBDCommand("STP 33");  // For 11-bit 500 kbps, example
        sendOBDCommand("ATH1");
        sendOBDCommand("ATS0");
        sendOBDCommand("AT SH 7DF");

        // 2) Actual request for fuel
        // This is a placeholder for your required 2-3 byte command, e.g. "22 12 56"
        // or some standard that your vehicle uses for FUEL(52).
        sendOBDCommand("1 8");

        // 'receivedMessage' now contains your raw OBD response. You can parse it if needed.
        Log.d(TAG, "OBD Fuel Response: " + receivedMessage);
    }

    private void sendOBDCommand(String command) {
        command += "\r";
        receivedMessage = "";
        if (outStream != null) {
            try {
                outStream.write(command.getBytes());
                outStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Send error: ", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Send error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
            }
        } else {
            runOnUiThread(() ->
                    Toast.makeText(this, "Not connected!", Toast.LENGTH_SHORT).show()
            );
        }

        // Wait until 'receivedMessage' is filled (or we time out).
        long startTime = System.currentTimeMillis();
        while (receivedMessage.isEmpty()) {
            if (System.currentTimeMillis() - startTime > 3000) { // 3 seconds
                break;
            }
            try {
                Thread.sleep(50);
            } catch (Exception ignored) {}
        }
        obdResponseList.add(receivedMessage);
    }

    // Perform a network request to Odomatic server for the Fuel parameter (ID 52).
    // We'll pass the "vin" or device address for demonstration
    private String performNetworkRequest(String vinOrAddr, int requestType) {
        // Or you might pass actual VIN if you have it from your extraction code
        Future<String> future = executorService.submit(() -> {
            String result;
            try {
                String authHeader = Base64.encodeToString(AUTH_CREDENTIALS.getBytes(), Base64.NO_WRAP);
                URL url = new URL("https://us.odomatic.com/GetRequestFull"
                        + "?vin=" + vinOrAddr
                        + "&requestType=" + requestType);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", authHeader);

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    result = "Error: " + connection.getResponseCode();
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    result = response.toString();
                }
            } catch (Exception e) {
                result = "Error: " + e.getMessage();
            }
            return result;
        });
        try {
            return future.get(); // block until done
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // A simple parser for the JSON result from Odomatic for a Fuel request.
    // Example: {"result":[0, 10, 16, 6, 1, ...]}
    private String parseFuelResponse(String jsonResponse) {
        String fuelInfo = "";
        try {
            JSONObject obj = new JSONObject(jsonResponse);
            JSONArray arr = obj.getJSONArray("result");

            if (arr.length() < 5) {
                return "Not enough fuel data in 'result'.";
            }
            // For instance: result[4] might indicate a "fuel code" or some value
            // or result[4] could be the "fuel" ID or reading
            int fuelVal = arr.getInt(4);
            // This depends on how your system encodes the data. Some vehicles return
            // a percentage, others a code to be further converted. We'll just show raw for demonstration:
            fuelInfo = "Fuel Reading = " + fuelVal;
        } catch (Exception e) {
            fuelInfo = "Error parsing Fuel data: " + e.getMessage();
        }
        return fuelInfo;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopReading = true;
        if (readThread != null && readThread.isAlive()) {
            readThread.interrupt();
        }
        closeConnection();
        executorService.shutdown();
    }

    private void closeConnection() {
        try {
            if (inStream != null) inStream.close();
        } catch (Exception ignored) {}
        try {
            if (outStream != null) outStream.close();
        } catch (Exception ignored) {}
        try {
            if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                bluetoothSocket.close();
            }
        } catch (Exception ignored) {}
    }
}
