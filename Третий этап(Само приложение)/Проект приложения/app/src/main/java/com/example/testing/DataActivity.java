package com.example.testing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DataActivity extends AppCompatActivity {

    TextView textViewData, textViewUsername;
    private int userId;

    // Подключение к MySQL
    private static final String API_URL = "http://192.168.0.104/API/users.php"; // Замените на адрес вашего API
    private static final String API_MATH_STAT_URL = "http://192.168.0.104/API/math_stat.php"; // Замените на адрес вашего API
    private static final String API_UPDATE_MATH_STAT_URL = "http://192.168.0.104/API/update_math_stat.php"; // Замените на адрес вашего API

    private TextView textView0Total, textView0Right, textView0Wrong;
    private TextView textView1Total, textView1Right, textView1Wrong;
    private TextView textView2Total, textView2Right, textView2Wrong;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        Button logoutButton = findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(view -> {
            finish(); // Закрытие текущей Activity
        });
        textViewData = findViewById(R.id.textViewEmail);
        textViewUsername = findViewById(R.id.textViewUsername);

        // Получение имени пользователя из интента
        String username = getIntent().getStringExtra("username");
        textViewUsername.setText("Приветствую, " + username);
        Button buttonSolver = findViewById(R.id.buttonReadySolve);
        buttonSolver.setOnClickListener(v->{
            Intent intent =new Intent(DataActivity.this,mathgenActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);

        });

        // Загрузка данных из MySQL
        new LoadDataTask().execute(username);
        Button buttonColor = findViewById(R.id.buttonColor);
        buttonColor.setOnClickListener((view -> {
            LinearLayout layout = findViewById(R.id.layout);
            int red = (int) (Math.random() * 256);
            int green = (int) (Math.random() * 256);
            int blue = (int) (Math.random() * 256);
            int randomColor = Color.rgb(red, green, blue);
            layout.setBackgroundColor(randomColor);
        }));
        Button buttonReload =findViewById(R.id.buttonReload);
        buttonReload.setOnClickListener(v -> {
            new LoadDataTask().execute(username);
        });

        // Инициализация TextView для отображения статистики
        textView0Total = findViewById(R.id.textView0Total);
        textView0Right = findViewById(R.id.textView0Right);
        textView0Wrong = findViewById(R.id.textView0Wrong);
        textView1Total = findViewById(R.id.textView1Total);
        textView1Right = findViewById(R.id.textView1Right);
        textView1Wrong = findViewById(R.id.textView1Wrong);
        textView2Total = findViewById(R.id.textView2Total);
        textView2Right = findViewById(R.id.textView2Right);
        textView2Wrong = findViewById(R.id.textView2Wrong);
    }

    private class LoadDataTask extends AsyncTask<String, Void, String> {
        @NonNull
        @Override
        protected String doInBackground(@NonNull String... params) {
            String username = params[0];
            try {
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // Отправляем данные в теле запроса
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", username);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                Log.d("LoadDataTask", "Response code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    // Читаем все строки из ответа
                    while ((line = in.readLine()) != null) {
                        responseBody.append(line);
                    }
                    in.close();
                    Log.d("LoadDataTask", "Response body: " + responseBody.toString());
                    return responseBody.toString();
                } else {
                    return "Не удалось загрузить данные";
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "Не удалось загрузить данные";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Log.d("LoadDataTask", "Result: " + result);
                JSONObject jsonObject = new JSONObject(result);

                // Получаем email из JSON-ответа
                String email = jsonObject.getString("email");
                textViewData.setText("Ваша почта: " + email);

                // Получаем id пользователя
                userId = jsonObject.getInt("id");

                // Создаем запись в math_stat для пользователя
                createStat(getIntent().getStringExtra("username"));

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(DataActivity.this, "Ошибка загрузки данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CreateStatTask extends AsyncTask<String, Void, String> {
        @NonNull
        @Override
        protected String doInBackground(@NonNull String... params) {
            String username = params[0];
            try {
                URL url = new URL(API_MATH_STAT_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // Отправляем данные в теле запроса
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", userId);
                jsonObject.put("username", username);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                Log.d("CreateStatTask", "Response code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    // Читаем все строки из ответа
                    while ((line = in.readLine()) != null) {
                        responseBody.append(line);
                    }
                    in.close();
                    Log.d("CreateStatTask", "Response body: " + responseBody.toString());
                    return responseBody.toString();
                } else {
                    return "Не удалось создать статистику";
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "Не удалось создать статистику";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Обработка результата создания статистики
            updateStatisticsView();
        }
    }

    private class UpdateStatTask extends AsyncTask<Map<String, String>, Void, String> {
        @NonNull
        @Override
        protected String doInBackground(@NonNull Map<String, String>... params) {
            Map<String, String> data = params[0];
            try {
                URL url = new URL(API_UPDATE_MATH_STAT_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // Отправляем данные в теле запроса
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", userId);
                jsonObject.put("field", data.get("field"));
                jsonObject.put("value", data.get("value"));
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                Log.d("UpdateStatTask", "Response code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    // Читаем все строки из ответа
                    while ((line = in.readLine()) != null) {
                        responseBody.append(line);
                    }
                    in.close();
                    Log.d("UpdateStatTask", "Response body: " + responseBody.toString());
                    return responseBody.toString();
                } else {
                    return "Не удалось обновить статистику";
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return "Не удалось обновить статистику";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Обработка результата обновления статистики
            updateStatisticsView();
        }
    }

    private void updateStat(String field, int value) {
        Map<String, String> data = new HashMap<>();
        data.put("field", field);
        data.put("value", String.valueOf(value));
        new UpdateStatTask().execute(data);
    }

    private void createStat(String username) {
        new CreateStatTask().execute(username);
    }

    private void updateStatisticsView() {
        new LoadStatTask().execute(userId);
    }

    private class LoadStatTask extends AsyncTask<Integer, Void, String> {
        @NonNull
        @Override
        protected String doInBackground(@NonNull Integer... params) {
            int userId = params[0];
            try {
                URL url = new URL(API_MATH_STAT_URL + "?id=" + userId); // Используем GET для загрузки статистики
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET"); // Используем GET для загрузки статистики
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(false); // Устанавливаем DoOutput в false для GET-запроса
                conn.setDoInput(true);

                int responseCode = conn.getResponseCode();
                Log.d("LoadStatTask", "Response code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    // Читаем все строки из ответа
                    while ((line = in.readLine()) != null) {
                        responseBody.append(line);
                    }
                    in.close();
                    Log.d("LoadStatTask", "Response body: " + responseBody.toString());
                    return responseBody.toString();
                } else {
                    return "Не удалось загрузить данные статистики";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Ошибка подключения к серверу"; // Или другое подходящее сообщение
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Log.d("LoadStatTask", "Result: " + result);
                JSONObject jsonObject = new JSONObject(result);

                // Обновляем текст в TextView
                textView0Total.setText(jsonObject.getString("0_total"));
                textView0Right.setText(jsonObject.getString("0_right"));
                textView0Wrong.setText(jsonObject.getString("0_wrong"));
                textView1Total.setText(jsonObject.getString("1_total"));
                textView1Right.setText(jsonObject.getString("1_right"));
                textView1Wrong.setText(jsonObject.getString("1_wrong"));
                textView2Total.setText(jsonObject.getString("2_total"));
                textView2Right.setText(jsonObject.getString("2_right"));
                textView2Wrong.setText(jsonObject.getString("2_wrong"));

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(DataActivity.this, "Ошибка загрузки данных статистики: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}