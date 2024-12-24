package com.example.myapplicationfirst;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameView;
    private EditText emailView;
    private Button saveButton, logoutButton, btnCatalog, btnCart, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Инициализация элементов интерфейса
        usernameView = findViewById(R.id.profile_username);
        emailView = findViewById(R.id.profile_email);
        saveButton = findViewById(R.id.save_button);
        logoutButton = findViewById(R.id.logout_button);

        // Кнопки для переходов между активностями
        btnCatalog = findViewById(R.id.btn_catalog);
        btnCart = findViewById(R.id.btn_cart);
        btnProfile = findViewById(R.id.btn_profile);

        // Загрузка данных пользователя
        loadUserProfile();

        // Слушатель для кнопки "Сохранить"
        saveButton.setOnClickListener(v -> saveProfile());

        // Слушатель для кнопки "Выйти"
        logoutButton.setOnClickListener(v -> logout());

        // Переходы через кнопки
        btnCatalog.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CatalogActivity.class);
            startActivity(intent);
        });

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            // Уже в профиле
            Toast.makeText(ProfileActivity.this, "Вы уже в профиле", Toast.LENGTH_SHORT).show();
        });
    }

    // Метод для загрузки данных пользователя
    private void loadUserProfile() {
        int userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("user_id", -1);
        String username = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("username", "Неизвестный");
        String email = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("email", "Не указан");
        if (userId != -1) {
            // Например, можно загрузить имя и email из API или из базы данных
            usernameView.setText(username); // Выводим username
            emailView.setText(email); // Выводим email
        }
    }

    // Метод для сохранения данных профиля
    private void saveProfile() {
        String newEmail = emailView.getText().toString();

        // Проверка на пустоту email
        if (newEmail.isEmpty()) {
            Toast.makeText(this, "Email не может быть пустым", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получаем user_id из SharedPreferences
        int userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("user_id", -1);

        if (userId != -1) {
            // Отправляем новый email на сервер для обновления
            updateEmail(userId, newEmail);
        } else {
            Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для обновления email на сервере
    private void updateEmail(int userId, String newEmail) {
        String url = "http://10.0.2.2/autopart/update_email.php"; // URL вашего API

        // Создаем JSON-объект с данными для отправки
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("email", newEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Создаем очередь запросов
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Создаем запрос
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                // Обновление прошло успешно
                                Toast.makeText(ProfileActivity.this, "Email обновлен успешно", Toast.LENGTH_SHORT).show();

                                // Сохраняем новый email в SharedPreferences
                                getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                        .edit()
                                        .putString("email", newEmail)
                                        .apply();

                                // Обновляем отображение на экране
                                emailView.setText(newEmail);
                            } else {
                                // Ошибка обновления
                                Toast.makeText(ProfileActivity.this, "Ошибка: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Ошибка данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this, "Ошибка сети: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Добавляем запрос в очередь
        requestQueue.add(jsonObjectRequest);
    }

    // Метод для выхода из профиля
    private void logout() {
        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .edit()
                .remove("user_id")
                .remove("username")
                .remove("email")
                .apply();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

