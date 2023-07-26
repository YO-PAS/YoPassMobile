package com.yopass.yopass_nexgo;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nexgo.oaf.apiv3.APIProxy;
import com.nexgo.oaf.apiv3.DeviceEngine;
import com.nexgo.oaf.apiv3.SdkResult;
import com.nexgo.oaf.apiv3.device.printer.AlignEnum;
import com.nexgo.oaf.apiv3.device.printer.DotMatrixFontEnum;
import com.nexgo.oaf.apiv3.device.printer.FontEntity;
import com.nexgo.oaf.apiv3.device.printer.GrayLevelEnum;
import com.nexgo.oaf.apiv3.device.printer.OnPrintListener;
import com.nexgo.oaf.apiv3.device.printer.Printer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;

import static com.android.volley.Request.Method.GET;
import static com.android.volley.Request.Method.POST;

public class RePrintTicket extends AppCompatActivity {
    private Button login;
    private TextView signInDesc;
    private EditText username;
    private EditText password;
    private ProgressDialog progress;
    private String accessToken;
    private String userID;
    private String eventID = "2";
    private String eventName;
    private String eventVenue = "Kampala";
    private String eventCatalogueOneID;
    private String eventCatalogueOne = "Adults";
    private String eventCatalogueTwoID;
    private String eventCatalogueThreeID;
    private String eventCatalogueTwo = "";
    private String eventCatalogueThree = "";
    private boolean eventCatalogue = false;
    private double eventCatalogueOnePrice = 10000;
    private double eventCatalogueTwoPrice=0;
    private double eventCatalogueThreePrice=0;
    private String eventStart;
    private String eventEnd;
    private String eventPeriod;
    private String balance;
    private String user_name;
    private String user_password;
    private String terminalID;
    private String endpoint = "http://185.247.116.230:5000/users/authenticate";
    private int reset = 0;
    private String names;

    private DeviceEngine deviceEngine;
    private Printer printer;
    private final int FONT_SIZE_SMALL = 20;
    private final int FONT_SIZE_NORMAL = 24;
    private final int FONT_SIZE_BIG = 30;
    private final int PAPER_WIDTH = 5;
    private FontEntity fontSmall = new FontEntity(DotMatrixFontEnum.CH_SONG_20X20, DotMatrixFontEnum.ASC_SONG_8X16);
    private FontEntity fontNormal = new FontEntity(DotMatrixFontEnum.CH_SONG_24X24, DotMatrixFontEnum.ASC_SONG_12X24);
    private FontEntity fontBold = new FontEntity(DotMatrixFontEnum.CH_SONG_24X24, DotMatrixFontEnum.ASC_SONG_BOLD_16X24);
    private FontEntity fontBig = new FontEntity(DotMatrixFontEnum.CH_SONG_24X24, DotMatrixFontEnum.ASC_SONG_12X24, false, true);
    private String eventCategory;
    public  String ticketNumber = "232";
    public String batchNumber= "343";
    private String paid_date;
    private String cashier;
    private String terminal;
    private String adults = "1";
    private String unitcost;
    private String eventDuration;
    private String totalTicketAmount;
    private TextView printer_text;
    private SimpleDateFormat dmyFormat;
    private String status;
    private String transcation_type;
    private String openingBal;
    private String closingBal;
    private String eventId;
    private String userId;
    private String event_category_id;
    private String apiResMessage;
    private String responseCode;
    private String ServerResponse;
    Bitmap bitmap;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private LocalDateTime paidDate = LocalDateTime.now();

    // The singleton HTTP client.
    private final OkHttpClient client = new OkHttpClient();

    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reprint_ticket);
        //  Log.d("static ",MainActivity.eventID);
        eventName = MainActivity.eventName;
        eventVenue = MainActivity.eventVenue;
        eventCategory = getIntent().getStringExtra("eventCategory");
//        batchNumber = getIntent().getStringExtra("Batch_id");
        terminal = MainActivity.terminalID;
        eventDuration = MainActivity.eventPeriod;
        paid_date =  getIntent().getStringExtra("paidDate");
        unitcost = getIntent().getStringExtra("unitCost");
        adults = getIntent().getStringExtra("headcount");
        openingBal = MainActivity.balance;
        closingBal = MainActivity.balance;
        transcation_type = getIntent().getStringExtra("Transcation_type");
        eventId = MainActivity.eventID;
        userId = MainActivity.userID;
        event_category_id = getIntent().getStringExtra("Event_category_id");
        totalTicketAmount = getIntent().getStringExtra("totalTicketAmount");
        ticketNumber = getIntent().getStringExtra("ticket_id");
        batchNumber = getIntent().getStringExtra("batch_id");
        status = MainActivity.status;
        accessToken = MainActivity.accessToken;

        login = (Button) findViewById(R.id.login_button);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

//        signInDesc.setText(Html.fromHtml("<h2>Welcome to Lawuna</h2>\n"
//                +" Conserving and Rehabilitating the Environment"));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = ProgressDialog.show(RePrintTicket.this, "Signing in",
                        "Please Wait...", true);
//              Gets string format of input
                user_name = username.getText().toString();

                user_password = password.getText().toString();
                if(user_password.equals("0202")){

                    rePrintTicket();

                }else{
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),
                            "FAILED, INSUFFICIENT PREVILEGES", Toast.LENGTH_LONG).show();
                    Intent logout = new Intent(RePrintTicket.this, LoginActivity.class);
                    startActivity(logout);
                   return;
                }

            }
        });
    }
    public void postRequest(String postUrl, RequestBody postBody) {

        final Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept","application/json")
                .build();
        // create HTTPClient object
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                call.cancel();
                //Log.d("FAIL", e.getMessage());

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
                    final JSONObject jsonObject = new JSONObject(resstring);
                    final int accessForbidden = 400;
                    final int accessSuccessful = 200;
                    JSONArray eventCatalogueList = new JSONArray();
                    JSONObject firstCatalogue = new JSONObject();
                    JSONObject secondCatalogue = new JSONObject();
                    JSONObject threeCatalogue = new JSONObject();
                    JSONObject eventDetails = new JSONObject();
                    JSONObject terminalDetails = new JSONObject();
                    DateTimeFormatter dateObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
                    // Gets event details
                    try{


                        eventDetails = jsonObject.getJSONObject("eventDetails");
                        terminalDetails = jsonObject.getJSONObject("terminalDetails");
                    }catch(JSONException e){
                        progress.dismiss();
                        e.printStackTrace();
                    }
//                    Gets the event catalogue
                    try{
                        names = jsonObject.getString("user_name");
                        eventCatalogueList = new JSONArray(jsonObject.getString("inventCatlist"));
                        if (eventCatalogueList.length() == 2){
                            firstCatalogue = eventCatalogueList.getJSONObject(0);
                            eventCatalogueOneID = firstCatalogue.getString("id");
                            eventCatalogueOne = firstCatalogue.getString("category_name");
                            eventCatalogueOnePrice = firstCatalogue.getDouble("amount");

                            secondCatalogue = eventCatalogueList.getJSONObject(1);
                            eventCatalogueTwoID = secondCatalogue.getString("id");
                            eventCatalogueTwo = secondCatalogue.getString("category_name");
                            eventCatalogueTwoPrice = secondCatalogue.getDouble("amount");
                        }
                        else if (eventCatalogueList.length() > 2){
                            firstCatalogue = eventCatalogueList.getJSONObject(0);
                            eventCatalogueOneID = firstCatalogue.getString("id");
                            eventCatalogueOne = firstCatalogue.getString("category_name");
                            eventCatalogueOnePrice = firstCatalogue.getDouble("amount");

                            secondCatalogue = eventCatalogueList.getJSONObject(1);
                            eventCatalogueTwoID = secondCatalogue.getString("id");
                            eventCatalogueTwo = secondCatalogue.getString("category_name");
                            eventCatalogueTwoPrice = secondCatalogue.getDouble("amount");

                            threeCatalogue = eventCatalogueList.getJSONObject(2);
                            eventCatalogueThreeID = threeCatalogue.getString("id");
                            eventCatalogueThree = threeCatalogue.getString("category_name");
                            eventCatalogueThreePrice = threeCatalogue.getDouble("amount");
                        }
                        else{
                            firstCatalogue = eventCatalogueList.getJSONObject(0);
                            eventCatalogueOne = firstCatalogue.getString("category_name");
                            eventCatalogueOnePrice = firstCatalogue.getDouble("amount");
                        }
                    }catch(JSONException e){
                        progress.dismiss();
                        e.printStackTrace();
                    }
                    try{
                        accessToken = jsonObject.getString("token");
                        balance = jsonObject.getString("balance");
                        userID = jsonObject.getString("id");

                        eventID = eventDetails.getString("id");
                        eventName = eventDetails.getString("eventTitle");
                        eventVenue = eventDetails.getString("venue");
                        terminalID = terminalDetails.getString("id");
                        eventStart = dateObj.format(LocalDateTime.parse(eventDetails.getString("startDate")));
//                        eventStart = eventDetails.getString("startDate");
                        eventEnd = dateObj.format(LocalDateTime.parse(eventDetails.getString("endDate")));
//                        eventEnd = eventDetails.getString("endDate");
                        eventPeriod = eventStart+" - "+eventEnd;
                    } catch(Exception e) {
                        progress.dismiss();
                        e.printStackTrace();
                    }
                   // Log.d("RESPONSE:==>",resstring);
                   // Log.d("EVENTNAME:==>",eventDetails.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
//                                jsonObject.getString("token").isEmpty();
                                if(jsonObject.getInt("responseCode") == accessForbidden){
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(),
                                            "Wrong username or password", Toast.LENGTH_LONG).show();
                                }else if(jsonObject.getInt("responseCode") == accessSuccessful) {

                                    new Thread(new Runnable() {
                                        public void run() {
                                            try {
                                                Thread.sleep(1000);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            progress.dismiss();
                                        }
                                    }).start();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),
                                                    "Processing Data", Toast.LENGTH_LONG).show();
                                        }
                                    },950);
                                    reset = jsonObject.getInt("isReset");
                                   // Log.d("RESET FLAG:==>",String.valueOf(reset));
                                    if(String.valueOf(reset).trim().equalsIgnoreCase("0")) {
                                        //redirect user to reset the user
                                        Intent intent = new Intent(RePrintTicket.this, MainActivity.class);
                                        intent.putExtra("accessToken", accessToken);
                                        intent.putExtra("userid", userID);
                                        startActivity(intent);
                                    }else
                                    {
                                        // Access Token
                                        Intent intent = new Intent(RePrintTicket.this, MainActivity.class);
                                        intent.putExtra("accessToken", accessToken);
                                        intent.putExtra("balance", balance);
                                        intent.putExtra("userID", userID);
                                        intent.putExtra("eventID", eventID);
                                        intent.putExtra("eventName", eventName);
                                        intent.putExtra("eventVenue", eventVenue);
                                        intent.putExtra("terminalID", terminalID);
                                        intent.putExtra("names", names);
                                        intent.putExtra("eventCatalogueOneID", eventCatalogueOneID);
                                        intent.putExtra("eventCatalogueOne", eventCatalogueOne);
                                        intent.putExtra("eventCatalogueTwoID", eventCatalogueTwoID);
                                        intent.putExtra("eventCatalogueTwo", eventCatalogueTwo);
                                        intent.putExtra("eventCatalogueOnePrice", String.valueOf((int) eventCatalogueOnePrice));
                                        intent.putExtra("eventCatalogueTwoPrice", String.valueOf((int) eventCatalogueTwoPrice));
                                        intent.putExtra("eventCatalogueThreeID", eventCatalogueThreeID);
                                        intent.putExtra("eventCatalogueThree", eventCatalogueThree);
                                        intent.putExtra("eventCatalogueThreePrice", String.valueOf((int) eventCatalogueThreePrice));
                                        intent.putExtra("eventPeriod", eventPeriod);
                                        startActivity(intent);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                response.body().close();
            }
        });
    }
    public void rePrintTicket(){



            try{
                deviceEngine = APIProxy.getDeviceEngine(RePrintTicket.this);
                printer = deviceEngine.getPrinter();

                    printer.initPrinter();                              //init printer
                    printer.setTypeface(Typeface.DEFAULT);            //change print type
                    printer.setLetterSpacing(1);                        //change the line space between each line
                    printer.setGray(GrayLevelEnum.LEVEL_2);             //change print gray
                   // bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.uma1);
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.yopaslogo);
                    printer.appendImage(bitmap, AlignEnum.CENTER);      //append image
                  //  printer.appendPrnStr("\n", FONT_SIZE_NORMAL, AlignEnum.LEFT, false);
                   // printer.appendPrnStr("------------------------------------", FONT_SIZE_NORMAL, AlignEnum.CENTER, false);
                    printer.appendPrnStr(getString(R.string.print_TIN),FONT_SIZE_SMALL, AlignEnum.CENTER,true);
                    printer.appendPrnStr(getString(R.string.print_customercare), FONT_SIZE_SMALL, AlignEnum.CENTER, false);
                    printer.appendPrnStr("------------------------------------", FONT_SIZE_SMALL, AlignEnum.CENTER, false);
                    printer.appendPrnStr(getString(R.string.print_receiptno), ticketNumber, FONT_SIZE_NORMAL, false);
                    printer.appendPrnStr(getString(R.string.print_tradedate), paid_date, FONT_SIZE_NORMAL, false);
                    printer.appendPrnStr(getString(R.string.print_terminalno), terminal, FONT_SIZE_NORMAL, false);
                    printer.appendPrnStr("------------------------------------", FONT_SIZE_SMALL, AlignEnum.CENTER, false);
                    printer.appendPrnStr(eventName, FONT_SIZE_BIG, AlignEnum.CENTER, true);
                    printer.appendPrnStr(getString(R.string.print_total_amount), totalTicketAmount+" UGX", FONT_SIZE_NORMAL, false);
                  //  printer.appendPrnStr("\n", FONT_SIZE_NORMAL, AlignEnum.LEFT, false);
                    printer.appendPrnStr(batchNumber, FONT_SIZE_NORMAL, AlignEnum.CENTER, true);
                    printer.appendPrnStr(adults+" "+getString(R.string.print_adults), FONT_SIZE_NORMAL, AlignEnum.CENTER, false);
                    printer.appendPrnStr(eventCategory, FONT_SIZE_NORMAL, AlignEnum.CENTER, false);
                    printer.appendPrnStr(paidDate.format(formatter).toString(), FONT_SIZE_NORMAL, AlignEnum.CENTER, false);
                   // printer.appendPrnStr("------------------------------------", FONT_SIZE_NORMAL, AlignEnum.CENTER, false);
                    printer.appendQRcode(getString(R.string.print_qrcode)+ticketNumber, 150, AlignEnum.CENTER);
                    printer.appendPrnStr("Thank you for attending this event", FONT_SIZE_SMALL, AlignEnum.CENTER, false);
                    printer.appendPrnStr(eventDuration, FONT_SIZE_SMALL, AlignEnum.CENTER, false);
                    printer.appendPrnStr(eventVenue, FONT_SIZE_SMALL, AlignEnum.CENTER, false);
//                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mawokota5);
//                    printer.appendImage(bitmap, AlignEnum.CENTER);
                    printer.appendPrnStr("Printed by YoPas", FONT_SIZE_NORMAL, AlignEnum.CENTER, true);
                    printer.appendPrnStr("\n", FONT_SIZE_SMALL, AlignEnum.LEFT, false);
                    printer.appendPrnStr("\n", FONT_SIZE_SMALL, AlignEnum.LEFT, false);
//                printer.appendBarcode(getString(R.string.print_barcode), 50, 0, 2, BarcodeFormatEnum.CODE_128, AlignEnum.CENTER);

                    printer.appendPrnStr("\n", FONT_SIZE_SMALL, AlignEnum.LEFT, false);
                    // printer.appendPrnStr("\n", FONT_SIZE_NORMAL, AlignEnum.LEFT, false);

            }catch (Exception e){
                progress.dismiss();
                e.printStackTrace();
            }
//
                    try {
                            // Print Result
                            printer.startPrint(false, new OnPrintListener() {       //roll paper or not
                                @Override
                                public void onPrintResult(final int retCode) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RePrintTicket.this, retCode + "", Toast.LENGTH_SHORT).show();

                                            if(printer.getStatus() == SdkResult.Success){

                                                JSONObject loginData = new JSONObject();
                                                try{
                                                    getData();
                                                    loginData.put("username", LoginActivity.user);
                                                    loginData.put("password", LoginActivity.userpwd);
                                                }catch (JSONException err){
                                                    err.printStackTrace();
                                                }
                                                RequestBody body = RequestBody.create(loginData.toString(), MediaType.parse("application/json; charset=utf-8"));
                                                //Log.d("JSONDATA:==>",loginData.toString());
                                                 postRequest(endpoint, body);
                                            }
                                        }
                                    });
                                }
                            });



                    } catch (Exception e) {
                        e.printStackTrace();
                    }


        }
    // Get Request For JSONObject
    public void  getData(){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            String url = getString(R.string.print_qrcode)+"RESET_"+ticketNumber;
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Toast.makeText(getApplicationContext(), "I am OK !" + response.toString(), Toast.LENGTH_LONG).show();
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void LogTicket(String ticketNumber){

        final String ticketNo = ticketNumber.trim();
        String url = "";

        StringRequest stringRequest = new StringRequest(POST, url,
                 new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RePrintTicket.this,response,Toast.LENGTH_LONG).show();


                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RePrintTicket.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("ticketNumber",ticketNo);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
