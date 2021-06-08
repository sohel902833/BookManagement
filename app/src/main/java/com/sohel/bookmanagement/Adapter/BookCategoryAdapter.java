package com.sohel.bookmanagement.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sohel.bookmanagement.BookListActivity;
import com.sohel.bookmanagement.Model.BookCategory;
import com.sohel.bookmanagement.Model.TimeDateModel;
import com.sohel.bookmanagement.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookCategoryAdapter extends RecyclerView.Adapter<BookCategoryAdapter.MyViewHolder>{

    private Context context;
    private List<TimeDateModel> categoryList;
    private  OnItemClickListner listner;
    private  String checker="null";
    private  int year=0;

    public BookCategoryAdapter(Context context, List<TimeDateModel> categoryList,int year) {
        this.context = context;
        this.categoryList = categoryList;
        this.year=year;
    }
    public BookCategoryAdapter(Context context, List<TimeDateModel> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.category_item_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TimeDateModel currentItem=categoryList.get(position);
        if(year==0){
            holder.textView.setText(currentItem.getDate()+"/"+currentItem.getMonth()+"/"+currentItem.getYear());

        }else{
            holder.textView.setText(currentItem.getDate()+"/"+currentItem.getMonth()+"/"+year);
        }





    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
       TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView=itemView.findViewById(R.id.admin_main_TextViewid);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listner!=null){
                int position=getAbsoluteAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    listner.onItemClick(position,year);
                }
            }
        }

    }
    public interface  OnItemClickListner{
        void onItemClick(int position,int year);

    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
