package com.example.sandip.stormbidder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.sandip.stormbidder.SecondActivity.MY_PREFS_NAME;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.OnItemClickListener,NavigationView.OnNavigationItemSelectedListener{

    EditText search_edit_text;
    private RecyclerView mRecyclerView;


    private ProgressBar mProgressCircle;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private Button button1;

    private List<Upload> mUploads;

    private SearchAdapter mSearchAdapter;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        search_edit_text=(EditText)findViewById(R.id.edit_text_search);
        button1=(Button)findViewById(R.id.button1);

        mRecyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle=(ProgressBar)findViewById(R.id.progress_circle);

        mUploads=new ArrayList<>();

        mSearchAdapter=new SearchAdapter(SearchActivity.this,mUploads);
        mRecyclerView.setAdapter(mSearchAdapter);
        mSearchAdapter.setOnItemClickListener(SearchActivity.this);

        mStorage=FirebaseStorage.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                setAdapter(search_edit_text.getText().toString());
            }
        });

        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        //mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void setAdapter( final String searchedString )
    {
        mDBListener=mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {

                mUploads.clear();
                //mRecyclerView.removeAllViews();

                //int counter=0;

                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    //String uid=snapshot.getKey();
                    String full_name=snapshot.child("mName").getValue(String.class);
                    //String user_name=snapshot.child("user_name").getValue(String.class);
                    String profile_pic=snapshot.child("mImageUrl").getValue(String.class);

                    if(full_name.toLowerCase().contains(searchedString.toLowerCase()))
                    {
                        Upload upload=snapshot.getValue(Upload.class);
                        upload.setmKey(snapshot.getKey());
                        mUploads.add(upload);
                        //counter++;
                    }
                    else
                    {
                        continue;
                    }

                    /*if(counter==5)
                        break;*/
                }

                //mSearchAdapter=new SearchAdapter(SearchActivity.this,mUploads);
                //mRecyclerView.setAdapter(mSearchAdapter);

                //mSearchAdapter.notifyDataSetChanged();

                //mProgressCircle.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
                Toast.makeText(SearchActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
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

        Intent intent=new Intent(SearchActivity.this,ShowActivity.class);

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
        Toast.makeText(this,"Whatever click at position: "+position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick( int position ) {
        //Toast.makeText(this,"Delete click at position: "+position,Toast.LENGTH_SHORT).show();
        Upload selectedItem=mUploads.get(position);
        final String selectedKey=selectedItem.getmKey();
        StorageReference imareRef=mStorage.getReferenceFromUrl(selectedItem.getmImageUrl());
        imareRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess( Void aVoid ) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(SearchActivity.this,"Item Deleted",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
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
