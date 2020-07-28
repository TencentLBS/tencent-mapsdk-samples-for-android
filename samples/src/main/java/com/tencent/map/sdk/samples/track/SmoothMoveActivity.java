package com.tencent.map.sdk.samples.track;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;
import com.tencent.tencentmap.mapsdk.vector.utils.animation.MarkerTranslateAnimator;

import java.util.Arrays;

public class SmoothMoveActivity extends AbsMapActivity {

    private final String mLine = "39.98409,116.30804,39.98409,116.3081,39.98409,116.3081,39.98397,116.30809,39.9823,116.30809,39.9811,116.30817,39.9811,116.30817,39.97918,116.308266,39.97918,116.308266,39.9791,116.30827,39.9791,116.30827,39.979008,116.3083,39.978756,116.3084,39.978386,116.3086,39.977867,116.30884,39.977547,116.308914,39.976845,116.308914,39.975826,116.308945,39.975826,116.308945,39.975666,116.30901,39.975716,116.310486,39.975716,116.310486,39.975754,116.31129,39.975754,116.31129,39.975784,116.31241,39.975822,116.31327,39.97581,116.31352,39.97588,116.31591,39.97588,116.31591,39.97591,116.31735,39.97591,116.31735,39.97593,116.31815,39.975967,116.31879,39.975986,116.32034,39.976055,116.32211,39.976086,116.323395,39.976105,116.32514,39.976173,116.32631,39.976254,116.32811,39.976265,116.3288,39.976345,116.33123,39.976357,116.33198,39.976418,116.33346,39.976418,116.33346,39.97653,116.333755,39.97653,116.333755,39.978157,116.333664,39.978157,116.333664,39.978195,116.33509,39.978195,116.33509,39.978226,116.33625,39.978226,116.33625,39.97823,116.33656,39.97823,116.33656,39.978256,116.33791,39.978256,116.33791,39.978016,116.33789,39.977047,116.33791,39.977047,116.33791,39.97706,116.33768,39.97706,116.33768,39.976967,116.33706,39.976967,116.33697";
    private TencentMap mMap;
    private Marker mCarMarker;
    private LatLng[] mCarLatLngArray;
    private MarkerTranslateAnimator mAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, TencentMap pTencentMap) {
        super.onCreate(savedInstanceState, pTencentMap);
        mMap = pTencentMap;

        setupLineAndData();
    }

    private void setupLineAndData() {
        if (checkMapInvalid()) {
            return;
        }

        //解析路线
        String[] linePointsStr = mLine.split(",");
        mCarLatLngArray = new LatLng[linePointsStr.length / 2];
        for (int i = 0; i < mCarLatLngArray.length; i++) {
            double latitude = Double.parseDouble(linePointsStr[i * 2]);
            double longitude = Double.parseDouble(linePointsStr[i * 2 + 1]);
            mCarLatLngArray[i] = new LatLng(latitude, longitude);
        }

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(mCarLatLngArray)
                // 折线设置圆形线头
                .lineCap(true)
                // 折线的颜色
                .color(PolylineOptions.Colors.GREEN)
                // 折线宽度为25像素
                .width(25)
                // 必须打开这个开关，允许在线上绘制纹理
                .arrow(true)
                // 支持设置纹理的间距
                .arrowSpacing(30)
                // 设置纹理图片
                .arrowTexture(
                        BitmapDescriptorFactory
                                .fromAsset("color_arrow_texture.png"));

        //添加小车路线
        mMap.addPolyline(polylineOptions);

        //添加小车
        LatLng carLatLng = mCarLatLngArray[0];
        mCarMarker = mMap.addMarker(
                new MarkerOptions(carLatLng)
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.taxi))
                        .flat(true)
                        .clockwise(false));

        //创建移动动画
        mAnimator = new MarkerTranslateAnimator(mCarMarker, 50 * 100, mCarLatLngArray, true);

        //调整最佳视界
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                LatLngBounds.builder().include(Arrays.asList(mCarLatLngArray)).build(), 50));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.smooth_move, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_smooth_start).setVisible(!mAnimator.getAnimatorSet().isRunning());
        menu.findItem(R.id.menu_smooth_end).setVisible(mAnimator.getAnimatorSet().isRunning());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_smooth_start:
                mAnimator.startAnimation();
                break;
            case R.id.menu_smooth_end:
                mAnimator.endAnimation();
                break;
        }
        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }
}
