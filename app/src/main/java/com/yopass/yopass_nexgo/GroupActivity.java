package com.yopass.yopass_nexgo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class GroupActivity extends AppCompatActivity {
//    public TextView groupMembers;
    private EditText groupMembers;
    TelephonyManager telephonyManager;
    public TextView totalAmount;
    public RadioButton category1;
    public RadioButton category2;
    private Button confirm;
    private int totalTicketAmount;
    private int unitAmount;
    private String eventName;
    private String ticketNumber;
    private String batchNumber;
    private String date;
    private String cashier = "Paul";
    private String terminal;
    private String headcount;
    private String eventDuration;
    private String accessToken;
    private String status;
    private String paid_date;
    private String transaction_type;
    private int amount;
    private int opening_bal;
    private int balance;
    private int closing_bal;
    private int event_id;
    private int terminal_id;
    private int user_id;
    private String eventCategory;
    private int batch_id;
    private int event_category_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        groupMembers = (EditText)findViewById(R.id.people);
        totalAmount = (TextView)findViewById(R.id.totalAmount);
        confirm = (Button)findViewById(R.id.print);
        TelephonyManager telephonyManager;
       // telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        //terminal = telephonyManager.getDeviceId();
//        telephonyManager.getDeviceId();
//        confirm.setVisibility(View.GONE);
        eventCategory = "";
        accessToken = getIntent().getStringExtra("accessToken");
        eventName = getIntent().getStringExtra("eventName");
        batch_id = Integer.parseInt(getIntent().getStringExtra("Batch_id"));
        terminal_id = Integer.parseInt(getIntent().getStringExtra("terminalID"));
        eventDuration = getIntent().getStringExtra("eventDuration");
        unitAmount = Integer.parseInt(getIntent().getStringExtra("unitCost"));
        headcount = getIntent().getStringExtra("headcount");
        opening_bal = Integer.parseInt(getIntent().getStringExtra("Opening_bal"));
        closing_bal = Integer.parseInt(getIntent().getStringExtra("Closing_bal"));
        transaction_type = getIntent().getStringExtra("Transcation_type");
        event_id = Integer.parseInt(getIntent().getStringExtra("eventID"));
        user_id = Integer.parseInt(getIntent().getStringExtra("userID"));
        event_category_id = Integer.parseInt(getIntent().getStringExtra("Event_category_id"));
        totalTicketAmount = Integer.parseInt(getIntent().getStringExtra("totalTicketAmount"));
        paid_date = getIntent().getStringExtra("paidDate");
        status = getIntent().getStringExtra("Status");
        accessToken = getIntent().getStringExtra("accessToken");
        category1 = (RadioButton) findViewById(R.id.radio_category1);
        category2 = (RadioButton) findViewById(R.id.radio_category2);
        if (MainActivity.eventCatalogueOne == null || MainActivity.eventCatalogueTwo == null || MainActivity.eventCatalogueOne.isEmpty() || MainActivity.eventCatalogueTwo.isEmpty()) {
            category1.setVisibility(View.GONE);
            category1.setChecked(true);
            category1.setText(MainActivity.eventCatalogueOne+getString(R.string.tab)+getString(R.string.tab)+getString(R.string.tab)+ MainActivity.eventCatalogueOnePrice);
        }
        else {
            category2.setVisibility(View.VISIBLE);
            category2.setText(MainActivity.eventCatalogueTwo+getString(R.string.tab)+getString(R.string.tab)+getString(R.string.tab)+ MainActivity.eventCatalogueTwoPrice);
            category1.setText(MainActivity.eventCatalogueOne+getString(R.string.tab)+getString(R.string.tab)+getString(R.string.tab)+ MainActivity.eventCatalogueOnePrice);
            unitAmount = 0;
        }

        groupMembers.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0){
                    totalTicketAmount = unitAmount * Integer.parseInt(groupMembers.getText().toString());
                    headcount = groupMembers.getText().toString();
                    totalAmount.setText("UGX "+String.valueOf(totalTicketAmount));

                }else{
                    totalAmount.setText(String.valueOf(0));
                    totalTicketAmount = 0;
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceId();
                if(totalTicketAmount > 0){
                    Intent intent = new Intent(GroupActivity.this, PrinterActivity.class);
                   // intent.putExtra("accessToken", accessToken);
                   // intent.putExtra("balance", String.valueOf(balance));
                    intent.putExtra("Status", status);
                   // intent.putExtra("eventName", eventName);
                    intent.putExtra("paidDate", paid_date);
                   // intent.putExtra("eventDuration", eventDuration);
                    intent.putExtra("Transcation_type", transaction_type);
                    intent.putExtra("unitCost", String.valueOf(unitAmount));
                    intent.putExtra("headcount", headcount);
                  //  intent.putExtra("Opening_bal", String.valueOf(opening_bal));
                   // intent.putExtra("Closing_bal", String.valueOf(closing_bal));
                  //  intent.putExtra("Event_id", String.valueOf(event_id));
                   // intent.putExtra("Terminal_id", String.valueOf(terminal_id));
                   // intent.putExtra("User_id", String.valueOf(user_id));
                    intent.putExtra("eventCategory", String.valueOf(eventCategory));
                    intent.putExtra("Event_category_id", String.valueOf(event_category_id));
                    intent.putExtra("totalTicketAmount", String.valueOf(totalTicketAmount));
                    startActivity(intent);
                }else{
                    Toast.makeText(GroupActivity.this, "Number of people should be greater than 1!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.O)

    private void deviceId() {
        telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
            return;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
                        return;
                    }
                    terminal = telephonyManager.getDeviceId();
                    Toast.makeText(GroupActivity.this,terminal,Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(GroupActivity.this,"Without permission we check",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        totalTicketAmount= 0;
        totalAmount.setText("UGX "+String.valueOf(totalTicketAmount));
        groupMembers.setText("");
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_category1:
                if (checked)
                    unitAmount =  Integer.parseInt(MainActivity.eventCatalogueOnePrice);
                    totalTicketAmount= 0;
                    eventCategory = MainActivity.eventCatalogueOne;
                    event_category_id = Integer.parseInt(MainActivity.eventCatalogueOneID);
                    break;
            case R.id.radio_category2:
                if (checked)
                    unitAmount =  Integer.parseInt(MainActivity.eventCatalogueTwoPrice);
                    eventCategory = MainActivity.eventCatalogueTwo;
                    event_category_id = Integer.parseInt(MainActivity.eventCatalogueTwoID);
                    break;
        }
    }
}
