package com.example.yiquizapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BankRecyclerViewAdapter extends RecyclerView.Adapter<BankRecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<BankModel> bankModels;

    public BankRecyclerViewAdapter(Context context, ArrayList<BankModel> bankModels) {
        this.context = context;
        this.bankModels = bankModels;
    }

    @NonNull
    @Override
    public BankRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        This is where you inflate the layout (Giving a look to our rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new BankRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BankRecyclerViewAdapter.MyViewHolder holder, int position) {
//        Assigning values to the views we created in the recycler_view_row layout file
//        Based on the position of the recycler view
        holder.tv_BankName.setText(bankModels.get(position).getBankName());
        holder.tv_BankDescription.setText(bankModels.get(position).getBankDescription());
        holder.tv_BankDate.setText(bankModels.get(position).getBankDate());
//        holder.tv_BankImage.setImageResource(bankModels.get(position).getImage());
    }

    @Override
    public int getItemCount() {
//        The recycler view just wants to know the number of items you want displayed
        return bankModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
//        Grabbing the views from our recycler_view_row layout file
//        Kinda like in the onCreate method

        ImageView iv_BankImage;
        TextView tv_BankName, tv_BankDescription, tv_BankDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

//            iv_BankImage = itemView.findViewById(R.id.bank_image);
            tv_BankName = itemView.findViewById(R.id.bank_name);
            tv_BankDescription = itemView.findViewById(R.id.bank_description);
            tv_BankDate = itemView.findViewById(R.id.bank_date);
        }
    }
}

