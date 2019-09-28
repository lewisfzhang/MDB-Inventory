package com.example.mdbinventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    Adapter(Context context, ArrayList<Transaction> data) {
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
//        String[] parts = data.get(i).name.split(" ");
        // customViewHolder.name.setText(limitTextWidth(parts));
//        customViewHolder.name.setText(data.get(i).name);

//        String query = parts[0];
//        if (parts.length > 1 && parts[1].equals("(")) query = String.format("%s-%s", query, parts[2]);
//        String url = String.format("http://img.pokemondb.net/artwork/%s.jpg",query).toLowerCase();
//        ImageView itemView = customViewHolder.image;
//        Glide.with(itemView)  //2
//                .load(url) //3
//                .centerCrop() //4
//                .placeholder(R.drawable.pokeball) //5
//                .error(R.drawable.pokeball) //6
//                .fallback(R.drawable.pokeball) //7
//                .into(itemView); //8
    }

    private String limitTextWidth(String[] parts) {
        StringBuilder s = new StringBuilder();
        int count = 0;
        for (int i=0; i<parts.length; i++) {
            s.append(parts[i]).append(" ");
            count += parts[i].length();
            if (!(i + 1 < parts.length && parts[i].equals(")")) && count >= 10) { // limit text width to 10
                s.append(System.lineSeparator());
                count = 0;
            }
        }
        return s.toString();
    }

    // Must be overriden to explicitly tell your Recycler how much data to allocate space for (number of rows)
    @Override
    public int getItemCount() {
        return data.size();
    }

    // This describes the item view and meta data about its place within the recycler view, think of this as looking at one row and linking the relevant stuff from xml
    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;

        CustomViewHolder(@NonNull View itemView) {
            super(itemView);
//            name = itemView.findViewById(R.id.poke_name);
//            image = itemView.findViewById(R.id.poke_image);
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(context, PokeInfo.class);
//
//                    MainActivity mainActivity = ((MainActivity) context);
//                    int idx = mainActivity.findIndexName(name.getText().toString());
//                    if (idx == -1) Log.d("e", "index should not be -1");
//
//                    Pokemon p = mainActivity.list.get(idx);
//                    intent.putExtra("name", p.name);
//                    intent.putExtra("number", p.number);
//                    intent.putExtra("attack", p.attack);
//                    intent.putExtra("defense", p.defense);
//                    intent.putExtra("flavorText", p.flavorText);
//                    intent.putExtra("hp", p.hp);
//                    intent.putExtra("sp_atk", p.sp_atk);
//                    intent.putExtra("sp_def", p.sp_def);
//                    intent.putExtra("species", p.species);
//                    intent.putExtra("speed", p.speed);
//                    intent.putExtra("total", p.total);
//                    intent.putExtra("type", p.type.toString());

//                    context.startActivity(intent);

                }
            });
        }

    }
}

