package com.example.test2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test2.Model.FirebaseDataModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class FavouritesActivity extends AppCompatActivity {

    private RecyclerView favRecyclerView;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private String mailName = "", listName;
    private int pos;
    Dialog customDialog;
    TextView alertTitle, alertMessage;
    private String action;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        action = getIntent().getStringExtra("action");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        // Dialog
        customDialog = new Dialog(FavouritesActivity.this);
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
                startActivity(new Intent(FavouritesActivity.this, LoginActivity.class));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                finish();
            }
        });
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView favSearchBtn = findViewById(R.id.searchBtn);
        favSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FavouritesActivity.this, searchActivity.class));
            }
        });

        if (user != null && user.isEmailVerified()) {
            listName = user.getEmail();
            pos = listName.indexOf("@"); //this finds the first occurrence of "."
            if (pos != -1) {
                mailName = listName.substring(0, pos); //this will give abc
            }
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        favRecyclerView = findViewById(R.id.favRecyclerView);

        // Query
        Query query = firebaseFirestore.collection(mailName + action).orderBy("dateTime", Query.Direction.DESCENDING);

        // Recycler option
        FirestoreRecyclerOptions<FirebaseDataModel> options = new FirestoreRecyclerOptions.Builder<FirebaseDataModel>()
                .setQuery(query, FirebaseDataModel.class)
                .build();

        // Adapter
        adapter = new FirestoreRecyclerAdapter<FirebaseDataModel, FavViewHolder>(options) {
            @NonNull
            @Override
            public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_list_items, parent, false);

                return new FavViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FavViewHolder holder, int position, @NonNull FirebaseDataModel model) {
                String posAnimeName = model.getName() + "";
                holder.textTitle.setText(posAnimeName);
                holder.textDetail.setText(model.getDetail() + "");

                if ((model.getEps() + "").equals("")) {
                    holder.textEps.setText("Episodes : ");
                } else {
                    holder.textEps.setText("Episodes : " + model.getEps() + "");

                }

                if (model.getImg_url() != null) {
                    Picasso.get().load(model.getImg_url()).into(holder.imageUrl);
                }


//                holder.container.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent i=new Intent(FavouritesActivity.this, ListDetailActivity.class);
//                        i.putExtra("animeId", model.getMal_id()+"");
//                        startActivity(i);
//                    }
//                });

                holder.removeItemBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String message = "Are you sure you want to remove " + posAnimeName + " from your list?";
                        AlertDialog.Builder removeItemAlert = new AlertDialog.Builder(FavouritesActivity.this);
                        SpannableStringBuilder boldMessage = new SpannableStringBuilder(message);
                        int startIndex = message.indexOf(posAnimeName);
                        int endIndex = startIndex + posAnimeName.length();
                        boldMessage.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        removeItemAlert.setMessage(boldMessage).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int animeId = Integer.parseInt(model.getMal_id() + "");
                                firebaseFirestore.collection(mailName + action).whereEqualTo("mal_id", animeId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                            String docId = documentSnapshot.getId();
                                            firebaseFirestore.collection(mailName + action).document(docId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(FavouritesActivity.this, "Successfully Removed", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(FavouritesActivity.this, "Failed To delete", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(FavouritesActivity.this, "Data is not in the database", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }).setNegativeButton("No", null).show();
                    }
                });

            }
        };

        // View holder
        if (user != null && user.isEmailVerified()) {
            favRecyclerView.setHasFixedSize(true);
            favRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            favRecyclerView.setAdapter(adapter);
        } else {
            customDialog.show();
        }

    }

    public class FavViewHolder extends RecyclerView.ViewHolder {

        private TextView textTitle, textDetail, textEps;
        private ImageView imageUrl, removeItemBtn;
        private CardView container;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.favActTitle);
            textDetail = itemView.findViewById(R.id.favActDetails);
            textEps = itemView.findViewById(R.id.favActEps);
            imageUrl = itemView.findViewById(R.id.favActImg);
            container = itemView.findViewById(R.id.favActContainer);
            removeItemBtn = itemView.findViewById(R.id.removeItem);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();
    }
}