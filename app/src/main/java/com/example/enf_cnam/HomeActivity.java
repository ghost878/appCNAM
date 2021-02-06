package com.example.enf_cnam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.net.ftp.*;



@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeActivity extends AppCompatActivity {

    private LinearLayout homeLayout;

    private LinearLayout enseignements;

    private TextView hello;

    public static JSONObject uniteClick;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeLayout = (LinearLayout) findViewById(R.id.homeLayout);
        enseignements = (LinearLayout) findViewById(R.id.enseignements);
        hello = (TextView) findViewById(R.id.hello);

        hello.setTextColor(Color.BLACK);
        hello.setPadding(0,30,0,30);
        try {
            hello.setText("Bonjour " + MainActivity.auditeurInfo.getString("PRENOM"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hello.setTextSize(30);
        hello.setTypeface(Typeface.DEFAULT_BOLD);

        for(int i = 0; i < MainActivity.enseignements.length(); i++) {
            LinearLayout unite = new LinearLayout(this);
            unite.setOrientation(LinearLayout.VERTICAL);
            try {
                final JSONObject uniteDetails = MainActivity.enseignements.getJSONObject(i);
                TextView libelle = new TextView(this);
                libelle.setText(uniteDetails.getString("LIBELLE"));
                libelle.setGravity(Gravity.CENTER);
                libelle.setTextColor(Color.BLACK);
                libelle.setTypeface(Typeface.DEFAULT_BOLD);
                TextView code = new TextView(this);
                code.setText(uniteDetails.getString("CODE"));
                code.setGravity(Gravity.CENTER);
                code.setTextColor(Color.BLACK);
                code.setTypeface(Typeface.DEFAULT_BOLD);
                unite.addView(code);
                unite.addView(libelle);
                unite.setGravity(Gravity.CENTER);
                unite.setLayoutParams(new LinearLayout.LayoutParams(300, 400));
                //unite.setPadding(-30,0,-30,0);
                unite.setBackgroundResource(R.drawable.unite);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) unite.getLayoutParams();
                p.setMargins(20, 0, 20, 0);

                unite.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        uniteClick = uniteDetails;
                        Intent moodleActivity = new Intent(HomeActivity.this, MoodleActivity.class);
                        startActivity(moodleActivity);
                    }});

                enseignements.addView(unite);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


    public void viewUserInfo(View v) throws JSONException {
        Intent userActivity = new Intent(HomeActivity.this, UserActivity.class);
        startActivity(userActivity);
    }


    public void viewPlanning(View v) throws JSONException {
        Intent planningActivity = new Intent(HomeActivity.this, PlanningActivity.class);
        startActivity(planningActivity);
    }


    public void viewCursus(View v) {
        Intent cursusActivity = new Intent(HomeActivity.this, CursusActivity.class);
        startActivity(cursusActivity);
    }

    public void viewExamen(View v) {
        Intent examenActivity = new Intent(HomeActivity.this, ExamenActivity.class);
        startActivity(examenActivity);
    }


    public void logout(View v) {
        Intent mainActivity = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(mainActivity);
        MainActivity.token = "";
    }

    public void mail(View v) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=13&ct=1610371321&rver=7.0.6737.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.live.com%2fowa%2f%3fnlp%3d1%26RpsCsrfState%3db3d1dea9-4053-5262-434d-0b14a393acbf&id=292841&aadredir=1&CBCXT=out&lw=1&fl=dob%2cflname%2cwld&cobrandid=90015"));
        viewIntent.setPackage("com.android.chrome");
        startActivity(viewIntent);
    }

    public void viewMap(View v) {
        Intent mainActivity = new Intent(HomeActivity.this, MapActivity.class);
        startActivity(mainActivity);
    }

    public void viewHome(View v) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.token == "") {
            Intent mainActivity = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(mainActivity);
        }
    }

}