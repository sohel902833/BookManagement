package com.sohel.bookmanagement.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jsibbold.zoomage.ZoomageView;
import com.sohel.bookmanagement.Model.ImageModel2;
import com.sohel.bookmanagement.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReadBookAdapter extends RecyclerView.Adapter<ReadBookAdapter.MyViewHolder>{

    private Activity context;
    private List<ImageModel2> imageModelList;
    private  OnItemClickListner listner;
    private  String checker="null";
    private String type;
    public ReadBookAdapter(Activity context, List<ImageModel2> imageModelList,String type) {
        this.context = context;
        this.imageModelList = imageModelList;
        this.type=type;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.read_book_item_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ImageModel2 currentItem=imageModelList.get(position);

        holder.imageView.setVisibility(View.GONE);
        holder.imageView2.setVisibility(View.GONE);
        if(type.equals("grid")){
            holder.imageView2.setVisibility(View.VISIBLE);
            Picasso.get().load(currentItem.getImageUrl()).placeholder(R.drawable.select_image).into(holder.imageView2);
        }else{
            holder.imageView.setVisibility(View.VISIBLE);
            Picasso.get().load(currentItem.getImageUrl()).placeholder(R.drawable.select_image).into(holder.imageView);

        }

        if(type.equals("grid")) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageModel2 imageModel2 = imageModelList.get(position);
                    showViewDialoug(imageModel2);
                }
            });

            holder.imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageModel2 imageModel2 = imageModelList.get(position);
                    showViewDialoug(imageModel2);
                }
            });
        }

    }
    private void showViewDialoug(ImageModel2 imageModel) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view=context.getLayoutInflater().inflate(R.layout.view_dialog_layout,null);
        builder.setView(view);

        ImageButton closeButton=view.findViewById(R.id.closeIconId);
        ZoomageView imageView=view.findViewById(R.id.viewImageViewid);

        Picasso.get().load(imageModel.getImageUrl()).placeholder(R.drawable.select_image).into(imageView);

        final AlertDialog dialog=builder.create();
        dialog.show();


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    @Override
    public int getItemCount() {
        return imageModelList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        ZoomageView imageView;
        ImageView imageView2;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.read_ImageViewid);
            imageView2=itemView.findViewById(R.id.read_ImageViewid2);
            itemView.setOnClickListener(this);


        }


        @Override
        public void onClick(View v) {
            if(listner!=null){
                int position=getAdapterPosition();
                if(position!= RecyclerView.NO_POSITION){
                    listner.onItemClick(position);
                }
            }
        }


    }
    public interface  OnItemClickListner{
        void onItemClick(int position);
    }

    public void setOnItemClickListner(OnItemClickListner listner){
        this.listner=listner;
    }


}
