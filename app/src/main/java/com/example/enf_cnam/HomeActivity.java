package com.example.enf_cnam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private LinearLayout navigation;
    private LinearLayout homeLayout;
    private LinearLayout infoLayout;
    private LinearLayout enseignements;
    private TextView hello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LinearLayout header = (LinearLayout)findViewById(R.id.header);
        homeLayout = (LinearLayout) findViewById(R.id.homeLayout);
        infoLayout = (LinearLayout) findViewById(R.id.infoLayout);
        enseignements = (LinearLayout) findViewById(R.id.enseignements);
        hello = (TextView) findViewById(R.id.hello);
        hello.setTextColor(Color.BLACK);
        //System.out.println("JSON" + MainActivity.auditeurInfo);
        hello.setPadding(0,30,0,30);
        try {
            hello.setText("Bonjour " + MainActivity.auditeurInfo.getString("PRENOM"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hello.setTextSize(30);
        hello.setTypeface(Typeface.DEFAULT_BOLD);

        System.out.println(MainActivity.enseignements);
        for(int i = 0; i < MainActivity.enseignements.length(); i++) {
            LinearLayout unite = new LinearLayout(this);
            unite.setOrientation(LinearLayout.VERTICAL);
            try {
                JSONObject uniteDetails = MainActivity.enseignements.getJSONObject(i);
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
                enseignements.addView(unite);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }


    public void viewUserInfo(View v) throws JSONException {
        System.out.println("INFO");
        homeLayout.setVisibility(View.GONE);
        infoLayout.setVisibility(View.VISIBLE);
        infoLayout.removeAllViews();
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
                if(!(MainActivity.auditeurInfo.getString(key).equals("null"))) {
                    data.setText(key + " : " + MainActivity.auditeurInfo.getString(key));
                } else {
                    data.setText(key + " : " + "Non défini");
                }
                data.setTextColor(Color.BLACK);
                data.setPadding(20,10,20,10);
                infoLayout.addView(data);

            }
        }



    }
    public void viewHome(View v) {
        infoLayout.setVisibility(View.GONE);
        homeLayout.setVisibility(View.VISIBLE);
    }

    public void logout(View v) {
        Intent mainActivity = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }

    public void mail(View v) {
        Intent intent=Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_EMAIL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//Min SDK 15
        startActivity(intent);
    }

    public void unite(View v) {
        System.out.println("coucou");
    }
}