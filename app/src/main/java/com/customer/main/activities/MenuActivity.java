package com.customer.main.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.customer.R;
import com.customer.base.retrofit.RetrofitClient;
import com.customer.main.adapter.MenuAdapter;
import com.customer.main.api.IUsersApi;
import com.customer.main.model.request.get_all_products.AllProducts;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

;

public class MenuActivity extends Activity {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;
    private RecyclerView rvCategories;
    private ProgressBar mProgressView;
    private RelativeLayout rlMenu;
    private MenuAdapter categoriesAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.fragment_menu);

        rvCategories = findViewById(R.id.rvCategories);
        mProgressView = findViewById(R.id.mProgressView);
        rlMenu = findViewById(R.id.rlMenu);

        GridLayoutManager mLayoutManager = new GridLayoutManager(MenuActivity.this, 2);
        rvCategories.setLayoutManager(mLayoutManager);
        rvCategories.setItemAnimator(new DefaultItemAnimator());


        fetchCategories();
    }
    
    

    private void fetchCategories() {

        mProgressView.setVisibility(View.VISIBLE);
        RetrofitClient.getClient().create(IUsersApi.class).getAllProducts().enqueue(new Callback<AllProducts>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<AllProducts> call, @NonNull Response<AllProducts> response) {


                if (response.body() != null && response.body().getStatus() == 1) {

                    mProgressView.setVisibility(View.GONE);
                    rlMenu.setVisibility(View.VISIBLE);
                    categoriesAdapter = new MenuAdapter(MenuActivity.this, response.body().getData(), response.body().getUrl());

                    rvCategories.setAdapter(categoriesAdapter);
                } else {
                    if (response.body() != null) {
                        Toast.makeText(MenuActivity.this,
                                "Some error occurred fetching order details",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<AllProducts> call, Throwable t) {
                mProgressView.setVisibility(View.GONE);
                Toast.makeText(MenuActivity.this,
                        getString(R.string.error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
