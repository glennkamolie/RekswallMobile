package com.example.rekswallmobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class belirekasadana extends AppCompatActivity {

    private Button btnBack;
    private Button btnBuy;
    private EditText etAmount;
    private EditText etReksadanaValue;
    private TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_belirekasadana);

        btnBack = findViewById(R.id.back1);
        btnBuy = findViewById(R.id.btnBuy);
        etAmount = findViewById(R.id.etAmounts);
        etReksadanaValue = findViewById(R.id.etReksadanaValue);
        username = findViewById(R.id.username);

        Intent intent = getIntent();
        String receivedData = intent.getStringExtra("username");
        username.setText(receivedData);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(belirekasadana.this, mainmenu.class);
                intent.putExtra("username", receivedData);
                startActivity(intent1);
                finish();
            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String saldo = etAmount.getText().toString();
                Log.d("yourname", receivedData);
                makeBuyRequest(saldo, receivedData);
                finish();
            }
        });

        // Panggil metode untuk mendapatkan harga reksadana secara real-time
        getRealtimeReksadana();
    }

    private void getRealtimeReksadana() {
        // Buat AsyncTask untuk melakukan operasi jaringan
        new RealtimeReksadanaAsyncTask().execute();
    }

    private void makeBuyRequest(String amount, String username1) {
        // Buat objek JSON dengan nilai buy_reksadana dan username
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("buy_reksadana", amount);
            jsonBody.put("username", username.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Buat body permintaan
        String requestBody = jsonBody.toString();

        // Jalankan AsyncTask untuk melakukan operasi jaringan
        new BuyRequestAsyncTask().execute(requestBody);
    }

    private class RealtimeReksadanaAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String reksadanaValue = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL("http://192.168.1.27:3500/realtime");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Baca respons dari input stream
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    reksadanaValue = stringBuilder.toString();
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return reksadanaValue;
        }

        @Override
        protected void onPostExecute(String reksadanaValue) {
            if (reksadanaValue != null) {
                // Update nilai harga reksadana pada EditText
                etReksadanaValue.setText(reksadanaValue);
            } else {
                Toast.makeText(belirekasadana.this, "Failed to get real-time reksadana value", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class BuyRequestAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String requestBody = params[0];
            HttpURLConnection connection = null;
            int responseCode = -1;
            try {
                URL url = new URL("http://192.168.1.27:3500/buycostumers");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Tulis body permintaan ke output stream koneksi
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                writer.write(requestBody);
                writer.flush();
                writer.close();
                outputStream.close();

                // Baca respons dari input stream koneksi
                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(belirekasadana.this, "Berhasil membeli reksadana", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(belirekasadana.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }
}