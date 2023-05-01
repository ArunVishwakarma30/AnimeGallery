package com.example.test2.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test2.R;

public class CustomViewholder extends RecyclerView.ViewHolder {

    TextView text_title, text_eps;
    CardView cardView;
    ImageView img_headlines, threeDots;



    public CustomViewholder(@NonNull View itemView) {
        super(itemView);

        text_title = itemView.findViewById(R.id.text_title);
        img_headlines = itemView.findViewById(R.id.img_headline);
        text_eps = itemView.findViewById(R.id.text_eps);
        cardView = itemView.findViewById(R.id.main_container);
        threeDots = itemView.findViewById(R.id.threeDots);
    }
}
