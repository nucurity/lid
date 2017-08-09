package com.projects.lid;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.lid.entities.Message;
import com.projects.lid.entities.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener , AdapterView.OnItemClickListener {

    FloatingActionButton addBtn;
    ArrayAdapter<String> arrayAdapter;
    List<String> displayList;
    List<Message> messagesObjectList;

    User user;

    private ListView messagesDisplayList;
    private String operationDate;
    private String operationKey;
    private String messageId;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        displayList = new ArrayList<>();
        messagesObjectList = new ArrayList<>();

        addBtn = (FloatingActionButton) findViewById(R.id.add_btn);
        addBtn.setOnClickListener(this);

        messagesDisplayList = (ListView) findViewById(R.id.fee_list_view);
        registerForContextMenu(messagesDisplayList);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        messagesDisplayList.setAdapter(arrayAdapter);

        user = User.getInstance();

        fetchDataFromDB();

        messagesDisplayList.setOnItemClickListener(this);

    }

    public void fetchDataFromDB() {

        arrayAdapter.clear();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child(user.getGroupId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot groupDataSnapshot) {

                displayList.clear();
                messagesObjectList.clear();

                for (DataSnapshot userDataSnapshot : groupDataSnapshot.getChildren()) {

                    DataSnapshot sp = userDataSnapshot.child("messages");
                    for (DataSnapshot messageDataSnapshot : sp.getChildren()) {

                        Message message = new Message();

                        message.setTitle((String) messageDataSnapshot.child("title").getValue());
                        message.setDetails((String) messageDataSnapshot.child("details").getValue());
                        message.setDate((long) messageDataSnapshot.child("date").getValue());
                        message.setMessageId((String) messageDataSnapshot.getKey());
                        message.setAuthor((String) userDataSnapshot.child("name").getValue());
                        messagesObjectList.add(message);

                    }
                }


                for (int i = 0; i < messagesObjectList.size(); i++) {
                    displayList.add(messagesObjectList.get(i).getAuthor()+": " + messagesObjectList.get(i).getTitle());
                }
                arrayAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClick(View v) {
       Intent intent = new Intent(this, FormActivity.class);
       startActivity(intent);
    }




    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        final int messageId = i;
        PopupMenu menu = new PopupMenu (this, view);
        menu.setGravity(Gravity.RIGHT);
        menu.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener ()
        {
            @Override
            public boolean onMenuItemClick (MenuItem item)
            {
                int id = item.getItemId();
                switch (id)
                {
                    case R.id.menu_update: update(messageId); break;
                    case R.id.menu_delete: delete(messageId); break;
                }
                return true;
            }
        });
        menu.inflate (R.menu.context_menu_layout);
        menu.show();
    }

    public void update(int id){

        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra("messageId", messagesObjectList.get(id).getMessageId());
        intent.putExtra("title", messagesObjectList.get(id).getTitle());
        intent.putExtra("details", messagesObjectList.get(id).getDetails());
        intent.putExtra("date", messagesObjectList.get(id).getDate());
        startActivity(intent);

    }

    public void delete(int id){
        User user = User.getInstance();
        Log.i("GroupID", user.getGroupId());
        Log.i("UserID", user.getUserId());
        Log.i("msg", messagesObjectList.get(id).getMessageId());
        FirebaseDatabase.getInstance().getReference(user.getGroupId())
                .child(user.getUserId())
                .child("messages")
                .child(messagesObjectList.get(id).getMessageId())
                .removeValue();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_layout, menu);
        return true;
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show:
                logout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    public void logout(){
        Intent intent = new Intent(this, LoginActivity.class);
        User user = User.getInstance();
        user.setPassword(null);
        user.setUsername(null);
        user.setUserId(null);
        user.setGroupId(null);
        startActivity(intent);

        finish();
    }
}
