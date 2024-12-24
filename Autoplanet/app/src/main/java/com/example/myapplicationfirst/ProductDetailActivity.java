package com.example.myapplicationfirst;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ProductDetailActivity extends AppCompatActivity {

    private int productId;
    private String productName;
    private double productPrice;
    private String productImageUrl;
    private String productDescription;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Инициализация Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Получение user_id из SharedPreferences
        userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Получаем данные о товаре из Intent
        productId = getIntent().getIntExtra("id", -1);
        productName = getIntent().getStringExtra("name");
        productPrice = getIntent().getDoubleExtra("price", 0);
        productDescription = getIntent().getStringExtra("description");
        productImageUrl = getIntent().getStringExtra("image_url");
        String productDescription = getIntent().getStringExtra("description");  // Добавляем описание

        // Инициализация UI элементов
        ImageView productImage = findViewById(R.id.product_detail_image);
        TextView productNameView = findViewById(R.id.product_detail_name);
        TextView productdescriptionView = findViewById(R.id.product_detail_description);
        TextView productPriceView = findViewById(R.id.product_detail_price);
        TextView productDescriptionView = findViewById(R.id.product_detail_description);  // Для описания товара
        Button addToCartButton = findViewById(R.id.add_to_cart_button);

        // Заполнение данных о товаре
        productNameView.setText(productName);
        productdescriptionView.setText(productDescription);
        productPriceView.setText(String.format("%,.2f ₽", productPrice));
        Glide.with(this).load(productImageUrl).into(productImage);

        // Устанавливаем описание товара
        if (productDescription != null && !productDescription.isEmpty()) {
            productDescriptionView.setText(productDescription);
        } else {
            productDescriptionView.setText("Описание недоступно.");
        }

        // Обработчик нажатия на кнопку "Добавить в корзину"
        addToCartButton.setOnClickListener(v -> addToCart());

        // Обработка нажатий кнопок
        findViewById(R.id.btn_catalog).setOnClickListener(v -> {
            // Каталог: ничего не делаем, так как мы уже здесь
            Intent intent = new Intent(this, CatalogActivity.class);
            startActivity(intent);
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
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_catalog) {
//            startActivity(new Intent(this, CatalogActivity.class));
//            finish();
//            return true;
//        } else if (id == R.id.action_cart) {
//            Intent intent = new Intent(this, CartActivity.class);
//            startActivity(intent);
//            return true;
//        } else if (id == R.id.action_account) {
//            startActivity(new Intent(this, LoginActivity.class));
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void addToCart() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/autopart/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);


        Call<ApiResponse> call = apiService.addToCart(userId, productId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProductDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Ошибка добавления в корзину", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public interface ApiService {
        @FormUrlEncoded
        @POST("add_to_cart.php")
        Call<ApiResponse> addToCart(@Field("user_id") int userId, @Field("product_id") int productId);
    }


    public static class ApiResponse {
        private String status;
        private String message;

        public String getMessage() {
            return message;
        }
    }
}