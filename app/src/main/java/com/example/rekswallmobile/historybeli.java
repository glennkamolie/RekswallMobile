package com.example.rekswallmobile;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class historybeli extends AppCompatActivity {

    private Button btnback;
    private Button btnAmbilHistory; // Tombol Ambil History
    private String username;
    private List<String> buyHistoryList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historybeli);
        btnback = findViewById(R.id.back4);
        btnAmbilHistory = findViewById(R.id.ambilhistory);
        listView = findViewById(R.id.listViewbeli);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, buyHistoryList);
        listView.setAdapter(adapter);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(historybeli.this, mainmenu.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        btnAmbilHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengambil history beli dari API
                Log.d("historybeli", username);
                new GetBuyHistoryTask().execute();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                // Lakukan tindakan yang diperlukan saat item di ListView diklik
                Toast.makeText(historybeli.this, "Item selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GetBuyHistoryTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String apiUrl = "http://192.168.1.27:3500/buyhistory";
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                connection.setDoInput(true);
                connection.setDoOutput(true);

                connection.setRequestProperty("Content-Type", "application/json");

                String requestBody = "{\"username\": \"" + username + "\"}";

                connection.getOutputStream().write(requestBody.getBytes());
                connection.getOutputStream().flush();
                connection.getOutputStream().close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    inputStream.close();
                    return response.toString();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    buyHistoryList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String idBeli = jsonObject.getString("id_beli");
                        String userId = jsonObject.getString("user_id");
                        String totalReksadanaLama = jsonObject.getString("total_reksadana_lama");
                        String beliReksadana = jsonObject.getString("beli_reksadana");
                        String totalReksadanaBaru = jsonObject.getString("total_reksadana_baru");
                        String tanggalTransaksi = jsonObject.getString("tanggal_transaksi");

                        // Create a formatted string to display the data
                        String historyItem = "ID Beli: " + idBeli + "\n"
                                + "User ID: " + userId + "\n"
                                + "Total Reksadana Lama: " + totalReksadanaLama + "\n"
                                + "Beli Reksadana: " + beliReksadana + "\n"
                                + "Total Reksadana Baru: " + totalReksadanaBaru + "\n"
                                + "Tanggal Transaksi: " + tanggalTransaksi + "\n";

                        buyHistoryList.add(historyItem);
                    }

                    adapter.notifyDataSetChanged();

                    Toast.makeText(historybeli.this, "Buy history berhasil diambil", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(historybeli.this, "Gagal mengambil buy history", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(historybeli.this, "Gagal mengambil buy history", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
