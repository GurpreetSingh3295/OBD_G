package ca.mohawk.obdv0;

// MainActivity.java
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private Spinner functionSpinner, conversionSpinner;
    private TextView resultText;
    private Button processButton;

    // Replace these with actual values from your documentation
    private static final int ODOMETER = 1;
    private static final int RQ_TP2 = 2;
    private static final int MILES = 11;
    private static final int KILOMETERS = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initializeUI();
        setupSpinners();
    }

    private void initializeUI() {
        functionSpinner = findViewById(R.id.functionSpinner);
        conversionSpinner = findViewById(R.id.conversionSpinner);
        resultText = findViewById(R.id.resultText);
        processButton = findViewById(R.id.processButton);

        processButton.setOnClickListener(v -> processData());
    }

    private void setupSpinners() {
        // Function types
        List<String> functions = new ArrayList<>();
        functions.add("ODOMETER");
        functions.add("RQ_TP2");

        ArrayAdapter<String> functionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, functions);
        functionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        functionSpinner.setAdapter(functionAdapter);

        // Conversion styles
        List<String> conversions = new ArrayList<>();
        conversions.add("MILES");
        conversions.add("KILOMETERS");

        ArrayAdapter<String> conversionAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, conversions);
        conversionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conversionSpinner.setAdapter(conversionAdapter);
    }

    private void processData() {
        String rawData = ((TextView) findViewById(R.id.rawDataInput)).getText().toString();
        String selectedFunction = (String) functionSpinner.getSelectedItem();
        String selectedConversion = (String) conversionSpinner.getSelectedItem();

        if (rawData.isEmpty()) {
            showToast("Please enter CAN data");
            return;
        }

        new DataProcessingTask().execute(
                rawData,
                selectedFunction,
                selectedConversion
        );
    }

    private class DataProcessingTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String rawData = params[0];
                String function = params[1];
                String conversionStyle = params[2];

                // Convert function strings to numeric values
                int functionCode = function.equals("ODOMETER") ? ODOMETER : RQ_TP2;
                int conversionCode = conversionStyle.equals("MILES") ? MILES : KILOMETERS;

                // Preprocess data
                byte[] responseReceived = preprocessCANData(rawData);
                byte[] sentBuffer = new byte[19]; // Mock sent buffer

                // Mock conversion
                int[] convertedResult = new int[1];
                int[] convertedFraction = new int[1];

                int status = Odomatic_Convert(
                        sentBuffer,
                        functionCode,
                        responseReceived,
                        conversionCode,
                        convertedResult,
                        convertedFraction
                );

                if (status == 0) {
                    double value = convertedResult[0] + (convertedFraction[0] / 10.0);
                    return String.format("%s: %.1f %s",
                            function, value, conversionStyle);
                }
                return "Conversion failed (Error: " + status + ")";

            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            resultText.setText(result);
        }
    }

    private byte[] preprocessCANData(String rawData) {
        // Add leading zero if missing
        String correctedData = rawData.startsWith("0") ? rawData : "0" + rawData;

        List<Byte> byteList = new ArrayList<>();
        for (int i = 0; i < correctedData.length(); i += 2) {
            String hexByte = correctedData.substring(i, Math.min(i + 2, correctedData.length()));
            byteList.add((byte) Integer.parseInt(hexByte, 16));
        }

        byte[] result = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            result[i] = byteList.get(i);
        }
        return result;
    }

    // Mock implementation - replace with actual library calls
    private int Odomatic_Convert(byte[] sentBuffer, int function, byte[] responseReceived,
                                 int conversionStyle, int[] convertedResult,
                                 int[] convertedFraction) {
        // Simulate conversion logic
        if (function == ODOMETER) {
            if (conversionStyle == MILES) {
                convertedResult[0] = 236;
                convertedFraction[0] = 5;
                return 0;
            } else if (conversionStyle == KILOMETERS) {
                convertedResult[0] = 380;
                convertedFraction[0] = 6;
                return 0;
            }
        } else if (function == RQ_TP2) {
            convertedResult[0] = 35;
            convertedFraction[0] = 0;
            return 0;
        }
        return -1; // Error
    }

    private void showToast(String message) {
        runOnUiThread(() ->
                Toast.makeText(MainActivity2.this, message, Toast.LENGTH_SHORT).show());
    }
}