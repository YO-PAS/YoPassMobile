package com.yopass.yopass_nexgo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.nexgo.oaf.apiv3.APIProxy;
import com.nexgo.oaf.apiv3.DeviceEngine;
import com.nexgo.oaf.apiv3.SdkResult;
import com.nexgo.oaf.apiv3.device.printer.AlignEnum;
import com.nexgo.oaf.apiv3.device.printer.BarcodeFormatEnum;
import com.nexgo.oaf.apiv3.device.printer.OnPrintListener;
import com.nexgo.oaf.apiv3.device.printer.Printer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private final String TAG = "PrinterSample";
    private DeviceEngine deviceEngine;
    private Printer printer;

    public static  String accessToken;
    public static  String balance;
    public static String eventID;
    public static  String eventName;
    public static  String eventVenue;
    public static  String terminalID;
    public static  String eventDuration = "08th - 12th June 2022";
    public static  String eventCatalogueOneID;
    public static  String eventCatalogueOne = "Adults";
    public static  String eventCatalogueTwoID;
    public static  String eventCatalogueThreeID;
    public static  String eventCatalogueTwo = "Kids";
    public static  String eventCatalogueThree = "";
    public static  String eventCatalogueOnePrice;
    public static  String eventCatalogueTwoPrice;
    public static  String eventCatalogueThreePrice;
    public static  TextView accountDetails;
    public static  String eventPeriod;
    public static  String status = "Paid";
    public static  String eventCategory ;
    public static String userID;
    public static String names;
//    private String accessToken;
//    private String balance;
//    public static String eventID;
//    private String eventName;
//    private String eventVenue;
//    private String terminalID;
//    private String eventDuration = "08th - 12th June 2022";
//    private String eventCatalogueOneID;
//    private String eventCatalogueOne = "Adults";
//    private String eventCatalogueTwoID;
//    private String eventCatalogueTwo = "";
//    private String eventCatalogueOnePrice;
//    private String eventCatalogueTwoPrice;
//    private TextView accountDetails;
//    private String eventPeriod;
//    private String status = "Paid";
    //private String userID;

    private LocalDateTime paid_date = LocalDateTime.now();
    private String transaction_type = "Cash";
    private int amount = 10000;
    private int opening_bal = 1232;
    private int closing_bal = 1232;
    private int event_id = 1232;
    private int terminal_id = 1232;

    private int batch_id = 1232;
    private int event_category_id = 1232;
    private int unitAmount=10000;
    private String headcount = "1";

    private String eventStart = "8th";
    private String eventEnd = "12th";

    Button firstCategoryButton;
    Button secondCategoryButton;
    Button threeCategoryButton;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putExtra("accessToken", accessToken);
                    startActivity(intent);
                    return true;
                case R.id.navigation_dashboard:
                    Intent ticketintent = new Intent(MainActivity.this, TicketActivity.class);
                    ticketintent.putExtra("accessToken", accessToken);
                    startActivity(ticketintent);
                    return true;
                case R.id.navigation_notifications:
                    Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(logout);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if( getIntent().getExtras() != null) {
            accessToken = getIntent().getStringExtra("accessToken");
            balance = getIntent().getStringExtra("balance");
            userID = getIntent().getStringExtra("userID");
            eventID = getIntent().getStringExtra("eventID");
            eventName = getIntent().getStringExtra("eventName");
            eventVenue = getIntent().getStringExtra("eventVenue");
            terminalID = getIntent().getStringExtra("terminalID");
            eventCatalogueOneID = getIntent().getStringExtra("eventCatalogueOneID");
            eventCatalogueOne = getIntent().getStringExtra("eventCatalogueOne");
            eventCatalogueTwoID = getIntent().getStringExtra("eventCatalogueTwoID");
            eventCatalogueThreeID = getIntent().getStringExtra("eventCatalogueThreeID");
            eventCatalogueTwo = getIntent().getStringExtra("eventCatalogueTwo");
            eventCatalogueThree = getIntent().getStringExtra("eventCatalogueThree");
            eventCatalogueOnePrice = getIntent().getStringExtra("eventCatalogueOnePrice");
            eventCatalogueTwoPrice = getIntent().getStringExtra("eventCatalogueTwoPrice");
            eventCatalogueThreePrice = getIntent().getStringExtra("eventCatalogueThreePrice");
            eventPeriod = getIntent().getStringExtra("eventPeriod");
            names =  getIntent().getStringExtra("names");
        }
            firstCategoryButton = (Button) findViewById(R.id.single_1);
            secondCategoryButton = (Button) findViewById(R.id.single_2);
            threeCategoryButton = (Button) findViewById(R.id.single_3);
            firstCategoryButton.setText("SINGLE-TICKET: " + eventCatalogueOne + "\n AMOUNT: " + eventCatalogueOnePrice);
            secondCategoryButton.setText("SINGLE-TICKET: " + eventCatalogueTwo + "\n AMOUNT: " + eventCatalogueTwoPrice);
            threeCategoryButton.setText("SINGLE-TICKET: " + eventCatalogueThree + "\n AMOUNT: " + eventCatalogueThreePrice);
        // Checks if there is an extra event category
        if (eventCatalogueOne == null || eventCatalogueTwo == null || eventCatalogueOne.isEmpty() || eventCatalogueTwo.isEmpty()) {

            secondCategoryButton.setVisibility(View.GONE);
            threeCategoryButton.setVisibility(View.GONE);
        }
        else if((eventCatalogueTwo != null || !eventCatalogueTwo.isEmpty()) && (eventCatalogueThree.isEmpty() || eventCatalogueThree == null)){
            secondCategoryButton.setVisibility(View.VISIBLE);
            threeCategoryButton.setVisibility(View.GONE);
        }
        else if(eventCatalogueThree != null || !eventCatalogueThree.isEmpty()){
            secondCategoryButton.setVisibility(View.VISIBLE);
            threeCategoryButton.setVisibility(View.VISIBLE);
        }
        else {
            secondCategoryButton.setVisibility(View.VISIBLE);
            threeCategoryButton.setVisibility(View.VISIBLE);
        }
       // Log.d(""+ eventCatalogueThree);
        accountDetails = (TextView)findViewById(R.id.details);
        accountDetails.setText(Html.fromHtml("<h4>Hello "+names+"</h4>\n\n"  +
                "<h3>Account Details</h3>\n" +
                "Event Name: "+eventName +"\n"+
                "<br/>Current Balance: "+balance));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.single_1:
                Intent firstCatalogueIntent = new Intent(MainActivity.this, PrinterActivity.class);
                firstCatalogueIntent.putExtra("accessToken", accessToken);
                firstCatalogueIntent.putExtra("balance", String.valueOf(balance));
                firstCatalogueIntent.putExtra("Status", status);
                firstCatalogueIntent.putExtra("eventName", eventName);
                firstCatalogueIntent.putExtra("eventVenue", eventVenue);
                firstCatalogueIntent.putExtra("terminalID", terminalID);
                firstCatalogueIntent.putExtra("paidDate", paid_date.format(formatter).toString());
                firstCatalogueIntent.putExtra("eventPeriod", eventPeriod);
                firstCatalogueIntent.putExtra("Transcation_type", transaction_type);
                eventCategory = String.valueOf(eventCatalogueOne);
                firstCatalogueIntent.putExtra("eventCategory", String.valueOf(eventCatalogueOne));
                firstCatalogueIntent.putExtra("unitCost", String.valueOf(eventCatalogueOnePrice));
                firstCatalogueIntent.putExtra("headcount", headcount);
                firstCatalogueIntent.putExtra("Opening_bal", String.valueOf(balance));
                firstCatalogueIntent.putExtra("Closing_bal", String.valueOf(balance));
                firstCatalogueIntent.putExtra("eventID", String.valueOf(eventID));
                firstCatalogueIntent.putExtra("userID", String.valueOf(userID));
                firstCatalogueIntent.putExtra("Event_category_id", eventCatalogueOneID);
                firstCatalogueIntent.putExtra("totalTicketAmount", String.valueOf(eventCatalogueOnePrice));
                startActivity(firstCatalogueIntent);
                break;

            case R.id.single_2:
                Intent secondCatalogueIntent = new Intent(MainActivity.this, PrinterActivity.class);
                secondCatalogueIntent.putExtra("accessToken", accessToken);
                secondCatalogueIntent.putExtra("balance", String.valueOf(balance));
                secondCatalogueIntent.putExtra("Status", status);
                secondCatalogueIntent.putExtra("eventName", eventName);
                secondCatalogueIntent.putExtra("eventVenue", eventVenue);
                secondCatalogueIntent.putExtra("terminalID", terminalID);
                secondCatalogueIntent.putExtra("paidDate", paid_date.format(formatter).toString());
                secondCatalogueIntent.putExtra("eventPeriod", eventPeriod);
                secondCatalogueIntent.putExtra("Transcation_type", transaction_type);
                secondCatalogueIntent.putExtra("eventCategory", String.valueOf(eventCatalogueTwo));
                eventCategory = String.valueOf(eventCatalogueTwo);
                secondCatalogueIntent.putExtra("unitCost", String.valueOf(eventCatalogueTwoPrice));
                secondCatalogueIntent.putExtra("headcount", headcount);
                secondCatalogueIntent.putExtra("Opening_bal", String.valueOf(balance));
                secondCatalogueIntent.putExtra("Closing_bal", String.valueOf(balance));
                secondCatalogueIntent.putExtra("eventID", String.valueOf(eventID));
                secondCatalogueIntent.putExtra("userID", String.valueOf(userID));
                secondCatalogueIntent.putExtra("Event_category_id", String.valueOf(eventCatalogueTwoID));
                secondCatalogueIntent.putExtra("totalTicketAmount", String.valueOf(eventCatalogueTwoPrice));
                startActivity(secondCatalogueIntent);
                break;
            case R.id.single_3:
                Intent threeCatalogueIntent = new Intent(MainActivity.this, PrinterActivity.class);
                threeCatalogueIntent.putExtra("accessToken", accessToken);
                threeCatalogueIntent.putExtra("balance", String.valueOf(balance));
                threeCatalogueIntent.putExtra("Status", status);
                threeCatalogueIntent.putExtra("eventName", eventName);
                threeCatalogueIntent.putExtra("eventVenue", eventVenue);
                threeCatalogueIntent.putExtra("terminalID", terminalID);
                threeCatalogueIntent.putExtra("paidDate", paid_date.format(formatter).toString());
                threeCatalogueIntent.putExtra("eventPeriod", eventPeriod);
                threeCatalogueIntent.putExtra("Transcation_type", transaction_type);
                threeCatalogueIntent.putExtra("eventCategory", String.valueOf(eventCatalogueThree));
                eventCategory = String.valueOf(eventCatalogueThree);
                threeCatalogueIntent.putExtra("unitCost", String.valueOf(eventCatalogueThreePrice));
                threeCatalogueIntent.putExtra("headcount", headcount);
                threeCatalogueIntent.putExtra("Opening_bal", String.valueOf(balance));
                threeCatalogueIntent.putExtra("Closing_bal", String.valueOf(balance));
                threeCatalogueIntent.putExtra("eventID", String.valueOf(eventID));
                threeCatalogueIntent.putExtra("userID", String.valueOf(userID));
                threeCatalogueIntent.putExtra("Event_category_id", String.valueOf(eventCatalogueThreeID));
                threeCatalogueIntent.putExtra("totalTicketAmount", String.valueOf(eventCatalogueThreePrice));
                startActivity(threeCatalogueIntent);
                break;
            case R.id.group:
                Intent groupIntent = new Intent(MainActivity.this, GroupActivity.class);
                groupIntent.putExtra("accessToken", accessToken);
                groupIntent.putExtra("balance", String.valueOf(balance));
                groupIntent.putExtra("Status", status);
                groupIntent.putExtra("eventName", eventName);
                groupIntent.putExtra("terminalID", terminalID);
                groupIntent.putExtra("paidDate", paid_date.format(formatter).toString());
                groupIntent.putExtra("eventPeriod", eventPeriod);
                groupIntent.putExtra("Transcation_type", transaction_type);
                groupIntent.putExtra("eventCategory", String.valueOf(eventCatalogueOne));
                eventCategory = String.valueOf(eventCatalogueOne);
                groupIntent.putExtra("unitCost", String.valueOf(eventCatalogueOnePrice));
                groupIntent.putExtra("unitCost", String.valueOf(unitAmount));
                groupIntent.putExtra("headcount", headcount);
                groupIntent.putExtra("Opening_bal", String.valueOf(opening_bal));
                groupIntent.putExtra("Closing_bal", String.valueOf(closing_bal));
                groupIntent.putExtra("eventID", String.valueOf(eventID));
                groupIntent.putExtra("userID", String.valueOf(userID));
                groupIntent.putExtra("Batch_id", String.valueOf(batch_id));
                groupIntent.putExtra("Event_category_id", String.valueOf(eventCatalogueOneID));
                groupIntent.putExtra("totalTicketAmount", String.valueOf(eventCatalogueOnePrice));
                startActivity(groupIntent);
                break;

            default:
                break;
        }
    }



}
