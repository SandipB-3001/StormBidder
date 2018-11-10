package com.example.sandip.stormbidder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ItemViewHolder>{

    private Context mContext;
    private List<Upload> mUploads;
    //private SearchAdapter.OnItemClickListener mListener;

    public ProfileAdapter( Context mContext, List<Upload> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.activity_profile_list,parent,false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder( @NonNull ItemViewHolder holder, int position ) {
        Upload uploadCurrent=mUploads.get(position);
        holder.textViewName1.setText(uploadCurrent.getmName());
        Picasso.with(mContext)
                .load(uploadCurrent.getmImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()               //OR .centerInside()
                .into(holder.imageView1);
    }


    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName1;
        public ImageView imageView1;

        public ItemViewHolder( View itemView ) {
            super(itemView);

            textViewName1 = itemView.findViewById(R.id.text_view_name1);
            imageView1 = itemView.findViewById(R.id.image_view_upload1);

            //itemView.setOnClickListener(this);
            //itemView.setOnCreateContextMenuListener(this);

        }
    }

}
