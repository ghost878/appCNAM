package com.example.enf_cnam;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LinearLayout header = (LinearLayout)findViewById(R.id.header);
        //header.setBackgroundColor(getResources().getColor(R.color.red));
        //navigation =(LinearLayout)findViewById(R.id.navigation);
        //navigation.setBackgroundColor(Color.parseColor("#c1002a"));
        //navigation.setBackgroundColor(Color.rgb(193, 0, 42));
        //navigation.setBackgroundColor(getResources().getColor(R.color.red));
    }
}