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
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * Reads JSON files (makemodeltext.json, odomatic.json) from /assets.
 * Fetches Odomatic API response and decodes placeholders for year/make/model/fuel.
 */
public class MainActivity3 extends AppCompatActivity {

    private static final String AUTH_CREDENTIALS = "MohawkProd:tJ4U!L7*q.Dc@2hLNQz6";

    private Button submitButton;
    private EditText vinEditText, typeEditText;
    private TextView rawResultTextView, decodedResultTextView;

    private Map<String, JSONArray> makeModelMap = new HashMap<>();
    private Map<String, String> fuelTypes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // UI references
        vinEditText = findViewById(R.id.vinEditText);
        typeEditText = findViewById(R.id.typeEditText);
        submitButton = findViewById(R.id.submitButton);
        rawResultTextView = findViewById(R.id.rawResultTextView);
        decodedResultTextView = findViewById(R.id.decodedResultTextView);

        // Load JSON files into memory
        loadMakeModelData();
        loadFuelTypes();

        // Submit button to fetch Odomatic API response
        submitButton.setOnClickListener(v -> {
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
     * Reads the MakeModel JSON data from assets and stores it in HashMap.
     */
    private void loadMakeModelData() {
        try {
            String jsonString = readFileFromAssets("makemodeltext.json");
            JSONObject jsonObject = new JSONObject(jsonString);

            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String make = keys.next();
                makeModelMap.put(make, jsonObject.getJSONArray(make));
            }

        } catch (Exception e) {
            decodedResultTextView.setText("Error loading make/model data: " + e.getMessage());
        }
    }

    /**
     * Reads the Odomatic fuel types from JSON.
     */
    private void loadFuelTypes() {
        try {
            String jsonString = readFileFromAssets("odomatic.json");
            JSONObject jsonObject = new JSONObject(jsonString).getJSONObject("fuel_types");

            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                fuelTypes.put(key, jsonObject.getString(key));
            }

        } catch (Exception e) {
            decodedResultTextView.setText("Error loading fuel types: " + e.getMessage());
        }
    }

    /**
     * Reads a file from /assets into a String.
     */
    private String readFileFromAssets(String fileName) throws Exception {
        AssetManager am = getAssets();
        InputStream is = am.open(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    /**
     * AsyncTask that fetches JSON from Odomatic.
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
     * Parses Odomatic JSON => e.g. "result":[0,10,16,6,1,...]
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
            int rawYear = arr.getInt(1); // e.g. 10 => year offset
            int makeIdx = arr.getInt(2); // e.g. 16
            int modelIdx = arr.getInt(3); // e.g. 6
            int fuelIdx = arr.getInt(4); // e.g. 1

            int actualYear = 2000 + rawYear;

            // Find Make & Model
            String makeName = "Unknown Make";
            String modelName = "Unknown Model";

            // Search for the make using makeIdx
            int count = 0;
            for (String make : makeModelMap.keySet()) {
                if (count == makeIdx) {
                    makeName = make;
                    JSONArray models = makeModelMap.get(make);
                    if (modelIdx < models.length()) {
                        modelName = models.getString(modelIdx);
                    }
                    break;
                }
                count++;
            }

            // Get Fuel Type
            String fuelType = fuelTypes.getOrDefault(String.valueOf(fuelIdx), "Unknown Fuel");

            // Show final decoded info
            String decoded = "Year: " + actualYear +
                    "\nMake: " + makeName +
                    "\nModel: " + modelName +
                    "\nFuel: " + fuelType;

            decodedResultTextView.setText("Decoded:\n" + decoded);

        } catch (Exception e) {
            decodedResultTextView.setText("Error decoding data: " + e.getMessage());
        }
    }
}
