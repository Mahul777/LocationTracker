package com.chitkarauniversity.locationtracker;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.chitkarauniversity.locationtracker.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener
{

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LatLng lng;
    EditText et1;
    Button bt1,bt2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //ActivityCompat -> A helper for accessing features in Activity in a backwards compatible fashion
        ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
        //LocationManager -> it used for This class provides access to the system location services
        //getSystemService -> is used when we want to access one of few Android system-level services.
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "Please Wait", Toast.LENGTH_SHORT).show();
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, this);
        et1=findViewById(R.id.et1);
        bt1=findViewById(R.id.bt1);
        bt2=findViewById(R.id.bt2);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db=openOrCreateDatabase("mydb",MODE_PRIVATE,null);
                db.execSQL("create table if not exists locations(name varchar,latitude varchar,longitude varchar)");
                String query="insert into locations values('"+et1.getText().toString()+"','"+lng.latitude+"','"+lng.longitude+"')";
                db.execSQL(query);
                db.close();
                Toast.makeText(MapsActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db=openOrCreateDatabase("mydb",MODE_PRIVATE,null);
                db.execSQL("create table if not exists locations(name varchar,latitude varchar,longitude varchar)");
                String query="select latitude,longitude from locations where name='"+et1.getText().toString()+"'";
                Cursor cursor=db.rawQuery(query,null);
                if(cursor.moveToFirst())
                {
                    double lt=Double.parseDouble(cursor.getString(0));
                    double lg=Double.parseDouble(cursor.getString(1));
                    lng=new LatLng(lt,lg);
                    goToMyLocation();


                }
                else
                    Toast.makeText(MapsActivity.this, "Invalid Location", Toast.LENGTH_SHORT).show();
                db.close();
                Toast.makeText(MapsActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        lng=new LatLng(location.getLatitude(),location.getLongitude());
        Toast.makeText(MapsActivity.this, "Started", Toast.LENGTH_SHORT).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.mainmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    void goToMyLocation()
    {
        if(lng==null)
            Toast.makeText(MapsActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
        else
        {
            mMap.clear();
            CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(lng,15);
            mMap.animateCamera(cameraUpdate);
            MarkerOptions mo=new MarkerOptions();
            mo.position(lng);
            mo.title("Current Location");
            mMap.addMarker(mo);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.m1)
        {
            goToMyLocation();
        }
        return super.onOptionsItemSelected(item);
    }
}
