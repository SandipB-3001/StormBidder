package com.example.sandip.stormbidder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SecondActivity extends AppCompatActivity {

    private EditText Name, Password;
    private Button Login;
    private TextView Info;
    private TextView forgetPassword;
    private int counter = 5;
    protected TextView userRegistration;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    protected Button registerhere;
    private DatabaseReference mDatabaseRef;
    public final String prefered_uname="";
    int newHighScore=5;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Name = (EditText) findViewById(R.id.name);
        Password = (EditText) findViewById(R.id.password);
        Info = (TextView) findViewById(R.id.attempts);
        Login = (Button) findViewById(R.id.btnLogin);
        userRegistration = (TextView) findViewById(R.id.tvRegister);
        forgetPassword=(TextView)findViewById(R.id.forget_password);
        // registerhere=(Button)findViewById(R.id.register);
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("user");

        Info.setText("No.of attempts remaining: 5");

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        // init();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            finish();
            startActivity(new Intent(SecondActivity.this, SecondActivity.class));
        }

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {


                if (Name.getText().toString().isEmpty() || Password.getText().toString().isEmpty())

                {
                    Toast.makeText(SecondActivity.this, "Please fill in all login credentials", Toast.LENGTH_SHORT).show();

                } else {
                    validate(Name.getText().toString(), Password.getText().toString());
                }
            }
        });

        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view ) {

                // openNewActivity();
                Intent intent = new Intent(SecondActivity.this, RegistrationActivity.class);
                startActivity(intent);
                //  startActivity(new Intent(MainActivity.this, RegistrationActivity.class));

            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent intent = new Intent(SecondActivity.this,ResetActivity.class);
                startActivity(intent);
            }
        });

    }

    private void validate( final String userName, final String userPassword ) {

        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete( @NonNull Task<AuthResult> task ) {


                if (task.isSuccessful()) {
                    if(getIntent().getExtras().getString("ACTIVITY").equals("ShowActivity"))
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SecondActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, 0).edit();
                        editor.putString("name", userName);
                        editor.putInt("idName", 12);
                        editor.apply();
                        editor.commit();
                        Intent intent=new Intent(SecondActivity.this, ShowActivity.class);
                        intent.putExtra("PRODUCTID",getIntent().getExtras().getString("ITEMKEY"));
                        intent.putExtra("MAIN","LoginActivity");
                        startActivity(intent);
                    }
                    else if(getIntent().getExtras().getString("ACTIVITY").equals("UploadActivity"))
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SecondActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, 0).edit();
                        editor.putString("name", userName);
                        editor.putInt("idName", 12);
                        editor.apply();
                        editor.commit();
                        Intent intent=new Intent(SecondActivity.this, UploadActivity.class);
                        intent.putExtra("MAIN","LoginActivity");
                        startActivity(intent);
                    }

                    else if(getIntent().getExtras().getString("ACTIVITY").equals("HomeActivity")){

                        progressDialog.dismiss();
                        Toast.makeText(SecondActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, 0).edit();
                        editor.putString("name", userName);
                        editor.putInt("idName", 12);
                        editor.apply();
                        editor.commit();

                        startActivity(new Intent(SecondActivity.this, MainActivity.class));
                    }
                } else {
                    Toast.makeText(SecondActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    counter--;

                    Info.setText("No. of attempts remaining: " + counter);
                    progressDialog.dismiss();
                    if (counter == 0) {
                        Login.setEnabled(false);
                    }


                }

            }
        });

    }
}
