package com.example.enf_cnam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Iterator;

public class UserActivity extends AppCompatActivity {

    private LinearLayout infoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        infoLayout = (LinearLayout) findViewById(R.id.infoLayout);

        System.out.println("INFO");


        //infoLayout.removeAllViews();
        final TextView titre = new TextView(this);
        titre.setText("Informations personnelles : ");
        titre.setPadding(200,40,20,30);
        titre.setTextSize(20);
        titre.setTextColor(Color.BLACK);
        titre.setTypeface(Typeface.DEFAULT_BOLD);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                infoLayout.addView(titre);
            }
        });

        Iterator<String> keys = MainActivity.auditeurInfo.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            if (key.length() > 2) {
                TextView data = new TextView(this);
                try {
                    if(!(MainActivity.auditeurInfo.getString(key).equals("null"))) {
                        data.setText(key + " : " + MainActivity.auditeurInfo.getString(key));
                    } else {
                        data.setText(key + " : " + "Non défini");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                data.setTextColor(Color.BLACK);
                data.setPadding(20,10,20,10);
                infoLayout.addView(data);

            }
        }



    }

    public void viewHome(View v) {
        Intent homeActivity = new Intent(UserActivity.this, HomeActivity.class);
        startActivity(homeActivity);
    }

    public void logout(View v) {
        Intent mainActivity = new Intent(UserActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }

    public void mail(View v) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=13&ct=1610371321&rver=7.0.6737.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.live.com%2fowa%2f%3fnlp%3d1%26RpsCsrfState%3db3d1dea9-4053-5262-434d-0b14a393acbf&id=292841&aadredir=1&CBCXT=out&lw=1&fl=dob%2cflname%2cwld&cobrandid=90015"));
        viewIntent.setPackage("com.android.chrome");
        startActivity(viewIntent);
    }
}