package com.example.sandip.stormbidder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.sandip.stormbidder.SecondActivity.MY_PREFS_NAME;

public class ShowActivity extends AppCompatActivity {

    private TextView name,price,bid,desc;
    private ImageView mImage;
    private Button mBID;
    Upload mU;

    StorageReference mStorageRef;
    Context context=this;
    private String key;

    //DANGER
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    ValueEventListener mDBListener;
    String uname;

    private DatabaseReference mDatabaseReference;
    List<Upload> mUploads,myUploads;

    //private Button join;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        uname = prefs.getString("name", "No name defined");//"No name defined" is the default value.

        mBID=(Button)findViewById(R.id.bid_show);
        name=(TextView)findViewById(R.id.name_show);
        price=(TextView)findViewById(R.id.price_show);
        bid=(TextView)findViewById(R.id.text_view_bid_show);
        desc=(TextView)findViewById(R.id.textViewDesc);
        mImage=(ImageView)findViewById(R.id.image_view_show);
        key=getIntent().getExtras().getString("KEY");

        if(getIntent().getExtras().getString("STATUS").equals("INACTIVE") || getIntent().getExtras().getString("SELLER").equals(uname))
        {
            mBID.setEnabled(false);
        }


        mDatabaseReference= FirebaseDatabase.getInstance().getReference("bids");
        //join=(Button)findViewById(R.id.joining);

        //SessionManager session = new SessionManager(getApplicationContext());
        //key=session.getKey();

        if(getIntent().getExtras().getString("MAIN").equals("LoginActivity"))
        {
            mUploads=new ArrayList<>();
            myUploads=new ArrayList<>();

            mStorage= FirebaseStorage.getInstance();
            mDatabaseRef= FirebaseDatabase.getInstance().getReference("uploads");

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
                        if(u.getmKey().equals(getIntent().getExtras().getString("PRODUCTID")))
                        {
                            myUploads.add(u);
                        }
                    }
                    mAdapter.notifyDataSetChanged();

                    mProgressCircle.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled( DatabaseError databaseError ) {
                    Toast.makeText(ShowActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    mProgressCircle.setVisibility(View.INVISIBLE);
                }
            });
            for(Upload u:mUploads)
            {
                name.setText(u.getmName());
                price.setText(Double.toString(u.getmPrice()));
                bid.setText(Double.toString(u.getmBid()));
                desc.setText(u.getmDescription());
                Picasso.with(context)
                        .load(u.getmImageUrl())
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()               //OR .centerInside()
                        .into(mImage);
            }
        }
        else {
            name.setText(getIntent().getExtras().getString("NAME"));
            price.setText(getIntent().getExtras().getString("PRICE"));
            bid.setText(getIntent().getExtras().getString("BID"));
            desc.setText(getIntent().getExtras().getString("DESC"));
            Picasso.with(context)
                    .load(getIntent().getExtras().getString("URL"))
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    .centerCrop()               //OR .centerInside()
                    .into(mImage);
            }


        mBID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {

                if(uname.equals("No name defined"))
                {
                    Intent intent=new Intent(ShowActivity.this,SecondActivity.class);
                    intent.putExtra("ACTIVITY","ShowActivity");
                    intent.putExtra("ITEMKEY",getIntent().getExtras().getString("KEY"));
                    startActivity(intent);
                    Toast.makeText(ShowActivity.this,"No name",Toast.LENGTH_SHORT).show();
                }
                else {
                    double newBid = 0.0;

                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    if (Double.parseDouble(getIntent().getExtras().getString("BID"))
                            <= Double.parseDouble(getIntent().getExtras().getString("PRICE")) * 1.25) {
                        newBid = Double.parseDouble(getIntent().getExtras().getString("BID"))
                                + Double.parseDouble(getIntent().getExtras().getString("PRICE")) * 0.1;
                        //newBid=9000;
                    } else if (Double.parseDouble(getIntent().getExtras().getString("BID"))
                            > Double.parseDouble(getIntent().getExtras().getString("PRICE")) * 1.25
                            && Double.parseDouble(getIntent().getExtras().getString("BID"))
                            <= Double.parseDouble(getIntent().getExtras().getString("PRICE")) * 1.5
                            ) {
                        newBid = Double.parseDouble(getIntent().getExtras().getString("BID"))
                                + Double.parseDouble(getIntent().getExtras().getString("PRICE")) * 0.2;
                        //newBid=9000;
                    } else if (Double.parseDouble(getIntent().getExtras().getString("BID"))
                            > Double.parseDouble(getIntent().getExtras().getString("PRICE")) * 1.5) {
                        newBid = Double.parseDouble(getIntent().getExtras().getString("BID"))
                                + Double.parseDouble(getIntent().getExtras().getString("PRICE")) * 0.5;
                        //newBid=9000;
                    }
                    //User user = new User(userName.getText().toString().trim(),userEmail.getText().toString().trim());
                    Bids bids = new Bids(key, uname, date, newBid);
                    String biddingId = mDatabaseReference.push().getKey();
                    mDatabaseReference.child(biddingId).setValue(bids);
                    //String userId= mDatabaseRef.push().getKey();

                    DatabaseReference dbf = FirebaseDatabase.getInstance().getReference("uploads").child(key);
                    Upload upload = new Upload(getIntent().getExtras().getString("NAME"),
                            getIntent().getExtras().getString("URL"),
                            getIntent().getExtras().getString("DESC"),
                            Double.parseDouble(getIntent().getExtras().getString("PRICE")),
                            newBid, getIntent().getExtras().getString("SELLER"),
                            getIntent().getExtras().getString("DATE"), "ACTIVE",uname);
                    dbf.setValue(upload);
                    Toast.makeText(ShowActivity.this, "Bid Successful", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
