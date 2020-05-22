package com.tencent.map.sdk.samples.basic;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ShowMarkersActivity extends AbsMapActivity {

    private TencentMap tencentMap;
    private ArrayList<LatLng> latLngArrayList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, TencentMap pTencentMap) {
        tencentMap = pTencentMap;
        setupLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_markers, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_show_markers).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_show_markers) {
            adjustMapRect();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupLocation() {
        latLngArrayList = new ArrayList<>();
        LatLng latLng1 = new LatLng(39.90604,116.32168);
        latLngArrayList.add(latLng1);
        LatLng latLng2 = new LatLng(39.993098,116.336462);
        latLngArrayList.add(latLng2);
        LatLng latLng3 = new LatLng(39.8982,116.37509);
        latLngArrayList.add(latLng3);
        LatLng latLng4 = new LatLng(39.934059,116.451259);
        latLngArrayList.add(latLng4);
        LatLng latLng5 = new LatLng(39.954624,116.32296);
        latLngArrayList.add(latLng5);
        LatLng latLng6 = new LatLng(39.941474,116.416938);
        latLngArrayList.add(latLng6);
        LatLng latLng7 = new LatLng(39.947071,116.371438);
        latLngArrayList.add(latLng7);
        LatLng latLng8 = new LatLng(39.911171,116.411644);
        latLngArrayList.add(latLng8);
        LatLng latLng9 = new LatLng(39.975528,116.490346);
        latLngArrayList.add(latLng9);
        LatLng latLng10 = new LatLng(39.84636,116.37075);
        latLngArrayList.add(latLng10);
        LatLng latLng11 = new LatLng(39.889102,116.35787);
        latLngArrayList.add(latLng11);
        LatLng latLng12 = new LatLng(39.959084,116.288522);
        latLngArrayList.add(latLng12);
        LatLng latLng13 = new LatLng(39.884113,116.455896);
        latLngArrayList.add(latLng13);
        LatLng latLng14 = new LatLng(39.889102,116.35787);
        latLngArrayList.add(latLng14);


        for (int i = 0; i < latLngArrayList.size(); i++) {
            // 最后一个点作为中心点
            if (i == latLngArrayList.size() - 1) {
                Marker centerMarker = tencentMap.addMarker(new MarkerOptions().
                        position(latLngArrayList.get(i)).
                        title("中心点"));
                centerMarker.showInfoWindow();
            } else {
                tencentMap.addMarker(new MarkerOptions().
                        position(latLngArrayList.get(i)));
            }
            LatLngBounds latLngBounds = new LatLngBounds.Builder().include(latLngArrayList).build();
            tencentMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
        }
    }

    private void adjustMapRect() {
        LatLng center = new LatLng(39.889102,116.35787);
        LatLngBounds latLngBounds = new LatLngBounds.Builder().include(latLngArrayList).build();
        tencentMap.animateCamera(CameraUpdateFactory.newLatLngBoundsWithMapCenter(
                latLngBounds, center, 100));
    }
}
