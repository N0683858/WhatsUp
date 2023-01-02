package com.hamidraza.whatsup;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UsersViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);

        mView = itemView;
    }
}
