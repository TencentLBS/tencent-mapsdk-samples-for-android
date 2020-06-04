# 示例中心

# 配置

## 设置 key
您可以在 demo 的清单文件中定义

```xml
<meta-data
    android:name="TencentMapSDK"
    android:value="您的 key" />
```

也可以使用工程里的方式在本地工程中创建 local.properties 并添加： 
I can fly.........

```
mapsdk.key=您的 key
```

也可以在您工程的module下的build.gradle中配置:
```
  defaultConfig {
      manifestPlaceholders = ["TencentMapSDK_KEY": 您的key]
  }
```
## gradle配置

在app module下的build.gradle配置以下依赖：

```
dependencies {
    //地图SDK依赖
    implementation 'com.tencent.map:tencent-map-vector-sdk:4.3.1'
    //地图定位SDK依赖
    implementation 'com.tencent.map.geolocation:TencentLocationSdk-openplatform:7.2.6'
    //地图组件库依赖
    implementation 'com.tencent.map:sdk-utilities:1.0.5'
}
```

# 示例设计目的

帮助用户快速实现需要多种接口组合而成的较为复杂的业务场景需求。

# 示例内容

## 基础功能

### 设置地图中心点

#### 介绍：

该示例展示了如何自定义设置地图的默认中心点。

#### 使用场景：

自定义设置地图的默认中心点，比如：使用当前定位、或者其他城市某个位置来作为中心点。或者设置初始地图的中心点。

#### 使用产品：

| 类         | 接口                                                         | 说明                                                     |
| ---------- | ------------------------------------------------------------ | -------------------------------------------------------- |
| TencentMap | **[getCameraPosition](https://lbs.qq.com/AndroidDocs/doc_3d/com/tencent/tencentmap/mapsdk/maps/TencentMap.html#getCameraPosition--)**():CameraPosition | 获取当前地图的状态（包括中心点、比例尺、旋转角、倾斜角） |

#### 方法讲解：

```
 //添加一个地图中心点标注
 marker = tencentMap.addMarker(new MarkerOptions(tencentMap.getCameraPosition().target));
```

```
 //设置一个新的地图中心点标注
  LatLng newLatLng = tencentMap.getCameraPosition().target;
  newLatLng.latitude += (Math.random() > 0.5 ? 1 : -1) * Math.random();
  newLatLng.longitude += (Math.random() > 0.5 ? 1 : -1) * Math.random();
  //把地图变换到指定的状态,生成一个把地图移动到指定的经纬度到屏幕中心的状态变化对象
  tencent.moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
  marker = mMap.addMarker(new MarkerOptions(newLatLng));
```

### 限制地图显示范围

#### 介绍：

该示例主要是用于向用户展示如何限制地图只显示固定区域。

#### 使用场景：

景区类应用程序中的地图只展示自己景区及周边范围。如故宫小程序之中只展示故宫景区范围的地图。

#### 使用产品：

| 类                    | 接口                                                         | 说明                                                         |
| --------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| TencentMap            | setRestrictBounds(LatLngBounds restrictBounds, RestrictBoundsFitMode fitMode) | 限制地图的显示范围。此接口同时会限制地图的最小缩放级别，用户还可以通过 [`setMinZoomLevel(int)`](https://lbs.qq.com/AndroidDocs/doc_3d/com/tencent/tencentmap/mapsdk/maps/TencentMap.html#setMinZoomLevel-int-) 修改自己需要的最小缩放级别获取需要的展示效果 |
| RestrictBoundsFitMode | FIT_WIDTH    FIT_HEIGHT                                      | 此模式会以bounds宽度或高度为参考值限制地图的控制区域，在最小限制级别时bounds的纵向区域可能显示不完整，可调用TencentMap.setMinZoomLevel(int)修改最小限制级别以展示完整的区域。 |

#### 方法讲解：

```
LatLngBounds latLngBounds = new LatLngBounds(new LatLng(39.923297, 116.402335),new LatLng(39.912666, 116.391907));
        //获取地图状态
        CameraPosition cameraPosition = tencentMap.getCameraPosition();
        //添加多边形,并设置多边形相关属性
        Polygon polygon = tencentMap.addPolygon(new PolygonOptions().add( 
                latLngBounds.getNorthEast(),
                latLngBounds.getSouthEast(),
                latLngBounds.getSouthWest(),
                latLngBounds.getNorthWest())
                .fillColor(Color.TRANSPARENT)
                .strokeWidth(2)
                .strokeColor(Color.BLUE));
        //方式一：基于宽度限制地图显示范围
        tencentMap.setRestrictBounds(latLngBounds, RestrictBoundsFitMode.FIT_WIDTH);
        //方式二：基于高度显示地图范围
        tencentMap.setRestrictBounds(latLngBounds, RestrictBoundsFitMode.FIT_HEIGHT);
```

### 适配marker显示范围

#### 介绍：

该示例主要用于向用户展示如何根据marker来调整最合适的地图显示范围。

#### 使用场景：

在一定范围内显示所有统一类型的POI，比如查询腾讯大厦旁边的所有银行，就需要把银行的marker都在一个屏幕展示出来。

#### 使用产品:

| 类                  | 接口                                                         | 说明                                                         |
| ------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| TencentMap          | animateCamera([CameraUpdate](https://lbs.qq.com/AndroidDocs/doc_3d/com/tencent/tencentmap/mapsdk/maps/CameraUpdate.html) cameraUpdate) | 以动画的方式把地图变换到指定的状态                           |
| CameraUpdateFactory | newLatLngBoundsWithMapCenter([LatLngBounds](https://lbs.qq.com/AndroidDocs/doc_3d/com/tencent/tencentmap/mapsdk/maps/model/LatLngBounds.html) latLngBounds, [LatLng](https://lbs.qq.com/AndroidDocs/doc_3d/com/tencent/tencentmap/mapsdk/maps/model/LatLng.html) mapCenter, int padding) | 以mapCenter为中心，将latLngBounds对应的区域刚好缩放到可视区域内，并且在这个范围四周加上padding像素填充 |

#### 方法讲解:

第一步：获取需要显示的Marker坐标集合

```
       ArrayList<LatLng> latLngArrayList;latLngArrayList = new ArrayList<>();
        LatLng latLng1 = new LatLng(39.90604,116.32168);
        latLngArrayList.add(latLng1);
        LatLng latLng2 = new LatLng(39.993098,116.336462);
        latLngArrayList.add(latLng2);
        LatLng latLng3 = new LatLng(39.8982,116.37509);
        latLngArrayList.add(latLng3);
        LatLng latLng4 = new LatLng(39.934059,116.451259);
        latLngArrayList.add(latLng4);
        LatLng latLng5 = new LatLng(39.954624,116.32296);
        latLngArrayList.add(latLng5);
        LatLng latLng6 = new LatLng(39.941474,116.416938);
        latLngArrayList.add(latLng6);
        LatLng latLng7 = new LatLng(39.947071,116.371438);
        latLngArrayList.add(latLng7);
        LatLng latLng8 = new LatLng(39.911171,116.411644);
        latLngArrayList.add(latLng8);
        LatLng latLng9 = new LatLng(39.975528,116.490346);
        latLngArrayList.add(latLng9);
        LatLng latLng10 = new LatLng(39.84636,116.37075);
        latLngArrayList.add(latLng10);
        LatLng latLng11 = new LatLng(39.889102,116.35787);
        latLngArrayList.add(latLng11);
        LatLng latLng12 = new LatLng(39.959084,116.288522);
        latLngArrayList.add(latLng12);
        LatLng latLng13 = new LatLng(39.884113,116.455896);
        latLngArrayList.add(latLng13);
        LatLng latLng14 = new LatLng(39.889102,116.35787);
        latLngArrayList.add(latLng14);

        for (int i = 0; i < latLngArrayList.size(); i++) {
            // 最后一个点作为中心点
            if (i == latLngArrayList.size() - 1) {
                Marker centerMarker = tencentMap.addMarker(new MarkerOptions().
                        position(latLngArrayList.get(i)).
                        title("中心点"));
                centerMarker.showInfoWindow();
            } else {
                tencentMap.addMarker(new MarkerOptions().
                        position(latLngArrayList.get(i)));
            }
        }
```

第二步：调整视野范围

```
  LatLng center = new LatLng(39.889102,116.35787);
        LatLngBounds latLngBounds = new LatLngBounds.Builder().include(latLngArrayList).build();
        tencentMap.animateCamera(CameraUpdateFactory.newLatLngBoundsWithMapCenter(
                latLngBounds, center, 100));
```

### 点聚合

#### 介绍：

帮助用户快速实现需要多种接口组合而成的较为复杂的业务场景需求。

#### 使用场景：

当地图上需要展示的marker过多，可能会导致界面上marker压盖、性能变差。使用点聚合功能，则可以解决该问题。

#### 使用产品：

Android 地图SDK：

| 类         | 接口                                                         | 说明                         |
| ---------- | ------------------------------------------------------------ | ---------------------------- |
| TencentMap | setOnCameraChangeListener(TencentMap.OnCameraChangeListener onCameraChangeListener) | 在地图变换监听接口中添加聚合 |

Android地图组件库：

ClusterManager：点聚合管理类

| 类             | 接口                                 | 说明                                               |
| -------------- | ------------------------------------ | -------------------------------------------------- |
| ClusterManager | setAlgorithm(Algorithm<T> var1)      | 设置聚合策略                                       |
| ClusterManager | setRenderer(ClusterRenderer<T> var1) | 设置聚合渲染器，默认使用的是DefaultClusterRenderer |
| ClusterManager | cluster                              | 重新聚合时调用,如更改聚合配置或刷新地图状态        |

#### 方法讲解：

第一步：为了实现聚合功能，需要对ClusterManager进行初始化

```
      // 实例化点聚合管理者
        mClusterManager = new ClusterManager<MarkerClusterItem>(this, tencentMap);

        // 默认聚合策略，调用时不必添加，如果需要其他聚合策略可以按以下代码修改
        NonHierarchicalDistanceBasedAlgorithm<MarkerClusterItem> ndba = new NonHierarchicalDistanceBasedAlgorithm<>(this);
        // 设置点聚合生效距离，以dp为单位
        ndba.setMaxDistanceAtZoom(35);
        // 设置策略
        mClusterManager.setAlgorithm(ndba);

        // 设置聚合渲染器，默认使用的是DefaultClusterRenderer，可以不调用下列代码
        DefaultClusterRenderer<MarkerClusterItem> renderer = new DefaultClusterRenderer<>(this, tencentMap, mClusterManager);
        // 设置最小聚合数量，默认为4，这里设置为2，即有2个以上不包括2个marker才会聚合
        renderer.setMinClusterSize(2);
        // 定义聚合的分段，当超过5个不足10个的时候，显示5+，其他分段同理
        renderer.setBuckets(new int[]{5, 10, 20, 50});
        mClusterManager.setRenderer(renderer);
```

第二步：要使用腾讯地图提供的聚合功能，需要实现 ClusterItem 接口

```
public class MarkerClusterItem implements ClusterItem {
    private final LatLng mLatLng;

    // 自定义实例化方法
    public MarkerClusterItem(double latitude, double longitude) {
        // TODO Auto-generated constructor stub
        mLatLng = new LatLng(latitude, longitude);
    }

    @Override
    public LatLng getPosition() {
        // TODO Auto-generated method stub
        return mLatLng;
    }
}
```

第三步：添加聚合数据

```
List<TencentMapItem> items = new ArrayList<TencentMapItem>();
items.add(new TencentMapItem(39.984059，116.307621));
items.add(new TencentMapItem(39.981954，116.304703));
items.add(new TencentMapItem(39.984355，116.312256));
items.add(new TencentMapItem(39.980442，116.315346));
items.add(new TencentMapItem(39.981527，116.308994));
items.add(new TencentMapItem(39.979751，116.310539));
items.add(new TencentMapItem(39.977252，116.305776));
items.add(new TencentMapItem(39.984026，116.316419));
items.add(new TencentMapItem(39.976956，116.314874));
items.add(new TencentMapItem(39.978501，116.311827));
items.add(new TencentMapItem(39.980277，116.312814));
items.add(new TencentMapItem(39.980236，116.369022));
items.add(new TencentMapItem(39.978838，116.368486));
items.add(new TencentMapItem(39.977161，116.367488));
items.add(new TencentMapItem(39.915398，116.396713));
items.add(new TencentMapItem(39.937645，116.455421));
items.add(new TencentMapItem(39.896304，116.321182));
items.add(new TencentMapItem(31.254487，121.452827));
items.add(new TencentMapItem(31.225133，121.485443));
items.add(new TencentMapItem(31.216912，121.442528));
items.add(new TencentMapItem(31.251552，121.500893));
items.add(new TencentMapItem(31.249204，121.455917));
items.add(new TencentMapItem(22.546885，114.042892));
items.add(new TencentMapItem(22.538086，113.999805));
items.add(new TencentMapItem(22.534756，114.082031));
mClusterManager.addItems(items);
```

第四步：添加聚合

```
 tencentMap.setOnCameraChangeListener(mClusterManager);
```

## 覆盖物

### 隐藏文字标注

#### 介绍：

该示例主要是用于向用户展示如何控制地图文字标注的隐藏与展示

#### 使用场景：

在自己的APP中添加地图页面，需要隐藏掉文字标注，突出自己的业务场景。

#### 使用产品：

| 类         | 接口                                       | 说明                   |
| ---------- | ------------------------------------------ | ---------------------- |
| TencentMap | setPoisEnabled(boolean enabled) 默认为true | 是否显示地图标注及名称 |

#### 方法讲解:

```
//隐藏地图标注
tencentMap.setPoisEnabled(false); 
```

## 轨迹处理

### 平滑移动

#### 介绍：

该示例主要是向开发者展示如何在地图上实现轨迹点的平滑移动效果

#### 使用场景：

出行、运动等类别的app中常常会需要展示车辆或用户的行程轨迹、实时移动轨迹等数据，相应效果需要车辆在地图上平滑移动。

#### 使用产品：

Android地图SDK：

| 类         | 接口                                     | 说明         |
| ---------- | ---------------------------------------- | ------------ |
| TencentMap | addPolyline(PolylineOptions options)     | 添加小车路线 |
| TencentMap | addMarker(MarkerOptions options)         | 添加小车标记 |
| TencentMap | animateCamera(CameraUpdate cameraUpdate) | 调整最佳视野 |

Android地图组件库：

| 类                      | 接口                                                         | 说明         |
| ----------------------- | ------------------------------------------------------------ | ------------ |
| MarkerTranslateAnimator | MarkerTranslateAnimator(Marker marker, long duration,                                LatLng[] latLngs, boolean rotateEnabled) | 创建移动动画 |

#### 方法讲解：

第一步：解析路线

```
 private final String mLine = "39.98409,116.30804,39.98409,116.3081,39.98409,116.3081,39.98397,116.30809,39.9823,116.30809,39.9811,116.30817,39.9811,116.30817,39.97918,116.308266,39.97918,116.308266,39.9791,116.30827,39.9791,116.30827,39.979008,116.3083,39.978756,116.3084,39.978386,116.3086,39.977867,116.30884,39.977547,116.308914,39.976845,116.308914,39.975826,116.308945,39.975826,116.308945,39.975666,116.30901,39.975716,116.310486,39.975716,116.310486,39.975754,116.31129,39.975754,116.31129,39.975784,116.31241,39.975822,116.31327,39.97581,116.31352,39.97588,116.31591,39.97588,116.31591,39.97591,116.31735,39.97591,116.31735,39.97593,116.31815,39.975967,116.31879,39.975986,116.32034,39.976055,116.32211,39.976086,116.323395,39.976105,116.32514,39.976173,116.32631,39.976254,116.32811,39.976265,116.3288,39.976345,116.33123,39.976357,116.33198,39.976418,116.33346,39.976418,116.33346,39.97653,116.333755,39.97653,116.333755,39.978157,116.333664,39.978157,116.333664,39.978195,116.33509,39.978195,116.33509,39.978226,116.33625,39.978226,116.33625,39.97823,116.33656,39.97823,116.33656,39.978256,116.33791,39.978256,116.33791,39.978016,116.33789,39.977047,116.33791,39.977047,116.33791,39.97706,116.33768,39.97706,116.33768,39.976967,116.33706,39.976967,116.33697";
    private TencentMap mMap;
    private Marker mCarMarker;
    private LatLng[] mCarLatLngArray;
    private MarkerTranslateAnimator mAnimator;
 String[] linePointsStr = mLine.split(",");
        mCarLatLngArray = new LatLng[linePointsStr.length / 2];
        for (int i = 0; i < mCarLatLngArray.length; i++) {
            double latitude = Double.parseDouble(linePointsStr[i * 2]);
            double longitude = Double.parseDouble(linePointsStr[i * 2 + 1]);
            mCarLatLngArray[i] = new LatLng(latitude, longitude);
        }
```

第二步：添加小车路线

```
   mMap.addPolyline(new PolylineOptions().add(mCarLatLngArray));
```

第三步：添加小车

```
 LatLng carLatLng = mCarLatLngArray[0];
        mCarMarker = mMap.addMarker(
                new MarkerOptions(carLatLng)
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.taxi))
                        .flat(true)
                        .clockwise(false));
```

第四步：创建移动动画

```
mAnimator = new MarkerTranslateAnimator(mCarMarker, 50 * 1000, mCarLatLngArray, true);
```

第五步：调整最佳视野

```
  //调整最佳视界
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                LatLngBounds.builder().include(Arrays.asList(mCarLatLngArray)).build(), 50));
```

第六步：开启动画移动

```
 mAnimator.startAnimation();
```

## 更详细的设置请参考demo






