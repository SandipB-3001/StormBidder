package com.example.sandip.stormbidder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import static com.example.sandip.stormbidder.SecondActivity.MY_PREFS_NAME;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private TextView nameProfile,editProfile,myUploads,myPurchase,soldByMe;
    private Button mLogin,mLogout;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth firebaseAuth;
    private String uname,username;;
    List<User> mUploads;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameProfile=(TextView)findViewById(R.id.textViewProfileUser);
        editProfile=(TextView)findViewById(R.id.textViewEditProfile);
        myUploads=(TextView)findViewById(R.id.textViewMyUploads);
        myPurchase=(TextView)findViewById(R.id.textViewMyPurchase);
        soldByMe=(TextView)findViewById(R.id.textViewSoldByMe);
        mLogin=(Button)findViewById(R.id.buttonLogin);
        mLogout=(Button)findViewById(R.id.buttonLogout);
        mUploads=new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        uname = prefs.getString("name", "No name defined");//"No name defined" is the default value.




        mDatabaseRef= FirebaseDatabase.getInstance().getReference("user");

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot ) {

                mUploads.clear();

                for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                {
                    User user=postSnapshot.getValue(User.class);
                    user.setmKey(postSnapshot.getKey());
                    mUploads.add(user);
                }

                for(User u:mUploads)
                {
                    if(u.geteMail().equals(uname))
                    {
                        nameProfile.setText(u.getuName());
                        username=u.getuName();
                    }
                }

                /*mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);*/
            }

            @Override
            public void onCancelled( DatabaseError databaseError ) {
                Toast.makeText(ProfileActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                //mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

        nameProfile.setText(uname);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent intent=new Intent(ProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("UNAME",uname);
                intent.putExtra("USERNAME",username);
                startActivity(intent);
            }
        });
        myUploads.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick( View v ) {
                Intent intent1=new Intent(ProfileActivity.this,ShowUploadsActivity.class);
                startActivity(intent1);
            }
        });
        myPurchase.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick( View v ) {
                Intent intent=new Intent(ProfileActivity.this,ShowPurchasedActivity.class);
                startActivity(intent);
            }
        });
        soldByMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent intent=new Intent(ProfileActivity.this,SoldByMeActivity.class);
                startActivity(intent);
            }
        });
        if(uname.equals("No name defined"))
        {
            mLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    Intent intent=new Intent(ProfileActivity.this,SecondActivity.class);
                    intent.putExtra("ACTIVITY","HomeActivity");
                    startActivity(intent);
                }
            });
        }
        else
        {
            mLogin.setEnabled(false);
        }
        if(!uname.equals("No name defined"))
        {
            mLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, 0).edit();
                    editor.putString("name", "No name defined");
                    editor.putInt("idName", 12);
                    editor.apply();
                    editor.commit();
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                }
            });
        }
        else
        {
            mLogout.setEnabled(false);
        }
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
