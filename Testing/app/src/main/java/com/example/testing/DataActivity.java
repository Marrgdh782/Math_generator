package com.example.testing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DataActivity extends AppCompatActivity {

    TextView textViewData, textViewUsername;
    SQLiteDatabase db;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.avatar);
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
        // Получение данных из БД
        db = openOrCreateDatabase("UserData", MODE_PRIVATE, null);
        String data = loadDataFromDatabase(username);
        textViewData.setText(data);
        TextView textKolClick = findViewById(R.id.textKolClick);
        String clicks = loadClicksFromDatabase(username);
        textKolClick.setText(clicks);
        TextView solves = findViewById(R.id.textViewSolves);
        String solve = loadSolvesFromDatabase(username);
        solves.setText(solve);

        Button buttonClick = findViewById(R.id.buttonClick);
        buttonClick.setOnClickListener((view -> {

            String text = textKolClick.getText().toString();
            int colonIndex = text.indexOf(":");
            String numberString = text.substring(colonIndex + 1).trim();
            int number = Integer.parseInt(numberString);
            // Увеличиваем число на 1
            number++;
            // Записываем новое число обратно в TextView
            String clicker = loadClicksFromDatabase(username);
            textKolClick.setText(clicker);
            db.execSQL("UPDATE users SET click = ? WHERE login = ?", new Object[]{number, username});

        }));
        Button buttonColor =findViewById(R.id.buttonColor);
        buttonColor.setOnClickListener((view -> {
            LinearLayout layout =findViewById(R.id.layout);
            int red = (int) (Math.random() * 256);
            int green = (int) (Math.random() * 256);
            int blue = (int) (Math.random() * 256);
            int randomColor = Color.rgb(red, green, blue);
            layout.setBackgroundColor(randomColor);
        }));
        Button buttonReload =findViewById(R.id.buttonReload);
        buttonReload.setOnClickListener(v -> {String solver = loadSolvesFromDatabase(username);
            solves.setText(solver);});

    }

    private String loadDataFromDatabase(String username) {
        Cursor cursor = db.rawQuery("SELECT email FROM users WHERE login=?", new String[]{username});
        StringBuilder sb = new StringBuilder();
        if (cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            sb.append("Ваша почта: ").append(email);
        }
        cursor.close();
        return sb.toString();
    }
    private String loadClicksFromDatabase(String username) {
        Cursor cursor = db.rawQuery("SELECT click FROM users WHERE login=?", new String[]{username});
        StringBuilder sb = new StringBuilder();
        if (cursor.moveToFirst()) {
            String click = cursor.getString(cursor.getColumnIndexOrThrow("click"));
            sb.append("Кол-во кликов: ").append(click);
        }
        cursor.close();
        return sb.toString();
    }
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