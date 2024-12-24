package com.example.myapplicationfirst;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CatalogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CatalogAdapter catalogAdapter;
    private List<Product> productList;
    private int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerView = findViewById(R.id.catalog_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();

        userId = getUserId();

        catalogAdapter = new CatalogAdapter(productList, this, userId);
        recyclerView.setAdapter(catalogAdapter);

        loadProducts();

        // Обработка нажатий кнопок
        findViewById(R.id.btn_catalog).setOnClickListener(v -> {
            // Каталог: ничего не делаем, так как мы уже здесь
            Toast.makeText(this, "Вы уже в каталоге", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_cart).setOnClickListener(v -> {
            // Переход в корзину
            Intent intent = new Intent(this, CartActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        findViewById(R.id.btn_profile).setOnClickListener(v -> {
            // Переход в профиль
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_catalog);
//
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//
//        recyclerView = findViewById(R.id.catalog_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        productList = new ArrayList<>();
//
//        userId = getUserId();
//
//        catalogAdapter = new CatalogAdapter(productList, this, userId);
//        recyclerView.setAdapter(catalogAdapter);
//
//        loadProducts();
//    }



//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_catalog) {
//            startActivity(new Intent(this, CatalogActivity.class));
//            finish();
//            return true;
//        } else if (id == R.id.action_cart) {
//            Intent intent = new Intent(this, CartActivity.class);
//            intent.putExtra("user_id", userId);
//            startActivity(intent);
//            return true;
//        } else if (id == R.id.action_account) {
//            // Переход в активность профиля
//            Intent intent = new Intent(this, ProfileActivity.class);
//            startActivity(intent);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        // Убедитесь, что каждая кнопка меню будет отображаться, если есть место
//        for (int i = 0; i < menu.size(); i++) {
//            MenuItem item = menu.getItem(i);
//            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS); // Убедитесь, что они всегда показываются
//        }
//        return true;
//    }

    private void loadProducts() {
        String url = "http://10.0.2.2/autopart/get_products.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            productList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject product = response.getJSONObject(i);
                                int id = product.getInt("id");
                                String name = product.getString("name");
                                double price = product.getDouble("price");
                                String image_url = product.getString("image_url");
                                String description = product.getString("description"); // Получаем описание товара


                                productList.add(new Product(id, name, price, image_url, description));
                            }
                            catalogAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CatalogActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CatalogActivity", "Ошибка загрузки: " + error.getMessage());
                        Toast.makeText(CatalogActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }


    private int getUserId() {
        return getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getInt("user_id", -1); // -1, если user_id не найден
    }

    public static String formatPrice(double price) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,##0.00 ₽", symbols);
        return formatter.format(price);
    }
}