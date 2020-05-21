package com.tencent.map.sdk.samples.basic;

import androidx.annotation.Nullable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.TextView;

import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptor;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.Polygon;
import com.tencent.tencentmap.mapsdk.maps.model.PolygonOptions;

public class MobileGeofencingActivity extends AbsMapActivity {
    private TencentMap mTencentMap;
    private Marker marker;
    Polygon polygon;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, TencentMap pTencentMap) {
        super.onCreate(savedInstanceState, pTencentMap);
        textView = findViewById(R.id.tv_geofencing);
        mTencentMap = pTencentMap;
        LatLng position = new LatLng(39.984104, 116.307503);
        mTencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getBitMap(R.mipmap.marker));
        MarkerOptions options = new MarkerOptions().position(position).icon(bitmapDescriptor);
        marker = mTencentMap.addMarker(options);
        //设置Marker支持拖拽
        marker.setDraggable(true);
        //绘制矩形
        LatLng[] latLngs = {
                new LatLng(39.984864, 116.305756),
                new LatLng(39.983618, 116.305848),
                new LatLng(39.982347, 116.305966),
                new LatLng(39.982412, 116.308111),
                new LatLng(39.984122, 116.308224),
                new LatLng(39.984955, 116.308099),
        };
        polygon = mTencentMap.addPolygon(new PolygonOptions().
                add(latLngs)
                .fillColor(Color.TRANSPARENT)
                .strokeWidth(5)
                .visible(true)
                .strokeColor(Color.BLUE));

        mTencentMap.setOnMarkerDragListener(new TencentMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                //当前手指按住Marker拖拽开始
                LatLng position = marker.getPosition();
                if (polygon.contains(position)) {
                    //在围栏内
                    textView.setText("在围栏内");
                } else {
                    //在围栏外
                    textView.setText("在围栏外");
                }

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                //当前手指按住Marker拖拽中
                LatLng position = marker.getPosition();
                if (polygon.contains(position)) {
                    //在围栏内
                    textView.setText("在围栏内");
                } else {
                    //在围栏外
                    textView.setText("在围栏外");
                }
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                //当前手指按住Marker拖拽结束
                LatLng position = marker.getPosition();
                if (polygon.contains(position)) {
                    //在围栏内
                    textView.setText("在围栏内");
                } else {
                    //在围栏外
                    textView.setText("在围栏外");
                }
            }
        });
    }

    private Bitmap getBitMap(int resourceId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = 100;
        int newHeight = 100;
        float widthScale = ((float) newWidth) / width;
        float heightScale = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(widthScale, heightScale);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return bitmap;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mobile_geofencing;
    }


}
