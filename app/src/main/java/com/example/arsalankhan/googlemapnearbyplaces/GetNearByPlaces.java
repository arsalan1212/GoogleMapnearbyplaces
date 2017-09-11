package com.example.arsalankhan.googlemapnearbyplaces;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Arsalan khan on 9/11/2017.
 */

public class GetNearByPlaces {


    public static void getPlace(final Context context, final GoogleMap mMap, String url){

        final ArrayList<MapModel> arraylist = new ArrayList<>();
        if(arraylist.size()>0){
            arraylist.clear();
        }

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").equals("OK")){
                        JSONArray jsonArray = jsonObject.getJSONArray("results");

                        for(int i=0; i< jsonArray.length(); i++){

                            JSONObject childObject = jsonArray.getJSONObject(i);

                            String vicinity = childObject.getString("formatted_address");
                            String name = childObject.getString("name");

                            JSONObject latlng = childObject.getJSONObject("geometry").getJSONObject("location");

                            double latitude = Double.parseDouble(latlng.getString("lat"));
                            double longitude = Double.parseDouble(latlng.getString("lng"));

                            MapModel model = new MapModel(name,vicinity,longitude,latitude);

                            arraylist.add(model);
                        }

                    }
                    else{
                        Toast.makeText(context, "Sorry, No result Found: ", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setMapMarkerOnNearPlaces(mMap,arraylist);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(context).add(request);
    }

    private static void setMapMarkerOnNearPlaces(GoogleMap mMap,ArrayList<MapModel> arraylist) {

        mMap.clear();
        for(int i=0 ; i<arraylist.size(); i++){

            double latitude = arraylist.get(i).getLatitude();
            double longitude = arraylist.get(i).getLongitude();

            String name = arraylist.get(i).getName();
            String vicinity = arraylist.get(i).getVicinity();

            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(new LatLng(latitude,longitude)).title(name).snippet(vicinity);
            markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mMap.addMarker(markerOption);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));


        }

    }
}
