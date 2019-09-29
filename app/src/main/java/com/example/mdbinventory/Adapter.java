package com.example.mdbinventory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/*
 * Every recycler view needs an adapter (you can reuse adapters!)
 * The purpose of an adapter is literally bind our app-data to the views that are displayed within a RecyclerView object, this class will tell our recycler how to populate the "sub-views", view in each row, with our data
 */
public class Adapter extends RecyclerView.Adapter<Adapter.CustomViewHolder> {

    // It is helpful to have variable for context because `this` really only works when calling stuff from within Activities
    private Context context;
    List<Transaction> data;
    boolean listView = true; // default

    // Adapter construtor, whenever we make a new adapter from this class we need to pass in a context and the data that we want to bind
    Adapter(Context context, List<Transaction> data) {
        this.context = context;
        this.data = data;
    }


    /* Pro-tip look-up @NonNull its good practice to use it!
     * Inflate the row layout from `row_view.xml` when it is needed within the view lifecycle, so we map its location in the resource directory `R.layout.row_view`
     */
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_view, viewGroup, false);
        return new CustomViewHolder(view);
    }

    // Updates the `RecyclerView.ViewHolder` contents with the item at the given position (from your data)
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int i) {
        customViewHolder.supplier.setText(data.get(i).suppliers);
        customViewHolder.date.setText(data.get(i).date);

        String url = "https://assets.pokemon.com/assets/cms2/img/pokedex/full/025.png";
        ImageView itemView = customViewHolder.image;
        Glide.with(itemView)  //2
                .load(url) //3
                .centerCrop() //4
                .placeholder(R.mipmap.ic_launcher) //5
                .error(R.mipmap.ic_launcher) //6
                .fallback(R.mipmap.ic_launcher) //7
                .into(itemView); //8
    }

    // Must be overriden to explicitly tell your Recycler how much data to allocate space for (number of rows)
    @Override
    public int getItemCount() {
        return data.size();
    }

    // This describes the item view and meta data about its place within the recycler view, think of this as looking at one row and linking the relevant stuff from xml
    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView supplier;
        TextView date;
        ImageView image;

        CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            supplier = itemView.findViewById(R.id.supplier_text);
            date = itemView.findViewById(R.id.date_text);
            image = itemView.findViewById(R.id.transaction_image);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    DataActivity dataActivity = ((DataActivity) context);
                    Transaction t = dataActivity.findTransaction(supplier.getText().toString(), date.getText().toString());

                    Intent intent = new Intent(context, Transaction_Info.class);
                    intent.putExtra("cost", t.cost);
                    intent.putExtra("description", t.description);
                    intent.putExtra("supplier", t.suppliers);
                    intent.putExtra("date", t.date);

                    // CHANGE WHEN EVERYTHING WORKS!
                    String url = "https://assets.pokemon.com/assets/cms2/img/pokedex/full/025.png";
                    intent.putExtra("url", url);

                    context.startActivity(intent);

                }
            });
        }

    }

    public synchronized void updateData(List<Transaction> newData) {
        data = newData;
        notifyDataSetChanged();
    }
}

