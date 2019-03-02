package com.example.gochat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText username,password,email,phone;
    Button submit,signInWithGoogle;
    ProgressBar  progressBar;
    TextView text;
    ImageView image;

    static  final  int  GOOGLE_SIGN = 123;
    GoogleSignInOptions gso;

    FirebaseAuth mAuth ;
    FirebaseAuth auth;
    DatabaseReference reference;
    DatabaseReference databaseReference ;
    private static final String TAG = "RegisterActivity";
    GoogleSignInClient mGoogleSignInClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.emailid);
        phone  = findViewById(R.id.phoneNumber);

        submit = findViewById(R.id.register);

        signInWithGoogle = findViewById(R.id.signInWithGoogle);
        progressBar = findViewById(R.id.progress_bar);
        text = findViewById(R.id.text);
        image = findViewById(R.id.image);
        FirebaseApp.initializeApp(RegisterActivity.this);
        mAuth = FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String txt_username = username.getText().toString();
                String txt_email =  email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_phone = phone.getText().toString();


                if(TextUtils.isEmpty(txt_username)|| TextUtils.isEmpty(txt_email)|| TextUtils.isEmpty(txt_password)  ){
                    Toast.makeText(RegisterActivity.this,"All fields are required",Toast.LENGTH_SHORT).show();
                }
                else if(txt_password.length()<6){
                    Toast.makeText(RegisterActivity.this,"Password must be of atleast 6 characters",Toast.LENGTH_SHORT).show();
                }
                else {
                    register(txt_username,txt_email,txt_password,txt_phone);
                }

            }
        });
        signInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                       .requestIdToken(Constants.FIREBASE_WEB_CLIENT_ID)
                       .requestEmail()
                       .build();

               mGoogleSignInClient = GoogleSignIn.getClient(RegisterActivity.this,gso);
               signInWithGoogle();
            }
        });


    }
    private void register(final String username, String email, String password,String phone){

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressBar.setVisibility(View.INVISIBLE);
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userID   = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                            HashMap<String, String > hashMap = new HashMap<>();
                            hashMap.put("id",userID);
                            hashMap.put("username",username);
                            hashMap.put("imageURL","default");
                            hashMap.put("phone", String.valueOf(phone));
                            hashMap.put("email",email);
                            hashMap.put("status","offline");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(RegisterActivity.this,"You can't register with this email or password ",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    void signInWithGoogle(){
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,GOOGLE_SIGN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_SIGN){
            Log.d(TAG, "onActivityResult: request code "+ requestCode);
            CheckIfUserIsLoggedInOrNot(data);

        }
    }

    private void CheckIfUserIsLoggedInOrNot(Intent data) {
        Task<GoogleSignInAccount> task  = GoogleSignIn
                .getSignedInAccountFromIntent(data);

        try{

            GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
        }
        catch (ApiException e){
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(RegisterActivity.this,"Error while Login : ",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
        AuthCredential credential  =
                GoogleAuthProvider.getCredential(account.getIdToken(),null);

       mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                   Toast.makeText(RegisterActivity.this,"Login Successfully",Toast.LENGTH_SHORT).show();
                   progressBar.setVisibility(View.INVISIBLE);
                   FirebaseUser user = mAuth.getCurrentUser();
                   updateUI(user);
               }
               else {
                   Toast.makeText(RegisterActivity.this,"Login Unsuccessful",Toast.LENGTH_SHORT).show();
                   updateUI(null);
               }
           }
       });
    }

    private void updateUI(FirebaseUser user) {
      if(user != null){
          String name = user.getDisplayName();
          String email = user.getEmail();
          String photo  = String.valueOf(user.getPhotoUrl()) ;

          text.append("Info : /n" );
          text.append(name + "\n");
          text.append(email);

          Picasso.with(RegisterActivity.this).load(photo).into(image);



      }
    }
}
