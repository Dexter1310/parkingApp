package com.example.parking;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String direccion,matricula;
    Button volver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        volver=(Button)findViewById(R.id.btnVolver);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        int status= GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(status== ConnectionResult.SUCCESS){
            SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }else{
            Dialog dialog=GooglePlayServicesUtil.getErrorDialog(status,(Activity)getApplicationContext(),10);
            dialog.show();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Todo: volver a la pantalla principal:
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main=new Intent(map.this,MainActivity.class);
                main.putExtra("direccion",direccion);
                main.putExtra("matricula",matricula);
                startActivity(main);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        UiSettings uiSettings=mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        float zoom= 18;
        direccion=getIntent().getExtras().getString("direc");matricula=getIntent().getExtras().getString("matricula");
        LatLng sitio = new LatLng(getIntent().getExtras().getDouble("latitud"),getIntent().getExtras().getDouble("longitud"));
        mMap.addMarker(new MarkerOptions().position(sitio).title(direccion+matricula));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sitio));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sitio,zoom));
    }
}
