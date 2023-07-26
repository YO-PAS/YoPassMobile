package com.yopass.yopass_nexgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nexgo.oaf.apiv3.APIProxy;
import com.nexgo.oaf.apiv3.DeviceEngine;
import com.nexgo.oaf.apiv3.device.printer.AlignEnum;
import com.nexgo.oaf.apiv3.SdkResult;
import com.nexgo.oaf.apiv3.device.printer.BarcodeFormatEnum;
import com.nexgo.oaf.apiv3.device.printer.DotMatrixFontEnum;
import com.nexgo.oaf.apiv3.device.printer.FontEntity;
import com.nexgo.oaf.apiv3.device.printer.GrayLevelEnum;
import com.nexgo.oaf.apiv3.device.printer.OnPrintListener;
import com.nexgo.oaf.apiv3.device.printer.Printer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PrinterActivity extends AppCompatActivity {
    private DeviceEngine deviceEngine;
    private ProgressDialog progress;
    private Printer printer;
    private final int FONT_SIZE_SMALL = 20;
    private final int FONT_SIZE_NORMAL = 24;
    private final int FONT_SIZE_BIG = 30;
    private final int PAPER_WIDTH = 5;
    private FontEntity fontSmall = new FontEntity(DotMatrixFontEnum.CH_SONG_20X20, DotMatrixFontEnum.ASC_SONG_8X16);
    private FontEntity fontNormal = new FontEntity(DotMatrixFontEnum.CH_SONG_24X24, DotMatrixFontEnum.ASC_SONG_12X24);
    private FontEntity fontBold = new FontEntity(DotMatrixFontEnum.CH_SONG_24X24, DotMatrixFontEnum.ASC_SONG_BOLD_16X24);
    private FontEntity fontBig = new FontEntity(DotMatrixFontEnum.CH_SONG_24X24, DotMatrixFontEnum.ASC_SONG_12X24, false, true);
    private String eventName;
    private String eventVenue;
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
    private String accessToken;
    private String responseCode;
   private String ServerResponse;
    Bitmap bitmap;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private LocalDateTime paidDate = LocalDateTime.now();
    private String endpoint = "http://185.247.116.230:5000/transactions/createtransaction";

    // The singleton HTTP client.
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
      //  Log.d("static ",MainActivity.eventID);
        eventName = MainActivity.eventName;
        eventVenue = MainActivity.eventVenue;
        eventCategory = getIntent().getStringExtra("eventCategory");
//        batchNumber = getIntent().getStringExtra("Batch_id");
        terminal = MainActivity.terminalID;
        eventDuration =MainActivity.eventPeriod;
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
        status = MainActivity.status;
        accessToken = MainActivity.accessToken;

//        eventName = getIntent().getStringExtra("eventName");
//        eventVenue = getIntent().getStringExtra("eventVenue");
//        eventCategory = getIntent().getStringExtra("eventCategory");
////      batchNumber = getIntent().getStringExtra("Batch_id");
//        terminal = getIntent().getStringExtra("terminalID");
//        eventDuration = getIntent().getStringExtra("eventPeriod");
//        paid_date = getIntent().getStringExtra("paidDate");
//        unitcost = getIntent().getStringExtra("unitCost");
//        adults = getIntent().getStringExtra("headcount");
//        openingBal = getIntent().getStringExtra("Opening_bal");
//        closingBal = getIntent().getStringExtra("Closing_bal");
//        transcation_type = getIntent().getStringExtra("Transcation_type");
//        eventId = getIntent().getStringExtra("eventID");
//        userId = getIntent().getStringExtra("userID");
//        event_category_id = getIntent().getStringExtra("Event_category_id");
//        totalTicketAmount = getIntent().getStringExtra("totalTicketAmount");
//        status = getIntent().getStringExtra("Status");
//        accessToken = getIntent().getStringExtra("accessToken");
//
        printer_text = (TextView) findViewById(R.id.printer_text);
        printer_text.setText(Html.fromHtml("<h2>Ticket Details</h2>\n" +
                "Event Name: "+eventName+
                "<br/>Date: "+paid_date +
                "<br/>Terminal: "+terminal +
                "<br/>Event Duration: "+eventDuration +
                "<br/>Persons: "+adults +
                "<br/>Category: "+eventCategory +
                "<br/>Unit Cost: "+unitcost +
                "<br/>Total Ticket Cost: "+totalTicketAmount+"\n"));


    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.print_sale_order:

                JSONObject loginData = new JSONObject();
                try{
                    loginData.put("Status", status);
                    loginData.put("Paid_date", paid_date);
                    loginData.put("Transcation_type", transcation_type);
                    loginData.put("Amount", totalTicketAmount);
                    loginData.put("Opening_bal", openingBal);
                    loginData.put("Closing_bal", closingBal);
                    loginData.put("Event_id", eventId);
                    loginData.put("Terminal_id", terminal);
                    loginData.put("User_id", userId);
                    loginData.put("Batch_id", batchNumber);
                    loginData.put("ticket_category_id", event_category_id);
                  //  Log.d("event_category_id :==> ",event_category_id.toString());
                }catch (JSONException err){
                    err.printStackTrace();
                }
                RequestBody body = RequestBody.create(loginData.toString(), MediaType.parse("application/json; charset=utf-8"));


                postRequest(endpoint, body);

               // Log.d("REQUESTBODY: --->",resp);
                break;
        }
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        progress = ProgressDialog.show(this, "generating ticket",
                "Please Wait...", true);

        final Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
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
                    //PrinterActivity.this.ServerResponse =response.body().string().trim();
                    String resstring = response.body().string().trim();
                    //final String serverR = resstring.replace("\"", "");
                    final JSONObject jsonObject = new JSONObject(resstring);
                    apiResMessage ="";
                    try{
                        deviceEngine = APIProxy.getDeviceEngine(PrinterActivity.this);
                        printer = deviceEngine.getPrinter();
                        apiResMessage = jsonObject.getString("message");
                        responseCode = String.valueOf(jsonObject.getInt("responseCode"));
                        if(responseCode.equalsIgnoreCase("200")){
                            closingBal= String.valueOf(jsonObject.getInt("closing_bal"));
                            batchNumber = String.valueOf(jsonObject.getInt("batch_id")) ;
                            PrinterActivity.this.ticketNumber = String.valueOf(jsonObject.getInt("id")) ;
                           // Log.d("RESPONSE-ticketNumber:==>",""+ticketNumber);
                            printer.initPrinter();                              //init printer
                            printer.setTypeface(Typeface.DEFAULT);            //change print type
                            //printer.setLetterSpacing(5);
                            // change the line space between each line
                            printer.setLetterSpacing(1);
                            printer.setGray(GrayLevelEnum.LEVEL_2);             //change print gray
                           // bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.uma1);
                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.yopaslogo);
                            printer.appendImage(bitmap, AlignEnum.CENTER);      //append image
                            //printer.appendPrnStr("\n", FONT_SIZE_NORMAL, AlignEnum.LEFT, false);
                           // printer.appendPrnStr("------------------------------------", FONT_SIZE_NORMAL, AlignEnum.CENTER, false);
                           // printer.appendPrnStr(getString(R.string.print_TIN),FONT_SIZE_BIG, AlignEnum.CENTER,true);
                            printer.appendPrnStr(getString(R.string.print_TIN),FONT_SIZE_SMALL, AlignEnum.CENTER,true);
                            printer.appendPrnStr(getString(R.string.print_customercare), FONT_SIZE_SMALL, AlignEnum.CENTER, false);
                            printer.appendPrnStr("------------------------------------", FONT_SIZE_SMALL, AlignEnum.CENTER, false);
                            printer.appendPrnStr(getString(R.string.print_receiptno), ticketNumber, FONT_SIZE_NORMAL, false);
                            printer.appendPrnStr(getString(R.string.print_tradedate), paid_date, FONT_SIZE_NORMAL, false);
                            printer.appendPrnStr(getString(R.string.print_terminalno), terminal, FONT_SIZE_NORMAL, false);
                            printer.appendPrnStr("------------------------------------", FONT_SIZE_SMALL, AlignEnum.CENTER, false);
                            printer.appendPrnStr(eventName, FONT_SIZE_NORMAL, AlignEnum.CENTER, true);
                            printer.appendPrnStr(getString(R.string.print_total_amount), totalTicketAmount+" UGX", FONT_SIZE_NORMAL, false);
                           // printer.appendPrnStr("\n", FONT_SIZE_NORMAL, AlignEnum.LEFT, false);
                            printer.appendPrnStr(batchNumber, FONT_SIZE_NORMAL, AlignEnum.CENTER, true);
                            printer.appendPrnStr(adults+" "+getString(R.string.print_adults), FONT_SIZE_NORMAL, AlignEnum.CENTER, false);
                            printer.appendPrnStr(eventCategory, FONT_SIZE_NORMAL, AlignEnum.CENTER, false);
                            printer.appendPrnStr(paidDate.format(formatter).toString(), FONT_SIZE_NORMAL, AlignEnum.CENTER, false);
                            //printer.appendPrnStr("------------------------------------", FONT_SIZE_NORMAL, AlignEnum.CENTER, false);
                            printer.appendQRcode(getString(R.string.print_qrcode)+ticketNumber, 150, AlignEnum.CENTER);
                            printer.appendPrnStr("Thank you for attending this event", FONT_SIZE_SMALL, AlignEnum.CENTER, false);
                            printer.appendPrnStr(eventDuration, FONT_SIZE_SMALL, AlignEnum.CENTER, false);
                            printer.appendPrnStr(eventVenue, FONT_SIZE_SMALL, AlignEnum.CENTER, false);
//                            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mawokota5);
//                            printer.appendImage(bitmap, AlignEnum.CENTER);
                            printer.appendPrnStr("Printed by YoPas", FONT_SIZE_NORMAL, AlignEnum.CENTER, true);
                            printer.appendPrnStr("\n", FONT_SIZE_NORMAL, AlignEnum.LEFT, false);
                            printer.appendPrnStr("\n", FONT_SIZE_NORMAL, AlignEnum.LEFT, false);
//                printer.appendBarcode(getString(R.string.print_barcode), 50, 0, 2, BarcodeFormatEnum.CODE_128, AlignEnum.CENTER);

                            printer.appendPrnStr("\n", FONT_SIZE_NORMAL, AlignEnum.LEFT, false);
                           // printer.appendPrnStr("\n", FONT_SIZE_NORMAL, AlignEnum.LEFT, false);
                        }
                    }catch (JSONException e){
                        progress.dismiss();
                        e.printStackTrace();
                    }
//                    ticketNumber = jsonObject.getString("ticket_no");
//                    date = jsonObject.getString("paid_date");
                    //Log.d("RESPONSE:==>",""+response.isSuccessful());


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(apiResMessage.isEmpty()){
                                    progress.dismiss();
                                    Toast.makeText(getApplicationContext(),
                                            "Something went wrong", Toast.LENGTH_LONG).show();
                                }else {
                                    //ticketNumber ="11111114";

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

                                    Toast.makeText(getApplicationContext(),
                                            apiResMessage, Toast.LENGTH_LONG).show();
                                    // Print Result
                                    printer.startPrint(false, new OnPrintListener() {       //roll paper or not
                                        @Override
                                        public void onPrintResult(final int retCode) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(PrinterActivity.this, retCode + "", Toast.LENGTH_SHORT).show();

                                                    if(printer.getStatus() == SdkResult.Success){
                                                        Intent intent = new Intent(PrinterActivity.this, MainActivity.class);
                                                        intent.putExtra("accessToken", accessToken);
                                                       // intent.putExtra("accessToken", accessToken);
                                                        intent.putExtra("balance", closingBal);
                                                        intent.putExtra("userID", userId);
                                                        intent.putExtra("eventID", eventId);
                                                        intent.putExtra("eventName", eventName);
                                                        intent.putExtra("eventVenue", eventVenue);
                                                        intent.putExtra("terminalID", terminal);
                                                        intent.putExtra("eventCatalogueOneID", MainActivity.eventCatalogueOneID);
                                                        intent.putExtra("eventCatalogueThreeID", MainActivity.eventCatalogueThreeID);
                                                        intent.putExtra("eventCatalogueOne", MainActivity.eventCatalogueOne);
                                                        intent.putExtra("eventCatalogueTwoID", MainActivity.eventCatalogueTwoID);
                                                        intent.putExtra("eventCatalogueTwo", MainActivity.eventCatalogueTwo);
                                                        intent.putExtra("eventCatalogueThree", MainActivity.eventCatalogueThree);
                                                        intent.putExtra("eventCatalogueOnePrice", MainActivity.eventCatalogueOnePrice);
                                                        intent.putExtra("eventCatalogueThreePrice", MainActivity.eventCatalogueThreePrice);
                                                        intent.putExtra("eventCatalogueTwoPrice", MainActivity.eventCatalogueTwoPrice);
                                                        intent.putExtra("eventPeriod", MainActivity.eventPeriod);
                                                        intent.putExtra("names", MainActivity.names);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                        }
                                    });


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
                // Access Token
//                 Intent intent = new Intent(PrinterActivity.this, MainActivity.class);
//                 intent.putExtra("accessToken", accessToken);
//                intent.putExtra("balance", closingBal);
//                startActivity(intent);
                response.body().close();
            }
        });


    }


}
