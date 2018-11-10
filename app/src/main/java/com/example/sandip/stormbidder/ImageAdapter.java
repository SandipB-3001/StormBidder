package com.example.sandip.stormbidder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Upload> mUploads;

    private OnItemClickListener mListener;

    public ImageAdapter(Context context,List<Upload> uploads)
    {
        mContext=context;
        mUploads=uploads;
        /*String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Date date1=null,date2=null;
        for(Upload u:mUploads) {
            try {
                date1=new SimpleDateFormat("yyyy-MM-dd").parse(u.getmDate());
                date2=new SimpleDateFormat("yyyy-MM-dd").parse(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }



            long diff = Math.abs(date2.getTime() - date1.getTime());
            long diffDays = diff / (24 * 60 * 60 * 1000);


            if(diffDays>=3)
            {
                DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("uploads").child(u.getmKey());
                Upload up=new Upload(u.getmName(),u.getmDescription(),u.getmDescription(),u.getmPrice(),u.getmBid(),
                        u.getmUserName(),u.getmDate(),"ACTIVE",u.getmBidder());
                dbf.setValue(up);
            }
        }*/
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder( @NonNull ImageViewHolder holder, int position ) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Date date1=null,date2=null;
        for(Upload u:mUploads) {
            try {
                date1=new SimpleDateFormat("yyyy-MM-dd").parse(u.getmDate());
                date2=new SimpleDateFormat("yyyy-MM-dd").parse(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }



            long diff = Math.abs(date2.getTime() - date1.getTime());
            long diffDays = diff / (24 * 60 * 60 * 1000);


            if(diffDays>=3)
            {
                DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("uploads").child(u.getmKey());
                Upload up=new Upload(u.getmName(),u.getmImageUrl(),u.getmDescription(),u.getmPrice(),u.getmBid(),
                        u.getmUserName(),u.getmDate(),"INACTIVE",u.getmBidder());
                dbf.setValue(up);
            }
        }
        Upload uploadCurrent=mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getmName());
        Picasso.with(mContext)
                .load(uploadCurrent.getmImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()               //OR .centerInside()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener
    {
        public TextView textViewName;
        public ImageView imageView;
        public ImageViewHolder( View itemView ) {
            super(itemView);

            textViewName=itemView.findViewById(R.id.text_view_name);
            imageView=itemView.findViewById(R.id.image_view_upload);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick( View v ) {
            if(mListener!=null)
            {
                int position=getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION)
                {
                    mListener.onItemClick(position);
                }

            }
        }

        @Override
        public void onCreateContextMenu( ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo ) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatEver=menu.add(Menu.NONE,1,1,"Do whatever!!");
            MenuItem delete=menu.add(Menu.NONE,2,2,"Delete");

            doWhatEver.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick( MenuItem item ) {
            if(mListener!=null)
            {
                int position=getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION)
                {
                    //mListener.onItemClick(position);
                    switch (item.getItemId())
                    {
                        case 1:
                            mListener.onWhatEverClick(position);
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
                            return true;
                    }
                }

            }
            return false;
        }
    }
    public interface OnItemClickListener
    {
        void onItemClick(int position);

        void onWhatEverClick(int position);

        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener=listener;
    }

}
