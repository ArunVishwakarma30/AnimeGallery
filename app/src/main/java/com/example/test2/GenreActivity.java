package com.example.test2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.test2.Adapter.AnimeGenreAdapter;
import com.example.test2.Model.GenreModel;
import com.example.test2.Model.JikanGenreResponse;
import com.example.test2.Services.NetworkChangeListener;
import com.example.test2.Services.OnFetchGenreListener;
import com.example.test2.Services.RequestManager;
import com.example.test2.Services.SelectGenre;

import java.util.List;

public class GenreActivity extends AppCompatActivity implements SelectGenre {

    RecyclerView genreRecycler;
    AnimeGenreAdapter adapter;
    ProgressDialog dialog;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Fetching All Genre...");
        dialog.show();
        dialog.setCancelable(false);
        // 3. Starting Search Activity
        ImageView searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GenreActivity.this, searchActivity.class));
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        genreRecycler = findViewById(R.id.genreRecyclerView);
        RequestManager manager = new RequestManager(this);
        manager.getAnimeCatOnFilter(getGenreListener, "genres");

    }

    private final OnFetchGenreListener<JikanGenreResponse> getGenreListener = new OnFetchGenreListener<JikanGenreResponse>() {
        @Override
        public void onFetchGenre(List<GenreModel> genreModelList, String message) {
            showAllGenres(genreModelList, genreRecycler);
        }

        @Override
        public void OnGenreError(String msg) {

        }
    };

    private void showAllGenres(List<GenreModel> genreModelList, RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new AnimeGenreAdapter(this, genreModelList, this);
        recyclerView.setAdapter(adapter);
        dialog.dismiss();
    }

    @Override
    public void onGenreClicked(GenreModel genreType) {
        startActivity(new Intent(GenreActivity.this, GenreClickActivity.class)
                .putExtra("gen", genreType)
        );
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}