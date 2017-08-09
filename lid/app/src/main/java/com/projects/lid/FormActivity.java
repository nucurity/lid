package com.projects.lid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.lid.entities.Message;
import com.projects.lid.entities.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    EditText title;
    EditText details;
    Button commandbutton;
    Boolean addOrUpdate;
    LinearLayout datepanel;
    TextView dateValue;

    private DatabaseReference groupDataRef;
    private FirebaseDatabase firebaseInstance;
    private User user;
    private String messageId;
    private Date date;
    SimpleDateFormat sdf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        title = (EditText) findViewById(R.id.title);
        details = (EditText) findViewById(R.id.details);
        datepanel = (LinearLayout) findViewById(R.id.datepanel) ;
        dateValue = (TextView) findViewById(R.id.datevalue);
        commandbutton = (Button) findViewById(R.id.commandbutton);
        commandbutton.setOnClickListener(this);


        Bundle extras = this.getIntent().getExtras();


        user = User.getInstance();
        //user.setGroupId("group1");
        //user.setUserId("63ebb758-296b-11e7-93ae-92361f002671");

        groupDataRef = FirebaseDatabase.getInstance().getReference(user.getGroupId());
        sdf = new SimpleDateFormat("mm/dd/yyyy HH:mm");

        // Or passed from the other activity
        if (extras != null) {
            commandbutton.setText("Safe");
            addOrUpdate  = true;
            title.setText(extras.get("title").toString());
            details.setText(extras.get("details").toString());
            messageId = extras.get("messageId").toString();
            date = new Date(Long.parseLong( extras.get("date").toString()));
            dateValue.setText(sdf.format(date));
        } else {
            commandbutton.setText("Add");
            addOrUpdate = false;
            datepanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commandbutton:
                if(addOrUpdate){
                    update();
                }else {
                    create();
                }
            default:
                break;
        }
    }

    public void update(){

        Message message = new Message();
        message.setTitle(title.getText().toString());
        message.setDetails(details.getText().toString());
        message.setDate(date.getTime());

        DatabaseReference record;
        record = groupDataRef.child(user.getUserId()).child("messages").child(messageId);
        record.setValue(message);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void create(){

        Date date = new Date();
        UUID messageId = UUID.randomUUID();

        Message message = new Message();
        message.setTitle(title.getText().toString());
        message.setDetails(details.getText().toString());
        message.setDate(date.getTime());


        DatabaseReference newRecord;
        newRecord = groupDataRef.child(user.getUserId()).child("messages").child(messageId.toString());
        newRecord.setValue(message);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }




}
