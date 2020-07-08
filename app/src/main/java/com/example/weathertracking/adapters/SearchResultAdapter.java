package com.example.weathertracking.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weathertracking.R;
import com.example.weathertracking.weatherApi.SearchCityObject;

import java.util.ArrayList;
import java.util.List;


public  class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    //private List<String> mFavorites;
    private  ArrayList<SearchCityObject> mResultsList;

    private final LayoutInflater mInflater;
    private Context mContext;

    public SearchResultAdapter(Context context) {
        mContext=context;
        this.mInflater = LayoutInflater.from(context);

        this.mResultsList=new ArrayList<>();
    }

    public void setResults(List<SearchCityObject> resultsList){
        this.mResultsList=new ArrayList<>(resultsList);
    }
    public void deleteElements(){
            this.mResultsList.clear();
            notifyDataSetChanged();
    }

    @NonNull
    @Override
    // inflates the row layout from xml when needed
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.result_item,parent,false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        String location =mResultsList.get(position).getName();
        String weather = mResultsList.get(position).getSys().getCountry();
        holder.LocationItemView.setText(location);
        holder.DetailsItemView.setText(weather);
    }


    @Override
    //rownum
    public int getItemCount() {
        int size;
        if(mResultsList==null)
        {
            size=0;
        }
        else
        {
            size= mResultsList.size();


        }
        return size;
    }


    // stores and recycles views as they are scrolled off screen
    class SearchResultViewHolder extends RecyclerView.ViewHolder  {

        //TODO neveket rendesen

        private final TextView LocationItemView;
        private final TextView DetailsItemView;

        private SearchResultViewHolder(final View itemView) {
            super(itemView);
            LocationItemView = itemView.findViewById(R.id.nameTextView);
            DetailsItemView =itemView.findViewById(R.id.country);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //String location =LocationItemView.getText().toString();
                    //Toast.makeText(itemView.getContext(),location+" selected",Toast.LENGTH_SHORT).show();
                    //LocationItemView.setBackgroundColor(Color.LTGRAY);

                    Intent item_clicked = new Intent("LIST_ITEM_CLICKED");
                    item_clicked.putExtra("ITEM",getAdapterPosition());
                    mContext.sendBroadcast(item_clicked);
                }
            });
        }
    }
}
