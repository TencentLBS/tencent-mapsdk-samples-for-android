package com.tencent.map.sdk.samples.basic;

import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.R;

public class MarkerClusterActivity extends AbsMapActivity {

    private static final int TYPE_GENERAL = 0;
    private static final int TYPE_MASSIVE = 1;
    private static final int TYPE_CUSTOM = 2;

    private boolean mIsLoaded;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.marker_cluster, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_cluster_show).setVisible(!mIsLoaded);
        menu.findItem(R.id.menu_cluster_remove).setVisible(mIsLoaded);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cluster_general:
                performAddCluster(TYPE_GENERAL);
                break;
            case R.id.menu_cluster_massive:
                performAddCluster(TYPE_MASSIVE);
                break;
            case R.id.menu_cluster_custom:
                performAddCluster(TYPE_CUSTOM);
                break;
            case R.id.menu_cluster_remove:
                performRemoveCluster();
                break;
        }

        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    private void performAddCluster(int pType) {
        mIsLoaded = true;
    }


    private void performRemoveCluster() {
        mIsLoaded = false;
    }
}
