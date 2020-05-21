package com.tencent.map.sdk.samples.basic;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.MainActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapCenterActivity extends AbsMapActivity {
    private TencentMap mMap;
    private Marker mMapCenterMarker;
    private boolean mIsAdded;

    private LatLng centerSH = new LatLng(31.238068, 121.501654);// 上海市经纬度
    private LatLng centerBJ = new LatLng(39.904989, 116.405285);// 北京市经纬度
    private LatLng centerGZ = new LatLng(23.125178, 113.280637);// 广州市经纬度

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
    protected int getLayoutId() {
        return R.layout.activity_map_center;
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
                //performNewMapCenterMarker();
                BottomFullDialog dialog = new BottomFullDialog(this, R.style.BottomFullDialog);
                dialog.setCancelable(true);
                dialog.show();
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

    public class BottomFullDialog extends Dialog {
        public BottomFullDialog(Context context) {
            super(context);
        }

        public BottomFullDialog(Context context, int themeResId) {
            super(context, themeResId);
            View contentView = getLayoutInflater().inflate(
                    R.layout.popuplayout, null);
            Button beiijng = (Button) contentView.findViewById(R.id.btn_beijing);
            Button sahnghai = (Button) contentView.findViewById(R.id.btn_shanghai);
            Button guangzhou = (Button) contentView.findViewById(R.id.btn_guangzhou);
            beiijng.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //移动到相应的中心点
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(centerBJ));

                    if (mMapCenterMarker != null) {
                        mMapCenterMarker.remove();
                    }
                    mMapCenterMarker = mMap.addMarker(new MarkerOptions(centerBJ).title("北京"));
                    BottomFullDialog.this.dismiss();
                }
            });
            sahnghai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //移动到相应的中心点
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(centerSH));

                    if (mMapCenterMarker != null) {
                        mMapCenterMarker.remove();
                    }
                    mMapCenterMarker = mMap.addMarker(new MarkerOptions(centerSH));
                    BottomFullDialog.this.dismiss();
                }
            });
            guangzhou.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //移动到相应的中心点
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(centerGZ));
                    if (mMapCenterMarker != null) {
                        mMapCenterMarker.remove();
                    }
                    mMapCenterMarker = mMap.addMarker(new MarkerOptions(centerGZ));
                    BottomFullDialog.this.dismiss();
                }
            });

            super.setContentView(contentView);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setGravity(Gravity.BOTTOM);//设置显示在底部
            WindowManager windowManager = getWindow().getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.width = display.getWidth();//设置Dialog的宽度为屏幕宽度
            getWindow().setAttributes(layoutParams);
        }
    }
}


