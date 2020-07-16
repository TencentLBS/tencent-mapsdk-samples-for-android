package com.tencent.map.sdk.samples.overlay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.param.DrivingParam;
import com.tencent.lbssearch.object.result.DrivingResultObject;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.SupportMapFragment;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;
import com.yinglan.scrolllayout.ScrollLayout;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class LogisticsStateActivity extends AppCompatActivity {

    private FragmentManager fm;
    protected TencentMap tencentMap;
    private SupportMapFragment supportMapFragment;
    protected UiSettings mapUiSettings;
    private ScrollLayout mScrollLayout;
    private List<LatLng> lines;

    private LatLng fromPoint = new LatLng(39.042862, 115.861633); // 起点坐标
    private LatLng toPoint = new LatLng(22.19875, 113.54913); //终点坐标 22.19875
    private LatLng middle = new LatLng(30.58203, 114.02919); //终点坐标

    private LatLng shanghai = new LatLng(31.19668, 121.337601); // 起点坐标
    private LatLng changsha = new LatLng(28.194668, 112.976868); //终点坐标

    private ScrollLayout.Status mCurrentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics_state);
        initMap();
        RecyclerView rv_logistics = findViewById(R.id.rv_logistics);
        rv_logistics.setLayoutManager(new LinearLayoutManager(this));
        rv_logistics.setFocusable(false);
        rv_logistics.setNestedScrollingEnabled(false);
        rv_logistics.setHasFixedSize(true);
        rv_logistics.setAdapter(new LogisticsStateAdapter(this, R.layout.item_logistics, DataSources.getData()));
        mScrollLayout = (ScrollLayout) findViewById(R.id.scroll_down_layout);
        mScrollLayout.setMinOffset(0);
        mScrollLayout.setMaxOffset((int) (ScreenUtil.getScreenHeight(this) * 0.5));
        mScrollLayout.setExitOffset(ScreenUtil.dip2px(this, 80));
        mScrollLayout.setIsSupportExit(true);
        mScrollLayout.setAllowHorizontalScroll(true);
        mScrollLayout.setOnScrollChangedListener(mOnScrollChangedListener);
        mScrollLayout.setToOpen();

        mScrollLayout.getBackground().setAlpha(0);

    }

    private void initMap() {
        fm = getSupportFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_frag);
        tencentMap = supportMapFragment.getMap();
        mapUiSettings = tencentMap.getUiSettings();
        //对地图操作类进行操作
        CameraUpdate cameraSigma =
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        toPoint,
                        4.15f,
                        0f,
                        0f));
        //移动地图
        tencentMap.moveCamera(cameraSigma);
        getWalkingRoute();
        addMarkers();
        tencentMap.addPolyline(setLineStyle(0));
        tencentMap.addPolyline(setLineStyle(1));
    }


    /**
     * 获取步行导航规划
     */
    private void getWalkingRoute() {
        DrivingParam drivingParam = new DrivingParam(shanghai, changsha); //创建导航参数

        drivingParam.roadType(DrivingParam.RoadType.ON_MAIN_ROAD_BELOW_BRIDGE);
        drivingParam.heading(90);
        drivingParam.accuracy(30);
        TencentSearch tencentSearch = new TencentSearch(this);
        tencentSearch.getRoutePlan(drivingParam, new HttpResponseListener<DrivingResultObject>() {

            @Override
            public void onSuccess(int i, DrivingResultObject drivingResultObject) {
                if (drivingResultObject == null) {
                    return;
                }
                for (DrivingResultObject.Route route : drivingResultObject.result.routes) {
                    lines = route.polyline;
                    // tencentMap.addPolyline(new PolylineOptions().addAll(lines).width(5f).color(0xFFFF0000));
                    tencentMap.addPolyline(setLineStyle(3));
                }
                addMarkers();
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {

            }
        });
    }

    /**
     * 通过添加OverlayItem添加标注物
     */
    private void addMarkers() {
        tencentMap.enableMultipleInfowindow(true);
        tencentMap.setOnMarkerClickListener(new TencentMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });
        Marker marker1 = tencentMap.addMarker(new MarkerOptions(toPoint)
                .title("收件地址:")
                .snippet("宝安区新安公司")
                .anchor(0.5f, 0.5f)
                .viewInfoWindow(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true));// 设置默认显示一个infowinfow

        Marker marker2 = tencentMap.addMarker(new MarkerOptions(fromPoint)
                .title("发件地址")
                .snippet("河北省新安县")
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true));
        Marker marker3 = tencentMap.addMarker(new MarkerOptions(shanghai)
                .title("上海")
                .snippet("中转站")
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true));
        Marker marker4 = tencentMap.addMarker(new MarkerOptions(changsha)
                .title("长沙")
                .snippet("中转站")
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true));
        marker1.showInfoWindow();
        marker2.showInfoWindow();
        marker3.showInfoWindow();
        marker4.showInfoWindow();
        tencentMap.setOnInfoWindowClickListener(new TencentMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.showInfoWindow();
            }

            @Override
            public void onInfoWindowClickLocation(int i, int i1, int i2, int i3) {

            }
        });


    }

    private PolylineOptions setLineStyle(int type) {
        PolylineOptions polylineOptions = new PolylineOptions().addAll(getLatlons(type)).lineCap(true);
        switch (type) {
            case 0:
                //设置折线颜色、宽度
                polylineOptions
                        .color(0xff00ff00)
                        .width(5f);
                break;
            case 1:
                List<Integer> list = new ArrayList<>();
                list.add(35);
                list.add(20);
                polylineOptions
                        .lineType(PolylineOptions.LineType.LINE_TYPE_IMAGEINARYLINE)
                        .width(10)
                        .pattern(list);
                break;
            case 2:
                //线路颜色值纹理图片里的颜色索引
                polylineOptions
                        .colorType(PolylineOptions.ColorType.LINE_COLOR_TEXTURE)
                        .color(PolylineOptions.Colors.GREEN)
                        .colorTexture(BitmapDescriptorFactory.fromAsset("color_texture.png"));
                break;
            case 3:
                polylineOptions
                        .arrow(true)
                        .arrowSpacing(30)
                        .arrowTexture(BitmapDescriptorFactory.fromAsset("color_arrow_texture.png"));
                break;

        }
        return polylineOptions;
    }

    private List<LatLng> getLatlons(int type) {
        List<LatLng> latLngs = new ArrayList<LatLng>();
        switch (type) {
            case 0:
                //设置折线颜色、宽度
                latLngs.add(fromPoint);
                latLngs.add(shanghai);
                break;
            case 1:
                //设置折线颜色、宽度
                latLngs.add(changsha);
                latLngs.add(toPoint);
                break;
            case 3:
                //设置折线颜色、宽度
                latLngs.addAll(lines);
                break;
        }
        return latLngs;
    }
    private ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
        @Override
        public void onScrollProgressChanged(float currentProgress) {
            if (currentProgress >= 0) {
//                float precent = 255 * currentProgress;
//                if (precent > 255) {
//                    precent = 255;
//                } else if (precent < 0) {
//                    precent = 0;
//                }
//                mScrollLayout.getBackground().setAlpha(255 - (int) precent);
//                Log.i("TAG","currentProgress>=0"+"/////");

            }
            if (currentProgress < 0) {
//                if (currentProgress != 0.0f && currentProgress != 1.0f && currentProgress != -1.0f) {
//                    CameraUpdate cameraSigma =
//                            CameraUpdateFactory.newCameraPosition(new CameraPosition(
//                                    middle,
//                                    1.5f * Math.abs(currentProgress) + 2.5f,
//                                    0f,
//                                    0f));
//                    //移动地图
//                    tencentMap.moveCamera(cameraSigma);
                    Log.i("TAG","currentProgress < 0"+"/////");
               // }
            } else {
//                if (currentProgress != 0.0f && currentProgress != 1.0f && currentProgress != -1.0f) {
//                    CameraUpdate cameraSigma =
//                            CameraUpdateFactory.newCameraPosition(new CameraPosition(
//                                    middle,
//                                    2.5f * Math.abs(currentProgress) + 3.5f,
//                                    0f,
//                                    0f));
//                    //移动地图
//                    tencentMap.moveCamera(cameraSigma);
//                    Log.i("TAG","currentProgress默认"+"/////");
//                }
            }

          //  Log.e("currentProgress", "currentProgress：=" + currentProgress);

        }

        @Override
        public void onScrollFinished(ScrollLayout.Status currentStatus) {
            Log.e("currentStatus", currentStatus.toString()+"////");
            mCurrentStatus = currentStatus;
            if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
                CameraUpdate cameraSigma =
                        CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                middle,
                                5f,
                                0f,
                                0f));
                //移动地图
                tencentMap.moveCamera(cameraSigma);

            }
            if (currentStatus.equals(ScrollLayout.Status.OPENED)) {
                CameraUpdate cameraSigma =CameraUpdateFactory.newCameraPosition(new CameraPosition(


                        toPoint,
                                4.15f,
                                0f,
                                0f));
                //移动地图
                tencentMap.moveCamera(cameraSigma);
            }
        }

        @Override
        public void onChildScroll(int top) {
        }
    };

}

class ScreenUtil {
    /**
     * 获取屏幕内容高度
     *
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int result = 0;
        int resourceId = activity.getResources()
                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        int screenHeight = dm.heightPixels - result;
        return screenHeight;
    }

    /**
     * dp转px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}

class DataSources {
    /**
     * 物流列表的数据
     */
    public static List<LogisticsStateBean> getData() {
        List<LogisticsStateBean> data = new ArrayList<>();
        data.add(new LogisticsStateBean("2020-06-30 13:37:57", "收件地址:广东省深圳市宝安区新安公司 收件人: 陆黄星 电话133****9918\""));
        data.add(new LogisticsStateBean("2020-06-26 08:27:10", "【长沙转运中心】 已发出"));
        data.add(new LogisticsStateBean("2020-06-26 04:38:32", "【长沙转运中心】 已发出 下一站 【深圳转运中心】"));
        data.add(new LogisticsStateBean("2020-06-26 01:27:49", "【上海转运中心】 已发出 下一站 【长沙转运中心】"));
        data.add(new LogisticsStateBean("2020-06-26 01:17:19", "【上海转运中心】 已收入"));
        data.add(new LogisticsStateBean("2020-06-25 18:34:28", "【河北省保定市容城县公司】 已发出 下一站 【上海转运中心】"));
        data.add(new LogisticsStateBean("2020-06-25 18:33:23", "【河北省保定市容城县公司】 已打包"));
        data.add(new LogisticsStateBean("2020-06-25 18:27:21", "【河北省保定市容城县公司】 已收件"));
        return data;
    }
}
