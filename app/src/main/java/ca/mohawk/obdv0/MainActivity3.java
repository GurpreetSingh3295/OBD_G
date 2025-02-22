package ca.mohawk.obdv0;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;

/**
 * 1) Reads Odomatic.h, MakeModelNA.h, MakeModelText.h from /assets.
 * 2) Naively parses #define lines from Odomatic.h and MakeModelNA.h into defineMap.
 * 3) Fetches JSON from Odomatic => e.g. {"result":[0,10,16,6,1,...]} and decodes placeholders for year/make/model/fuel.
 */
public class MainActivity3 extends AppCompatActivity {

    private static final String AUTH_CREDENTIALS = "MohawkProd:tJ4U!L7*q.Dc@2hLNQz6";

    private Button readHeadersButton, submitButton;
    private TextView headerParseOutput;
    private EditText vinEditText, typeEditText;
    private TextView rawResultTextView, decodedResultTextView;

    // We'll store #define lines from Odomatic.h and MakeModelNA.h here
    private Map<String, Integer> defineMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // UI references
        readHeadersButton = findViewById(R.id.readHeadersButton);
        headerParseOutput = findViewById(R.id.headerParseOutput);
        vinEditText = findViewById(R.id.vinEditText);
        typeEditText = findViewById(R.id.typeEditText);
        submitButton = findViewById(R.id.submitButton);
        rawResultTextView = findViewById(R.id.rawResultTextView);
        decodedResultTextView = findViewById(R.id.decodedResultTextView);

        // 1) Read & parse .h files from assets
        readHeadersButton.setOnClickListener(v -> {
            try {
                // Read each file from assets
                String odomaticText = readFileFromAssets("Odomatic.h");
                String makeModelNAText = readFileFromAssets("MakeModelNA.h");
                String makeModelText = readFileFromAssets("MakeModelText.h");

                // Clear old results
                defineMap.clear();

                // Parse #defines from Odomatic.h and MakeModelNA.h
                parseDefines(odomaticText, defineMap);
                parseDefines(makeModelNAText, defineMap);

                // Build output: partial parse & partial file content
                StringBuilder sb = new StringBuilder();
                sb.append("--- #DEFINE Parsing ---\n");
                // Example: show if it found FUEL_GAS, FORD_CARS, etc.
                Integer fuelGasVal = defineMap.get("FUEL_GAS");
                if (fuelGasVal != null) {
                    sb.append("FUEL_GAS => ").append(fuelGasVal).append("\n");
                }
                Integer fordVal = defineMap.get("FORD_CARS");
                if (fordVal != null) {
                    sb.append("FORD_CARS => ").append(fordVal).append("\n");
                }

                sb.append("\n--- Odomatic.h (first 200 chars) ---\n")
                        .append(samplePreview(odomaticText, 200))
                        .append("\n\n--- MakeModelNA.h (first 200 chars) ---\n")
                        .append(samplePreview(makeModelNAText, 200))
                        .append("\n\n--- MakeModelText.h (first 200 chars) ---\n")
                        .append(samplePreview(makeModelText, 200));

                headerParseOutput.setText(sb.toString());

            } catch (IOException e) {
                headerParseOutput.setText("Error reading assets: " + e.getMessage());
            }
        });

        // 2) Odomatic request to get JSON
        submitButton.setOnClickListener((View v) -> {
            String vin = vinEditText.getText().toString().trim();
            String reqStr = typeEditText.getText().toString().trim();

            if (vin.isEmpty() || reqStr.isEmpty()) {
                rawResultTextView.setText("Please enter a VIN and request type (e.g. 150).");
                return;
            }
            try {
                int requestType = Integer.parseInt(reqStr);
                new OdomaticRequestTask().execute(vin, String.valueOf(requestType));
            } catch (NumberFormatException e) {
                rawResultTextView.setText("Request Type must be a number");
            }
        });
    }

    /**
     * Reads a file from /assets into a String.
     */
    private String readFileFromAssets(String fileName) throws IOException {
        AssetManager am = getAssets();
        try (InputStream is = am.open(fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * Naive parse of lines like "#define FUEL_GAS (1)" or "#define FUEL_GAS 1"
     * We'll store them in defineMap => defineMap.put("FUEL_GAS",1)
     */
    private void parseDefines(String content, Map<String,Integer> map) {
        // e.g. #define FUEL_GAS (1)
        Pattern pattern = Pattern.compile("#define\\s+([A-Za-z0-9_]+)\\s*\\(?([0-9]+)\\)?");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String key = matcher.group(1);
            String valStr = matcher.group(2);
            try {
                int val = Integer.parseInt(valStr);
                map.put(key, val);
            } catch (NumberFormatException ignored) {}
        }
    }

    /**
     * Returns the first n chars of a long string as a preview
     */
    private String samplePreview(String content, int n) {
        if (content.length() <= n) return content;
        return content.substring(0, n) + "...";
    }

    /**
     * AsyncTask that fetches JSON from Odomatic
     */
    private class OdomaticRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String vin = params[0];
            String requestType = params[1];
            try {
                String urlStr = "https://us.odomatic.com/GetRequestFull?vin=" + vin +
                        "&requestType=" + requestType;

                String authHeader = Base64.encodeToString(AUTH_CREDENTIALS.getBytes(), Base64.NO_WRAP);

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", authHeader);

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Error: HTTP " + conn.getResponseCode();
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                return sb.toString();

            } catch (Exception e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String json) {
            rawResultTextView.setText("Raw JSON:\n" + json);
            parseOdomaticResult(json);
        }
    }

    /**
     * Parse Odomatic JSON => e.g. "result":[0,10,16,6,1,...]
     * We do a placeholder decode with no default arrays for final output.
     */
    private void parseOdomaticResult(String jsonStr) {
        try {
            JSONObject obj = new JSONObject(jsonStr);
            JSONArray arr = obj.getJSONArray("result");
            if (arr.length() < 5) {
                decodedResultTextView.setText("Not enough data in 'result' array.");
                return;
            }

            // "result":[0,10,16,6,1,...]
            int rawYear = arr.getInt(1);   // e.g. 10 => year offset
            int makeIdx = arr.getInt(2);   // e.g. 16
            int modelIdx = arr.getInt(3);  // e.g. 6
            int fuelIdx = arr.getInt(4);   // e.g. 1

            int actualYear = 2000 + rawYear;

            // Show placeholders
            String yearStr = String.valueOf(actualYear);
            String makeStr = "Make=" + makeIdx + " (no defaults)";
            String modelStr = "Model=" + modelIdx + " (no defaults)";
            String fuelStr = "Fuel=" + fuelIdx + " (no defaults)";

            String decoded = "Year: " + yearStr +
                    "\n" + makeStr +
                    "\n" + modelStr +
                    "\n" + fuelStr;

            decodedResultTextView.setText("Decoded:\n" + decoded);

        } catch (Exception e) {
            decodedResultTextView.setText("Error decoding data: " + e.getMessage());
        }
    }
}
