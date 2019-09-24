package com.tencent.map.sdk.samples.basic;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.Polygon;
import com.tencent.tencentmap.mapsdk.maps.model.PolygonOptions;
import com.tencent.tencentmap.mapsdk.maps.model.RestrictBoundsFitMode;

public class LimitMapVisibleBoundActivity extends AbsMapActivity {

    private TencentMap mMap;
    private Polygon mLimitMapBound;
    private CameraPosition mMapOldCameraPos;
    private boolean mIsLimited;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, TencentMap pTencentMap) {
        super.onCreate(savedInstanceState, pTencentMap);
        mMap = pTencentMap;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.limit_map_bound, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_limit).setVisible(!mIsLimited);
        menu.findItem(R.id.menu_unlimit).setVisible(mIsLimited);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_limit_fit_width:
                performLimitMap(RestrictBoundsFitMode.FIT_WIDTH);
                break;
            case R.id.menu_limit_fit_height:
                performLimitMap(RestrictBoundsFitMode.FIT_HEIGHT);
                break;
            case R.id.menu_unlimit:
                performUnLimitMap();
                break;
        }
        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    private void performUnLimitMap() {
        mMap.setRestrictBounds(null, RestrictBoundsFitMode.FIT_WIDTH);

        if (mLimitMapBound != null) {
            mLimitMapBound.remove();
        }

        if (mMapOldCameraPos != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mMapOldCameraPos));
        }

        mIsLimited = false;
    }

    private void performLimitMap(RestrictBoundsFitMode pFitMode) {
        LatLngBounds latLngBounds = new LatLngBounds(
                new LatLng(39.923297, 116.402335),
                new LatLng(39.912666, 116.391907));

        if (mLimitMapBound != null) {
            mLimitMapBound.remove();
        }

        mMapOldCameraPos = mMap.getCameraPosition();

        mLimitMapBound = mMap.addPolygon(new PolygonOptions().add(
                latLngBounds.getNorthEast(),
                latLngBounds.getSouthEast(),
                latLngBounds.getSouthWest(),
                latLngBounds.getNorthWest())
                .fillColor(Color.TRANSPARENT)
                .strokeWidth(2)
                .strokeColor(Color.BLUE));

        mMap.setRestrictBounds(latLngBounds, pFitMode);

        mIsLimited = true;
    }
}
