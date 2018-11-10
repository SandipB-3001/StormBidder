package com.example.sandip.stormbidder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private EditText userName, userPassword, userEmail;
    protected Button regButton;
    protected TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        setupUIViews();

        firebaseAuth=FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("user");

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(validate())
                {
                    //Upload data to database
                    //String user_name=userName.getText().toString().trim();
                    String user_email=userEmail.getText().toString().trim();
                    String user_password=userPassword.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                User user = new User(userName.getText().toString().trim(),userEmail.getText().toString().trim());
                                String userId= mDatabaseRef.push().getKey();
                                mDatabaseRef.child(userId).setValue(user);
                                Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                            }

                            else

                            {
                                Toast.makeText(RegistrationActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
            }
        });

    }

    private void setupUIViews(){
        userName=(EditText)findViewById(R.id.etUserName);
        userPassword=(EditText)findViewById(R.id.etUserPassword);
        userEmail=(EditText)findViewById(R.id.etUserEmail);
        regButton=(Button)findViewById(R.id.button_register);
        userLogin=(TextView)findViewById(R.id.tvUserLogin);



    }

    private Boolean validate(){
        Boolean result=false;
        String name=userName.getText().toString();
        String password=userPassword.getText().toString();
        String email=userEmail.getText().toString();

        if(name.isEmpty() || password.isEmpty() || email.isEmpty())
        {
            Toast.makeText(this,"Please enter all the deatils",Toast.LENGTH_SHORT).show();

        }
        else
        {
            result=true;
        }

        return result;
    }

}