package com.wilson.foodorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wilson.foodorder.Common.Common;
import com.wilson.foodorder.Model.User;

public class SignIn extends AppCompatActivity {

    EditText edtPhone,edtPassword;
    Button btnSignIn;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignIn.this,MainActivity.class);
        startActivity(intent);
        SignIn.this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        btnSignIn = (Button)findViewById(R.id.btnSignIn2);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//INSIDE ONCLICK ------------------------------------------------------------------------------
                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();         // For loading process

                if(!edtPhone.getText().toString().isEmpty()||!edtPassword.getText().toString().isEmpty()){
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                mDialog.dismiss();

                                // Get User Info
                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                if (user.getPassword().equals(edtPassword.getText().toString())) {
                                    startActivity(new Intent(SignIn.this,Home.class));
                                    Common.currentUser = user;
                                    SignIn.this.finish();
                                }
                                else {
                                    Toast.makeText(SignIn.this, "Wrong Password", Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "User not exist in Database", Toast.LENGTH_LONG).show();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(SignIn.this, "Enter Registered Mobile Number", Toast.LENGTH_LONG).show();

                }
//-------------------------------------------------------------------------------------
            }
        });



    }
}
