package com.example.arsalankhan.googlemapnearbyplaces;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener{

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double longitude,latitude;
    private int APPROX_RADIUS =1000;
    private String MAP_SERVER_KEY="AIzaSyCNOfscvTQtg2L8K9igEeyotU0qxVSDDss";
    private double end_latitude,end_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location!=null){

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    String title ="Your Location";
                    setMarker(title);

                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (Build.VERSION.SDK_INT < 23){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
        else{

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else{

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            }
        }
    }

    private void setMarker(String title) {
        LatLng latLng = new LatLng(latitude,longitude);

        mMap.addMarker(new MarkerOptions().position(latLng).title(title)
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode ==1){

            if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                }
            }
        }
    }


    //place search button
    public void searchPlace(View view){

        EditText editText = findViewById(R.id.editText);
        String search_text = editText.getText().toString().trim();

        if(!TextUtils.isEmpty(search_text)){

            mMap.clear();

            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addressList = geocoder.getFromLocationName(search_text,1);

                Address address = addressList.get(0);

                latitude = address.getLatitude();
                longitude = address.getLongitude();
                setMarker(address.getLocality());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Fill the Field", Toast.LENGTH_SHORT).show();
        }

    }

    //Near by places
    public void searchNearByPlaces(View view){

        switch (view.getId()){
            case R.id.btn_hospital:
                String url = getUrl("hospital");
                GetNearByPlaces.getPlace(MapsActivity.this,mMap,url);
                break;

            case R.id.btn_school:
                url = getUrl("school");
                GetNearByPlaces.getPlace(MapsActivity.this,mMap,url);
                break;


            case R.id.btn_resturent:
                url = getUrl("restaurant");
                GetNearByPlaces.getPlace(MapsActivity.this,mMap,url);
                break;

            case R.id.btn_to:
                mMap.clear();
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(end_latitude,end_longitude));
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                options.title("Destination");

                float[] results = new float[10];
                Location.distanceBetween(latitude,longitude,end_latitude,end_longitude,results);

                DecimalFormat format = new DecimalFormat("####.##");
                double distanceInKm =Double.parseDouble(format.format( results[0]/1000));
                options.snippet("Distance: "+distanceInKm+" km");
                mMap.addMarker(options);
                Toast.makeText(this, "Distance: "+distanceInKm+" km and meter: "+results[0]+" meter", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    private String getUrl(String placeName){

        StringBuffer stringBuffer = new StringBuffer("https://maps.googleapis.com/maps/api/place/textsearch/json?");
        stringBuffer.append("location="+latitude+","+longitude);
        stringBuffer.append("&radius="+APPROX_RADIUS);
        stringBuffer.append("&type="+placeName);
        stringBuffer.append("&key="+MAP_SERVER_KEY);

        return stringBuffer.toString();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        end_latitude = marker.getPosition().latitude;
        end_longitude = marker.getPosition().longitude;
    }
}
