package com.mondaybs.monday.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mondaybs.monday.Interface.ItemClickListener;
import com.mondaybs.monday.R;

public class businessNameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView txtBusinessName;
    public ItemClickListener listener;
    public CardView cardView;

    public businessNameViewHolder(@NonNull View itemView)
    {
        super(itemView);


        txtBusinessName = (TextView) itemView.findViewById(R.id.business_name);
        cardView = itemView.findViewById(R.id.card_business_name);
    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
