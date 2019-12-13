package gis.gisdemo

import android.app.Activity
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import gis.hmap.GeneralMarker
import gis.hmap.GeoLocation
import gis.hmap.GeoServiceCallback
import gis.hmap.GisView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity2 : Activity(), NavigationView.OnNavigationItemSelectedListener, GeoServiceCallback {

    private val mPerms = arrayOf(
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
            "android.permission.WAKE_LOCK")

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
        gisView.loadMap(5, doubleArrayOf(22.6573017046106460, 114.0576151013374200))
        gisView.setRouteFacility(
                arrayOf("Lift", "InOut"),
                arrayOf(GeneralMarker(null, null, resources.getDrawable(R.drawable.elevator, null), 32, 32, null), GeneralMarker(null, null, resources.getDrawable(R.drawable.door, null), 32, 32, null)))
        gisView.setMaxZoomLevel(8)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (!PermissionDetect.hasPermissions(this, *mPerms))
                ActivityCompat.requestPermissions(this, mPerms, 1)
            else
                initLoc()
        else
            initLoc()
    }

    /**
     * 权限的结果回调函数
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
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
            R.id.loadMap -> gisView.loadMap(2, doubleArrayOf(22.6573017046106460, 114.0576151013374200))
            R.id.loadMap2 -> gisView.loadMap(5, doubleArrayOf(22.6573017046106460, 114.0576151013374200))
            R.id.encodeAddress -> gisView.getAddressOfLocation(114.0576151013374200, 22.6573017046106460, this)
            R.id.decodeAddress -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("搜索地址")
                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)
                builder.setPositiveButton("搜索") { dialog, which ->
                    val data = input.text.toString()
                    gisView.getLocationOfAddress(data, 5, this@MainActivity2)  //位置搜索（模糊匹配）
                }
                builder.setNegativeButton("取消") { dialog, which -> dialog.cancel() }
                builder.show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onQueryAddressFinished(loc: Array<out GeoLocation>) {
        Log.d("GisView", "onQueryAddressFinished: ")
        runOnUiThread {
            for (i in 0 until loc.size) {
                val c = loc[i]
                val markers = arrayOf(GeneralMarker(
                        doubleArrayOf(c.lat, c.lng),
                        c.address,
                        resources.getDrawable(R.drawable.tag_pin, null),
                        72, 72, null))
                gisView.addMarker("lm01", 999, markers)

                gisView.addPopup(
                        doubleArrayOf(c.lat, c.lng),
                        c.address,
                        doubleArrayOf(0.0, 0.0),
                        300,
                        100,
                        "hello marker"
                )
            }
        }

        Looper.prepare()
        Toast.makeText(this, loc[0].address + " is x=" + loc[0].lng + ",y=" + loc[0].lat, Toast.LENGTH_SHORT).show()
        Looper.loop()
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