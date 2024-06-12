package com.example.testing;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
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

public class RegistrationActivity extends AppCompatActivity {

    EditText editTextRegUsername, editTextRegPassword, edittextRegemail;
    Button buttonRegister, buttonAlready;
    private class RegisterUserTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            try {
                String json = "{\"name\":\"" + username + "\",\"password\":\"" + password + "\",\"email\":\"" + email + "\"}";

                URL url = new URL("http://192.168.0.104/API/registration.php");

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
                    return responseBody;
                } else {
                    return "Registration failed";
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "Registration failed. Check network connection.";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(RegistrationActivity.this, result, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextRegUsername = findViewById(R.id.editTextUsername);
        editTextRegPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        edittextRegemail = findViewById(R.id.editTextEmail);
        buttonAlready = findViewById(R.id.buttonalready);

        buttonAlready.setOnClickListener(v -> finish());
        CheckBox showPassword = findViewById(R.id.checkBoxShowPassword);
        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextRegPassword.setTransformationMethod(null);
            } else {
                editTextRegPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        buttonRegister.setOnClickListener(v -> {
            String username = editTextRegUsername.getText().toString();
            String password = editTextRegPassword.getText().toString();
            String email = edittextRegemail.getText().toString();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(RegistrationActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else {
                new RegisterUserTask().execute(username, password, email);
            }
        });
    }


}
