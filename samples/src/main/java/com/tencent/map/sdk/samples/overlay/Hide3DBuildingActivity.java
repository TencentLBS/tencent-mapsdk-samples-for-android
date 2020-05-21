package com.tencent.map.sdk.samples.overlay;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

public class Hide3DBuildingActivity extends AbsMapActivity {
    private TencentMap mTencentMap;
    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, TencentMap pTencentMap) {
        super.onCreate(savedInstanceState, pTencentMap);
        mTextView=findViewById(R.id.tv_hide_3d);
        mTencentMap = pTencentMap;
        mTencentMap.setIndoorEnabled(true);
        mTencentMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(new LatLng(39.979381, 116.314128), 18));

        mTencentMap.setOnCameraChangeListener(new TencentMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                String text="倾斜："+cameraPosition.tilt+","+"旋转："+cameraPosition.bearing+","+"缩放："+cameraPosition.zoom+
                        "倾斜角度和旋转角度为0时，关闭3D楼快效果";
                mTextView.setText(text);
                if (cameraPosition.tilt < 20) {
                    mTencentMap.setBuildingEnable(false);
                } else {
                    mTencentMap.setBuildingEnable(true);
                }
            }

            @Override
            public void onCameraChangeFinished(CameraPosition cameraPosition) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hide3_d_building;
    }
}
