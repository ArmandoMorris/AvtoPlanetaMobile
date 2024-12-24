package com.example.myapplicationfirst;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminPanelActivity extends AppCompatActivity {

    private EditText editTextCreateName, editTextCreatePrice, editTextCreateImageUrl, editTextCreateDescription;
    private EditText editTextUpdateId, editTextUpdateName, editTextUpdatePrice, editTextUpdateImageUrl, editTextUpdateDescription;
    private Button buttonCreate, buttonUpdate, buttonDelete, buttonLogout;
    private EditText editTextDeleteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);


        editTextCreateName = findViewById(R.id.editTextCreateName);
        editTextCreatePrice = findViewById(R.id.editTextCreatePrice);
        editTextCreateImageUrl = findViewById(R.id.editTextCreateImageUrl);
        editTextCreateDescription = findViewById(R.id.editTextCreateDescription); // Новое поле
        buttonCreate = findViewById(R.id.buttonCreate);

        // Поле для удаления товара
        editTextDeleteId = findViewById(R.id.editTextDeleteId);
        buttonDelete = findViewById(R.id.buttonDelete);

        editTextUpdateId = findViewById(R.id.editTextUpdateId);
        editTextUpdateName = findViewById(R.id.editTextUpdateName);
        editTextUpdatePrice = findViewById(R.id.editTextUpdatePrice);
        editTextUpdateImageUrl = findViewById(R.id.editTextUpdateImageUrl);
        editTextUpdateDescription = findViewById(R.id.editTextUpdateDescription);
        buttonUpdate = findViewById(R.id.buttonUpdate);


        buttonLogout = findViewById(R.id.buttonLogout);


        buttonCreate.setOnClickListener(v -> createProduct());
        buttonUpdate.setOnClickListener(v -> updateProduct());
        buttonDelete.setOnClickListener(v -> deleteProduct());
        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPanelActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void createProduct() {
        String name = editTextCreateName.getText().toString().trim();
        String price = editTextCreatePrice.getText().toString().trim();
        String imageUrl = editTextCreateImageUrl.getText().toString().trim();
        String description = editTextCreateDescription.getText().toString().trim(); // Получаем описание

        if (name.isEmpty() || price.isEmpty() || imageUrl.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/autopart/create_product.php";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("price", Double.parseDouble(price));
            jsonObject.put("image_url", imageUrl);
            jsonObject.put("description", description); // Добавляем описание
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        sendRequest(url, jsonObject, "Продукт создан успешно", "Ошибка при создании продукта");
    }

    private void updateProduct() {
        String id = editTextUpdateId.getText().toString().trim();
        String name = editTextUpdateName.getText().toString().trim();
        String price = editTextUpdatePrice.getText().toString().trim();
        String imageUrl = editTextUpdateImageUrl.getText().toString().trim();
        String description = editTextUpdateDescription.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(this, "ID продукта обязателен для изменения", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/autopart/update_product.php";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            if (!name.isEmpty()) jsonObject.put("name", name);
            if (!price.isEmpty()) jsonObject.put("price", Double.parseDouble(price));
            if (!imageUrl.isEmpty()) jsonObject.put("image_url", imageUrl);
            if (!description.isEmpty()) jsonObject.put("description", imageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        sendRequest(url, jsonObject, "Данные успешно обновлены", "Ошибка при обновлении данных");
    }
    private void deleteProduct() {
        String id = editTextDeleteId.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(this, "ID продукта обязателен для удаления", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/autopart/delete_product.php";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        sendRequest(url, jsonObject, "Продукт успешно удален", "Ошибка при удалении продукта");
    }

    private void sendRequest(String url, JSONObject jsonObject, String successMessage, String errorMessage) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(AdminPanelActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdminPanelActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AdminPanelActivity.this, "Ошибка ответа: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(AdminPanelActivity.this, errorMessage + ": " + error.getMessage(), Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }
}
