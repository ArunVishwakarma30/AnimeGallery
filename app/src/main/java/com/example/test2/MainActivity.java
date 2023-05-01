package com.example.test2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.test2.Model.AnimeData;
import com.example.test2.Adapter.CustomAdapter;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SelectListener, PopupMenu.OnMenuItemClickListener {
    int backButtonCount = 0;
    RecyclerView topPicksRecycler, completedAnimeRecycler, upcomingAnimeRecycler, seasonalAnimeRecycler;
    CustomAdapter adapter;
    ImageView allAnime, genre, watchList, favouriteAct, accountBtn;
    private int page = 1;
    ProgressDialog dialog;
    Dialog customDialog;
    TextView alertTitle, alertMessage;
    Date currentTime;

    // Firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestoreDb;

    private FirebaseUser user;
    private String mailName;

    // Internet Connection
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    // For watch list and fav;
    AnimeData anime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accountBtn = findViewById(R.id.accountBtn);

        currentTime = Calendar.getInstance().getTime();

        // custom dialog
        customDialog = new Dialog(MainActivity.this);
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
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
        dialog.setTitle("Getting Anime For You");
        dialog.show();

        dialog.setCancelable(false);

        ImageSlider imageSlider = findViewById(R.id.imageSlider);
        List<SlideModel> remoteImages = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Slider").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren())
                    remoteImages.add(new SlideModel(data.child("ImageUrl").getValue().toString(), data.child("Title").getValue().toString(), ScaleTypes.FIT));

                imageSlider.setImageList(remoteImages, ScaleTypes.FIT);

                imageSlider.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onItemSelected(int i) {
                        Toast.makeText(MainActivity.this, "Not Released Yet!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Anime Categories Button Implementation
        allAnime = findViewById(R.id.AllAnime);
        genre = findViewById(R.id.genre);
        watchList = findViewById(R.id.watchList);
        favouriteAct = findViewById(R.id.favourites);

        // On Click start particular activity
        // 1. Starting All Anime Activity
        allAnime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AllAnime.class));
            }
        });

        // 2. Starting Genre Activity
        genre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GenreActivity.class));
            }
        });

        // 3. Starting watchList Activity
        watchList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, FavouritesActivity.class);
                i.putExtra("action", "watchList");
                startActivity(i);
            }
        });

        // 4. Starting favourite Activity
        favouriteAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, FavouritesActivity.class));
                Intent i = new Intent(MainActivity.this, FavouritesActivity.class);
                i.putExtra("action", "favList");
                startActivity(i);
            }
        });

        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accountBtn.getTag().equals("logIn")) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    AlertDialog.Builder logOutAlert = new AlertDialog.Builder(MainActivity.this);
                    logOutAlert.setMessage("Are you sure you want to log out ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            accountBtn.setImageResource(R.drawable.ic_baseline_account_circle_24);
                            accountBtn.setTag("logIn");
                            Toast.makeText(MainActivity.this, "You are successfully logged out.", Toast.LENGTH_SHORT).show();

                            user = null;
                        }
                    }).setNegativeButton("No", null).show();

                }
            }
        });

        // Starting Search Activity
        ImageView seatchBtn = findViewById(R.id.seatchBtn);
        seatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, searchActivity.class));
            }
        });

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

        // Setting all the recycler views for different categories
        topPicksRecycler = findViewById(R.id.recycler_main);
        completedAnimeRecycler = findViewById(R.id.completedAnimeRecycler);
        upcomingAnimeRecycler = findViewById(R.id.upcomingAnimeRecycler);


        // Managing all the requests
        RequestManager manager = new RequestManager(this);
        manager.getAnimeHeadlines(listener, page);
        manager.getAnimeByStatus(completeAnimeListener, page, "completed", 0, "");
        manager.getSeasonalAnime(seasonalAnimeListener, page);

    }

    // implementing all the listener
    private final OnFetchDataListener<JikanApiResponse> listener = new OnFetchDataListener<JikanApiResponse>() {
        @Override
        public void onFetchData(List<AnimeData> list, String message) {
            showAnime(list, topPicksRecycler);
        }

        @Override
        public void onError(String message) {

        }
    };

    private final OnFetchDataListener<JikanApiResponse> completeAnimeListener = new OnFetchDataListener<JikanApiResponse>() {
        @Override
        public void onFetchData(List<AnimeData> list, String message) {
            showAnime(list, completedAnimeRecycler);
        }

        @Override
        public void onError(String message) {

        }
    };

    private final OnFetchDataListener<JikanApiResponse> seasonalAnimeListener = new OnFetchDataListener<JikanApiResponse>() {
        @Override
        public void onFetchData(List<AnimeData> list, String message) {
            showAnime(list, upcomingAnimeRecycler);
            dialog.dismiss();
        }

        @Override
        public void onError(String message) {

        }
    };

    private final OnFetchDataListener<JikanApiResponse> airingAnimeListener = new OnFetchDataListener<JikanApiResponse>() {
        @Override
        public void onFetchData(List<AnimeData> list, String message) {
            showAnime(list, seasonalAnimeRecycler);
        }

        @Override
        public void onError(String message) {

        }
    };


    private void showAnime(List<AnimeData> list, RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new CustomAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAnimeClicked(AnimeData animeData) {
        startActivity(new Intent(MainActivity.this, DetailsActivity.class).putExtra("data", animeData));
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

            firebaseFirestoreDb.collection(mailName + "favList").whereEqualTo("mal_id", anime.getMal_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        favMenuItem.setTitle("Remove from favourites");
                    } else {
                        favMenuItem.setTitle("Add to Favourites");
                    }
                }
            });

            firebaseFirestoreDb.collection(mailName + "watchList").whereEqualTo("mal_id", anime.getMal_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        if (user != null && user.isEmailVerified()) {
            accountBtn.setImageResource(R.drawable.log_out_icon);
            accountBtn.setTag("loggedIn");
        }
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

    @Override
    public void onBackPressed() {

        if (backButtonCount >= 1) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            int backButtonCount = 0;
            finish();
        } else {
            Toast.makeText(this, "Press back again to exit App", Toast.LENGTH_SHORT).show();
            backButtonCount++;
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
                    firebaseFirestoreDb.collection(mailName + action).add(animeCol).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(MainActivity.this, "Added", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    firebaseFirestoreDb.collection(mailName + action).whereEqualTo("mal_id", anime.getMal_id()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String docId = documentSnapshot.getId();
                                firebaseFirestoreDb.collection(mailName + action).document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MainActivity.this, "Successfully Removed", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "Failed To delete", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(MainActivity.this, "Data is not in the database", Toast.LENGTH_SHORT).show();
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
