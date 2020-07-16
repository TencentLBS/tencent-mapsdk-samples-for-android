package com.tencent.map.sdk.samples.track;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.map.sdk.samples.MainActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.map.track.TencentTrackClient;
import com.tencent.map.track.TencentTrackConfig;
import com.tencent.map.track.TencentTrackListener;
import com.tencent.map.track.TencentTrackLocation;

import com.tencent.map.track.search.ResultListener;
import com.tencent.map.track.search.param.TrackParam;
import com.tencent.map.track.search.result.TrackResult;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.Animation;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrackAcquisitionUploadActivity extends AppCompatActivity implements TencentTrackListener {
    private MapView mapView;
    private TencentMap tencentMap;
    private TextView tvCollection;
    private TextView tvHistory;
    private TencentTrackClient mClient;
    private String serviceId = "100007";
    private String objectId = "objectID";
    private LatLng latLng;
    //获取采集时间
    private long collectionTime;
    //获取历史采集的当前时间
    private long currentTime;
    private List<LatLng> latLngs;
    private boolean isFisrtUpload = false;
    private Timer timer = new Timer(true);
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //获取历史轨迹点
                getHistoryTrack();
            }
        }
    };

    private TimerTask task = new TimerTask() {
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_acquisition_upload);
        latLngs = new ArrayList<>();
        mapView = findViewById(R.id.map_track);
        tencentMap = mapView.getMap();
        tvCollection = findViewById(R.id.tv_location);
        tvHistory = findViewById(R.id.tv_history);
        mClient = TencentTrackClient.getInstance(this); //获取轨迹服务实例
        mClient.prepare(this);
        collectionTime = System.currentTimeMillis(); //获取采集位置信息时间
        isFisrtUpload = false;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public void onBindService() {
        Log.i("TAG", "TencentTrackService 已可用，可以修改追踪状态");
        TencentTrackConfig tencentTrackConfig = new TencentTrackConfig.Builder()
                .serviceId("100007")
                .objectId("objectID")
                .locationInterval(2000)
                .uploadInterval(4000)
                .locationMode(TencentTrackConfig.LocationMode.High_Accuracy)
                .build();

        mClient.startTrack(tencentTrackConfig);

    }

    @Override
    public void onUnbindService() {
        Log.i("TAG", "TencentTrackService 不可用");
    }

    @Override
    public void onTrackStatusChanged(int status, String message) {
        Log.i("TAG", message);
    }

    @Override
    public void onTrackLocationChanged(final TencentTrackLocation location, int errorCode, String msg) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (location == null) {
                    return;
                }
                tvCollection.setText("位置信息：" + location.getLatitude() + "," + location.getLongitude());
            }
        });


        if (isFisrtUpload == false) {
            timer.schedule(task, 0, 3000);
            isFisrtUpload = true;
        }
    }

    private void getHistoryTrack() {
        currentTime = System.currentTimeMillis();
        TrackParam param = new TrackParam(serviceId, objectId, collectionTime, currentTime);
        mClient.getTrack(param, new ResultListener<TrackResult>() {

            @Override
            public void onGetResult(TrackResult result) {
                if (result == null) {
                    tvHistory.setText("getTrackObject track failed");
                    return;
                }
                if (result.getCount() == 0) {
                    tvHistory.setText("历史采集点：" + result.getCount());
                    return;
                }

                tvHistory.setText("历史采集点：" + result.getCount());

                List<TencentTrackLocation> points = result.getPoints();
                for (TencentTrackLocation point : points) {
                    latLngs.add(new LatLng(point.getLatitude(), point.getLongitude()));
                }
                //将采集的经纬度坐标绘制成线
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(latLngs)
                        .color(PolylineOptions.Colors.RED)
                        .width(25)
                        .borderWidth(5);
                tencentMap.addPolyline(polylineOptions);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.track, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_track).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_track) {
            tencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
        }

        return super.onOptionsItemSelected(item);
    }
}
