package com.example.testing;

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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class mathgenActivity extends AppCompatActivity {

    // Подключение к API
    private static final String API_URL = "http://192.168.0.104/API"; // Замените на адрес вашего API

    TextView textViewTask, textViewSolution, textKolSolve;
    EditText editTextAnswer;
    String username;
    Button buttonNewTask, buttonCheckAnswer;
    int currentTaskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mathgen);
        username = getIntent().getStringExtra("username");

        textViewTask = findViewById(R.id.textViewTask);
        textViewSolution = findViewById(R.id.textViewSolution);
        editTextAnswer = findViewById(R.id.editTextAnswer);
        buttonNewTask = findViewById(R.id.buttonNewTask);
        buttonCheckAnswer = findViewById(R.id.buttonCheckAnswer);
        textKolSolve = findViewById(R.id.textKolSolve);

        // Загружаем начальное количество решенных задач
        new GetSolvesTask().execute(username);
        textViewSolution.setVisibility(View.GONE); // Скрываем решение по умолчанию

        buttonNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadTask().execute();
                textViewSolution.setVisibility(View.GONE); // Скрываем решение
                editTextAnswer.setText(""); // Очищаем поле ответа
            }
        });

        buttonCheckAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answerString = editTextAnswer.getText().toString();
                if (answerString.isEmpty()) {
                    Toast.makeText(mathgenActivity.this, "Введите ответ!", Toast.LENGTH_SHORT).show();
                    return;
                }
                new CheckAnswerTask().execute(currentTaskId, answerString, "hard"); // Передайте "hard", если нужно
            }
        });

        Button buttonExit = findViewById(R.id.Buttonexit);
        buttonExit.setOnClickListener(v -> finish());

        // Генерируем первую задачу при запуске
        new LoadTask().execute();
    }

    // Метод для загрузки новой задачи
    private class LoadTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(API_URL+"/tasks.php"); // API_URL - адрес вашего API
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // Отправляем данные на сервер
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write("{}"); // Отправляем пустой JSON-объект, если не нужны дополнительные параметры
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        responseBody.append(line);
                    }
                    in.close();
                    return responseBody.toString();
                } else {
                    return "Ошибка получения задачи";
                }
            } catch (IOException e) {
                return "Ошибка подключения к серверу";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("error")) {
                    Toast.makeText(mathgenActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                } else {
                    currentTaskId = jsonObject.getInt("id");
                    textViewTask.setText(jsonObject.getString("task"));
                    textViewSolution.setText(jsonObject.getString("solution"));
                }
            } catch (JSONException e) {
                Toast.makeText(mathgenActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Метод для проверки ответа пользователя
    private class CheckAnswerTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... objects) {
            int taskId = (int) objects[0];
            String answer = (String) objects[1];
            String hard = (String) objects[2];

            try {
                URL url = new URL(API_URL + "/check_answer.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // Создаем JSON-объект с данными
                JSONObject data = new JSONObject();
                data.put("taskId", taskId);
                data.put("answer", answer);
                data.put("hard", hard); // Добавлен hard

                // Преобразуем JSON-объект в строку
                String jsonInputString = data.toString();

                // Отправляем данные на сервер
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonInputString);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        responseBody.append(line);
                    }
                    in.close();
                    return responseBody.toString();
                } else {
                    return "Ошибка проверки ответа";
                }
            } catch (IOException | JSONException e) {
                return "Ошибка";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("error")) {
                    Toast.makeText(mathgenActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                } else {
                    if (jsonObject.getString("result").equals("correct")) {
                        Toast.makeText(mathgenActivity.this, "Правильно!", Toast.LENGTH_SHORT).show();
                        new UpdateSolvesTask().execute(username);
                        new LoadTask().execute();
                    } else {
                        Toast.makeText(mathgenActivity.this, "Неправильно. Попробуйте еще раз.", Toast.LENGTH_SHORT).show();
                        textViewSolution.setVisibility(View.VISIBLE);
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(mathgenActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Метод для обновления количества решенных задач в базе данных
    private class UpdateSolvesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(API_URL + "/update_solves.php"); // API_URL - адрес вашего API
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // Создаем JSON-объект с данными
                JSONObject data = new JSONObject();
                data.put("username", strings[0]);

                // Преобразуем JSON-объект в строку
                String jsonInputString = data.toString();

                // Отправляем данные на сервер
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonInputString);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        responseBody.append(line);
                    }
                    in.close();
                    return responseBody.toString();
                } else {
                    return "Ошибка обновления количества задач";
                }
            } catch (IOException | JSONException e) {
                return "Ошибка";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("error")) {
                    Toast.makeText(mathgenActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                }
                // ... обработка ответа ...
            } catch (JSONException e) {
                Toast.makeText(mathgenActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Метод для загрузки количества решенных задач из базы данных
    private class GetSolvesTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(API_URL + "/get_solves.php"); // API_URL - адрес вашего API
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // Создаем JSON-объект с данными
                JSONObject data = new JSONObject();
                data.put("username", strings[0]);

                // Преобразуем JSON-объект в строку
                String jsonInputString = data.toString();

                // Отправляем данные на сервер
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(jsonInputString);
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        responseBody.append(line);
                    }
                    in.close();
                    return responseBody.toString();
                } else {
                    return "Ошибка получения количества задач";
                }
            } catch (IOException | JSONException e) {
                return "Ошибка";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("error")) {
                    Toast.makeText(mathgenActivity.this, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                } else {
                    int solves = jsonObject.getInt("solves");
                    textKolSolve.setText("Кол-во решенных задач: " + solves);
                }
                // ... обработка ответа ...
            } catch (JSONException e) {
                Toast.makeText(mathgenActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
            }
        }
    }
}