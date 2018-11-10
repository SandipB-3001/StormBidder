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

import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private Context mContext;
    private List<Upload> mUploads;

    private OnItemClickListener mListener;

    public SearchAdapter( Context mContext, List<Upload> mUploads) {
        this.mContext = mContext;
        this.mUploads = mUploads;
    }


    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.search_item,parent,false);
        return new SearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder( @NonNull SearchViewHolder holder, int position ) {
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

    public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener
    {
        public TextView textViewName1;
        public ImageView imageView1;

        public SearchViewHolder( View itemView ) {
            super(itemView);

            textViewName1=itemView.findViewById(R.id.text_view_name1);
            imageView1=itemView.findViewById(R.id.image_view_upload1);

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
