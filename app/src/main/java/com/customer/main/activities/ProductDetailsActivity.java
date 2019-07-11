package com.customer.main.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.customer.R;
import com.customer.base.preference.UrbanForestPrefrences;
import com.customer.base.retrofit.RetrofitClient;
import com.customer.main.api.IUsersApi;
import com.customer.main.model.request.add_product_request.AddProductRequest;
import com.customer.main.model.request.add_to_cart.AddCart;
import com.customer.main.model.request.get_all_products.Datum;
import com.customer.main.model.request.get_product_details_by_id.ProductIdRequest;
import com.customer.main.model.response.general_response.GeneralResponse;
import com.customer.main.model.response.product_details_response.ProductDetails;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author karan
 */
public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public TextView tvProductName, tvProductPrice, tvProductDesc, tvProductSize;
    public ImageView ivProductImage;
    private Button btnAddProduct;
    private LinearLayout llAddImage;
    private String base64String;
    private Bitmap bitmap;
    private AddProductRequest addProductRequest;
    private ProgressBar mProgressView;
    private CardView cvAddProduct;

    private Context context;
    private String url;
    private LinearLayout llCategory;
    List<Datum> data;
    String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductDesc = findViewById(R.id.tvProductDesc);
        tvProductSize = findViewById(R.id.tvProductSize);
        mProgressView = findViewById(R.id.mProgressView);
        cvAddProduct = findViewById(R.id.cvAddProduct);

        findViewById(R.id.btnAddToCart).setOnClickListener(this);

//        llCategory = findViewById(R.id.llCategory);

        if (getIntent().getExtras() != null){

            String productId = getIntent().getStringExtra("category_id");
            ProductIdRequest productIdRequest = new ProductIdRequest();
            productIdRequest.setProductId(productId);

            getProductDetails(productIdRequest);
        }

//        validate();

    }

    private void getProductDetails(ProductIdRequest productIdRequest) {

        RetrofitClient.getClient().create(IUsersApi.class).getProductbyId(productIdRequest).enqueue(new Callback<ProductDetails>() {
            @Override
            public void onResponse(Call<ProductDetails> call, Response<ProductDetails> response) {
                mProgressView.setVisibility(View.GONE);
                cvAddProduct.setVisibility(View.VISIBLE);

                if (response.body() != null && response.body().getStatus() == 1) {
//                    Toast.makeText(ProductDetailsActivity.this,
//                            "" + response.body().getStatus(),
//                            Toast.LENGTH_SHORT).show();

                    productId = response.body().getData().get(0).getPid();


                    Glide.with(ProductDetailsActivity.this)
                            .load(response.body().getUrl() + response.body().getData().get(0).getPimage())
                            .into(ivProductImage);
                    tvProductName.setText(response.body().getData().get(0).getPname());
                    tvProductPrice.setText(" ₹ " + response.body().getData().get(0).getPrice());
                    tvProductDesc.setText(response.body().getData().get(0).getPdescription());
                    tvProductDesc.setText("The image is for reference purpose only. The actual product may vary in shape or appearance based on climate, age, height, etc.");
                    tvProductSize.setText("Size : " + "12 inches"/*response.body().getData().get(0).getPsize()*/);

//                    finish();

                } else {
                    if (response.body() != null) {
//                        Toast.makeText(ProductDetailsActivity.this,
//                                "Some error occurred fetching profile details",
//                                Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ProductDetails> call, Throwable t) {

                mProgressView.setVisibility(View.GONE);
                cvAddProduct.setVisibility(View.VISIBLE);
                Log.e("KKK", "FAIL");
                Toast.makeText(ProductDetailsActivity.this,
                        getString(R.string.error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddToCart:
                addToCart();
                break;
            case R.id.productImage:
                CropImage.activity()
                        .setOutputCompressQuality(40)
                        .setMinCropWindowSize(400, 600)
                        .start(this);
                break;
        }
    }

    private void addToCart() {
        AddCart addCart = new AddCart();
        addCart.setCustomerid(UrbanForestPrefrences.getInstance(this).getVid());
        addCart.setProductid(productId);
        addCart.setProductqty("1");
        RetrofitClient.getClient().create(IUsersApi.class).addCart(addCart).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                mProgressView.setVisibility(View.GONE);
                cvAddProduct.setVisibility(View.VISIBLE);

                if (response.body() != null && response.body().getStatus() == 1) {
                    Toast.makeText(ProductDetailsActivity.this,
                            "" + response.body().getMessage(),
                            Toast.LENGTH_SHORT).show();



                } else {
                    if (response.body() != null) {
                        Toast.makeText(ProductDetailsActivity.this,
                                "Some error occurred fetching profile details",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {

                mProgressView.setVisibility(View.GONE);
                cvAddProduct.setVisibility(View.VISIBLE);
                Log.e("KKK", "FAIL");
                Toast.makeText(ProductDetailsActivity.this,
                        getString(R.string.error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public boolean validate() {
//        if (TextUtils.isEmpty(edtProductName.getText().toString().trim())) {
//            edtProductName.setError("Name can't be empty");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(edtProductPrice.getText().toString().trim())) {
//            edtProductPrice.setError("Price can't be empty");
//            return false;
//        }
//
//        if (TextUtils.isEmpty(base64String)) {
//            Toast.makeText(this, "Please select a product image", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        return true;
//    }


    private void uploadProduct(AddProductRequest addProductRequest) {

        RetrofitClient.getClient().create(IUsersApi.class).addProduct(addProductRequest).enqueue(new Callback<GeneralResponse>() {
            @Override
            public void onResponse(Call<GeneralResponse> call, Response<GeneralResponse> response) {
                mProgressView.setVisibility(View.GONE);
                cvAddProduct.setVisibility(View.VISIBLE);

                if (response.body() != null && response.body().getStatus() == 1) {
                    Toast.makeText(ProductDetailsActivity.this,
                           response.body().getMessage(),
                            Toast.LENGTH_SHORT).show();

                    finish();

                } else {
                    if (response.body() != null) {
                        Toast.makeText(ProductDetailsActivity.this,
                                "Some error occurred fetching profile details",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<GeneralResponse> call, Throwable t) {

                mProgressView.setVisibility(View.GONE);
                cvAddProduct.setVisibility(View.VISIBLE);
                Log.e("KKK", "FAIL");
                Toast.makeText(ProductDetailsActivity.this,
                        getString(R.string.error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkIfEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
