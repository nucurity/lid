package com.projects.lid;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.projects.lid.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    CheckBox remember;

    List<String[]> users;
    User user;
    User currentUser;

    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        user = User.getInstance();
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginbtn);
        remember = (CheckBox) findViewById(R.id.checkbox_remember);

        users = new ArrayList<>();

        loginButton.setOnClickListener(this);

        fetchUsers();
        //getUserFromDB();


    }

    public void fetchUsers() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot groupsDataSnapshot) {

                for (DataSnapshot usersDataSnapshot : groupsDataSnapshot.getChildren()) {

                    for (DataSnapshot userDataSnapshot : usersDataSnapshot.getChildren()) {
                        String[] data = new String[5];
                        data[0] = (String) userDataSnapshot.child("username").getValue();
                        data[1] = (String) userDataSnapshot.child("password").getValue();
                        data[2] = (String) userDataSnapshot.getKey();
                        data[3] = (String) usersDataSnapshot.getKey();
                        data[4] = (String) usersDataSnapshot.child("name").getValue();
                        users.add(data);

                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        String username = this.usernameEditText.getText().toString();
        String password = this.passwordEditText.getText().toString();



        if (username.equals("") || password.equals("")) {
            showMsg("Please, enter username and password");
        } else {
            Boolean flag = false;

            for (String[] user : users) {
                if (user[1].equals(password) && user[0].equals(username)) {
                    flag = true;
                    this.user.setUsername(user[0]);
                    this.user.setPassword(user[1]);
                    this.user.setUserId(user[2]);
                    this.user.setGroupId(user[3]);
                    this.user.setName(user[4]);

                }
            }





            if (flag) {

                if(remember.isChecked()){
                    //saveUserCredentials();
                }

                startActivity(new Intent(this, MainActivity.class));
            } else {
                showMsg("Credentials not found");
            }
        }

    }

    public void showMsg(String msg) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }



    @Override
    public void onBackPressed() {
        this.finish();
        System.exit(0);
    }




//    ////////
//    public void getUserFromDB(){
//
//        String[] projection = { UserTableHandler.COLUMN_ID,
//                UserTableHandler.COLUMN_NAME,
//                UserTableHandler.COLUMN_GROUPID,
//                UserTableHandler.COLUMN_NAME,
//                UserTableHandler.COLUMN_PASSWORD,
//                UserTableHandler.COLUMN_USERNAME};
//
//
//        Uri uri = Uri.parse(UserContentProvider.CONTENT_URI + "/" + 1);
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//
//            Log.i("TAGSIS", "USER exists");
////            user.setText(cursor.getString(cursor.getColumnIndexOrThrow(CourseTableHandler.COLUMN_NAME)));
////            code.setText(cursor.getString(cursor.getColumnIndexOrThrow(CourseTableHandler.COLUMN_CODE)));
//
//            cursor.close();
//        } else{
//            Log.i("TAGSIS", "USER  NOT exists");
//        }
//    }
//
//    public void saveUserCredentials(){
//
//        ContentValues values = new ContentValues();
//        values.put(UserTableHandler.COLUMN_NAME, user.getName());
//        values.put(UserTableHandler.COLUMN_GROUPID, user.getGroupId());
//        values.put(UserTableHandler.COLUMN_USERID, user.getUserId());
//        values.put(UserTableHandler.COLUMN_USERNAME, user.getUsername());
//        values.put(UserTableHandler.COLUMN_PASSWORD, user.getPassword());
//
//        getContentResolver().insert(UserContentProvider.CONTENT_URI, values);
//    }

}
