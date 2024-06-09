package com.example.testing;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class mathgenActivity extends AppCompatActivity {
    SQLiteDatabase db;
    TextView textViewTask, textViewSolution,textKolSolve;
    EditText editTextAnswer;
    String username;
    Button buttonNewTask, buttonCheckAnswer;
    int level = 1;  // Начальный уровень сложности
    int num1, num2, result;
    char operator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mathgen);
        username= getIntent().getStringExtra("username");
        db =openOrCreateDatabase("UserData", MODE_PRIVATE, null);
        textViewTask = findViewById(R.id.textViewTask);
        textViewSolution = findViewById(R.id.textViewSolution);
        editTextAnswer = findViewById(R.id.editTextAnswer);
        buttonNewTask = findViewById(R.id.buttonNewTask);
        buttonCheckAnswer = findViewById(R.id.buttonCheckAnswer);
        textKolSolve=findViewById(R.id.textKolSolve);
        textKolSolve.setText(loadSolvesFromDatabase(username));
        textViewSolution.setVisibility(View.GONE); // Скрываем решение по умолчанию

        buttonNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateTask(level);
                textViewSolution.setVisibility(View.GONE); // Скрываем решение
                editTextAnswer.setText(""); // Очищаем поле ответа
            }
        });

        buttonCheckAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer();
            }
        });
        Button buttonExit = findViewById(R.id.Buttonexit);
        buttonExit.setOnClickListener(v-> finish());
        generateTask(level); // Генерируем первую задачу при запуске

    }

    // Метод для генерации математической задачи
    private void generateTask(int level) {
        Random random = new Random();
        num1 = random.nextInt(10 * level) + 1; // Числа зависят от уровня сложности
        num2 = random.nextInt(10 * level) + 1;

        switch (random.nextInt(4)) { // Случайно выбираем оператор
            case 0:
                operator = '+';
                result = num1 + num2;
                break;
            case 1:
                operator = '-';
                result = num1 - num2;
                break;
            case 2:
                operator = '*';
                result = num1 * num2;
                break;
            case 3:
                operator = '/'; // Деление, обеспечиваем целочисленное деление
                if (num2 == 0) {
                    num2 = 1; // Избегаем деления на 0
                }
                result = num1 / num2;
                break;
        }

        String task = num1 + " " + operator + " " + num2 + " = ?";
        textViewTask.setText(task);
    }

    // Метод для проверки ответа пользователя
    private void checkAnswer() {
        String answerString = editTextAnswer.getText().toString();
        if (answerString.isEmpty()) {
            Toast.makeText(this, "Введите ответ!", Toast.LENGTH_SHORT).show();
            return;
        }

        int answer;
        try {
            answer = Integer.parseInt(answerString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Неверный формат ответа!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (answer == result) {
            Toast.makeText(this, "Правильно!", Toast.LENGTH_SHORT).show();
            if (level < 3) {
                level++; // Увеличиваем уровень сложности
            }
            TextView textKolSolve = findViewById(R.id.textKolSolve);
            String text = textKolSolve.getText().toString();
            int colonIndex = text.indexOf(":");
            String numberString = text.substring(colonIndex + 1).trim();
            int number = Integer.parseInt(numberString);
            // Увеличиваем число на 1
            number++;
            // Записываем новое число обратно в TextView

            db.execSQL("UPDATE users SET solves = ? WHERE login = ?", new Object[]{number, username});
            generateTask(level); // Генерируем новую задачу
            String clicker = loadSolvesFromDatabase(username);
            textKolSolve.setText(clicker);
        } else {
            Toast.makeText(this, "Неправильно. Попробуйте еще раз.", Toast.LENGTH_SHORT).show();
            textViewSolution.setText("Решение: " + result);
            textViewSolution.setVisibility(View.VISIBLE); // Показываем решение
        }}
        private String loadSolvesFromDatabase(String username) {

            Cursor cursor = db.rawQuery("SELECT solves FROM users WHERE login=?", new String[]{username});
            StringBuilder sb = new StringBuilder();
            if (cursor.moveToFirst()) {
                String click = cursor.getString(cursor.getColumnIndexOrThrow("solves"));
                sb.append("Кол-во решенных задач: ").append(click);
            }
            cursor.close();
            return sb.toString();
        }
    }
