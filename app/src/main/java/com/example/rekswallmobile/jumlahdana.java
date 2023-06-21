package com.example.rekswallmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class jumlahdana extends AppCompatActivity {
    private String username;

    private Button btnBack, btnGetJumlah;
    private TextView tvSaldo, tvReksadana;

    private static final String API_URL = "http://192.168.1.27:3500/costumersdana";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jumlahdana);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        btnBack = findViewById(R.id.btnBack);
        btnGetJumlah = findViewById(R.id.btnGetJumlah);
        tvSaldo = findViewById(R.id.tvSaldo);
        tvReksadana = findViewById(R.id.tvReksadana);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(jumlahdana.this, mainmenu.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        btnGetJumlah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengakses saldo dan reksadana menggunakan AsyncTask
                Log.d("jumlahdana", username);
                new ApiTask().execute(API_URL);
            }
        });
    }

    private class ApiTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String apiUrl = strings[0];
            String response = "";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");

                JSONObject requestBody = new JSONObject();
                requestBody.put("username", username);

                connection.getOutputStream().write(requestBody.toString().getBytes());
                connection.getOutputStream().flush();
                connection.getOutputStream().close();

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuilder stringBuilder = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    response = stringBuilder.toString();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    // Mendapatkan data saldo dan reksadana
                    int saldo = jsonObject.getInt("saldo");
                    double reksadana = jsonObject.getDouble("reksadana");

                    // Menampilkan saldo dan reksadana
                    tvSaldo.setText("Saldo: " + saldo);
                    tvReksadana.setText("Reksadana: " + reksadana);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
