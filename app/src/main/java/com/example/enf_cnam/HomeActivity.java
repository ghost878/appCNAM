package com.example.enf_cnam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.net.ftp.*;



public class HomeActivity extends AppCompatActivity {

    private LinearLayout navigation;
    private LinearLayout homeLayout;
    private LinearLayout infoLayout;
    private LinearLayout enseignements;
    private LinearLayout moodleLayout;
    private TextView hello;
    private LinearLayout cadreFichiers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LinearLayout header = (LinearLayout)findViewById(R.id.header);
        homeLayout = (LinearLayout) findViewById(R.id.homeLayout);
        infoLayout = (LinearLayout) findViewById(R.id.infoLayout);
        moodleLayout = (LinearLayout) findViewById(R.id.moodleLayout);
        enseignements = (LinearLayout) findViewById(R.id.enseignements);
        cadreFichiers = (LinearLayout) findViewById(R.id.cadreFichiers);
        infoLayout.setVisibility(View.GONE);
        moodleLayout.setVisibility(View.GONE);
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
                        homeLayout.setVisibility(View.GONE);
                        moodleLayout.setVisibility(View.VISIBLE);
                        cadreFichiers.removeAllViews();
                        cadreFichiers.setPadding(10, 10 , 10, 10);
                        TextView moodleInfo1 = (TextView) findViewById(R.id.moodleInfo1);
                        TextView moodleInfo2 = (TextView) findViewById(R.id.moodleInfo2);
                        TextView moodleInfo3 = (TextView) findViewById(R.id.moodleInfo3);
                        try {
                            moodleInfo1.setText(uniteDetails.getString("CODE"));
                            moodleInfo1.setTextColor(Color.BLACK);
                            moodleInfo1.setTextSize(20);
                            moodleInfo1.setTypeface(Typeface.DEFAULT_BOLD);
                            moodleInfo2.setText(uniteDetails.getString("LIBELLE"));
                            moodleInfo2.setTextColor(Color.BLACK);
                            moodleInfo2.setTextSize(15);
                            moodleInfo2.setTypeface(Typeface.DEFAULT_BOLD);
                            moodleInfo3.setText(uniteDetails.getString("ANNEE_DEBUT") + " - " + uniteDetails.getString("ANNEE_FIN"));
                            moodleInfo3.setTextColor(Color.BLACK);
                            moodleInfo3.setTextSize(15);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
// APPROCHE PAR HTTP (plus securisé)
                        Thread docs = new Thread(new Runnable() {
                            public void run() {
                                OkHttpClient client = new OkHttpClient();
                                Request request = null;
                                try {
                                    request = new Request.Builder()
                                            .url("https://apicnam.000webhostapp.com/API/Controllers/AuditeurController.php?view=files_unite&unite=" + uniteDetails.getString("CODE") )
                                            .build();
                                    Response response = client.newCall(request).execute();
                                    String responseBody = response.body().string();
                                    System.out.println("Response :  " + responseBody);
                                    JSONObject fichiersMoodle = new JSONObject(responseBody);
                                    Iterator<String> keys = fichiersMoodle.keys();
                                    while(keys.hasNext()) {
                                        final String key = keys.next();
                                        final TextView dirName = new TextView(getApplicationContext());
                                            dirName.setText(key);
                                            dirName.setTextColor(Color.BLACK);
                                            dirName.setTypeface(Typeface.DEFAULT_BOLD);
                                            dirName.setPadding(5, 20, 5, 20);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    cadreFichiers.addView(dirName);
                                                }
                                            });
                                            final JSONObject files = fichiersMoodle.getJSONObject(key);
                                            Iterator<String> listFiles = files.keys();
                                            while(listFiles.hasNext()) {
                                                final String cpt = listFiles.next();
                                                final TextView filename = new TextView(getApplicationContext());
                                                filename.setText(files.getString(cpt));
                                                filename.setTextColor(Color.rgb(51, 102, 187));
                                                filename.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                                                filename.setPadding(15, 5, 5, 5);
                                                filename.setOnClickListener(new View.OnClickListener() {
                                                    public void onClick(View v) {
                                                        Intent viewIntent = null;
                                                        try {
                                                            viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://apicnam.000webhostapp.com/Moodle/" + uniteDetails.getString("CODE") + "/" + key + "/" + files.getString(cpt)));
                                                            viewIntent.setPackage("com.android.chrome");
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        startActivity(viewIntent);
                                                        }
                                                });
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        cadreFichiers.addView(filename);
                                                    }
                                                });

                                            }
                                    }
                                } catch (IOException | JSONException /*| JSONException*/ e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        docs.start();

// APPROCHE PAR FTP
//                        Thread docs = new Thread(new Runnable() {
//                            public void run() {
//                                System.out.println("coucou2");
//                                String server = "files.000webhost.com";
//                                int port = 21;
//                                String user = "apicnam";
//                                String pass = "apicnamserver";
//                                final LinearLayout cadreFichiers = (LinearLayout) findViewById(R.id.cadreFichiers);
//                                cadreFichiers.removeAllViews();
//                                cadreFichiers.setPadding(10, 10 , 10, 10);
//                                FTPClient ftpClient = new FTPClient();
//                                try {
//                                    ftpClient.connect(server, port);
//                                    boolean res = ftpClient.login(user, pass);
//                                    ftpClient.enterLocalPassiveMode();
//                                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//                                    FTPFile[] dirs = ftpClient.listDirectories("/public_html/Moodle/" + uniteDetails.getString("CODE") );
//                                    for(final FTPFile dir : dirs) {
//                                        if (!dir.getName().equals(".") && !dir.getName().equals("..")) {
//                                            FTPFile[] files = ftpClient.listFiles("/public_html/Moodle/" + uniteDetails.getString("CODE") + "/" + dir.getName());
//                                            final TextView dirName = new TextView(getApplicationContext());
//                                            dirName.setText(dir.getName());
//                                            dirName.setTextColor(Color.BLACK);
//                                            dirName.setTypeface(Typeface.DEFAULT_BOLD);
//                                            dirName.setPadding(5, 20, 5, 20);
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    cadreFichiers.addView(dirName);
//                                                }
//                                            });
//                                            for(final FTPFile file : files) {
//                                                if (!file.getName().equals(".") && !file.getName().equals("..")) {
//                                                    final TextView filename = new TextView(getApplicationContext());
//                                                    filename.setText(file.getName());
//                                                    filename.setTextColor(Color.rgb(51, 102, 187));
//                                                    filename.setPadding(15, 5, 5, 5);
//                                                    filename.setOnClickListener(new View.OnClickListener() {
//                                                        public void onClick(View v) {
//                                                            Intent viewIntent = null;
//                                                            try {
//                                                                viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://apicnam.000webhostapp.com/Moodle/" + uniteDetails.getString("CODE") + "/" + dir.getName() + "/" + file.getName()));
//                                                                viewIntent.setPackage("com.android.chrome");
//                                                            } catch (JSONException e) {
//                                                                e.printStackTrace();
//                                                            }
//                                                            startActivity(viewIntent);
//                                                        }
//                                                    });
//                                                    runOnUiThread(new Runnable() {
//                                                        @Override
//                                                        public void run() {
//                                                            cadreFichiers.addView(filename);
//                                                        }
//                                                    });
//                                                }
//                                            }
//                                        }
//                                    }
//                                } catch (SocketException e) {
//                                    e.printStackTrace();
//                                } catch (FileNotFoundException e) {
//                                    e.printStackTrace();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }});
//                        docs.start();

                    }});

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
        moodleLayout.setVisibility(View.GONE);
        homeLayout.setVisibility(View.VISIBLE);
    }

    public void viewPlanning(View v) {

    }

    public void viewCursus(View v) {

    }

    public void viewExamen(View v) {

    }


    public void logout(View v) {
        Intent mainActivity = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }

    public void mail(View v) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=13&ct=1610371321&rver=7.0.6737.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.live.com%2fowa%2f%3fnlp%3d1%26RpsCsrfState%3db3d1dea9-4053-5262-434d-0b14a393acbf&id=292841&aadredir=1&CBCXT=out&lw=1&fl=dob%2cflname%2cwld&cobrandid=90015"));
        viewIntent.setPackage("com.android.chrome");
        startActivity(viewIntent);
    }

}