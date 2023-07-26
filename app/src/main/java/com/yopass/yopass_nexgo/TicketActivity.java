package com.yopass.yopass_nexgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nexgo.oaf.apiv3.DeviceEngine;
import com.nexgo.oaf.apiv3.SdkResult;
import com.nexgo.oaf.apiv3.device.printer.OnPrintListener;
import com.nexgo.oaf.apiv3.device.printer.Printer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TicketActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ProgressDialog progress;
    private final String TAG = "PrinterSample";
    private DeviceEngine deviceEngine;
    private Printer printer;
    private String accessToken;
    private String balance;
    private TextView details;
    private String eventName = "Tiwa Savage";
    private String status = "Paid";
    private LocalDateTime paid_date = LocalDateTime.now();
    private String transaction_type = "Cash";
    private String amount;
    private String ticket_id;
    private String category_id;
    private String category;
    private int opening_bal = 1232;
    private int closing_bal = 1232;
    private int event_id = 1232;
    private int terminal_id = 1232;
    private int user_id = 10;
    private int batch_id = 1232;
    private int event_category_id = 1232;
    private String eventCategory;
    private int unitAmount=10000;
    private int totalTicketAmount=10000;
    private String headcount = "1";
    private String eventDuration = "08th - 12th June 2022";
    String[] transactionsArray={};
    JSONArray transactions = new JSONArray();
    private String getTransactionsEndpoint = "http://185.247.116.230:5000/transactions/GetById/"+MainActivity.userID;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    // The singleton HTTP client.
    private final OkHttpClient client = new OkHttpClient();

    String[] mobileArray = {"TiccketID: Android\nAmount: 4000\nCategory: EarlyBird","IPhone","WindowsMobile","Blackberry",
            "WebOS","Ubuntu","Windows7","Max OS X","Max OS X","Max OS X","Max OS X","Max OS X"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);

        accessToken = getIntent().getStringExtra("accessToken");
        balance = getIntent().getStringExtra("balance");

      //  mTextMessage = (TextView) findViewById(R.id.message);
        RequestBody body = RequestBody.create("", MediaType.parse("application/json; charset=utf-8"));
       // Log.d("TOKEN: ", accessToken);
        postRequest(getTransactionsEndpoint, body);
        //Log.d("Transactionsreretretretr: ", Arrays.toString(transactionsArray));




    }

    public void postRequest(String postUrl, RequestBody postBody) {
        progress = ProgressDialog.show(this, "Loading transactions...",
                "Please Wait...", true);
        final Request request = new Request.Builder()
                .url(postUrl)
                .get()
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
        // create HTTPClient object
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                call.cancel();
                Log.d("FAIL", e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.sleep(950);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                progress.dismiss();
                            }
                        }).start();
                        Toast.makeText(getApplicationContext(),
                                "Failed to connect to Server, Please try again", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                try {
                    final String serverResponse = response.body().toString().trim();
                    String resstring = response.body().string().trim();
                    final String serverR = resstring.replace("\"", "");
                    JSONArray jsonObjectArray = new JSONArray(resstring.toString());
                    JSONObject transaction = new JSONObject();
                    JSONObject ticketDetails = new JSONObject();
                   // transaction = jsonObjectArray.getJSONObject(0);
                   //
                    transactionsArray = new String[jsonObjectArray.length()-1];
                    transactions = jsonObjectArray;
                    for(int i=0; i < jsonObjectArray.length() -1; i++){
                        transaction = jsonObjectArray.getJSONObject(i);

                        ticket_id = transaction.getString("id");
                        amount = transaction.getString("amount");
                        try {
                            ticketDetails = (JSONObject) transaction.getJSONObject("ticketCat");
                             category = ticketDetails.getString("category_name");
                        }catch (JSONException e){
                           progress.dismiss();
                           e.printStackTrace();
                        }

                        transactionsArray[i] = "Ticket Id : "+ ticket_id +"\n"+"Amount: "+amount+"\nCategory: "+category;
                    }

//                    try{
//                        jsonObject = new JSONObject(resstring);
//                    }catch (JSONException e){
//                        progress.dismiss();
//                        e.printStackTrace();6
//                    }
                    progress.dismiss();
                   // Log.d("JSONRESPONSE:==>",resstring.toString());
//            TODO: Add Network Signal Strength Check
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter adapter = new ArrayAdapter<String>(TicketActivity.this,
                                    R.layout.activity_ticket_listview, R.id.tickets, transactionsArray);

                            ListView listView = (ListView) findViewById(R.id.ticket_list);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedItem = (String) parent.getItemAtPosition(position);
                                    try{
                                        try{
                                        JSONObject transaction = transactions.getJSONObject(position);
                                        JSONObject ticketDetails = transaction.getJSONObject("ticketCat");
                                       // Log.d("Transaction",  transactions.getJSONObject(position).toString());
                                        unitAmount = ticketDetails.getInt("amount");
                                        event_category_id =  ticketDetails.getInt("id");
                                        eventCategory =  ticketDetails.getString("category_name");
                                        batch_id =transaction.getInt("batch_id");
                                        ticket_id = String.valueOf(transaction.getInt("id"));
                                        totalTicketAmount = transaction.getInt("amount");
                                    }catch (JSONException e){
                                        progress.dismiss();
                                        e.printStackTrace();
                                    }
                                        Log.d("Transaction",  transactions.getJSONObject(position).toString());
                                        Intent intent = new Intent(TicketActivity.this, RePrintTicket.class);
                                        intent.putExtra("accessToken", accessToken);
                                        intent.putExtra("Status", status);
                                        intent.putExtra("eventName", MainActivity.eventName);
                                        intent.putExtra("eventVenue", MainActivity.eventVenue);
                                        intent.putExtra("terminalID", MainActivity.terminalID);
                                        intent.putExtra("eventID",MainActivity.eventID);
                                        intent.putExtra("userID", String.valueOf(MainActivity.eventID));
                                        intent.putExtra("eventPeriod", MainActivity.eventPeriod);
                                        intent.putExtra("paidDate", paid_date.format(formatter).toString());

                                        intent.putExtra("batch_id", String.valueOf(batch_id));
                                        intent.putExtra("ticket_id", ticket_id);
                                        intent.putExtra("eventCategory", eventCategory);
                                        intent.putExtra("unitCost", String.valueOf(unitAmount));
                                        headcount =  String.valueOf(totalTicketAmount  / unitAmount);
                                        intent.putExtra("headcount", String.valueOf(headcount));
                                        intent.putExtra("Event_category_id", String.valueOf(event_category_id) );
                                        intent.putExtra("totalTicketAmount", String.valueOf(totalTicketAmount));
                                        startActivity(intent);
                                }catch (JSONException e){
                                    //progress.dismiss();
                                    e.printStackTrace();
                                }

                                }
                            });

                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
                response.body().close();
            }
        });
    }



}
