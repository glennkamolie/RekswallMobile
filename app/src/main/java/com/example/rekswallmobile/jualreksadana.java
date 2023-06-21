package com.example.rekswallmobile;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class jualreksadana extends AppCompatActivity {

    private Button btnback;
    private Button btnSell;
    private EditText etAmount1;

    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jualreksadana);
        btnback = findViewById(R.id.back2);
        btnSell = findViewById(R.id.btnSell);
        etAmount1 = findViewById(R.id.etAmounts);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(jualreksadana.this, mainmenu.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sellAmount = etAmount1.getText().toString();
                makeSellRequest(sellAmount, username);
            }
        });
    }

    private void makeSellRequest(String sellAmount, String username) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("sell_reksadana", sellAmount);
            jsonBody.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String requestBody = jsonBody.toString();

        new SellRequestAsyncTask().execute(requestBody);
    }

    private class SellRequestAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String requestBody = params[0];
            HttpURLConnection connection = null;
            int responseCode = -1;
            try {
                URL url = new URL("http://192.168.1.27:3500/sellcostumers");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                writer.write(requestBody);
                writer.flush();
                writer.close();
                outputStream.close();

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
                Toast.makeText(jualreksadana.this, "Berhasil menjual reksadana", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(jualreksadana.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
