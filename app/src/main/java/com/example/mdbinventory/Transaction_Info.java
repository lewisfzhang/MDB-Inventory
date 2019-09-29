package com.example.mdbinventory;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class Transaction_Info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_info);

        float cost = getIntent().getFloatExtra("cost", 0);
        String description = getIntent().getStringExtra("description");
        String suppliers = getIntent().getStringExtra("supplier");
        String date = getIntent().getStringExtra("date");

        ((TextView) findViewById(R.id.cost_info)).setText("Cost: "+cost);
        ((TextView) findViewById(R.id.description_info)).setText(description);
        ((TextView) findViewById(R.id.supplier_info)).setText(suppliers);
        ((TextView) findViewById(R.id.date_info)).setText("Date: "+date);

        String url = getIntent().getStringExtra("url");
        ImageView itemView = findViewById(R.id.picture_info);
        Glide.with(itemView)  //2
                .load(url) //3
                .centerCrop() //4
                .placeholder(R.mipmap.ic_launcher) //5
                .error(R.mipmap.ic_launcher) //6
                .fallback(R.mipmap.ic_launcher) //7
                .into(itemView); //8

        Button back = findViewById(R.id.info_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
