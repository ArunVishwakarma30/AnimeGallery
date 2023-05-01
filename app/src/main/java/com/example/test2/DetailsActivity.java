package com.example.test2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test2.Model.AnimeData;
import com.example.test2.Model.Genre;
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DetailsActivity extends AppCompatActivity {
    private AnimeData data;
    private TextView detailsSynopsis, detailsGenre, DetailsMembers, DetailsRating, detailsAired, detailsDuration, scoreTitle, detailsEps, detailsBackground;
    private TextView detailsSeason, backgroundheading, detailsSourceType, detailsEnglishTitle, DetailsFavourites, DetailsPopularity, animeDetailsTitle, DetailsRank, detailsStatus, detailsTv;
    private Button showMore;
    private ImageView detailsImage, favBtn, watchLaterBtn, BackBtn;
    private TextUtils.TruncateAt noneEllipsize;
    private TextView alertTitle, alertMessage;


    // Firebase
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String mailName;
    private FirebaseFirestore firebaseFirestoreDb;
    private AnimeData anime;
    Date currentTime;
    Dialog customDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        // setting all the views
        currentTime = Calendar.getInstance().getTime();
        getAndSet();

        // Custom dialog
        customDialog = new Dialog(DetailsActivity.this);
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
                startActivity(new Intent(DetailsActivity.this, LoginActivity.class));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseFirestoreDb = FirebaseFirestore.getInstance();
        anime = data;

        String listName;
        int pos;

        if (user != null) {
            listName = user.getEmail();
            pos = listName.indexOf("@"); //this finds the first occurrence of "."
            if (pos != -1) {
                mailName = listName.substring(0, pos); //this will give abc
            }
        }

        checkDatabse();
        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Fav btn click listener
        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsActAddOrRemove("" + favBtn.getTag(), "favList");
            }
        });

        watchLaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsActAddOrRemove("" + watchLaterBtn.getTag(), "watchList");
            }
        });


        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullPlot();
            }
        });

        // Video Player
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId;
                if (data.getTrailer().getYoutube_id() == null) {
                    videoId = "";
                    youTubePlayerView.setVisibility(View.GONE);
                    Toast.makeText(DetailsActivity.this, "Trailer is not available for this anime!", Toast.LENGTH_SHORT).show();
                } else {
                    videoId = data.getTrailer().getYoutube_id();
                }

                youTubePlayer.cueVideo(videoId, 0);
            }
        });


    }

    private void showFullPlot() {
        noneEllipsize = detailsSynopsis.getEllipsize();
        if (detailsSynopsis.getMaxLines() == 5) {
            detailsSynopsis.setMaxLines(50);
            detailsSynopsis.setEllipsize(TextUtils.TruncateAt.END);
            showMore.setBackground(getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24));
        } else {
            detailsSynopsis.setMaxLines(5);
            detailsSynopsis.setEllipsize(noneEllipsize);
            showMore.setBackground(getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24));
        }
    }

    private String removeComma(String content) {
        if (content == null) {
            backgroundheading.setVisibility(View.INVISIBLE);
            return "";
        } else {
            return content.replace("\"", "");
        }
    }

    void getAndSet() {
        detailsSynopsis = findViewById(R.id.detailsSynopsis);
        showMore = findViewById(R.id.showMore);
        scoreTitle = findViewById(R.id.scoreTitle);
        detailsImage = findViewById(R.id.detailsImage);
        animeDetailsTitle = findViewById(R.id.animeDetailsTitle);
        detailsTv = findViewById(R.id.detailsTv);
        detailsStatus = findViewById(R.id.detailsStatus);
        detailsEps = findViewById(R.id.detailsEps);
        detailsDuration = findViewById(R.id.detailsDuration);
        DetailsRank = findViewById(R.id.DetailsRank);
        DetailsPopularity = findViewById(R.id.DetailsPopularity);
        DetailsMembers = findViewById(R.id.DetailsMembers);
        DetailsFavourites = findViewById(R.id.DetailsFavourites);
        detailsEnglishTitle = findViewById(R.id.detailsEnglishTitle);
        detailsSourceType = findViewById(R.id.detailsSourceType);
        detailsSeason = findViewById(R.id.detailsSeason);
        detailsAired = findViewById(R.id.detailsAired);
        DetailsRating = findViewById(R.id.DetailsRating);
        detailsGenre = findViewById(R.id.detailsGenre);
        detailsBackground = findViewById(R.id.detailsBackground);
        backgroundheading = findViewById(R.id.backgroundheading);
        favBtn = findViewById(R.id.favBtn);
        watchLaterBtn = findViewById(R.id.watchLaterBtn);
        BackBtn = findViewById(R.id.DetailsbackBtn);

        // Getting data from previous activity
        data = (AnimeData) getIntent().getSerializableExtra("data");
        anime = data;
        DetailsRating.setText(data.getRating());
        scoreTitle.setText(String.valueOf(data.getScore()));
        DetailsRank.setText(String.valueOf("#" + data.getRank()));
        DetailsPopularity.setText("#" + String.valueOf(data.getPopularity()));
        DetailsMembers.setText(String.valueOf(data.getMembers()));
        DetailsFavourites.setText(String.valueOf(data.getFavorites()));
        animeDetailsTitle.setText(data.getTitle());
        detailsEnglishTitle.setText(data.getTitle_english());
        detailsSourceType.setText(data.getSource());
        detailsTv.setText(data.getType());
        Picasso.get().load(data.getImages().getJpg().getImage_url()).into(detailsImage);
        detailsStatus.setText(data.getStatus());
        detailsEps.setText(String.valueOf(data.getEpisodes()));
        detailsDuration.setText(data.getDuration());
        detailsSeason.setText(data.getSeason() + " " + String.valueOf(data.getYear()));
        detailsAired.setText(data.getAired().getString());

        String synopsis = data.getSynopsis();
        String resultSynopsis = removeComma(synopsis);
        detailsSynopsis.setText(resultSynopsis);

        List<Genre> allGens = data.getGenres();
        final String[] gens = {""};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            allGens.forEach((temp) -> {
                Log.d("gettingGen", "" + temp.getName());
                gens[0] += temp.getName() + " | ";
                Log.d("gettingGen", " " + gens[0]);
            });
        }
        detailsGenre.setText(gens[0].substring(0, gens[0].length() - 2));
        String background = data.getBackground();
        String resultbackground = removeComma(background);
        detailsBackground.setText(resultbackground);
    }

    void checkDatabse() {
        if (auth.getCurrentUser() != null) {

            firebaseFirestoreDb.collection(mailName + "favList")
                    .whereEqualTo("mal_id", anime.getMal_id())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                favBtn.setImageResource(R.drawable.ic_baseline_favorite_red_24);
                                favBtn.setTag("Remove from fav");
                            } else {
                                favBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                favBtn.setTag("Add to fav");

                            }
                        }
                    });

            firebaseFirestoreDb.collection(mailName + "watchList")
                    .whereEqualTo("mal_id", anime.getMal_id())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                watchLaterBtn.setImageResource(R.drawable.ic_baseline_playlist_added_24);
                                watchLaterBtn.setTag("Remove from watch list");

                            } else {
                                watchLaterBtn.setImageResource(R.drawable.ic_baseline_playlist_add_24);
                                watchLaterBtn.setTag("Add to watch list");

                            }
                        }
                    });
        }
    }

    public void detailsActAddOrRemove(String title, String action) {
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
                                    Toast.makeText(DetailsActivity.this, "Successfully Added.", Toast.LENGTH_SHORT).show();

                                    if (title.equals("Add to fav")) {
                                        favBtn.setImageResource(R.drawable.ic_baseline_favorite_red_24);
                                        favBtn.setTag("Remove from fav");

                                    } else {
                                        watchLaterBtn.setImageResource(R.drawable.ic_baseline_playlist_added_24);
                                        watchLaterBtn.setTag("Remove from watch list");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DetailsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                                                        Toast.makeText(DetailsActivity.this, "Successfully Removed", Toast.LENGTH_SHORT).show();
                                                        if (title.equals("Remove from fav")) {
                                                            favBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                                            favBtn.setTag("Add to fav");
                                                        } else {
                                                            watchLaterBtn.setImageResource(R.drawable.ic_baseline_playlist_add_24);
                                                            watchLaterBtn.setTag("Add to watch list");
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(DetailsActivity.this, "Failed To delete", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(DetailsActivity.this, "Data is not in the database", Toast.LENGTH_SHORT).show();
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