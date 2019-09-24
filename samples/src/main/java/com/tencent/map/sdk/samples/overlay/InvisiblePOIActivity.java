package com.tencent.map.sdk.samples.overlay;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;

public class InvisiblePOIActivity extends AbsMapActivity {

    private TencentMap mMap;
    private boolean mIsVisiblePoi = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, TencentMap pTencentMap) {
        super.onCreate(savedInstanceState, pTencentMap);
        mMap = pTencentMap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.invisible_poi, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_poi_hide).setVisible(mIsVisiblePoi);
        menu.findItem(R.id.menu_poi_show).setVisible(!mIsVisiblePoi);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_poi_show:
                performPoiShow();
                break;
            case R.id.menu_poi_hide:
                performPoiHide();
                break;
        }

        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    private void performPoiHide() {
        if (checkMapInvalid()) {
            return;
        }

        mMap.setPoisEnabled(false);
        mIsVisiblePoi = false;
    }

    private void performPoiShow() {
        if (checkMapInvalid()) {
            return;
        }

        mMap.setPoisEnabled(true);
        mIsVisiblePoi = true;
    }
}
