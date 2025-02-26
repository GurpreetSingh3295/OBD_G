package ca.mohawk.obdv0;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private EditText vinEditText, typeEditText;
    private TextView resultTextView, canResult;
    private static final String AUTH_CREDENTIALS = "MohawkProd:tJ4U!L7*q.Dc@2hLNQz6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vinEditText = findViewById(R.id.vinEditText);
        typeEditText = findViewById(R.id.typeEditText);
        resultTextView = findViewById(R.id.resultTextView);
        canResult = findViewById(R.id.canResult);
        Button submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vin = vinEditText.getText().toString().trim();
                String requestType = typeEditText.getText().toString().trim();

                // Validate input
                if (vin.isEmpty() || requestType.isEmpty()) {
                    resultTextView.setText("Please enter both VIN and Request Type.");
                    return;
                }

                new NetworkRequestTask().execute(vin, requestType);
            }
        });
    }

    public void nextActivity(View view) {
        Intent switch2Activity2 = new Intent(MainActivity.this, MainActivity3.class);
        startActivity(switch2Activity2);
    }

    private class NetworkRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String vin = params[0];
                int requestType = Integer.parseInt(params[1]);

                // Create authorization header
                String authHeader = Base64.encodeToString(AUTH_CREDENTIALS.getBytes(), Base64.NO_WRAP);

                // Build URL
                URL url = new URL("https://us.odomatic.com/GetRequestFull?vin=" + vin + "&requestType=" + requestType);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", authHeader);

                // Handle response
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Error: " + connection.getResponseCode();
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();

            } catch (NumberFormatException e) {
                return "Error: Invalid request type (must be a number)";
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            resultTextView.setText(result);
            Log.d("CAN Data", result);
            filterCanData(result);
        }
    }

    public void filterCanData(String rawData) {
        try {
            JSONObject jsonObject = new JSONObject(rawData);
            JSONArray resultArray = jsonObject.getJSONArray("result");

            // Extract values from JSON
            int protocol = resultArray.getInt(0); // Protocol type (1 = 11-bit CAN)
            int idMSB = resultArray.getInt(1); // ID MSB
            int idByte2 = resultArray.getInt(2);
            int idByte3 = resultArray.getInt(3);
            int idLSB = resultArray.getInt(4); // ID LSB (E0 in example)
            int txLength = resultArray.getInt(5); // Tx length
            int rxLength = resultArray.getInt(6); // Rx length
            int rxDataStart = resultArray.getInt(7); // Where data starts
            int txByte1 = resultArray.getInt(10);
            int txByte2 = resultArray.getInt(11);
            int txByte3 = resultArray.getInt(12);

            // Convert identifier to hex format for OBD command
            String obdIdentifier = String.format("AT SH %X%X%X%X", idMSB, idByte2, idByte3, idLSB);

            // Convert bytes to hex for OBD request
            String obdCommand = String.format("%X %X %X", txByte1, txByte2, txByte3);

            // Store extracted values in an array
            int[] extractedValues = {
                    protocol, idMSB, idByte2, idByte3, idLSB,
                    txLength, rxLength, rxDataStart,
                    txByte1, txByte2, txByte3
            };

            // Update UI with extracted OBD values
            runOnUiThread(() -> {
                canResult.setText(
                        "OBD Identifier: " + obdIdentifier + "\n" +
                                "OBD Command: " + obdCommand + "\n" +
                                "Extracted Values: " + Arrays.toString(extractedValues)
                );
            });

        } catch (Exception e) {
            canResult.setText("Error parsing CAN Data: " + e.getMessage());
        }
    }
}
