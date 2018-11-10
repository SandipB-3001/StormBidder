package com.example.sandip.stormbidder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    TextView name,username;
    EditText address,dob,phNo;
    Button save;
    private DatabaseReference mDatabaseRef;
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name=(TextView)findViewById(R.id.textViewNameP);
        username=(TextView)findViewById(R.id.textViewEmailP);
        address=(EditText)findViewById(R.id.editTextAddress) ;
        dob=(EditText)findViewById(R.id.editTextDOB) ;
        phNo=(EditText)findViewById(R.id.editTextPhNo);
        save=(Button)findViewById(R.id.buttonSave);

        mDatabaseRef= FirebaseDatabase.getInstance().getReference("profile");
        name.setText(getIntent().getExtras().getString("UNAME"));
        username.setText(getIntent().getExtras().getString("USERNAME"));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Profile profile=new Profile(getIntent().getExtras().getString("UNAME"),getIntent().getExtras().getString("USERNAME"),
                        address.getText().toString().trim(),phNo.getText().toString().trim(),dob.getText().toString().trim());
                String profileId= mDatabaseRef.push().getKey();
                mDatabaseRef.child(profileId).setValue(profile);
                Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
            }
        });

    }
}
