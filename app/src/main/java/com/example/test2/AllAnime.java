package com.example.test2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test2.Adapter.AllAnimeAdapter;
import com.example.test2.Model.AnimeData;
import com.example.test2.Model.JikanApiResponse;
import com.example.test2.Services.NetworkChangeListener;
import com.example.test2.Services.OnFetchDataListener;
import com.example.test2.Services.RequestManager;
import com.example.test2.Services.SelectListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllAnime extends AppCompatActivity implements SelectListener, PopupMenu.OnMenuItemClickListener {

    RecyclerView AllAnimeRecycler;
    AllAnimeAdapter adapter;
    private int page = 1;
    ProgressDialog dialog;
    TextView alertTitle, alertMessage;


    // For watch list and fav;
    AnimeData anime;

    Date currentTime;
    Dialog customDialog;


    // Firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestoreDb;
    private FirebaseUser user;
    private String mailName;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_anime);

        // Starting Search Activity
        ImageView searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllAnime.this, searchActivity.class));
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        currentTime = Calendar.getInstance().getTime();

        // custom dialog
        customDialog = new Dialog(AllAnime.this);
        customDialog.setContentView(R.layout.custom_dialog_layout);
        customDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        customDialog.getWindow().setLayout(700, 550);
        customDialog.setCancelable(false);
        Button okay = customDialog.findViewById(R.id.btn_okay);
        Button cancel = customDialog.findViewById(R.id.btn_cancel);
        alertTitle = customDialog.findViewById(R.id.alertTitle);
        alertMessage = customDialog.findViewById(R.id.alertMessage);
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                startActivity(new Intent(AllAnime.this, LoginActivity.class));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        
        
        // Loading Dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("Getting All Anime For You");
        dialog.show();
        dialog.setCancelable(false);

        // Recycler View
        AllAnimeRecycler = findViewById(R.id.allAnimeRecyclerView);

        RequestManager manager = new RequestManager(this);
        manager.getAnimeByStatus(AnimeListener, page, "", 0, "");

        // Firebase connection
        user = auth.getCurrentUser(); // full user mail
        firebaseFirestoreDb = FirebaseFirestore.getInstance();

        String listName;
        int pos;
        if (user != null) {
            listName = user.getEmail();
            pos = listName.indexOf("@"); //this finds the first occurrence of "."
            if (pos != -1) {
                mailName = listName.substring(0, pos); //this will give abc
            }
        }
    }

    private final OnFetchDataListener<JikanApiResponse> AnimeListener = new OnFetchDataListener<JikanApiResponse>() {
        @Override
        public void onFetchData(List<AnimeData> list, String message) {
            showAnime(list, AllAnimeRecycler);
            dialog.dismiss();
        }

        @Override
        public void onError(String message) {

        }
    };

    private void showAnime(List<AnimeData> list, RecyclerView recyclerView)
    {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new AllAnimeAdapter(getApplicationContext(), list, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAnimeClicked(AnimeData animeData) {
        startActivity(new Intent(AllAnime.this, DetailsActivity.class)
                .putExtra("data", animeData));
    }

    @Override
    public void onDotsClicked(AnimeData animeData, CardView cardView) {
        anime = new AnimeData();
        anime = animeData;
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.fav_and_watchlater);

        Menu menu = popupMenu.getMenu();
        MenuItem favMenuItem = menu.getItem(0);
        MenuItem watchMenuItem = menu.getItem(1);

        if (auth.getCurrentUser() != null) {

            firebaseFirestoreDb.collection(mailName + "favList")
                    .whereEqualTo("mal_id", anime.getMal_id())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                favMenuItem.setTitle("Remove from favourites");
                            } else {
                                favMenuItem.setTitle("Add to Favourites");
                            }
                        }
                    });

            firebaseFirestoreDb.collection(mailName + "watchList")
                    .whereEqualTo("mal_id", anime.getMal_id())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                watchMenuItem.setTitle("Remove from watch list");
                            } else {
                                watchMenuItem.setTitle("Add to Watchlist");
                            }
                        }
                    });
        }
        popupMenu.show();
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


    @Override
    public boolean onMenuItemClick(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.favouriteItem:
                String favTitle = (String) item.getTitle();
                addOrRemove(favTitle, "favList");
                return true;

            case R.id.watchListItem:
                String title = (String) item.getTitle();
                addOrRemove(title, "watchList");
                return true;

            default:
                return false;
        }


    }

    public void addOrRemove(String title, String action) {
        if (auth.getCurrentUser() != null) {
            Map<String, Object> animeCol = new HashMap<>();
            int eps = anime.getEpisodes();
            int mal_id = anime.getMal_id();
            String eng_name = anime.getTitle_english();
            String detail = anime.getSynopsis();


            if (anime.getImages().getJpg().getImage_url() != null) {
                String url = anime.getImages().getJpg().getImage_url();
                animeCol.put("img_url", url);
            }
            animeCol.put("eps", eps);
            animeCol.put("name", eng_name);
            animeCol.put("mal_id", mal_id);
            animeCol.put("detail", detail);
            animeCol.put("dateTime", currentTime);
            if (action.equals("favList")) {
                boolean userFav = true;
                animeCol.put("userFav", userFav);
            } else {
                boolean userWatchList = true;
                animeCol.put("userWatchList", userWatchList);
            }

            int pos;
            String watchOrFav;

            pos = title.indexOf(" "); //this finds the first occurrence of " "
            watchOrFav = title.substring(0, pos); //this will give abc


            if (user != null && user.isEmailVerified()) {
                if (watchOrFav.equals("Add")) {
                    firebaseFirestoreDb.collection(mailName + action)
                            .add(animeCol)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AllAnime.this, "Added", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AllAnime.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {

                    firebaseFirestoreDb.collection(mailName + action)
                            .whereEqualTo("mal_id", anime.getMal_id())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                        String docId = documentSnapshot.getId();
                                        firebaseFirestoreDb.collection(mailName + action)
                                                .document(docId)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(AllAnime.this, "Successfully Removed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(AllAnime.this, "Failed To delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(AllAnime.this, "Data is not in the database", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }


            } else {
                customDialog.show();
            }


        } else {
            alertTitle.setText("Login!");
            alertMessage.setText("You have to login to use this feature");
            customDialog.show();
        }
    }
}