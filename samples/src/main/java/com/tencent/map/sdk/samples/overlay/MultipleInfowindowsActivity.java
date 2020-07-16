package com.tencent.map.sdk.samples.overlay;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptor;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MultipleInfowindowsActivity extends AbsMapActivity {
    private TencentMap tencentMap;
    private ArrayList<LatLng> latLngArrayList;
    private ArrayList<String> titles;
    private ArrayList<String> snippets;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, TencentMap pTencentMap) {
        tencentMap = pTencentMap;
        setupLocation();
    }

    private void setupLocation() {
        latLngArrayList = new ArrayList<>();
        LatLng latLng1 = new LatLng(39.873911, 116.379548);
        latLngArrayList.add(latLng1);
        LatLng latLng2 = new LatLng(39.946595, 116.387788);
        latLngArrayList.add(latLng2);
        LatLng latLng3 = new LatLng(39.980277, 116.305390);
        latLngArrayList.add(latLng3);

        titles = new ArrayList<>();
        snippets = new ArrayList<>();

        titles.add("西城区");
        titles.add("什刹海公园");
        titles.add("海淀区");

        snippets.add("北京市西城区南二环");
        snippets.add("北京市西城区鼓楼西什刹海公园");
        snippets.add("北京市海淀区苏州街29号");

        tencentMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(39.873911, 116.379548), 10));

        //开启多窗口模式
        tencentMap.enableMultipleInfowindow(true);

        for (int i = 0; i < latLngArrayList.size(); i++) {
            Marker marker = tencentMap.addMarker(new MarkerOptions().
                    position(latLngArrayList.get(i)).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(titles.get(i)).snippet(snippets.get(i)));

            tencentMap.setInfoWindowAdapter(new TencentMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(final Marker marker) {
                    //创建无边框自定义View
                    View view = View.inflate(MultipleInfowindowsActivity.this, R.layout.custom_view, null);
                    TextView textView = ((TextView) view.findViewById(R.id.title));
                    String content = marker.getTitle() + "\n" + marker.getSnippet() + "\n";
                    textView.setText(content);
                    return view;
                }

                @Override
                public View getInfoContents(final Marker marker) {
                    //创建带边框自定义View
                    return null;
                }
            });
            marker.showInfoWindow();
        }
    }
}
