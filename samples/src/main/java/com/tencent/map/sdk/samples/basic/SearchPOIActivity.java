package com.tencent.map.sdk.samples.basic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.param.SearchParam;
import com.tencent.lbssearch.object.param.SuggestionParam;
import com.tencent.lbssearch.object.result.SearchResultObject;
import com.tencent.lbssearch.object.result.SuggestionResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.sdk.samples.AbsMapActivity;
import com.tencent.map.sdk.samples.R;
import com.tencent.map.sdk.samples.tools.location.TencentLocationHelper;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.Animation;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle;
import com.tencent.tencentmap.mapsdk.maps.model.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图选点
 */
public class SearchPOIActivity extends AbsMapActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener, View.OnFocusChangeListener, TencentMap.OnCameraChangeListener {

    private TencentSearch mTencentSearch;
    private SearchView mSearchView;
    private TencentMap mMap;
    private RecyclerView mRecyclerView;
    private SearchPoiAdapter mSearchPoiAdapter;
    private List<PoiInfo> mPoiInfos;
    private TencentLocationHelper mTencentLocationHelper;

    /**
     * 是否能进行下一步操作
     */
    private boolean mIsEnableNext = true;
    private Marker mPoiMarker;

    /**
     * 是否运行使用搜索建议
     */
    private boolean mIsUseSug = true;
    private Marker mMapCenterPointerMarker;
    private LatLng mLatPosition;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_poi;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState, TencentMap pTencentMap) {
        super.onCreate(savedInstanceState, pTencentMap);

        mMap = pTencentMap;
        mTencentSearch = new TencentSearch(this);

        //数据界面初始化
        mRecyclerView = findViewById(R.id.layout_recycle_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchPoiAdapter = new SearchPoiAdapter(this);
        mPoiInfos = new ArrayList<>();
        mSearchPoiAdapter.submitList(mPoiInfos);
        mRecyclerView.setAdapter(mSearchPoiAdapter);
        mMap.setOnCameraChangeListener(this);

        //定位设置
        mTencentLocationHelper = new TencentLocationHelper(this);
        mMap.setLocationSource(mTencentLocationHelper);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                requestLocation();
            }
        } else if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }
    }

    @Override
    protected String[] onRequestPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int ret : grantResults) {
            if (ret == PackageManager.PERMISSION_DENIED) {
                mIsEnableNext = false;
                Toast.makeText(this, "授权不成功，无法使用示例", Toast.LENGTH_LONG).show();
                return;
            }
        }

        requestLocation();
    }

    /**
     * 请求定位
     */
    private void requestLocation() {
        if (!mIsEnableNext) {
            return;
        }

        mIsEnableNext = false;
        mTencentLocationHelper.startLocation(new TencentLocationHelper.LocationCallback() {
            @Override
            public void onStatus(boolean status, String source) {
                if (status) {
                    mMap.setMyLocationEnabled(true);
                    //设置地图不跟随定位移动地图中心
                    mMap.setMyLocationStyle(new MyLocationStyle()
                            .myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER));
                }
            }

            @Override
            public void onLocation(LatLng pLastLocation) {
                if (pLastLocation != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(pLastLocation), new TencentMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            mMapCenterPointerMarker = mMap.addMarker(new MarkerOptions(pLastLocation).icon(
                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                            Point point = mMap.getProjection().toScreenLocation(pLastLocation);
                            mMapCenterPointerMarker.setFixingPoint(point.x, point.y);
                            mMapCenterPointerMarker.setFixingPointEnable(true);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    mIsEnableNext = true;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_poi, menu);
        mSearchView = (SearchView) menu.findItem(R.id.menu_find_poi_search).getActionView();
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnCloseListener(this);
        mSearchView.setOnQueryTextFocusChangeListener(this);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_find_poi_search).setEnabled(mIsEnableNext);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (TextUtils.isEmpty(query)) {
            clearList();
            return false;
        }
        mIsUseSug = false;
        //根据关键字，请求搜索列表
        TencentLocation location = mTencentLocationHelper.getLastLocation();
        SearchParam param = new SearchParam();
        param.keyword(query).boundary(new SearchParam.Region(location.getCity()));
        mTencentSearch.search(param, new HttpResponseListener<SearchResultObject>() {

            @Override
            public void onSuccess(int pI, SearchResultObject pSearchResultObject) {
                if (pSearchResultObject != null) {
                    Log.i("TAG", "onScuess()" + "////");
                    mRecyclerView.setVisibility(View.VISIBLE);
                    updateSearchPoiList(pSearchResultObject.data);
                }
            }

            @Override
            public void onFailure(int pI, String pS, Throwable pThrowable) {
                mRecyclerView.setVisibility(View.INVISIBLE);
                Log.e("tencent-map-samples", pS, pThrowable);
            }
        });
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mIsUseSug = true;
            clearList();
            return false;
        }

        if (!mIsUseSug) {
            return false;
        }

        //搜索建议
        TencentLocation location = mTencentLocationHelper.getLastLocation();
        SuggestionParam param = new SuggestionParam();
        param.keyword(newText).region(location.getCity()).location(mTencentLocationHelper.getLastLocationLatLng());
        mTencentSearch.suggestion(param, new HttpResponseListener<SuggestionResultObject>() {
            @Override
            public void onSuccess(int pI, SuggestionResultObject pSuggestionResultObject) {
                if (pSuggestionResultObject != null && mIsUseSug) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    updateSuggestionPoiList(pSuggestionResultObject.data);
                }
            }

            @Override
            public void onFailure(int pI, String pS, Throwable pThrowable) {
                mRecyclerView.setVisibility(View.INVISIBLE);
                Log.e("tencent-map-samples", pS, pThrowable);
            }
        });
        return true;
    }

    /**
     * 更新搜索POI结果
     *
     * @param pData
     */
    private void updateSearchPoiList(List<SearchResultObject.SearchResultData> pData) {
        if (!pData.isEmpty()) {
            mPoiInfos.clear();
            for (SearchResultObject.SearchResultData data : pData) {
                PoiInfo poiInfo = new PoiInfo();
                poiInfo.id = data.id;
                poiInfo.name = data.title;
                poiInfo.address = data.address;
                poiInfo.latLng = data.latLng;
                poiInfo.source = PoiInfo.SOURCE_SEARCH;
                mPoiInfos.add(poiInfo);
            }

            mSearchPoiAdapter.notifyDataSetChanged();
        } else {
            clearList();
        }
    }

    /**
     * 更新搜索建议结果
     *
     * @param pData
     */
    private void updateSuggestionPoiList(List<SuggestionResultObject.SuggestionData> pData) {

        if (!pData.isEmpty()) {
            mPoiInfos.clear();
            for (SuggestionResultObject.SuggestionData data : pData) {
                PoiInfo poiInfo = new PoiInfo();
                poiInfo.id = data.id;
                poiInfo.name = data.title;
                poiInfo.latLng = data.latLng;
                poiInfo.source = PoiInfo.SOURCE_SUG;
                mPoiInfos.add(poiInfo);
            }

            mSearchPoiAdapter.notifyDataSetChanged();
        } else {
            clearList();
        }

    }

    /**
     * 在地图上显示POI
     *
     * @param pInfo
     */
    private void performShowPoiInMap(PoiInfo pInfo) {

        if (checkMapInvalid()) {
            return;
        }

        if (mPoiMarker != null) {
            mPoiMarker.remove();
        }

        mPoiMarker = mMap.addMarker(new MarkerOptions(pInfo.latLng).title(pInfo.name).snippet(pInfo.address));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(pInfo.latLng));
        mSearchView.clearFocus();
    }


    /**
     * 清空列表
     *
     * @return
     */
    private boolean clearList() {
        if (!mPoiInfos.isEmpty()) {
            mPoiInfos.clear();
            mSearchPoiAdapter.notifyDataSetChanged();
            return true;
        }

        return false;
    }

    @Override
    public boolean onClose() {
        return clearList();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        //mRecyclerView.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    @Override
    public void onCameraChangeFinished(CameraPosition cameraPosition) {
        mLatPosition = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
        //获取当前地图中心点，请求搜索接口
        SearchParam.Nearby nearby = new SearchParam.Nearby();
        nearby.point(mLatPosition);
        nearby.r(1000);
        nearby.autoExtend(true);
        SearchParam param = new SearchParam("北京", nearby);
        if (mTencentSearch != null) {
            mTencentSearch.search(param, new HttpResponseListener<SearchResultObject>() {
                @Override
                public void onSuccess(int i, SearchResultObject baseObject) {
                    if (baseObject != null) {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        updateSearchPoiList(baseObject.data);
                    }
                }

                @Override
                public void onFailure(int i, String s, Throwable throwable) {
                    mRecyclerView.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private class PoiInfo {
        static final int SOURCE_SUG = 0;
        static final int SOURCE_SEARCH = 1;
        int source;
        String id;
        String name;
        String address;
        LatLng latLng;
    }

    private class SearchPoiAdapter extends ListAdapter<PoiInfo, SearchPoiItemViewHolder> {

        Context mContext;

        SearchPoiAdapter(Context pContext) {
            super(new DiffUtil.ItemCallback<PoiInfo>() {
                @Override
                public boolean areItemsTheSame(@NonNull PoiInfo oldItem, @NonNull PoiInfo newItem) {
                    return oldItem.id.equals(newItem.id);
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull PoiInfo oldItem, @NonNull PoiInfo newItem) {
                    return oldItem.equals(newItem);
                }
            });

            mContext = pContext;
        }

        @NonNull
        @Override
        public SearchPoiItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SearchPoiItemViewHolder(this, parent, viewType);
        }


        @Override
        public int getItemViewType(int position) {
            PoiInfo poiInfo = getItem(position);
            return poiInfo.source;
        }

        @Override
        public void onBindViewHolder(@NonNull SearchPoiItemViewHolder holder, int position) {
            holder.bindView(getItem(position));
        }

        public void onItemClick(PoiInfo pItem) {
            if (pItem.source == PoiInfo.SOURCE_SUG) {
                mIsUseSug = false;
                mSearchView.setQuery(pItem.name, true);
            } else if (pItem.source == PoiInfo.SOURCE_SEARCH) {
                performShowPoiInMap(pItem);
            }
        }
    }


    private static class SearchPoiItemViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private TextView mSubTitle;
        private SearchPoiAdapter mAdapter;

        SearchPoiItemViewHolder(SearchPoiAdapter pAdapter, ViewGroup pParent, int pViewType) {
            super(LayoutInflater.from(pAdapter.mContext).inflate(getItemLayoutId(pViewType), pParent, false));
            mAdapter = pAdapter;
            mTitle = itemView.findViewById(android.R.id.text1);
            mSubTitle = itemView.findViewById(android.R.id.text2);
        }

        private static int getItemLayoutId(int pViewType) {
            if (pViewType == PoiInfo.SOURCE_SUG) {
                return android.R.layout.simple_list_item_1;
            } else if (pViewType == PoiInfo.SOURCE_SEARCH) {
                return android.R.layout.simple_list_item_2;
            }
            return android.R.layout.simple_list_item_2;
        }

        public void bindView(PoiInfo pItem) {
            mTitle.setText(pItem.name);
            if (mSubTitle != null) {
                mSubTitle.setText(pItem.address);
                mSubTitle.setVisibility(TextUtils.isEmpty(pItem.address) ? View.GONE : View.VISIBLE);
            }

            itemView.setOnClickListener(v -> mAdapter.onItemClick(pItem));
        }
    }

}
