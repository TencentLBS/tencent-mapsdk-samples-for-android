package com.tencent.map.sdk.samples.basic;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

public class MapCenterActivity extends AbsMapActivity {

    private TencentMap mMap;
    private Marker mMapCenterMarker;
    private boolean mIsAdded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, TencentMap pTencentMap) {
        super.onCreate(savedInstanceState, pTencentMap);
        mMap = pTencentMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_center, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_map_center_add).setVisible(!mIsAdded);
        menu.findItem(R.id.menu_map_center_remove).setVisible(mIsAdded);
        menu.findItem(R.id.menu_map_center_update).setVisible(mIsAdded);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_map_center_add:
                performAddMapCenterMarker();
                break;
            case R.id.menu_map_center_remove:
                performRemoveMapCenterMarker();
                break;
            case R.id.menu_map_center_update:
                performNewMapCenterMarker();
                break;
        }

        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    private void performRemoveMapCenterMarker() {
        if (checkMapInvalid()) {
            return;
        }

        if (mMapCenterMarker != null) {
            mMapCenterMarker.remove();
        }

        mIsAdded = false;
    }

    private void performNewMapCenterMarker() {
        if (checkMapInvalid()) {
            return;
        }

        LatLng newLatLng = mMap.getCameraPosition().target;
        newLatLng.latitude += (Math.random() > 0.5 ? 1 : -1) * Math.random();
        newLatLng.longitude += (Math.random() > 0.5 ? 1 : -1) * Math.random();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));

        if (mMapCenterMarker != null) {
            mMapCenterMarker.remove();
        }
        mMapCenterMarker = mMap.addMarker(new MarkerOptions(newLatLng));

        Toast.makeText(this, "设置一个新的地图中心点", Toast.LENGTH_SHORT).show();
    }

    private void performAddMapCenterMarker() {
        if (checkMapInvalid()) {
            return;
        }

        mMapCenterMarker = mMap.addMarker(new MarkerOptions(mMap.getCameraPosition().target));
        Toast.makeText(this, "添加一个地图中心点标注", Toast.LENGTH_SHORT).show();
        mIsAdded = true;
    }

}
