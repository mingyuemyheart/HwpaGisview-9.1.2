package gis.gisdemo

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import gis.hmap.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity2 : Activity(), NavigationView.OnNavigationItemSelectedListener, GeoServiceCallback, IndoorCallback, ZoomToIndoorListener,
        CalculateRouteListener, MarkerListener, BuildingListener, ModelListener, ZoomListener, MapListener, LocationListener, QueryCallback{

    private val mPerms = arrayOf(
            "android.permission.LOCATION_HARDWARE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_PHONE_STATE")
    private var cnt = 0
    private val popups = ArrayList<Any>()
    private var markerId: String? = null
    private val mUIHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initMap()
        checkPermission()
    }

    private fun initToolbar() {
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun initMap() {
        gisView.setLogEnable(true)
        gisView.setMaxZoomLevel(8)
        gisView.loadMap(5, doubleArrayOf(22.6573017046106460, 114.0576151013374200))
        gisView.setRouteFacility(
                arrayOf("Lift", "InOut"),
                arrayOf(GeneralMarker(null, null, resources.getDrawable(R.drawable.elevator, null), 32, 32, null),
                        GeneralMarker(null, null, resources.getDrawable(R.drawable.door, null), 32, 32, null)))
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!PermissionDetect.hasPermissions(this, *mPerms)) {
                ActivityCompat.requestPermissions(this, mPerms, 1001)
            } else {
                initLoc()
            }
        } else {
            initLoc()
        }
    }

    /**
     * 权限的结果回调函数
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
        }
        initLoc()
    }

    private fun initLoc() {
        GisView.initEngine(applicationContext,
                "NzNhZDZkYzgtYmQyNy00MmQ3LWJjY2UtOGY2YTViZmVhYTYy",
                "xgAUPmQsEPU+iwt+TNJZ7va+Td5ri3EgHp6+pSNS0jY",
                "https://100.95.92.144",
                "/api/66aea767-6429-4b20-8ec0-74f4e58c60fe/HuaweiServer/locationRequest",
                true,
                "Pl0sh9bE8TZBhz8Nz+7PDg==",
                "com.huawei.jinanq.ioc",
                "http://apigw-beta.huawei.com/api/service/Qproject/locationRequest")
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.loadMap -> gisView.loadMap(5, doubleArrayOf(22.6573017046106460, 114.0576151013374200))
            R.id.setCenter -> {
                gisView.setCenter(22.6573017046106460, 114.0576151013374200)
                Toast.makeText(this, String.format("中心：%f, %f", gisView.center[0], gisView.center[1]), Toast.LENGTH_SHORT).show()
            }
            R.id.getCenter -> {
                Toast.makeText(this, String.format("中心：%f, %f", gisView.center[0], gisView.center[1]), Toast.LENGTH_SHORT).show()
            }
            R.id.zoom1 -> gisView.zoom = 1
            R.id.zoom7 -> gisView.zoom = 7
            R.id.zoomIn -> gisView.zoomInMap()
            R.id.zoomOut -> gisView.zoomOutMap()
            R.id.unloadMap -> gisView.destroyMap()


            R.id.hidelevel -> gisView.setHideLevel(2)
            R.id.indoorlevel -> {
                //参数一设置为0，关闭放大到一定级别显示室内功能
                //参数二不设置回调对象，需要设置默认开启的室内楼层
//            gisView.setSwitchIndoor(4, null, "F1");
                //参数三设置回调对象，默认楼层参数被忽略，回调参数含有buildingId，可自行处理显示室内或其他效果
                gisView.setSwitchIndoor(4, this, "")
            }


            R.id.addMarker -> {
                val drawable = R.layout.layout_marker.layoutToDrawable()
                val markers = arrayOfNulls<GeneralMarker>(2)
                for (i in 0 .. 1) {
                    val generalMarker = GeneralMarker()
                    generalMarker.position = doubleArrayOf(22.655299, 114.058249)
                    generalMarker.markerId = String.format("layout_marker%d", cnt++)
                    generalMarker.image = drawable
                    generalMarker.width = 96
                    generalMarker.height = 96
                    generalMarker.tag = null
                    markers[i] = generalMarker
                }
//                val markers = arrayOf(
//                        GeneralMarker(doubleArrayOf(22.655299, 114.058249),
//                        String.format("layout_marker%d", cnt++),
//                        drawable,
//                        128, 128, null),
//                        GeneralMarker(doubleArrayOf(22.650246, 114.052121),
//                        String.format("layout_marker%d", cnt++),
//                        resources.getDrawable(R.drawable.marker_2, null),
//                        128, 128, null))
                gisView.addMarker("lm01", 999, markers)
            }
            R.id.addMarkerUrl -> {
                val markers = arrayOf(GeneralMarker(
                        doubleArrayOf(22.655299, 114.058249),
                        String.format("layout_marker%d", cnt++), "./images/pic1.png", 96, 96, null), GeneralMarker(
                        doubleArrayOf(22.655299, 114.058249),
                        String.format("layout_marker%d", cnt++), "./images/pic2.png", 96, 96, null))
                gisView.addMarker("lm01", 999, markers)
            }
            R.id.addFlashMarker -> {
                val ani = arrayOf(resources.getDrawable(R.drawable.marker_1, null), resources.getDrawable(R.drawable.marker_2, null), resources.getDrawable(R.drawable.marker_3, null), resources.getDrawable(R.drawable.marker_4, null), resources.getDrawable(R.drawable.marker_5, null))
                val markers = arrayOf(FlashMarker(
                        doubleArrayOf(22.655299, 114.058249),
                        String.format("layout_marker%d", cnt++), ani, 500, 10000, 96, 96, null), FlashMarker(
                        doubleArrayOf(22.655299, 114.058249),
                        String.format("layout_marker%d", cnt++), ani, 500, 10000, 96, 96, null))
                //gisView.addMarker("lm02", 999, markers);
                gisView.addFlashMarker("lm02", 999, markers)
            }
            R.id.addFlashMarkerUrl -> {
                val ani = arrayOf("./images/1.png", "./images/2.png", "./images/3.png", "./images/4.png", "./images/5.png")
                val markers = arrayOf(FlashMarker(
                        doubleArrayOf(22.655299, 114.058249),
                        String.format("layout_marker%d", cnt++), ani, 500, 10000, 96, 96, null), FlashMarker(
                        doubleArrayOf(22.655299, 114.058249),
                        String.format("layout_marker%d", cnt++), ani, 500, 10000, 96, 96, null))
                gisView.addFlashMarker("lm02", 999, markers)
            }
            R.id.markerPos -> {
                val lat = (Math.random() - 0.5) * 0.00028 + 22.655299
                val lng = (Math.random() - 0.5) * 0.0005 + 114.058249
                gisView.changeMarkerPosition("layout_marker1", lat, lng) //设置marker位置
            }
            R.id.deleteMarker -> {
                if (markerId != null) {
                    gisView.deleteMarker(markerId)
                }
            }
            R.id.deleteLayer -> {
                gisView.deleteLayer("lm01")
                gisView.deleteLayer("lm02")
            }
            R.id.addpopup -> {
                val o = gisView.addPopup(
                        doubleArrayOf(22.655299147231652, 114.05824998467759),
                        "信息框 " + popups.size,
                        doubleArrayOf(0.0, 0.0),
                        300,
                        100,
                        "hello layout_marker"
                )
                popups.add(o)
            }
            R.id.closepopup -> gisView.closePopup()


            R.id.menuGPS -> {
                val loc = gisView.getMyLocation(this) //获取我的定位
                if (loc == null) {
                    Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show()
                } else {
                    val lat = loc.lat + 14.40128786492045
                    val lng = loc.lng + 3.65470084578991
                    val str = String.format("位置: lng:%f, lat:%f, heading: %f, addr:%s", lng, lat, loc.direction, loc.address)
                    Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
                    val markers = arrayOf(GeneralMarker(
                            doubleArrayOf(lat, lng),
                            "位置",
                            resources.getDrawable(R.drawable.marker_1, null),
                            64, 64, null))
                    gisView.addMarker("lm01", 999, markers)
                }
            }
            R.id.getBounds -> {
                //获取地图边界
                val bounds = gisView.mapBounds
                //【上纬度，左经度，下纬度，右经度】

                val topLeftLat = bounds[0]
                val topLeftLng = bounds[1]
                val topRightLat = bounds[0]
                val topRightLng = bounds[3]

                val botLeftLat = bounds[2]
                val botLeftLng = bounds[1]
                val botRightLat = bounds[2]
                val botRightLng = bounds[3]

                //画一个方框
                val routePoints = arrayOfNulls<RoutePoint>(5)
                //左上角的点
                routePoints[0] = RoutePoint(
                        doubleArrayOf(topLeftLat, topLeftLng),
                        Color.RED, "none", "none", 8, 120)
                //右上角
                routePoints[1] = RoutePoint(
                        doubleArrayOf(topRightLat, topRightLng),
                        Color.RED, "none", "none", 8, 120)
                //右下角
                routePoints[2] = RoutePoint(
                        doubleArrayOf(botRightLat, botRightLng),
                        Color.RED, "none", "none", 8, 120)
                //左下角
                routePoints[3] = RoutePoint(
                        doubleArrayOf(botLeftLat, botLeftLng),
                        Color.RED, "none", "none", 8, 120)
                //左上角，闭合回路
                routePoints[4] = RoutePoint(
                        doubleArrayOf(topLeftLat, topLeftLng),
                        Color.RED, "none", "none", 8, 120)
                gisView.drawCustomPath(routePoints)
            }
            R.id.encodeAddress -> gisView.getAddressOfLocation(114.0576151013374200, 22.6573017046106460, this)
            R.id.decodeAddress -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("搜索地址")
                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)
                builder.setPositiveButton("搜索") { dialog, which ->
                    val data = input.text.toString()
                    gisView.getLocationOfAddress(data,  this@MainActivity2)  //位置搜索（模糊匹配）
                }
                builder.setNegativeButton("取消") { dialog, which -> dialog.cancel() }
                builder.show()
            }
            R.id.getBuilding -> gisView.getBuldingInfo(Common.parkId(), "J03", this)
            R.id.getObject -> gisView.queryObject(Common.parkId(), "1号楼", this)
            R.id.queryCN -> {
                //查询J01栋，F1楼所有会议室
                val result = gisView.query("F01", "BUILDINGID = \"J01\" and TYPE = \"会议室\"")
                if (result != null) {
                    val msg = String.format("查询到%d条结果：\n", result.size)
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    for (info in result) {
                        val pos = info.center
                        gisView.addPopup(
                                doubleArrayOf(pos[0], pos[1]),
                                info.getStrParam("NAME"), //取中文名称
                                doubleArrayOf(0.0, 0.0),
                                300,
                                100,
                                ""
                        )
                    }
                }
            }
            R.id.queryEN -> {
                //查询J01栋，F1楼所有会议室
                val result = gisView.query("F01", "BUILDINGID = \"J01\" and TYPE = \"会议室\"")
                if (result != null) {
                    val msg = String.format("查询到%d条结果：\n", result.size)
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    for (info in result) {
                        val pos = info.center
                        gisView.addPopup(
                                doubleArrayOf(pos[0], pos[1]),
                                info.getStrParam("E_NAME"), //取英文名称
                                doubleArrayOf(0.0, 0.0),
                                300,
                                100,
                                ""
                        )
                    }
                }
            }
            R.id.objdata -> {
                val result = gisView.query("F01", "BUILDINGID = \"J01\" AND TYPE = \"会议室\"")
                if (result != null) {
                    val msg = String.format("查询到%d条结果：\n", result.size)
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    for (info in result) {
                        val pos = info.center
                        gisView.addPopup(
                                doubleArrayOf(pos[0], pos[1]),
                                info.getStrParam("NAME"), //取中文名称
                                doubleArrayOf(0.0, 0.0),
                                300,
                                100,
                                ""
                        )
                    }
                }
            }


            R.id.loadB1 -> gisView.showIndoorMap("J01", "B01")
            R.id.loadF1 -> gisView.showIndoorMap("J01", "F01", this)
            R.id.loadF2 -> gisView.showIndoorMap("J01", "F02", this)
            R.id.loadF3 -> gisView.showIndoorMap("J01", "F03", this)
            R.id.loadF4 -> gisView.showIndoorMap("J01", "F04", this)
            R.id.roomstyle -> {
                val roomStyle = RoomStyle()
                roomStyle.lineColor = Color.parseColor("#909000")
                roomStyle.lineOpacity = 150
                roomStyle.lineWidth = 2
                roomStyle.fillColor = Color.parseColor("#009090")
                roomStyle.fillOpacity = 128
                gisView.setRoomStyle("J01", "F1", "1L45R", roomStyle)
            }
            R.id.delroomstyle -> gisView.setRoomStyle("J01", "F1", "1L45R", null)
            R.id.typestyle -> {
                val roomStyle = RoomStyle()
                roomStyle.lineColor = Color.parseColor("#ff0000")
                roomStyle.lineOpacity = 150
                roomStyle.lineWidth = 2
                roomStyle.fillColor = Color.parseColor("#ff9090")
                roomStyle.fillOpacity = 128
                gisView.setRoomStyle("J01", "F01", "家具", "TYPE", roomStyle)
            }
            R.id.deltypestyle -> gisView.setRoomStyle("J01", "F01", "家具", "TYPE", null)
            R.id.loadOutdoor -> gisView.switchOutdoor()
            R.id.displayPerimeter -> {
                //显示周界
                gisView.displayPerimeter(
                        "1",
                        "#0000FF",
                        20,
                        50,
                        "#FF00FF",
                        40,
                        50,
                        intArrayOf(10, 12, 14, 16))
            }
            R.id.displayPerimeter2 -> {
                //显示周界
                gisView.displayPerimeter(
                        "1",
                        "#0000FF",
                        20,
                        50,
                        "#FF0000",
                        40,
                        50,
                        intArrayOf(10, 12))
            }
            R.id.removePerimeter -> gisView.removePerimeter()
            R.id.modalHighlight -> {
                gisView.showIndoorMap("", "B01")
//            gisView.showModelHighlight(Common.parkId(), intArrayOf(1,2,3))
                val ids = ArrayList<IntArray>()
                ids.add(intArrayOf(1, 2, 3))
                ids.add(intArrayOf(41, 42, 43))
                val pss = ArrayList<PresentationStyle>()
                var ps = PresentationStyle()
                ps.opacity = 150
                ps.lineWidth = 1
                ps.fillColor = Color.parseColor("#EE2222")
                pss.add(ps)
                ps = PresentationStyle()
                ps.opacity = 150
                ps.lineWidth = 1
                ps.fillColor = Color.parseColor("#EEEE22")
                pss.add(ps)
                ps = PresentationStyle()
                ps.opacity = 150
                ps.lineWidth = 1
                ps.fillColor = Color.parseColor("#2B94BF")
                gisView.showModelHighlight(ids, pss, ps)
            }
            R.id.disableHighlight -> {
                gisView.removeModelhighlighting()
                gisView.switchOutdoor()
            }


            R.id.drawRoute -> {
                val routePoints = arrayOfNulls<RoutePoint>(5)
                for (i in 0..4) {
                    val routePoint = RoutePoint(
                            doubleArrayOf(22.6573017046106460 + (Math.random() - 0.5) / 100.0, 114.0576151013374200 + (Math.random() - 0.5) / 100.0),
                            Color.YELLOW, "none", "none", 5, 120)
                    routePoints[i] = routePoint
                }
                gisView.drawCustomPath(routePoints)
            }
            R.id.caclRoute -> {
                val ps = PresentationStyle()
                ps.opacity = 120
                ps.fillColor = Color.parseColor("#02D6F2")
                ps.lineWidth = 20
                gisView.calcRoutePath(
                        RoutePoint(doubleArrayOf(22.655674, 114.05721),
                                Color.parseColor("#F20216"),
                                "J01", "F01", 20, 100, ContextCompat.getDrawable(this, R.drawable.marker_1), 64, 64),
                        RoutePoint(doubleArrayOf(22.65592, 114.05719),
                                Color.parseColor("#F20216"),
                                "J01", "F01", 20, 100, ContextCompat.getDrawable(this, R.drawable.marker_3), 64, 64), arrayOf(),
                        ps)
            }
            R.id.clearRoute -> gisView.clearPath()
            R.id.showHeatmap -> {
                //生成热力图
                //22.972860320987436, 113.35606992244722
                val heatNumbers = 100
                val radius = 30
                val heatPoints = arrayOfNulls<HeatPoint>(heatNumbers)
                for (i in 0 until heatNumbers) {
                    heatPoints[i] = HeatPoint()
                    heatPoints[i]!!.lat = (Math.random() - 0.5) * 0.00028 + 22.655299147231652
                    heatPoints[i]!!.lng = (Math.random() - 0.5) * 0.0005 + 114.05824998467759
                    heatPoints[i]!!.value = (Math.random() * 100).toInt()
                    heatPoints[i]!!.tag = null
                }
                gisView.showHeatMap(heatPoints, radius, 0.3)
            }
            R.id.clearHeatmap -> gisView.clearHeatMap()


            R.id.addmkrevent -> gisView.addMarkerListener(this)
            R.id.delmkrevent -> gisView.removeMarkerListener(this)
            R.id.addbudevent -> gisView.addBuildingListener(this)
            R.id.delbudevent -> gisView.removeBuildingListener(this)
            R.id.addmodevent -> gisView.addModelListener(this)
            R.id.delmodevent -> gisView.removeModelListener(this)
            R.id.addzmevent -> gisView.addZoomListener(this)
            R.id.delzmevent -> gisView.removeZoomListener(this)
            R.id.addmaptap -> gisView.addMapListener(this)
            R.id.delmaptap -> gisView.removeMapListener(this)
            R.id.addloc -> GisView.addLocateListener(this)
            R.id.delloc -> GisView.removeLocateListener(this)
            R.id.startloc -> gisView.startLocate()
            R.id.stoploc -> gisView.stopLocate()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * 经纬度、地址查询回调
     */
    override fun onQueryAddressFinished(loc: Array<out GeoLocation>) {
        runOnUiThread {
            for (i in 0 until loc.size) {
                val c = loc[i]
                val markers = arrayOf(GeneralMarker(
                        doubleArrayOf(c.lat, c.lng),
                        c.cnName,
                        resources.getDrawable(R.drawable.tag_pin, null),
                        72, 72, null))
                gisView.addMarker("lm01", 999, markers)

                gisView.addPopup(
                        doubleArrayOf(c.lat, c.lng),
                        c.cnName,
                        doubleArrayOf(0.0, 0.0),
                        300,
                        100,
                        "hello layout_marker"
                )
            }
        }
    }

    /**
     * 绘制室内地图回调
     */
    override fun done() {
        Toast.makeText(this@MainActivity2, "室内显示完成", Toast.LENGTH_LONG).show()
    }

    /**
     * 室内地图缩放回调
     */
    override fun zoomEvent(ze: ZoomToIndoorEvent?) {
//        if (!TextUtils.isEmpty(ze.buildingId))
//            gisView.showIndoorMap(ze.buildingId, "F02");
        if (TextUtils.isEmpty(ze!!.buildingId)) {
            Log.i("-->", "Out Door")
            //            gisView.showIndoorMap("", "");
        } else
            Log.i("-->", ze.buildingId)
    }

    /**
     * 绘制路线回调
     */
    override fun calculateRouteEvent(event: RouteEvent?) {
        mUIHandler.post {
            val str = String.format("%s, 路径长度=%f", if (event!!.success) "成功" else "路径规划失败", event.totalLength)
            Toast.makeText(this@MainActivity2, str, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 地图缩放回调
     */
    override fun zoomEvent(ze: ZoomEvent?) {
        val msg = String.format("%s, %d", ze!!.eventType.toString(), ze.level)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun markerEvent(me: MarkerEvent?) {
        val msg = String.format("%s, %s", me!!.eventType.toString(), me.markerId)
        markerId = me.markerId
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun markerEvent(mes: Array<out MarkerEvent>?) {
        var msg = String.format("共有%d个Marker：", mes!!.size)
        for (me in mes)
            msg += me.markerId + ","
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun buildingEvent(be: BuildingEvent?) {
        val msg = String.format("%s, %s, %s", be!!.eventType.toString(), be.buildingId, be.getStrParam("NAME"))
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun modelEvent(me: ModelEvent?) {
        val msg = String.format("%s, 建筑：%s, 楼层：%s, 房间：%s, 名称：%s", me!!.eventType.toString(), me.buildingId, me.floorId, me.modelId, me.getStrParam("NAME"))
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        val o = gisView.addPopup(
                doubleArrayOf(me.getNumParam("SMSDRIN"), me.getNumParam("SMSDRIW")),
                msg,
                doubleArrayOf(0.0, 0.0),
                300,
                100,
                "hello layout_marker"
        )
        popups.add(o)
    }

    override fun mapEvent(me: MapEvent?) {
        var msg = String.format("%s, (%d, %d), latlng(%f, %f)", me!!.eventType.toString(), me.screenPos[0], me.screenPos[1], me.geoPos[0], me.geoPos[1])
        if (me.addrs != null && me.addrs.isNotEmpty())
            msg += "\r\naddr: " + me.addrs[0]
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onLocation(le: LocationEvent?) {
        val msg = String.format("%s, latlng(%f, %f), heading: %f, building:%s, floor:%s",
                le!!.address, le.lat, le.lng, le.direction, le.buildingId, le.floorId)
        mUIHandler.postDelayed( {
            if (gisView.isInRoute(le.lat, le.lng, 15.0))
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(applicationContext, msg + "偏离导航", Toast.LENGTH_SHORT).show()
            val markers = arrayOf(GeneralMarker(
                    doubleArrayOf(le.lat, le.lng),
                    "位置",
                    resources.getDrawable(gis.hmap.R.drawable.red_marker, null),
                    64, 64, null))
            gisView.deleteLayer("lm01")
            gisView.addMarker("lm01", 999, markers)
            //gisView.setZoom(new double[]{le.lat, le.lng},5);
        }, 100)
    }

    /**
     * 查询数据回调
     */
    override fun onQueryFinished(info: ObjectInfo?) {
        mUIHandler.post {
            val str = String.format("%s, (lng,lat)=%f, %f, NAME=%s", info!!.address, info.lng, info.lat, info.getStrParam("NAME"))
            Toast.makeText(this@MainActivity2, str, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * layout转drawable
     */
    private fun Int.layoutToDrawable(): Drawable {
        val view = layoutInflater.inflate(this, null)
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)  //根据字符串的长度显示view的宽度
        view.buildDrawingCache()
        return BitmapDrawable(resources, view.drawingCache)
    }

    override fun onBackPressed() {
        if (drawer_layout != null && drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (gisView != null) {
            gisView.deinitEngine()
        }
    }

}