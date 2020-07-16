package com.tencent.map.sdk.samples.overlay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MarkerPaddingActivity extends AppCompatActivity {
    public MapView mMapView;
    private TencentMap mTencentMap;
    private RecyclerView recyclerView;
    private MineModelAdpter mineModelAdpter;
    private ArrayList<MineModel> list;
    private BottomSheetBehavior<View> behavior;
    private View bottomSheet;
    private int heightPixels = 0;
    private int heightPixels2 = 0;
    private int offsetDistance = 0;
    private int peekHeight = 0;
    private int marginTop = 50;
    private ConstraintLayout mapcontain;
    private Marker marker;
    private static final LatLng NE = new LatLng(39.890000, 116.350777);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_padding);
        initView();
    }

    protected void initView() {
        list = new ArrayList<>();
        //获取屏幕高度
        heightPixels = getResources().getDisplayMetrics().heightPixels;
        System.out.println("heightPixels = " + heightPixels);

        float behaviorHeight = px2dp(this, Float.valueOf(heightPixels / 2));
        System.out.println("behaviorHeight = " + behaviorHeight);
        peekHeight =
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, behaviorHeight, getResources().getDisplayMetrics());

        recyclerView = findViewById(R.id.rec);
        mapcontain = findViewById(R.id.mapcontain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mineModelAdpter = new MineModelAdpter(this, R.layout.item, list);
        for (int i = 0; i < 20; i++) {
            list.add(new MineModel("腾讯总部", "100m内海淀区" + i));
        }
        recyclerView.setAdapter(mineModelAdpter);
        mineModelAdpter.notifyDataSetChanged();
        mMapView = findViewById(R.id.map);
        mTencentMap = mMapView.getMap();
        mTencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NE, 15));
        marker = mTencentMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(NE));
        bottomSheet = findViewById(R.id.bottom_sheet);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mMapView.getLayoutParams();
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        offsetDistance = layoutParams.topMargin;
        heightPixels2 = layoutParams.height;
        System.out.println("offsetDistance = " + offsetDistance);
        System.out.println("heightPixels2 = " + heightPixels2);

        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                if (bottomSheet.getHeight() > heightPixels - heightPixels / 3) {
                    //屏幕高度减去marinTop作为控件的Height
                    layoutParams.height = heightPixels - heightPixels / 3;
                    bottomSheet.setLayoutParams(layoutParams);
                }
                String state = "null";
                switch (newState) {
                    case 1:
                        state = "STATE_DRAGGING";//过渡状态此时用户正在向上或者向下拖动bottom sheet
                        break;
                    case 2:
                        state = "STATE_SETTLING"; // 视图从脱离手指自由滑动到最终停下的这一小段时间
                        break;
                    case 3:
                        state = "STATE_EXPANDED"; //处于完全展开的状态

                        break;
                    case 4:
                        state = "STATE_COLLAPSED"; //默认的折叠状态
                        break;
                    case 5:
                        state = "STATE_HIDDEN"; //下滑动完全隐藏 bottom sheet
                        break;
                }
                System.out.println("state = " + state);
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                Float distance = 0F;
                // distance = offsetDistance * v;
                distance = (heightPixels - heightPixels / 3) * v;
                ViewGroup.LayoutParams layoutParams = mapcontain.getLayoutParams();
                if (distance > 0) {
                    layoutParams.height = (int) (heightPixels - distance);
                     //mapcontain.setTranslationY( -distance);
                    // bottomSheet.setLayoutParams(layoutParams);
                    mapcontain.setLayoutParams(layoutParams);
                    Log.i("TAG","distance>0"+"/////");
                } else {

                }
            }
        });
    }

    @Override
    protected void onStart() {
        mMapView.onStart();
        super.onStart();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mMapView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    public static Float px2dp(Context context, Float f) {
        Float x = context.getResources().getDisplayMetrics().density;
        return f / x;
    }
}

class MineModelAdpter extends CommonAdapter<MineModel> implements MultiItemTypeAdapter.OnItemClickListener {
    public MineModelAdpter(Context context, int layoutId, List<MineModel> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, MineModel topBean, int position) {
        TextView view = (TextView) holder.getView(R.id.item_title);
        TextView view3 = (TextView) holder.getView(R.id.item_title3);
        view.setText(topBean.getTitel());
        view3.setText(topBean.getValue());
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder viewHolder, int i) {
        return false;
    }
}

class MineModel {
    private String titel;
    private String value;
    private String id;

    public MineModel(String titel, String value) {
        this.titel = titel;
        this.value = value;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

