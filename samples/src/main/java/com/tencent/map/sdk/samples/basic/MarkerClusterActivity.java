package com.tencent.map.sdk.samples.basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.vector.utils.clustering.Cluster;
import com.tencent.tencentmap.mapsdk.vector.utils.clustering.ClusterItem;
import com.tencent.tencentmap.mapsdk.vector.utils.clustering.ClusterManager;
import com.tencent.tencentmap.mapsdk.vector.utils.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.tencent.tencentmap.mapsdk.vector.utils.clustering.view.DefaultClusterRenderer;
import com.tencent.tencentmap.mapsdk.vector.utils.ui.IconGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MarkerClusterActivity extends AbsMapActivity {

    private static final int TYPE_GENERAL = 0;
    private static final int TYPE_MASSIVE = 1;
    private static final int TYPE_CUSTOM = 2;

    private boolean mIsLoaded;

    private TencentMap tencentMap;
    // 点聚合管理者
    private ClusterManager<MarkerClusterItem> mClusterManager;
    // 自定义点聚合管理者
    private ClusterManager<PetalItem> customClusterManager;
    // 普通坐标list
    private List<MarkerClusterItem> generalItemList;
    // 海量坐标list
    private List<MarkerClusterItem> massiveItemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, TencentMap pTencentMap) {
        tencentMap = pTencentMap;

        configDefaultClusterManager();
        configCustomClusterManager();

        Toast.makeText(this, "准备聚合", Toast.LENGTH_SHORT).show();
    }

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
                generalItemList=new ArrayList<>();
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
    private void configDefaultClusterManager() {
        // 实例化点聚合管理者
        mClusterManager = new ClusterManager<MarkerClusterItem>(this, tencentMap);

        // 默认聚合策略，调用时不必添加，如果需要其他聚合策略可以按以下代码修改
        NonHierarchicalDistanceBasedAlgorithm<MarkerClusterItem> ndba = new NonHierarchicalDistanceBasedAlgorithm<>(this);
        // 设置点聚合生效距离，以dp为单位
        ndba.setMaxDistanceAtZoom(35);
        // 设置策略
        mClusterManager.setAlgorithm(ndba);

        // 设置聚合渲染器，默认使用的是DefaultClusterRenderer，可以不调用下列代码
        DefaultClusterRenderer<MarkerClusterItem> renderer = new DefaultClusterRenderer<>(this, tencentMap, mClusterManager);
        // 设置最小聚合数量，默认为4，这里设置为2，即有2个以上不包括2个marker才会聚合
        renderer.setMinClusterSize(2);
        // 定义聚合的分段，当超过5个不足10个的时候，显示5+，其他分段同理
        renderer.setBuckets(new int[]{5, 10, 20, 50});
        mClusterManager.setRenderer(renderer);
    }
    private void configCustomClusterManager() {
        customClusterManager = new ClusterManager<PetalItem>(this, tencentMap);
        CustomIconClusterRenderer customIconClusterRenderer = new CustomIconClusterRenderer(this, tencentMap, mClusterManager);
        customClusterManager.setRenderer(customIconClusterRenderer);
    }


    //添加cluster
    private void performAddCluster(int pType) {
        mIsLoaded = true;
        if(pType==TYPE_GENERAL){ //普通
            customClusterManager.cancel();

            // 添加聚合
            tencentMap.setOnCameraChangeListener(mClusterManager);

            mClusterManager.addItems(getGeneralCoords());
            tencentMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.984059,116.307621), 15));
            mClusterManager.cluster();
        }else if (pType == TYPE_MASSIVE) { // 海量

            customClusterManager.cancel();

            // 添加聚合
            tencentMap.setOnCameraChangeListener(mClusterManager);

            mClusterManager.addItems(getMassiveCoords());
            tencentMap.animateCamera(CameraUpdateFactory.zoomTo(7));
            Toast.makeText(this, "海量点聚合", Toast.LENGTH_SHORT).show();
            mClusterManager.cluster();
        }else{
            mClusterManager.cancel();

            // 添加聚合
            tencentMap.setOnCameraChangeListener(customClusterManager);

            addCustomClusterItem();
            tencentMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.971595,116.294747),15));
            Toast.makeText(this, "自定义聚合", Toast.LENGTH_SHORT).show();
            customClusterManager.cluster();
        }
    }

    private void addCustomClusterItem() {
        customClusterManager.addItem(new PetalItem(39.971595,116.294747, R.mipmap.petal_blue));

        customClusterManager.addItem(new PetalItem(39.971595,116.314316, R.mipmap.petal_red));

        customClusterManager.addItem(new PetalItem(39.967385,116.317063, R.mipmap.petal_green));

        customClusterManager.addItem(new PetalItem(39.951596,116.302300, R.mipmap.petal_yellow));

        customClusterManager.addItem(new PetalItem(39.970543,116.290627, R.mipmap.petal_orange));

        customClusterManager.addItem(new PetalItem(39.966333,116.311569, R.mipmap.petal_purple));
    }
    /**
     * 移除所有的cluster
     */
    private void performRemoveCluster() {
        mIsLoaded = false;

        mClusterManager.clearItems();
        mClusterManager.cluster();

        customClusterManager.clearItems();
        customClusterManager.cluster();
    }

    // 普通
    private List<MarkerClusterItem> getGeneralCoords() {
        if (generalItemList == null || generalItemList.size() == 0) {
            generalItemList = getItemWithFileName("cluster_new");
        }

        return generalItemList;
    }

    // 海量
    private List<MarkerClusterItem> getMassiveCoords() {
        if (massiveItemList == null || massiveItemList.size() == 0) {
            massiveItemList = getItemWithFileName("datab");
        }
        return massiveItemList;
    }
    private List<MarkerClusterItem> getItemWithFileName(String fileName) {

        ArrayList<MarkerClusterItem> arrayList = new ArrayList<>();

        try {
            InputStream is = getAssets().open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                double longitude = Double.parseDouble(data[0]);
                double latitude = Double.parseDouble(data[1]);
                arrayList.add(new MarkerClusterItem(latitude, longitude));
            }
            is.close();
            isr.close();
            br.close();
            return arrayList;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    // 自定义
    public class PetalItem implements ClusterItem {

        private final LatLng mLatLng;

        private int mDrawableResourceId;

        public PetalItem(double latitude, double longitude, int resourceId) {
            // TODO Auto-generated constructor stub
            mLatLng = new LatLng(latitude, longitude);
            mDrawableResourceId = resourceId;
        }

        /* (non-Javadoc)
         * @see com.tencent.mapsdk.clustering.ClusterItem#getPosition()
         */
        @Override
        public LatLng getPosition() {
            // TODO Auto-generated method stub
            return mLatLng;
        }

        public int getDrawableResourceId() {
            return mDrawableResourceId;
        }

    }

    class CustomIconClusterRenderer extends DefaultClusterRenderer<PetalItem> {

        private IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private ImageView mItemImageView = new ImageView(getApplicationContext());
        private ImageView mClusterImageView = new ImageView(getApplicationContext());

        public CustomIconClusterRenderer(
                Context context, TencentMap map, ClusterManager clusterManager) {
            super(context, map, clusterManager);
            mItemImageView.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            mIconGenerator.setContentView(mItemImageView);

            mClusterImageView.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            mClusterIconGenerator.setContentView(mClusterImageView);

            setMinClusterSize(1);
        }

        @Override
        public void onBeforeClusterRendered(
                Cluster<PetalItem> cluster, MarkerOptions markerOptions) {
            int[] resources = new int[cluster.getItems().size()];
            int i = 0;
            for (PetalItem item : cluster.getItems()) {
                resources[i++] = item.getDrawableResourceId();
            }
            PetalDrawable drawable = new PetalDrawable(getApplicationContext(), resources);
            mClusterImageView.setImageDrawable(drawable);
            Bitmap icon = mClusterIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            //不显示 infowindow
//            markerOptions.infoWindowEnable(false);
        }

        @Override
        public void onBeforeClusterItemRendered(PetalItem item, MarkerOptions markerOptions) {
            mItemImageView.setImageResource(item.getDrawableResourceId());
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }
}
