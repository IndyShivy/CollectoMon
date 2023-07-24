package com.example.collectomon;

import android.content.Context;
import android.util.Log;
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

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<CardItem> cardItems;
    private Context context;
    private CardDatabase databaseHelper;
    private boolean[] selectedPositions;
    ArrayList<CardItem> cardStuff;
    private CardDatabase cardDatabase;
    public CardAdapter(List<CardItem> cardItems, Context context) {
        this.cardItems = cardItems;
        this.context = context;
    }

    public CardAdapter(List<CardItem> cardItems, Context context, boolean[] selectedPositions) {
        this.cardItems = cardItems;
        this.context = context;
        this.selectedPositions = selectedPositions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrow, parent, false);
        databaseHelper = new CardDatabase(context);
        cardStuff = new ArrayList<>();
        cardDatabase = new CardDatabase(context);
        return new ViewHolder(view);
    }
    public void filterList(List<CardItem> filteredList) {
        cardItems = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= cardItems.size()) {
            return; // Avoid accessing out-of-bounds positions
        }

        CardItem cardItem = cardItems.get(position);
        boolean isChecked = databaseHelper.isCardExists(cardItem.getCardId());
        cardItem.setChecked(isChecked); // Set the checked state of the CardItem

        // Set the checkbox checked state
        holder.checkbox.setChecked(cardItem.isChecked());

        Picasso.get()
                .load(cardItem.getImageSrc())
                .resize(600,600 )
                .centerInside()
                .into(holder.imageView);

        holder.cardNameTextView.setText(cardItem.getCardName());
        holder.setDetailsTextView.setText(cardItem.getSetDetails());
        holder.cardDetailsTextView.setText(cardItem.getCardDetails());

        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(cardItem.isChecked());

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cardItem.setChecked(isChecked);

                if (!cardDatabase.isCardExists(cardItem.getCardId())) {
                    //databaseHelper.addCard(cardItem);
                    //Log.d("CardAdded", cardItem.getCardId() + cardItem.getCardName());
                } else if (!isChecked && cardDatabase.isCardExists(cardItem.getCardId())) {
                    //databaseHelper.deleteCard(cardItem);
                    //Log.d("CardRemoved", cardItem.getCardId() + cardItem.getCardName());
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

                }
            });
        }
    }

    public List<CardItem> getSelectedCardItems() {
        List<CardItem> selectedItems = new ArrayList<>();
        for (CardItem cardItem : cardItems) {
            if (cardItem.isChecked()) {
                selectedItems.add(cardItem);
                cardItem.setChecked(false);
            }
        }
        return selectedItems;
    }

}
