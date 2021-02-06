package com.example.enf_cnam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    private TextView libelleCNAM;
    private TextView adresseCNAM;
    public static String origins ="";
    public static String destinations = "";
    private JSONArray etablissements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.mapView);
        libelleCNAM = (TextView) findViewById(R.id.libelleCNAM);
        adresseCNAM = (TextView) findViewById(R.id.adresseCNAM);


        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Thread markers = new Thread(new Runnable() {
            public void run() {
                //final JSONArray etablissements = getEtablissements();
                etablissements = getEtablissements();
                for (int i = 0; i < etablissements.length(); i++) {
                    try {
                        final JSONObject etab = etablissements.getJSONObject(i);
                        destinations += etab.getDouble("LATITUDE") + "," + etab.getDouble("LONGITUDE") + "|";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LatLng cnamEtab = null;
                                try {
                                    cnamEtab = new LatLng(etab.getDouble("LATITUDE"), etab.getDouble("LONGITUDE"));
                                    mMap.addMarker(new MarkerOptions()
                                            .position(cnamEtab)
                                            .title(etab.getString("LIBELLE")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
//
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        markers.start();
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(lm.getBestProvider(criteria, true));
        try {
            Location location = lm.getLastKnownLocation(bestProvider);
            origins = location.getLatitude() + "," + location.getLongitude();
            final LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            Handler posHandler = new Handler(getApplicationContext().getMainLooper());
            posHandler.post(new Runnable() {
                @Override
                public void run() {
                    BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.position);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);
                    mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title("Auditeur")
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                }});
        } catch (SecurityException e) {
            System.out.println("Erreur"); // lets the user know there is a problem with the gps
        }
        //Zoom sur Grand-Est
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.72234, 5.65267), 6));

    }

        
    public void findNearestCNAM(View v) {
        Thread nearest = new Thread(new Runnable() {
            public void run() {
                JSONArray distances = getDistances(origins,destinations);
                int indexMinimum = -1;
                try {
                    //int minValue = distances.getJSONObject(0).getJSONObject("distance").getInt("value");
                    int minValue = 99999999;
                    System.out.println(minValue);
                    for(int i= 0; i < distances.length();i++) {
                        if(distances.getJSONObject(i).getJSONObject("distance").getInt("value") < minValue){
                            minValue = distances.getJSONObject(i).getJSONObject("distance").getInt("value");
                            indexMinimum = i;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
                final int finalIndexMinimum = indexMinimum;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            libelleCNAM.setText(etablissements.getJSONObject(finalIndexMinimum).getString("LIBELLE") + " - " + etablissements.getJSONObject(finalIndexMinimum).getString("TELEPHONE"));
                            adresseCNAM.setText(etablissements.getJSONObject(finalIndexMinimum).getString("ADRESSE"));
                            libelleCNAM.setVisibility(View.VISIBLE);
                            adresseCNAM.setVisibility(View.VISIBLE);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(etablissements.getJSONObject(finalIndexMinimum).getDouble("LATITUDE"), etablissements.getJSONObject(finalIndexMinimum).getDouble("LONGITUDE")), 12));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((Double) listDestinations.get(finalIndexMinimum)[1], (Double) listDestinations.get(finalIndexMinimum)[2]), 12));

                    }
                });
            }
        });
        nearest.start();
    }

    public JSONArray getEtablissements() {
        JSONArray results = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://apicnam.000webhostapp.com/API/Controllers/EtablissementController.php?view=all")
                    .addHeader("content-type", "application/json")
                    .addHeader("accept-Language", "fr")
                    .addHeader("authorization", MainActivity.token)
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            //System.out.println("Response :  " + responseBody);
            JSONObject jsonResponse = new JSONObject(responseBody);

                results = jsonResponse.getJSONArray("etablissements");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return results;
    }

    public JSONArray getDistances(String origins, String destinations) {
        JSONArray results = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+ origins +"&destinations="+ destinations +"&key=AIzaSyAN9k9wDxeBADhS0HyPvo4OHli7T7go1w4&mode=driving&language=en&units=metrics")
                    .addHeader("content-type", "application/json")
                    .addHeader("accept-Language", "fr")
                    .addHeader("authorization", MainActivity.token)
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            results = jsonResponse.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return results;
    }




    public void viewHome(View v) {
        Intent homeActivity = new Intent(MapActivity.this, HomeActivity.class);
        startActivity(homeActivity);
    }

    public void viewUserInfo(View v) throws JSONException {
        Intent userActivity = new Intent(MapActivity.this, UserActivity.class);
        startActivity(userActivity);
    }

    public void logout(View v) {
        Intent mainActivity = new Intent(MapActivity.this, MainActivity.class);
        startActivity(mainActivity);
        MainActivity.token = "";
    }

    public void mail(View v) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=13&ct=1610371321&rver=7.0.6737.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.live.com%2fowa%2f%3fnlp%3d1%26RpsCsrfState%3db3d1dea9-4053-5262-434d-0b14a393acbf&id=292841&aadredir=1&CBCXT=out&lw=1&fl=dob%2cflname%2cwld&cobrandid=90015"));
        viewIntent.setPackage("com.android.chrome");
        startActivity(viewIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.token == "") {
            Intent mainActivity = new Intent(MapActivity.this, MainActivity.class);
            startActivity(mainActivity);
        }
    }
}