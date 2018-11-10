package com.example.sandip.stormbidder;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.sandip.stormbidder.SecondActivity.MY_PREFS_NAME;

public class UploadActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    //private TextView mTextViewShowUploads;
    private EditText mEditTextFileName,mEditTextDescription,mEditTextPrice;
    private ImageView mImageView;
    private ProgressBar mProgerssBar;
    //private Button search;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth firebaseAuth;

    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    private static final int PICK_IMAGE_REQUEST=1;
    static String uname;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        uname = prefs.getString("name", "No name defined");//"No name defined" is the default value.

        if(uname.equals("No name defined"))
        {
            Intent intent=new Intent(UploadActivity.this,SecondActivity.class);
            intent.putExtra("ACTIVITY","UploadActivity");
            startActivity(intent);
        }
        //UPLOAD
        else {
            mButtonChooseImage = (Button) findViewById(R.id.button_choose_image);
            mButtonUpload = (Button) findViewById(R.id.button_upload);
            //mTextViewShowUploads=(TextView)findViewById(R.id.text_view_show_uploads);
            mEditTextFileName = (EditText) findViewById(R.id.edit_text_file_name);
            mEditTextDescription = (EditText) findViewById(R.id.edit_text_description);
            mEditTextPrice = (EditText) findViewById(R.id.edit_text_price);
            mImageView = (ImageView) findViewById(R.id.image_view);
            mProgerssBar = (ProgressBar) findViewById(R.id.progress_bar);
            //search=(Button)findViewById(R.id.search);

            mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

            mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    OpenFileChooser();
                }
            });

            mButtonUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    if (mUploadTask != null && mUploadTask.isInProgress()) {
                        Toast.makeText(UploadActivity.this, "Upload  in progress", Toast.LENGTH_SHORT).show();
                    } else {
                        upLoadFile();
                    }
                }
            });

            //NAVIGATION MENU
            firebaseAuth = FirebaseAuth.getInstance();
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
            //mToggle=new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
            mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
            mDrawerLayout.addDrawerListener(mToggle);
            mToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
            navigationView.setNavigationItemSelectedListener(this);
        }
    }
    private void OpenFileChooser()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK
                && data!=null && data.getData()!=null)
        {
            mImageUri=data.getData();

            Picasso.with(this).load(mImageUri).into(mImageView);
            //IF WE DO NOT WANT TO USE PICASSO THE WE CAN USE THE FOLLOWING CODE
            //mImageView.setImageURI(mImageUri);

        }
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void upLoadFile()
    {
        if(mImageUri!=null)
        {
            StorageReference fileReference=mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            mUploadTask=fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess( UploadTask.TaskSnapshot taskSnapshot ) {
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgerssBar.setProgress(0);
                                }
                            },5000);
                            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            Toast.makeText(UploadActivity.this,"Upload Successfull",Toast.LENGTH_LONG).show();
                            Upload upload=new Upload(mEditTextFileName.getText().toString().trim(),
                                    taskSnapshot.getDownloadUrl().toString(),mEditTextDescription.getText().toString().trim(),
                                    Double.parseDouble(mEditTextPrice.getText().toString().trim()),
                                    Double.parseDouble(mEditTextPrice.getText().toString().trim()),
                                    uname,date,"ACTIVE","XXXX");

                            String uploadId=mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure( @NonNull Exception e ) {
                            Toast.makeText(UploadActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress( UploadTask.TaskSnapshot taskSnapshot ) {
                            double progress=(100.0 *taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgerssBar.setProgress((int)progress);
                        }
                    });
        }
        else
        {
            Toast.makeText(this,"No File Selected!!!",Toast.LENGTH_SHORT).show();
        }
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
