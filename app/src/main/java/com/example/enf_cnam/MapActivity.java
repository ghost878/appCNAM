package com.example.enf_cnam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.mapView);


        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Thread markers = new Thread(new Runnable() {
            public void run() {
                final JSONArray etablissements = getEtablissements();
                //System.out.println(etablissements);
                //final String[] destinations = {""};
                for (int i = 0; i < etablissements.length(); i++) {
                    try {
                        final JSONObject etab = etablissements.getJSONObject(i);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LatLng cnamEtab = null;
                                try {
                                    cnamEtab = new LatLng(etab.getDouble("LATITUDE"), etab.getDouble("LONGITUDE"));
                                    mMap.addMarker(new MarkerOptions()
                                            .position(cnamEtab)
                                            .title(etab.getString("LIBELLE")));
                                    //destinations += etab.getDouble("LATITUDE") + "," + etab.getDouble("LATITUDE") + "|";

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                //mMap.moveCamera(CameraUpdateFactory.newLatLng(cnamEtab));
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


        // Afficher Sydney
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.72234, 5.65267), 6));

    }

        
    public void findNearestCNAM(View v) {
        Thread nearest = new Thread(new Runnable() {
            public void run() {
                final JSONArray etablissements = getEtablissements();
                String destinations = "";
                String origins = "";
                for (int i = 0; i < etablissements.length(); i++) {
                    try {
                        final JSONObject etab = etablissements.getJSONObject(i);
                        destinations += etab.getDouble("LATITUDE") + "," + etab.getDouble("LONGITUDE") + "|";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

//                LocationManager lm = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
//                try {
//                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                    origins = location.getLatitude() + "," + location.getLongitude();
////                    System.out.println(location);
////                    double longitude = location.getLongitude();
////                    double latitude = location.getLatitude();
////                    System.out.println("Coucou " + latitude + "--" + longitude);
//                } catch (SecurityException e) {
//                    System.out.println("Erreur"); // lets the user know there is a problem with the gps
//                }
                System.out.println("Origins" + origins + " Dest" + destinations );
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




    public void viewHome(View v) {
        Intent homeActivity = new Intent(MapActivity.this, HomeActivity.class);
        startActivity(homeActivity);
    }
}