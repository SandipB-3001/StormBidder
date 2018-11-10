package com.example.sandip.stormbidder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBEmptyNode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;

import static com.example.sandip.stormbidder.SecondActivity.MY_PREFS_NAME;


public class SoldByMeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth firebaseAuth;
    TextView name;
    String uname;
    //static int idName;

    private RecyclerView mRecyclerView;
    private ProfileAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;

    List<Upload> mUploads,myUploads;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_by_me);
        mRecyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle=(ProgressBar)findViewById(R.id.progress_circle);

        mUploads=new ArrayList<>();
        myUploads=new ArrayList<>();

        mAdapter=new ProfileAdapter(SoldByMeActivity.this,myUploads);
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.setOnItemClickListener(ShowUploadsActivity.this);

        mStorage=FirebaseStorage.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        uname = prefs.getString("name", "No name defined");//"No name defined" is the default value.

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {

                mUploads.clear();
                myUploads.clear();

                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    Upload upload=postSnapshot.getValue(Upload.class);
                    upload.setmKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }
                for(Upload u:mUploads)
                {
                    if(u.getmUserName().equals(uname) && u.getmStatus().equals("INACTIVE"))
                    {
                        myUploads.add(u);
                    }
                }
                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
                Toast.makeText(SoldByMeActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
        firebaseAuth=FirebaseAuth.getInstance();
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
}
