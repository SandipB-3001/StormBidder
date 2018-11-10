package com.example.sandip.stormbidder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.sandip.stormbidder.SecondActivity.MY_PREFS_NAME;

public class DealsEndingTodayActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        SearchAdapter.OnItemClickListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth firebaseAuth;
    TextView name;
    String uname;
    //static int idName;

    private RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;

    List<Upload> mUploads,myUploads;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals_ending_today);

        mRecyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle=(ProgressBar)findViewById(R.id.progress_circle);

        mUploads=new ArrayList<>();
        myUploads=new ArrayList<>();

        mAdapter=new SearchAdapter(DealsEndingTodayActivity.this,myUploads);
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.setOnItemClickListener(ShowUploadsActivity.this);

        mStorage= FirebaseStorage.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        uname = prefs.getString("name", "No name defined");//"No name defined" is the default value.

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {

                mUploads.clear();
                myUploads.clear();

                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                Date date1=null,date2=null;
                long diff,diffDays;
                //long  = diff / (24 * 60 * 60 * 1000);

                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Upload upload=postSnapshot.getValue(Upload.class);
                    upload.setmKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }
                for(Upload u:mUploads)
                {
                    try{
                        date1=new SimpleDateFormat("yyyy-MM-dd").parse(u.getmDate());
                        date2=new SimpleDateFormat("yyyy-MM-dd").parse(date);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    diff = Math.abs(date2.getTime() - date1.getTime());
                    diffDays = diff / (24 * 60 * 60 * 1000);
                    if(diffDays==2)
                    {
                        myUploads.add(u);
                    }
                }
                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
                Toast.makeText(DealsEndingTodayActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
        firebaseAuth= FirebaseAuth.getInstance();
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public boolean onOptionsItemSelected( MenuItem item)
    {
        if(mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected( @NonNull MenuItem item ) {
        int id=item.getItemId();
        if(id==R.id.home)
        {
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        if(id==R.id.profile)
        {
            Intent intent=new Intent(this,ProfileActivity.class);
            startActivity(intent);
        }
        if(id==R.id.upload)
        {
            Intent intent=new Intent(this,UploadActivity.class);
            startActivity(intent);
        }
        if(id==R.id.home_items)
        {
            Intent intent=new Intent(this,DealsEndingTodayActivity.class);
            startActivity(intent);
        }
        if(id==R.id.search)
        {
            Intent intent=new Intent(this,SearchActivity.class);
            startActivity(intent);
        }
        if(id==R.id.logout)
        {
            //Toast.makeText(this,"This is Logout",Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, 0).edit();
            editor.putString("name", "No name defined");
            editor.putInt("idName", 12);
            editor.apply();
            editor.commit();
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
        return false;
    }

    @Override
    public void onItemClick( int position ) {
        Toast.makeText(this,"Normal click at position: "+position,Toast.LENGTH_SHORT).show();
        Upload selectedItem=mUploads.get(position);
        final String selectedKey=selectedItem.getmKey();
        final String name,desc,imageR,date;

        name=selectedItem.getmName();
        double price=selectedItem.getmPrice();
        double bid=selectedItem.getmBid();
        imageR=selectedItem.getmImageUrl();
        date=selectedItem.getmDate();
        desc=selectedItem.getmDescription();

        Intent intent=new Intent(DealsEndingTodayActivity.this,ShowActivity.class);

        intent.putExtra("NAME",name);
        intent.putExtra("PRICE",Double.toString(price));
        intent.putExtra("DESC",desc);
        intent.putExtra("BID",Double.toString(bid));
        intent.putExtra("URL",imageR);
        intent.putExtra("DATE",date);
        intent.putExtra("KEY",selectedKey);
        intent.putExtra("STATUS",selectedItem.getmStatus());
        intent.putExtra("SELLER",selectedItem.getmUserName());
        intent.putExtra("MAIN","HomeActivity");

        startActivity(intent);

        startActivity(intent);
    }

    @Override
    public void onWhatEverClick( int position ) {

    }

    @Override
    public void onDeleteClick( int position ) {
        Upload selectedItem=mUploads.get(position);
        final String selectedKey=selectedItem.getmKey();
        StorageReference imareRef=mStorage.getReferenceFromUrl(selectedItem.getmImageUrl());
        imareRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess( Void aVoid ) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(DealsEndingTodayActivity.this,"Item Deleted",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
