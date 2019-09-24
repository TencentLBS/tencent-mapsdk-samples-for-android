/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.map.sdk.samples.tools;

import com.tencent.map.lib.basemap.data.DoublePoint;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

public class SphericalMercatorProjection {

    /**
     * Radius of the earth in meter.
     */
    public static double EARTH_RADIUS = 6378137;

    final double mWorldWidth;

    /**
     * Mercator projection of the earth.
     */
    public SphericalMercatorProjection() {
        mWorldWidth = 2 * Math.PI * EARTH_RADIUS;
    }

    public SphericalMercatorProjection(final double worldWidth) {
        mWorldWidth = worldWidth;
    }

    /**
     * 转换成墨卡托坐标，原点在左上角，x 轴正方向向左，y 轴向下。
     *
     * @param latLng
     * @return
     */
    public DoublePoint toPoint(final LatLng latLng) {
        final double x = latLng.longitude / 360 + .5;
        final double siny = Math.sin(Math.toRadians(latLng.latitude));
        final double y = 0.5 * Math.log((1 + siny) / (1 - siny)) / -(2 * Math.PI) + .5;

        return new DoublePoint(x * mWorldWidth, y * mWorldWidth);
    }

    public LatLng toLatLng(DoublePoint point) {
        final double x = point.x / mWorldWidth - 0.5;
        final double lng = x * 360;

        double y = .5 - (point.y / mWorldWidth);
        final double lat = 90 - Math.toDegrees(Math.atan(Math.exp(-y * 2 * Math.PI)) * 2);

        return new LatLng(lat, lng);
    }

    /**
     * 计算两个经纬度坐标间的直线距离
     *
     * @param p1
     * @param p2
     * @return p1、p2 间的距离,单位:米
     */
    public double distanceBetween(LatLng p1, LatLng p2) {
        double x1 = p1.longitude;
        double y1 = p1.latitude;
        double x2 = p2.longitude;
        double y2 = p2.latitude;

        double NF_pi = 0.01745329251994329; // 弧度 PI/180

        x1 *= NF_pi;
        y1 *= NF_pi;
        x2 *= NF_pi;
        y2 *= NF_pi;
        double sinx1 = Math.sin(x1);
        double siny1 = Math.sin(y1);
        double cosx1 = Math.cos(x1);
        double cosy1 = Math.cos(y1);
        double sinx2 = Math.sin(x2);
        double siny2 = Math.sin(y2);
        double cosx2 = Math.cos(x2);
        double cosy2 = Math.cos(y2);
        double[] v1 = new double[3];
        double[] v2 = new double[3];
        v1[0] = cosy1 * cosx1;
        v1[1] = cosy1 * sinx1;
        v1[2] = siny1;
        v2[0] = cosy2 * cosx2;
        v2[1] = cosy2 * sinx2;
        v2[2] = siny2;
        //Haversine formula
        double dist = Math.sqrt((v1[0] - v2[0]) * (v1[0] - v2[0])
                + (v1[1] - v2[1]) * (v1[1] - v2[1]) + (v1[2] - v2[2])
                * (v1[2] - v2[2]));

        return Math.asin(dist / 2) * mWorldWidth * Math.PI;
    }
}
