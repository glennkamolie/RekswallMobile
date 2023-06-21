package com.example.rekswallmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class loginactivity extends AppCompatActivity {

    private EditText usernamelogin, passwordlogin;
    private Button btnLogin;
    private static final String API_URL = "http://192.168.1.27:3500/loginrekswall"; // Update the API URL with your server address
    private LoginListener loginListener;

    public interface LoginListener {
        void onLoginSuccess();
    }

    public void setLoginListener(LoginListener listener) {
        loginListener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);
        usernamelogin = findViewById(R.id.usernamelogin);
        passwordlogin = findViewById(R.id.passwordlogin);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernamelogin.getText().toString();
                String password = passwordlogin.getText().toString();
                loginCustomer(username, password);
            }
        });

    }

    private void loginCustomer(String username, String password) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", username);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new LoginCustomerTask().execute(API_URL, postData.toString());
    }

    private class LoginCustomerTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            String jsonData = params[1];
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(apiUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(jsonData.getBytes());
                outputStream.flush();
                outputStream.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
                    bufferedReader.close();
                    inputStream.close();
                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("LoginCustomer", "Response: " + result);
            Toast.makeText(loginactivity.this, result, Toast.LENGTH_SHORT).show();
            // Check if login is successful
            if (result.equals("Login Berhasil")) { //equal = membandingkan jawaban
                Intent intent = new Intent(loginactivity.this, mainmenu.class);
                intent.putExtra("username", usernamelogin.getText().toString());
                startActivity(intent);
                if (loginListener != null) {
                    loginListener.onLoginSuccess();

                }
            }
        }
    }



}