package ca.mohawk.obdv0;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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

public class SendSeatbeltActivity extends AppCompatActivity {

    private static final String TAG = "OBD_Seatbelt";
    private static final String AUTH_CREDENTIALS = "MohawkProd:tJ4U!L7*q.Dc@2hLNQz6";
    private static final UUID OBD_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private TextView connectionStatus, seatbeltDisplay, decodedResultTextView;
    private Button getSeatbeltButton;

    private BluetoothManager bluetoothManager;
    private BluetoothSocket bluetoothSocket;
    private InputStream inStream;
    private OutputStream outStream;
    private Thread readThread;
    private volatile boolean stopReading = false;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private String deviceAddr;
    private String receivedMessage = "";
    private String msgBuffer = "";

    // Local list to store OBD responses (for debugging)
    private final ArrayList<String> obdResponseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        connectionStatus = findViewById(R.id.connectionStatus);
        seatbeltDisplay = findViewById(R.id.seatbeltInfo);
        getSeatbeltButton = findViewById(R.id.getSeatbeltButton);
        decodedResultTextView = findViewById(R.id.decodedResultTextView);

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        // Get device address from Intent extra
        deviceAddr = getIntent().getStringExtra("device_address");
        if (deviceAddr == null) {
            Toast.makeText(this, "No device address provided!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getSeatbeltButton.setEnabled(false);
        connectToDevice(deviceAddr);

        getSeatbeltButton.setOnClickListener(v -> {
            getSeatbeltButton.setEnabled(false);
            new Thread(() -> {
                // Send local OBD commands for seatbelt parameter (181)
                requestSeatbeltOBD();

                // After local commands, perform a network request to decode seatbelt data.
                // For example, use VIN if available (here we hardcode a sample VIN).
                String response = performNetworkRequest("1FMCU0D75AKB86662", 181);
                Log.d(TAG, "Odomatic Response: " + response);
                // Parse the seatbelt response and display the status
                parseSeatbeltResult(response);

                runOnUiThread(() -> getSeatbeltButton.setEnabled(true));
            }).start();
        });
    }

    private void connectToDevice(String address) {
        new Thread(() -> {
            BluetoothAdapter adapter = bluetoothManager.getAdapter();
            if (adapter == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Missing BLUETOOTH_CONNECT permission!", Toast.LENGTH_SHORT).show();
                    });
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
                    Toast.makeText(this, "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();
                });
                startReading();
                runOnUiThread(() -> getSeatbeltButton.setEnabled(true));
            } catch (IOException e) {
                Log.e(TAG, "Error connecting", e);
                runOnUiThread(() -> {
                    connectionStatus.setText("Connection failed");
                    Toast.makeText(this, "Connection failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        String msg = new String(buffer, 0, bytes);
                        msgBuffer += msg;
                        if (msg.contains(">")) {
                            receivedMessage = msgBuffer;
                            msgBuffer = "";
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error reading input", e);
                    break;
                }
            }
        });
        readThread.start();
    }

    // Sends OBD commands to request the seatbelt parameter (181)
    private void requestSeatbeltOBD() {
        // Standard 11-bit setup commands
        sendOBDCommand("ATZ");       // Reset device
        sendOBDCommand("STP 33");    // Set protocol (CAN 11-bit 500kbps)
        sendOBDCommand("ATH1");      // Enable header
        sendOBDCommand("ATS0");      // Disable spacing
        // Special: Mask out the top bit so that the transmit header becomes 0x737
        sendOBDCommand("AT SH 737");
        // Send the seatbelt request command – this is the hex command for parameter 181 (example: 22 5B 19)
        sendOBDCommand("22 5B 19");
        Log.d(TAG, "OBD Seatbelt Response: " + receivedMessage);
    }

    private void sendOBDCommand(String command) {
        command += "\r";
        receivedMessage = "";
        if (outStream != null) {
            try {
                outStream.write(command.getBytes());
                outStream.flush();
            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Send error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        } else {
            runOnUiThread(() ->
                    Toast.makeText(this, "Not connected!", Toast.LENGTH_SHORT).show()
            );
        }
        // Wait until we get a response (or timeout after 3 seconds)
        long startTime = System.currentTimeMillis();
        while (receivedMessage.isEmpty()) {
            if (System.currentTimeMillis() - startTime > 3000) {
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}
        }
        obdResponseList.add(receivedMessage);
    }

    // Performs a network request to the Odomatic server for a given VIN and request type.
    private String performNetworkRequest(String vin, int requestType) {
        try {
            Future<String> future = executorService.submit(new Callable<String>() {
                @Override
                public String call() {
                    String result;
                    try {
                        String authHeader = Base64.encodeToString(AUTH_CREDENTIALS.getBytes(), Base64.NO_WRAP);
                        URL url = new URL("https://us.odomatic.com/GetRequestFull?vin=" + vin + "&requestType=" + requestType);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setRequestProperty("Authorization", authHeader);
                        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            result = "Error: HTTP " + conn.getResponseCode();
                        } else {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line);
                            }
                            reader.close();
                            result = sb.toString();
                        }
                    } catch (Exception e) {
                        result = "Error: " + e.getMessage();
                    }
                    return result;
                }
            });
            return future.get();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Parses the seatbelt result from the JSON response. Assumes the seatbelt bitfield is at index 4.
    private void parseSeatbeltResult(String jsonStr) {
        try {
            JSONObject obj = new JSONObject(jsonStr);
            JSONArray arr = obj.getJSONArray("result");

            if (arr.length() < 5) {
                runOnUiThread(() -> decodedResultTextView.setText("Not enough data in 'result' array."));
                return;
            }

            // For seatbelt, assume the value is at index 4.
            int seatbeltStatus = arr.getInt(4);
            // Use bitmasks for each seatbelt (based on definitions from the JSON conversion)
            final int LF_FASTENED = 1;
            final int RF_FASTENED = 2;
            final int CF_FASTENED = 4;
            final int L2_FASTENED = 8;
            final int R2_FASTENED = 16;
            final int C2_FASTENED = 32;
            final int L3_FASTENED = 64;
            final int R3_FASTENED = 128;
            final int C3_FASTENED = 256;

            StringBuilder status = new StringBuilder();
            status.append("Seatbelt Status Bitfield: ").append(seatbeltStatus).append("\n");
            status.append("Front Left: ").append((seatbeltStatus & LF_FASTENED) != 0 ? "Fastened" : "Unfastened").append("\n");
            status.append("Front Right: ").append((seatbeltStatus & RF_FASTENED) != 0 ? "Fastened" : "Unfastened").append("\n");
            status.append("Front Center: ").append((seatbeltStatus & CF_FASTENED) != 0 ? "Fastened" : "Unfastened").append("\n");
            status.append("Second Row Left: ").append((seatbeltStatus & L2_FASTENED) != 0 ? "Fastened" : "Unfastened").append("\n");
            status.append("Second Row Right: ").append((seatbeltStatus & R2_FASTENED) != 0 ? "Fastened" : "Unfastened").append("\n");
            status.append("Second Row Center: ").append((seatbeltStatus & C2_FASTENED) != 0 ? "Fastened" : "Unfastened").append("\n");
            status.append("Third Row Left: ").append((seatbeltStatus & L3_FASTENED) != 0 ? "Fastened" : "Unfastened").append("\n");
            status.append("Third Row Right: ").append((seatbeltStatus & R3_FASTENED) != 0 ? "Fastened" : "Unfastened").append("\n");
            status.append("Third Row Center: ").append((seatbeltStatus & C3_FASTENED) != 0 ? "Fastened" : "Unfastened").append("\n");

            runOnUiThread(() -> decodedResultTextView.setText("Decoded Seatbelt Info:\n" + status.toString()));
        } catch (Exception e) {
            runOnUiThread(() -> decodedResultTextView.setText("Error parsing seatbelt data: " + e.getMessage()));
        }
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
