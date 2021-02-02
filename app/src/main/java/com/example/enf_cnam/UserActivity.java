package com.example.enf_cnam;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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
        TextView titre = new TextView(this);
        titre.setText("Informations personnelles : ");
        titre.setPadding(200,40,20,30);
        titre.setTextSize(20);
        titre.setTextColor(Color.BLACK);
        titre.setTypeface(Typeface.DEFAULT_BOLD);
        infoLayout.addView(titre);
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
}