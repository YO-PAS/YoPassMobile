package com.yopass.yopass_nexgo;

import android.app.ProgressDialog;
import android.content.Intent;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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

public class LoginActivity extends AppCompatActivity {
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
    public static String user;
    public static String userpwd;
    // The singleton HTTP client.
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        login = (Button) findViewById(R.id.login_button);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

//        signInDesc.setText(Html.fromHtml("<h2>Welcome to Lawuna</h2>\n"
//                +" Conserving and Rehabilitating the Environment"));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject loginData = new JSONObject();
//              Gets string format of input
                user_name = username.getText().toString();
                user = user_name;
//              Log.d("USERNAME:==>",user_name);
                user_password = password.getText().toString();
                userpwd = password.getText().toString();
                try{
                    loginData.put("username", user_name);
                    loginData.put("password", user_password);
                }catch (JSONException err){
                    err.printStackTrace();
                }
                RequestBody body = RequestBody.create(loginData.toString(), MediaType.parse("application/json; charset=utf-8"));
                Log.d("JSONDATA:==>",loginData.toString());
                postRequest(endpoint, body);
            }
        });
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        progress = ProgressDialog.show(this, "Signing in",
                "Please Wait...", true);
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
                    Log.d("RESPONSE:==>",resstring);
                    Log.d("EVENTNAME:==>",eventDetails.toString());
//                    Log.d("EVENTCATALOGUELIST:==>",eventCatalogueList.toString());
//                    Log.d("FIRSTEVENTCATALOGUE:==>",firstCatalogue.toString());
                   // Log.d("EVENTPERIOD:==>",eventPeriod);
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
                                    Log.d("RESET FLAG:==>",String.valueOf(reset));
                                    if(String.valueOf(reset).trim().equalsIgnoreCase("0")) {
                                        //redirect user to reset the user
                                        Intent intent = new Intent(LoginActivity.this, Reset.class);
                                        intent.putExtra("accessToken", accessToken);
                                        intent.putExtra("userid", userID);
                                        startActivity(intent);
                                    }else
                                    {
                                        // Access Token
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
//                            if (success.equals(signed)){
//                                String email = null;
//                                String username = null;
//                                try {
//                                    username = jsonObject.getString("username");
//                                    email = jsonObject.getString("email");
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
                                // Creating user login session
//                                session.createUserLoginSession(accessToken);
//                                Log.d("HERE:==>",success);


//                            }else {
////                                progress.dismiss();
//                                Toast.makeText(getApplicationContext(),
//                                        "Something went wrong", Toast.LENGTH_LONG).show();
//                            }
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
