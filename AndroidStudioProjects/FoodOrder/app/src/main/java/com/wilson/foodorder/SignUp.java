package com.wilson.foodorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wilson.foodorder.Model.User;

public class SignUp extends AppCompatActivity {

    EditText edtName,edtPhone,edtPassword;
    Button btnSignUp;
    DatabaseReference table_user;
    FirebaseDatabase database;

    boolean active=true;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUp.this,MainActivity.class);
        startActivity(intent);
        SignUp.this.finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        edtName = (EditText)findViewById(R.id.edtName);
        btnSignUp = (Button)findViewById(R.id.btnSignUp2);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog =  new ProgressDialog(SignUp.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(edtPhone.getText().toString().isEmpty()){
                            if(mDialog.isShowing()){mDialog.dismiss();}
                            Toast.makeText(SignUp.this, "Empty Phone", Toast.LENGTH_SHORT).show();
                        }
                        else if(edtName.getText().toString().isEmpty()){
                            if(mDialog.isShowing()){mDialog.dismiss();}
                            Toast.makeText(SignUp.this, "Empty Name", Toast.LENGTH_SHORT).show();
                        }
                        else if(edtPassword.getText().toString().isEmpty()){
                            if(mDialog.isShowing()){mDialog.dismiss();}
                            Toast.makeText(SignUp.this, "Empty Password", Toast.LENGTH_SHORT).show();
                        }
                        else if(dataSnapshot.child(edtPhone.getText().toString()).exists()&&active) {
                            if(mDialog.isShowing()){mDialog.dismiss();}
                            Toast.makeText(SignUp.this, "Phone Number already registered", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                            User user = new User(edtName.getText().toString(),edtPassword.getText().toString());
                            table_user.child(edtPhone.getText().toString()).setValue(user);
                            Intent main_intent = new Intent(SignUp.this,MainActivity.class);
                            active = false;
                            startActivity(main_intent);
                            SignUp.this.finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SignUp.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

        });

    }
}
//complete
