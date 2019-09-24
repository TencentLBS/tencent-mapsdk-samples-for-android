package com.tencent.map.sdk.samples.tools.location;

import android.content.Context;
import android.location.Location;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.maps.LocationSource;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

public class TencentLocationHelper implements LocationSource, TencentLocationListener {

    private Context mContext;
    private OnLocationChangedListener mOnLocationChangedListener;
    private LocationCallback mLocationCallback;
    private LatLng mLastLocation;

    public TencentLocationHelper(Context pContext) {
        mContext = pContext;
    }

    @Override
    public void onLocationChanged(TencentLocation pTencentLocation, int pError, String pS) {
        if (pError == TencentLocation.ERROR_OK) {
            Location location = new Location("TencentLocation");
            location.setAccuracy(pTencentLocation.getAccuracy());
            location.setAltitude(pTencentLocation.getAltitude());
            location.setBearing(pTencentLocation.getBearing());
            location.setLatitude(pTencentLocation.getLatitude());
            location.setLongitude(pTencentLocation.getLongitude());
            location.setSpeed(pTencentLocation.getSpeed());
            location.setTime(pTencentLocation.getTime());

            if (mLastLocation == null) {
                mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (mLocationCallback != null) {
                    mLocationCallback.onLocation(mLastLocation);
                }
            }
            if (mOnLocationChangedListener != null) {
                mOnLocationChangedListener.onLocationChanged(location);
            }
        }
    }

    @Override
    public void onStatusUpdate(String pS, int pI, String pS1) {
        if (mLocationCallback != null) {
            mLocationCallback.onStatus(pI == TencentLocationListener.STATUS_ENABLED, pS);
        }
    }

    @Override
    public void activate(OnLocationChangedListener pOnLocationChangedListener) {
        mOnLocationChangedListener = pOnLocationChangedListener;
    }

    @Override
    public void deactivate() {
        TencentLocationManager.getInstance(mContext).removeUpdates(this);
    }

    public void startLocation(LocationCallback pLocationCallback) {
        mLocationCallback = pLocationCallback;
        TencentLocationManager.getInstance(mContext).requestLocationUpdates(
                TencentLocationRequest.create().setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_POI),
                this);
    }

    public LatLng getLastLocationLatLng() {
        TencentLocation location = TencentLocationManager.getInstance(mContext).getLastKnownLocation();
        if (location != null) {
            mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        return mLastLocation;
    }

    public TencentLocation getLastLocation() {
        return TencentLocationManager.getInstance(mContext).getLastKnownLocation();
    }

    public interface LocationCallback {
        void onStatus(boolean status, String source);

        void onLocation(LatLng pLastLocation);
    }
}
