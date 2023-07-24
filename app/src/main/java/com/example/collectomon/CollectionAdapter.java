package com.example.collectomon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private List<CardItem> cardItems;
    private Context context;
    private CardDatabase databaseHelper;
    private ArrayList<CardItem> cardStuff;

    public CollectionAdapter(List<CardItem> cardItems, Context context) {
        this.cardItems = cardItems;
        this.context = context;
        this.databaseHelper = new CardDatabase(context);
        this.cardStuff = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collectionrow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardItem cardItem = cardItems.get(position);

        // Load image using Picasso
        Picasso.get()
                .load(cardItem.getImageSrc())
                .resize(600, 600)
                .centerInside()
                .into(holder.imageView);

        holder.cardNameTextView.setText(cardItem.getCardName());
        holder.setDetailsTextView.setText(cardItem.getSetDetails());
        holder.cardDetailsTextView.setText(cardItem.getCardDetails());

        holder.checkbox.setOnCheckedChangeListener(null); // Clear any previous listener
        holder.checkbox.setChecked(cardItem.isChecked()); // Set the initial checked state

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the isChecked value of the corresponding CardItem
                cardItem.setChecked(isChecked);

                // Add or remove the card details from the ArrayList based on the checkbox selection
                if (isChecked) {
                    cardStuff.add(cardItem);
                } else {
                    cardStuff.remove(cardItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView cardNameTextView;
        TextView setDetailsTextView;
        TextView cardDetailsTextView;
        CheckBox checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            cardNameTextView = itemView.findViewById(R.id.cardNameTextView);
            setDetailsTextView = itemView.findViewById(R.id.setDetailsTextView);
            cardDetailsTextView = itemView.findViewById(R.id.cardDetailsTextView);
            checkbox = itemView.findViewById(R.id.checkbox);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkbox.setChecked(!checkbox.isChecked());
                    // Handle checkbox state change as per your requirements
                }
            });
        }
    }

    public List<CardItem> getSelectedCardItems() {
        List<CardItem> selectedItems = new ArrayList<>(cardStuff);
        cardStuff.clear(); // Clear the cardStuff ArrayList after retrieving the selected items
        return selectedItems;
    }

    public void filterList(List<CardItem> filteredList) {
        cardItems = filteredList;
        notifyDataSetChanged();
    }
}
