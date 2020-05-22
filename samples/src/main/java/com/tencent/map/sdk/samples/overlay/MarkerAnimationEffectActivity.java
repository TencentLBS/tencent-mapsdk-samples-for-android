package com.tencent.map.sdk.samples.overlay;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.Animation;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.ScaleAnimation;
import com.tencent.tencentmap.mapsdk.maps.model.TranslateAnimation;

public class MarkerAnimationEffectActivity extends AbsMapActivity implements TencentMap.OnCameraChangeListener {
    private TencentMap mTencentMap;
    public Marker marker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, TencentMap pTencentMap) {
        super.onCreate(savedInstanceState, pTencentMap);
        mTencentMap = pTencentMap;
        LatLng latLng = new LatLng(39.984066, 116.307548);
        CameraUpdate cameraSigma =
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        latLng,
                        15,
                        0f,
                        0f));
        //移动地图
        mTencentMap.moveCamera(cameraSigma);
        mTencentMap.setOnCameraChangeListener(this);
        //addMarkerInScreenCenter(latLng);
        mTencentMap.setOnMapLoadedCallback(new TencentMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                addMarkerInScreenCenter(latLng);
            }
        });
    }

    private void addMarkerInScreenCenter(LatLng locationLatLng) {
        LatLng latLng = mTencentMap.getCameraPosition().target;
        Point screenPosition = mTencentMap.getProjection().toScreenLocation(latLng);
        marker = mTencentMap.addMarker(new MarkerOptions(locationLatLng).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        //设置Marker在屏幕上,不跟随地图移动
        marker.setFixingPoint(screenPosition.x, screenPosition.y);
        marker.setZIndex(1);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_marker_animation_effect;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.marker_skip, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_marker_ship).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_marker_ship) {
            //给Marker添加上浮动画
            Animation animation = new ScaleAnimation(0, 1, 0, 1);
            animation.setDuration(2000);
            marker.setAnimation(animation);
            marker.startAnimation();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }


    @Override
    public void onCameraChangeFinished(CameraPosition cameraPosition) {
        LatLng latLng = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
        Point point = mTencentMap.getProjection().toScreenLocation(latLng);
        if (marker != null) {
            marker.setFixingPoint(point.x, point.y);
            marker.setFixingPointEnable(true);
            point.y -= dip2px(this, 125);
            LatLng target = mTencentMap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            animation.setDuration(600);
            //给Marker添加跳跃动画
            marker.setAnimation(animation);
            marker.startAnimation();
        }
    }

    //dip和px转换
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
