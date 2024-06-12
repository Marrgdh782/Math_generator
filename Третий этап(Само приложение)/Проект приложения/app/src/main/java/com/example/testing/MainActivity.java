package com.example.testing;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    Button buttonLogin, buttonRegister;

    // Подключение к MySQL
    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            try {
                String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

                URL url = new URL("http://192.168.0.104/API/login.php");
                Log.d("LoginTask", "Sending request to: " + url.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(json);
                writer.flush();
                writer.close();
                int responseCode = conn.getResponseCode();
                Log.d("LoginTask", "Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String responseBody = in.readLine();
                    in.close();
                    Log.d("LoginTask", "Response body: " + responseBody);
                    return responseBody;
                } else {
                    Log.e("LoginTask", "Error: " + responseCode);
                    return "Login failed";
                }


            } catch (IOException e) {
                e.printStackTrace();
                return "Login failed. Check network connection.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("<br />")) {
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, DataActivity.class);
                intent.putExtra("username", editTextUsername.getText().toString());
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(view -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else {
                new LoginTask().execute(username, password);
            }
        });

        buttonRegister.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        CheckBox ShowPassword = findViewById(R.id.checkBoxShowPassword);
        ShowPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextPassword.setTransformationMethod(null);
            } else {
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    private void login(String username, String password) {
        try {
            String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

            URL url = new URL("http://192.168.0.104/API/login.php");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(json);
            writer.flush();
            writer.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String responseBody = in.readLine();
                in.close();

                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, DataActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Login failed. Check network connection.", Toast.LENGTH_SHORT).show();
        }
    }
}