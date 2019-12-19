package gis.gisdemo;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gis.hmap.BuildingEvent;
import gis.hmap.BuildingListener;
import gis.hmap.CalculateRouteListener;
import gis.hmap.FlashMarker;
import gis.hmap.GeneralMarker;
import gis.hmap.GeoLocation;
import gis.hmap.GeoServiceCallback;
import gis.hmap.GisView;
import gis.hmap.HeatPoint;
import gis.hmap.IndoorCallback;
import gis.hmap.LocationEvent;
import gis.hmap.LocationListener;
import gis.hmap.MapEvent;
import gis.hmap.MapListener;
import gis.hmap.MapLoadedEvent;
import gis.hmap.MapLoadedListener;
import gis.hmap.MarkerEvent;
import gis.hmap.MarkerListener;
import gis.hmap.ModelEvent;
import gis.hmap.ModelListener;
import gis.hmap.ObjectInfo;
import gis.hmap.PresentationStyle;
import gis.hmap.QueryCallback;
import gis.hmap.RoomStyle;
import gis.hmap.RouteEvent;
import gis.hmap.RoutePoint;
import gis.hmap.ZoomEvent;
import gis.hmap.ZoomListener;
import gis.hmap.ZoomToIndoorEvent;
import gis.hmap.ZoomToIndoorListener;

public class MainActivity extends Activity
        implements NavigationView.OnNavigationItemSelectedListener,
        MarkerListener, BuildingListener, ModelListener, ZoomListener, MapListener, IndoorCallback,
        LocationListener, GeoServiceCallback, QueryCallback, ZoomToIndoorListener,
        MapLoadedListener, CalculateRouteListener {

    private String[] mPerms = {
            "android.permission.INTERNET",
            "android.permission.LOCATION_HARDWARE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.CHANGE_WIFI_STATE",
            "android.permission.CHANGE_WIFI_MULTICAST_STATE",
            "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS",
            "android.permission.READ_PHONE_STATE",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.READ_PHONE_STATE",
            "android.permission.VIBRATE",
            "android.permission.WAKE_LOCK"};

    private int cnt = 0;
    private String markerId;
    private boolean permissionflag = false; //
    private Handler mainHandler= new Handler();
    private ArrayList<Object> popups = new ArrayList<>();
    GisView gisView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        gisView = (GisView) findViewById(R.id.gisView);
//        gisView.setGisServer("http://mcloud-uat.huawei.com/mcloud/mag/FreeProxyForText/BTYQ_json");//华为平安园区
//        gisView.setGisServer("http://apigw-beta.huawei.com/api");
//        gisView.setGisServer("http://42.202.130.191:8090/iserver/services");
//        gisView.setGisServer("https://42.202.130.191:443/iserver");
//        gisView.setGisServer("http://iserver.raytue.com:8090/iserver");
//        gisView.setGisServer("http://192.168.1.112:8090/iserver/services");
//        gisView.setGisServer("http://10.0.1.47:8090/iserver/services");
//        gisView.setRTLSServer("http://10.240.155.52:18889");
        gisView.addMapLoadedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gisView.decodeLocLocation("b6dd2707-58e4-46a3-b0a6-3bbb1b97a44c", 1);

//        gisView.loadMap(2, new double[] {36.65221619825378, 117.16909751245657}, "jinanQxiangmu", "jinanQxiangmu");
//        gisView.loadMap(5, new double[]{22.6573017046106460, 114.0576151013374200}, "BTYQ", "BTYQ");
        gisView.loadMap(5, new double[]{22.6573017046106460, 114.0576151013374200});
//        List<String> exparam = new ArrayList<>();
//        exparam.add("X-HW-ID=com.huawei.gis_lbs");
//        exparam.add("X-HW-APPKEY=sTcZjQDrvIW5qSf3JnEDMA==");
//        gisView.loadMap(2, new double[] {36.65221619825378, 117.16909751245657}, "BTYQ", "BTYQ", exparam);

        gisView.setRouteFacility(
                new String[] { "Lift", "InOut" },
                new GeneralMarker[] {
                        new GeneralMarker(null, null, getResources().getDrawable(R.drawable.elevator, null), 32, 32, null),
                        new GeneralMarker(null, null, getResources().getDrawable(R.drawable.door, null), 32, 32, null)
                });
        gisView.setMaxZoomLevel(8);
        initPermission();
        checkPermission();
    }

    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (!PermissionDetect.hasPermissions(this, mPerms))
                ActivityCompat.requestPermissions(this, mPerms,1);
            else
                initLoc();
        else
            initLoc();
    }

    private void initLoc() {
//        GisView.initEngine(getApplicationContext(),
//                "M2M4OTkyNTYtODA5Ny00OGNmLTg1MTAtOTc4ZmIzYWExYzU2",
//                "WQDVM1Vc+LZSZmqp5RxhXPPLiCNcSUYVxV8HtSKToWw",
//                "https://10.186.248.36",
//                "/api/daf9b02d-fb2c-4d03-af38-94825a5b64bd/HuaweiServer/locationRequest",
//                false,
//                "",
//                "",
//                "");
        GisView.initEngine(getApplicationContext(),
                "NzNhZDZkYzgtYmQyNy00MmQ3LWJjY2UtOGY2YTViZmVhYTYy",
                "xgAUPmQsEPU+iwt+TNJZ7va+Td5ri3EgHp6+pSNS0jY",
                "https://100.95.92.144",
                "/api/66aea767-6429-4b20-8ec0-74f4e58c60fe/HuaweiServer/locationRequest",
                true,
                "Pl0sh9bE8TZBhz8Nz+7PDg==",
                "com.huawei.jinanq.ioc",
                "http://apigw-beta.huawei.com/api/service/Qproject/locationRequest");
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                permissionflag = true;
            }
        } else {
            permissionflag = true;
        }
    }

    /**
     * 权限的结果回调函数
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
        }
        initLoc();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gisView.deinitEngine();
    }

    public Drawable LayoutToDrawable( int layout_id, int size ){

        LayoutInflater inflator = getLayoutInflater();
        View viewHelp = inflator.inflate(/*R.layout.test */ layout_id, null);


        Bitmap snapshot = convertViewToBitmap(viewHelp, size);
//        Drawable drawable = (Drawable)new BitmapDrawable(snapshot);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), snapshot);
        return drawable;
    }

    public static Bitmap convertViewToBitmap(View view, int size) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                     View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int width = size;
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());  //根据字符串的长度显示view的宽度
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //加载地图
        if (id == R.id.loadMap) {
            gisView.loadMap(2, new double[] {22.6573017046106460,114.0576151013374200});
        }
        else if(id == R.id.encodeAddress){  //查询经纬度对应地名
//            gisView.getAddressOfLocation(117.16909751245657,36.65221619825378,0.0005,5,this);
            gisView.getAddressOfLocation(114.0576151013374200,22.6573017046106460, this);
            Log.d("GisView", "location ok");
        } else if(id == R.id.decodeAddress){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("搜索地址");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String data = input.getText().toString();
                    gisView.getLocationOfAddress(data,5,MainActivity.this);  //位置搜索（模糊匹配）
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            //gisView.getLocationOfAddress("2号楼",5,this);  //位置搜索（模糊匹配）
        } else if(id == R.id.menuGPS){
//            GeoLocation loc = gisView.getMyLocation();  //获取我的定位
//            if(loc == null) //定位失败
//                Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
//            else {
//                String str = String.format("位置: lng:%f, lat:%f, addr:%s", loc.lng, loc.lat, loc.address);
//                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
//                GeneralMarker[] markers = new GeneralMarker[]{
//                        new GeneralMarker(
//                                new double[]{loc.lat, loc.lng},
//                                "位置",
//                                getResources().getDrawable(R.drawable.marker_1, null),
//                                64, 64, null)
//                };
//                gisView.addMarker("lm01", 999, markers);
//                gisView.setCenter(loc.lat, loc.lng);
//            }

            GeoLocation loc = gisView.getMyLocation(this);  //获取我的定位
            if(loc == null) //定位失败
                Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
            else {
                double lat = loc.lat + 14.40128786492045;
                double lng = loc.lng + 3.65470084578991;
                String str = String.format("位置: lng:%f, lat:%f, heading: %f, addr:%s", lng, lat, loc.direction, loc.address);
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
                GeneralMarker[] markers = new GeneralMarker[]{
                        new GeneralMarker(
                                new double[]{lat, lng},
                                "位置",
                                getResources().getDrawable(R.drawable.marker_1, null),
                                64, 64, null)
                };
                gisView.addMarker("lm01", 999, markers);
            }

        } else if (id == R.id.unloadMap) {
            gisView.destroyMap();
//            finish();
            //加载楼层
        } else if (id == R.id.loadF1) {
            gisView.showIndoorMap("J04","F01", this);
        } else if (id == R.id.loadF2) {
            gisView.showIndoorMap("J01","F02", this);
        } else if (id == R.id.loadF3) {
            gisView.showIndoorMap("J03","F1", this);
        } else if (id == R.id.loadF4) {
            gisView.showIndoorMap("J01","F1", this);
        } else if (id == R.id.roomstyle) {
            RoomStyle roomStyle = new RoomStyle();
            roomStyle.lineColor = Color.parseColor("#909000");
            roomStyle.lineOpacity = 150;
            roomStyle.lineWidth = 2;
            roomStyle.fillColor = Color.parseColor("#009090");
            roomStyle.fillOpacity = 128;
            gisView.setRoomStyle("J03", "F1", "8107", roomStyle);
        } else if (id == R.id.delroomstyle) {
            gisView.setRoomStyle("J03", "F1", "8107", null);
        } else if (id == R.id.typestyle) {
            RoomStyle roomStyle = new RoomStyle();
            roomStyle.lineColor = Color.parseColor("#ff0000");
            roomStyle.lineOpacity = 150;
            roomStyle.lineWidth = 2;
            roomStyle.fillColor = Color.parseColor("#ff9090");
            roomStyle.fillOpacity = 128;
            gisView.setRoomStyle("A03", "F1", "洗衣机", "TYPE", roomStyle);
        }  else if (id == R.id.deltypestyle) {
            gisView.setRoomStyle("A03", "F1", "洗衣机", "TYPE", null);
        } else if (id == R.id.loadB1) {
            gisView.showIndoorMap("J04","B01");
        } else if (id == R.id.loadOutdoor) {
            gisView.switchOutdoor();
        } else if (id == R.id.addMarker) {

//            GeneralMarker[] markers = new GeneralMarker[] {
//                    new GeneralMarker(
//                            new double[] { 36.65221619825378 + (Math.random()-0.5) / 1000, 117.16909751245657 + (Math.random()-0.5) / 1000 },
//                            String.format("layout_marker%d", cnt++),
//                            getResources().getDrawable(R.drawable.marker_1, null),
//                            64, 64, null),
//                    new GeneralMarker(
//                            new double[] { 36.65221619825378 + (Math.random()-0.5) / 1000, 117.16909751245657 + (Math.random()-0.5) / 1000 },
//                            String.format("layout_marker%d", cnt++),
//                            getResources().getDrawable(R.drawable.marker_2, null),
//                            64, 64, null)
//            };


            Drawable drawable =  LayoutToDrawable(R.layout.layout_marker,168);



            GeneralMarker[] markers = new GeneralMarker[] {
                    new GeneralMarker(
                            new double[] { 22.655299147231652, 114.05824998467759 },
                            String.format("layout_marker%d", cnt++),
                            drawable,
                            128, 128, null),
                    new GeneralMarker(
                            new double[] { 22.65024607457551, 114.05212154169743 },
                            String.format("layout_marker%d", cnt++),
                            getResources().getDrawable(R.drawable.marker_2, null),
                            128, 128, null)
            };
            gisView.addMarker("lm01", 999, markers);




        } else if (id == R.id.addMarkerUrl) {

            GeneralMarker[] markers = new GeneralMarker[] {
                    new GeneralMarker(
                            new double[] { 36.65221619825378 + (Math.random()-0.5) / 1000, 117.16909751245657 + (Math.random()-0.5) / 1000 },
                            String.format("layout_marker%d", cnt++), "./images/pic1.png", 64, 64, null),
                    new GeneralMarker(
                            new double[] { 36.65221619825378 + (Math.random()-0.5) / 1000, 117.16909751245657 + (Math.random()-0.5) / 1000 },
                            String.format("layout_marker%d", cnt++), "./images/pic2.png", 64, 64, null)
            };
            gisView.addMarker("lm01", 999, markers);

        } else if (id == R.id.addFlashMarker) {
            Drawable[] ani = new Drawable[] {
                    getResources().getDrawable(R.drawable.marker_1, null),
                    getResources().getDrawable(R.drawable.marker_2, null),
                    getResources().getDrawable(R.drawable.marker_3, null),
                    getResources().getDrawable(R.drawable.marker_4, null),
                    getResources().getDrawable(R.drawable.marker_5, null)
            };
            FlashMarker[] markers = new FlashMarker[] {
                    new FlashMarker(
                            new double[] { 36.65221619825378 + (Math.random()-0.5) / 1000, 117.16909751245657 + (Math.random()-0.5) / 1000 },
                            String.format("layout_marker%d", cnt++), ani, 500, 10000, 64, 64, null),
                    new FlashMarker(
                            new double[] { 36.65221619825378 + (Math.random()-0.5) / 1000, 117.16909751245657 + (Math.random()-0.5) / 1000 },
                            String.format("layout_marker%d", cnt++), ani, 500, 10000, 64, 64, null),
            };
            //gisView.addMarker("lm02", 999, markers);
            gisView.addFlashMarker("lm02", 999, markers);
        } else if (id == R.id.addFlashMarkerUrl) {
            String[] ani = new String[] { "./images/1.png", "./images/2.png", "./images/3.png", "./images/4.png", "./images/5.png" };
            FlashMarker[] markers = new FlashMarker[] {
                    new FlashMarker(
                            new double[] { 36.65221619825378 + (Math.random()-0.5) / 1000, 117.16909751245657 + (Math.random()-0.5) / 1000 },
                            String.format("layout_marker%d", cnt++), ani, 500, 10000, 64, 64, null),
                    new FlashMarker(
                            new double[] { 36.65221619825378 + (Math.random()-0.5) / 1000, 117.16909751245657 + (Math.random()-0.5) / 1000 },
                            String.format("layout_marker%d", cnt++), ani, 500, 10000, 64, 64, null),
            };
            gisView.addFlashMarker("lm02", 999, markers);
        } else if (id == R.id.zoom1) {
            gisView.setZoom(new double []{36.65221619825378, 117.16909751245657}, 1);
        } else if (id == R.id.zoom7) {
            gisView.setZoom(new double []{36.65221619825378, 117.16909751245657}, 6);
        } else if(id == R.id.zoomIn){
            gisView.zoomInMap();
        } else if(id == R.id.zoomOut){
            gisView.zoomOutMap();
        } else if(id == R.id.indoorlevel) {
            //参数一设置为0，关闭放大到一定级别显示室内功能
            //参数二不设置回调对象，需要设置默认开启的室内楼层
//            gisView.setSwitchIndoor(4, null, "F1");
            //参数三设置回调对象，默认楼层参数被忽略，回调参数含有buildingId，可自行处理显示室内或其他效果
            gisView.setSwitchIndoor(4, this, "");
        } else if(id == R.id.addpopup){
            //添加信息框
            Object o = gisView.addPopup(
                    new double []{22.655299147231652, 114.05824998467759},
                    "信息框 "+popups.size(),
                    new double[] {-150,-164},
                    300,
                    100,
                    "hello layout_marker"

            );
            popups.add(o);
        } else if(id == R.id.closepopup){
            //关闭信息框
//            if(popups.size() > 0)
//                gisView.closePopup(popups.get(0));
            gisView.closePopup();

        } else if(id == R.id.displayPerimeter){
            //显示周界
            gisView.displayPerimeter(
                    "1",
                    "#0000FF",
                    20,
                    50,
                    "#FF00FF",
                    40,
                    50,
                    new int [] {10, 12, 14, 16});
        } else if(id == R.id.displayPerimeter2){
            //显示周界
            gisView.displayPerimeter(
                    "1",
                    "#0000FF",
                    20,
                    50,
                    "#FF0000",
                    40,
                    50,
                    new int [] {10, 12});
        } else if(id == R.id.removePerimeter){
            //移除周界
            gisView.removePerimeter();
        } else if(id == R.id.drawRoute){
            RoutePoint[] routePoints = new RoutePoint[5];
            for (int i = 0; i < 5; i++) {
                RoutePoint routePoint = new RoutePoint(
                        new double[] {
                                22.6573017046106460 + (Math.random()-0.5) / 100.0,
                                114.0576151013374200 + (Math.random()-0.5) / 100.0
                        },
                        Color.YELLOW, "none", "none", 5, 120);
                routePoints[i] = routePoint;
            }
            gisView.drawCustomPath(routePoints);
        } else if(id == R.id.caclRoute){
//            gisView.calcRoutePath(
//                    new RoutePoint(new double[] { 22.972897436243493, 113.35581243077175 },
//                            Color.parseColor("#F20216"),
//                            "", "", 20, 100),
//                    new RoutePoint(new double[] { 22.972386774354412, 113.35689159098212 },
//                            Color.parseColor("#F20216"),
//                            "", "", 20, 100),
//                    new RoutePoint[] {
//                            new RoutePoint(new double[] { 22.972724848576696, 113.35585224707808 },
//                                    Color.parseColor("#F20216"),
//                                    "", "", 20, 100),
//                            new RoutePoint(new double[] { 22.972630664983477, 113.35606172043653 },
//                                    Color.parseColor("#F20216"),
//                                    "", "", 20, 100),
//                            new RoutePoint(new double[] { 22.97276682508405, 113.3562745404476 },
//                                    Color.parseColor("#F20216"),
//                                    "", "", 20, 100),
//                            new RoutePoint(new double[] { 22.972969445613277, 113.35655969681977 },
//                                    Color.parseColor("#F20216"),
//                                    "", "", 20, 100),
//                            new RoutePoint(new double[] { 22.9724934566077, 113.3569276414497 },
//                                    Color.parseColor("#F20216"),
//                                    "", "", 20, 100),
//                    });
            gisView.addRouteListener(this);
            PresentationStyle ps = new PresentationStyle();
            ps.opacity = 120;
            ps.fillColor = Color.parseColor("#02D6F2");
            ps.lineWidth = 20;
            gisView.calcRoutePath(
                    new RoutePoint(new double[]{22.655299147231652, 114.05824998467759},
                            Color.parseColor("#F20216"),
                            "", "", 20, 100, getResources().getDrawable(R.drawable.marker_1), 64, 64),
                    new RoutePoint(new double[]{22.65024607457551, 114.05212154169743},
                            Color.parseColor("#F20216"),
                            "", "", 20, 100, getResources().getDrawable(R.drawable.marker_3), 64, 64)
                    , new RoutePoint[]{},

                    ps);
        } else if(id == R.id.clearRoute){
            gisView.clearPath();
        } else if(id == R.id.showHeatmap){
            //生成热力图
            //22.972860320987436, 113.35606992244722
            int heatNumbers = 100;
            int radius = 30;
            HeatPoint[] heatPoints  = new HeatPoint[heatNumbers];

            for(int i = 0; i < heatNumbers; i++) {
                heatPoints[i] = new HeatPoint();
                heatPoints[i].lat = (Math.random() - 0.5) * 0.00028 + 36.65221619825378;
                heatPoints[i].lng =  (Math.random() - 0.5) * 0.0005 + 117.16909751245657;
                heatPoints[i].value = (int)(Math.random() * 100);
                heatPoints[i].tag = null;
            }

            gisView.showHeatMap(heatPoints, radius, 0.3);
        }
        //移除热力图
        else if(id == R.id.clearHeatmap){
            gisView.clearHeatMap();
        }
        //车位/模型高亮
        else if(id == R.id.modalHighlight){
            gisView.showIndoorMap("","B1");
//            gisView.showModelHighlight("1",new int[]{1, 2, 3});
            List<int[]> ids = new ArrayList<>();
            ids.add(new int[] { 1, 2, 3 });
            ids.add(new int[] { 41, 42, 43 });
            List<PresentationStyle> pss = new ArrayList<>();
            PresentationStyle ps = new PresentationStyle();
            ps.opacity = 150;
            ps.lineWidth = 1;
            ps.fillColor = Color.parseColor("#EE2222");
            pss.add(ps);
            ps = new PresentationStyle();
            ps.opacity = 150;
            ps.lineWidth = 1;
            ps.fillColor = Color.parseColor("#EEEE22");
            pss.add(ps);
            ps = new PresentationStyle();
            ps.opacity = 150;
            ps.lineWidth = 1;
            ps.fillColor = Color.parseColor("#2B94BF");
            gisView.showModelHighlight(ids, pss, ps);
        }
        //车位模型关闭高亮
        else if(id == R.id.disableHighlight){
            gisView.removeModelhighlighting();
            gisView.switchOutdoor();
        }
        //设置地图中心
        else if(id == R.id.setCenter){
//            gisView.setCenter(36.65221619825378, 117.16909751245657);
            gisView.setCenter(22.6573017046106460, 114.0576151013374200);
        }
        else if(id == R.id.getCenter){
            double[] cener = gisView.getCenter();
            String msg = String.format("地图中心：lat=%f, lng=%f", cener[0], cener[1]);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.markerPos){
            double lat = (Math.random() - 0.5) * 0.00028 + 36.65221619825378;
            double lng = (Math.random() - 0.5) * 0.0005 + 117.16909751245657;


            gisView.changeMarkerPosition("marker1", lat, lng, getResources().getDrawable(R.drawable.marker_2, null)); //设置marker位置
            //gisView.showCurrentPosition();
//            gisView.setCenter(lat, lng); //移动地图
        }
        //删除指定marker
        else if (id == R.id.deleteMarker) {
            if (markerId != null)
                gisView.deleteMarker(markerId);
        }
        else if (id == R.id.deleteLayer) {
            gisView.deleteLayer("lm01");
            gisView.deleteLayer("lm02");
        }
        else if (id == R.id.addmkrevent) {
            gisView.addMarkerListener(this);
        }
        else if (id == R.id.delmkrevent) {
            gisView.removeMarkerListener(this);
        }
        else if (id == R.id.addbudevent) {
            gisView.addBuildingListener(this);
        }
        else if (id == R.id.delbudevent) {
            gisView.removeBuildingListener(this);
        }
        else if (id == R.id.addmodevent) {
            gisView.addModelListener(this);
        }
        else if (id == R.id.delmodevent) {
            gisView.removeModelListener(this);
        }

        else if (id == R.id.addzmevent) {
            gisView.addZoomListener(this);
        }
        else if (id == R.id.delzmevent) {
            gisView.removeZoomListener(this);
        }
        else if (id == R.id.hidelevel) {
            gisView.setHideLevel(2);
        }
        else if (id == R.id.addmaptap) {
            gisView.addMapListener(this);
        }
        else if (id == R.id.delmaptap) {
            gisView.removeMapListener(this);
        }
        else if (id == R.id.addloc) {
            GisView.addLocateListener(this);
        }
        else if (id == R.id.delloc) {
            GisView.removeLocateListener(this);
        }
        else if (id == R.id.startloc) {
            gisView.startLocate();
        }
        else if (id == R.id.stoploc) {
            gisView.stopLocate();
        }
        else if (id == R.id.getBuilding) {
            gisView.getBuldingInfo("BTYQ", "J03", this);
        }
        else if (id == R.id.getObject) {
            gisView.queryObject("BTYQ", "1号楼", this);
        }
        else if (id == R.id.objdata) {

            //查询A02栋，F1楼，F2楼的所有卫生间，洗衣机也一样给
            long t1 = System.currentTimeMillis();
//            List<ObjectInfo> result = gisView.query("F1", "BUILDINGID = \"A03\" AND TYPE = \"洗衣机\"");
            List<ObjectInfo> result = gisView.query("B1,F1,F2,F3,F4,F5", "TYPE = \"洗衣机\"");
            long t2 = System.currentTimeMillis();
            Log.d("----", String.format("t2 - t1: %d", t2-t1));
            if(result != null){
                String msg = String.format("查询到%d条结果：\n", result.size());
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                //清除图标图层
                gisView.deleteLayer("卫生间");

                for (ObjectInfo info : result) {
                    double[] pos = info.getCenter();
                    //每一个卫生间，添加一个图标
                    GeneralMarker[] markers = new GeneralMarker[]{
                            new GeneralMarker(
                                    new double[]{pos[0], pos[1]},
                                    info.getStrParam("NAME"),
                                    getResources().getDrawable(gis.hmap.R.drawable.red_marker, null),
                                    32, 64, null)
                    };

                    String roomCode = info.getStrParam("ROOMCODE"); //长编码
                    //CN_EC_37_001_QYQ_000_A02_F1_A2101
                    String floorId= roomCode.split("_")[7];
                    String buildingId = roomCode.split("_")[6];
                    String roomId = info.getStrParam("ROOMID"); //短编码

                    gisView.addMarker("卫生间", 999, markers);

                }
            }
        }
        else if (id == R.id.queryCN) {


            //查询J01栋，F1楼所有会议室
            List<ObjectInfo> result = gisView.query("F01", "BUILDINGID = \"J01\" and TYPE = \"会议室\"");

            if(result != null){
                String msg = String.format("查询到%d条结果：\n", result.size());
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                for (ObjectInfo info : result) {
                    double[] pos = info.getCenter();

                    gisView.addPopup(
                            new double[]{pos[0], pos[1]},
                            info.getStrParam("NAME"), //取中文名称
                            new double[] {0,0},
                            300,
                            100,
                            ""

                    );


                }
            }
        }
        else if (id == R.id.queryEN) {


            //查询J01栋，F1楼所有会议室
            List<ObjectInfo> result = gisView.query("F01", "BUILDINGID = \"J01\" and TYPE = \"会议室\"");

            if(result != null){
                String msg = String.format("查询到%d条结果：\n", result.size());
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();


                for (ObjectInfo info : result) {
                    double[] pos = info.getCenter();

                    gisView.addPopup(
                            new double[]{pos[0], pos[1]},
                            info.getStrParam("E_NAME"), //取英文名称
                            new double[] {0,0},
                            300,
                            100,
                            ""

                    );


                }
            }

        }
        else if (id == R.id.getBounds) {


            //获取地图边界
           double [] bounds = gisView.getMapBounds();
           //【上纬度，左经度，下纬度，右经度】

            double topLeftLat = bounds[0];
            double topLeftLng = bounds[1];
            double topRightLat = bounds[0];
            double topRightLng = bounds[3];

            double botLeftLat = bounds[2];
            double botLeftLng = bounds[1];
            double botRightLat = bounds[2];
            double botRightLng = bounds[3];


            //画一个方框
            RoutePoint[] routePoints = new RoutePoint[5];
            //左上角的点
            routePoints[0] = new RoutePoint(
                    new double[] {
                            topLeftLat,
                            topLeftLng
                    },
                    Color.RED, "none", "none", 8, 120);
            //右上角
            routePoints[1] = new RoutePoint(
                    new double[] {
                            topRightLat,
                            topRightLng
                    },
                    Color.RED, "none", "none", 8, 120);

            //右下角
            routePoints[2] = new RoutePoint(
                    new double[] {
                            botRightLat,
                            botRightLng
                    },
                    Color.RED, "none", "none", 8, 120);

            //左下角
            routePoints[3] = new RoutePoint(
                    new double[] {
                            botLeftLat,
                            botLeftLng
                    },
                    Color.RED, "none", "none", 8, 120);

            //左上角，闭合回路
            routePoints[4] = new RoutePoint(
                    new double[] {
                            topLeftLat,
                            topLeftLng
                    },
                    Color.RED, "none", "none", 8, 120);


            gisView.drawCustomPath(routePoints);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void markerEvent(MarkerEvent me) {
        String msg = String.format("%s, %s", me.eventType.toString(), me.markerId);
        markerId = me.markerId;
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void markerEvent(MarkerEvent[] mes) {
        String msg = String.format("共有%d个Marker：", mes.length);
        for (MarkerEvent me : mes)
            msg += me.markerId + ",";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void zoomEvent(ZoomEvent ze) {
        String msg = String.format("%s, %d", ze.eventType.toString(), ze.level);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void buildingEvent(BuildingEvent be) {
        String msg = String.format("%s, %s, %s", be.eventType.toString(), be.buildingId, be.getStrParam("NAME"));
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void modelEvent(ModelEvent me) {
        String msg = String.format("%s, 建筑：%s, 楼层：%s, 房间：%s, 名称：%s", me.eventType.toString(), me.buildingId, me.floorId, me.modelId, me.getStrParam("NAME"));
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Object o = gisView.addPopup(
                new double []{me.getNumParam("SMSDRIN"), me.getNumParam("SMSDRIW")},
                msg,
                new double[] {0,0},
                300,
                100,
                "hello layout_marker"

        );
        popups.add(o);
    }

    @Override
    public void mapEvent(MapEvent me) {
        String msg = String.format("%s, (%d, %d), latlng(%f, %f)", me.eventType.toString(), me.screenPos[0], me.screenPos[1],
                me.geoPos[0], me.geoPos[1]);
        if (me.addrs != null && me.addrs.length > 0)
            msg += "\r\naddr: " + me.addrs[0];
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocation(final LocationEvent le) {
        final String msg = String.format("%s, latlng(%f, %f), heading: %f, building:%s, floor:%s",
                le.address, le.lat, le.lng, le.direction, le.buildingId, le.floorId);
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gisView.isInRoute(le.lat, le.lng, 15))
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), msg + "偏离导航", Toast.LENGTH_SHORT).show();
                GeneralMarker[] markers = new GeneralMarker[]{
                        new GeneralMarker(
                                new double[]{le.lat, le.lng},
                                "位置",
                                getResources().getDrawable(gis.hmap.R.drawable.red_marker, null),
                                64, 64, null)
                };
                gisView.deleteLayer("lm01");
                gisView.addMarker("lm01", 999, markers);
                //gisView.setZoom(new double[]{le.lat, le.lng},5);
            }
        }, 100);
    }

    class MyRunnable implements Runnable{
        public GeoLocation [] locations;
        public MyRunnable(GeoLocation [] loc){
            locations = loc;
        }
        @Override
        public void run() {


        }
    }

    @Override
    public void onQueryAddressFinished(GeoLocation[] loc) { //获取地址匹配结束
        Log.d("GisView", "onQueryAddressFinished: ");
        if(loc.length > 0){

            runOnUiThread(new MyRunnable(loc) {

                @Override
                public void run() {
                    for (GeoLocation c:this.locations ) {

                        GeneralMarker[] markers = new GeneralMarker[] {
                                new GeneralMarker(
                                        new double[] {c.lat,c.lng },
                                        c.address,
                                        getResources().getDrawable(R.drawable.tag_pin, null),
                                        72, 72, null),

                        };
                        gisView.addMarker("lm01", 999, markers);

                        gisView.addPopup(
                                new double[] {c.lat,c.lng},
                                c.address,
                                new double[] {0,0},
                                300,
                                100,
                                "hello layout_marker"

                        );
                    }
                }
            });

            Looper.prepare();
            Toast.makeText(this, loc[0].address +" is x=" + loc[0].lng + ",y=" + loc[0].lat, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    @Override
    public void onQueryFinished(final ObjectInfo info) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                String str = String.format("%s, (lng,lat)=%f, %f, NAME=%s", info.address, info.lng, info.lat, info.getStrParam("NAME"));
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void done() {
        Toast.makeText(MainActivity.this, "室内显示完成", Toast.LENGTH_LONG).show();
    }

    @Override
    public void zoomEvent(ZoomToIndoorEvent ze) {
//        if (!TextUtils.isEmpty(ze.buildingId))
//            gisView.showIndoorMap(ze.buildingId, "F02");
        if (TextUtils.isEmpty(ze.buildingId)) {
            Log.i("-->", "Out Door");
//            gisView.showIndoorMap("", "");
        } else
            Log.i("-->", ze.buildingId);
    }

    @Override
    public void calculateRouteEvent(RouteEvent re) {
        final RouteEvent event = re;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                String str = String.format("%s, 路径长度=%f", event.success ? "成功":"路径规划失败", event.totalLength);
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void OnMapLoaded(final MapLoadedEvent me) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                String str = String.format("(top,left)=%f, %f, (bottom,right)=%f, %f, ParkId=%s", me.bounds[0], me.bounds[1], me.bounds[2], me.bounds[3], me.parkId);
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
            }
        });
    }
}
