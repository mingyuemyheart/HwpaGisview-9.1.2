package gis.gisdemo

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import gis.hmap.GisView
import gis.hmap.IVASMappingData
import gis.hmap.IVASMappingListener
import kotlinx.android.synthetic.main.activity_blank.*

class BlankActivity : Activity() {

    private val mUIHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank)
        initMap()
    }

    private fun initMap() {
//        btnIntent.visibility = View.GONE
        btnIntent.setOnClickListener {
            startActivity(Intent(this@BlankActivity, MainActivity2::class.java))
        }

//        GisView.setGisServer("http://mcloud-uat.huawei.com/mcloud/mag/FreeProxyForText/BTYQ_json")//华为平安园区
//        GisView.setGisServer("http://w3m.huawei.com/mcloud/mag/FreeProxyForText/BTYQ_json")//生产环境
        GisView.setGisServer("http://192.168.1.249:8090/iserver/services")
//        GisView.setLocDecoder(false, object : IVASMappingListener {
//            override fun onIVASMappingSuccess(iVasMapping: MutableMap<String, IVASMappingData>?) {
//                mUIHandler.post {
//                    if (iVasMapping != null) {
//                        for ((key, value) in iVasMapping) {
//                            Log.e("onIVASMappingSuccess", key)
//                        }
//                        Toast.makeText(this@BlankActivity, "获取IVAS数据成功", Toast.LENGTH_SHORT).show()
//                        btnIntent.visibility = View.VISIBLE
//
//                        val externLocData = GisView.decodeLocLocation(this@BlankActivity, "f8296b86-4090-49f6-a84c-6fd345f1fc16", 1)
//                        for (i in externLocData.values.indices) {
//                            Log.e("values", externLocData.fields[i]+"---"+externLocData.values[i])
//                        }
//                        Log.e("externLocData", externLocData.lat.toString()+","+externLocData.lng.toString()+","+externLocData.buildingId+","+externLocData.floorId+","+externLocData.roomCode)
//                    }
//                }
//            }
//
//            override fun onIVASMappingFailed(msg: String?) {
//                mUIHandler.post {
//                    Toast.makeText(this@BlankActivity, msg, Toast.LENGTH_SHORT).show()
//                }
//            }
//        })

        //获取对应经纬度的园区信息
        GisView.queryWorkspace(114.057771, 22.656049) { loc ->
            mUIHandler.post {
                if (loc != null && loc.isNotEmpty()) {
                    for (i in 0 until loc.size) {
                        val geoLocation = loc[i]
                        if (!TextUtils.isEmpty(geoLocation.address)) {
                            Log.e("switchWorkspace", geoLocation.address)
                        }
                    }
                }
            }
        }

        //该方法由于超图没有适配android24及以上
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            GisView.enableAutoClearCache(true)
        }

        gisView.setLogEnable(true)
        gisView.loadMap(4, doubleArrayOf(22.656049, 114.057771))
//        gisView.addMapLoadedListener { Toast.makeText(this@BlankActivity, "地图加载完成", Toast.LENGTH_SHORT).show() }

        //poi查询，获取对应经纬度下园区信息、地理位置信息等，需要实例化gisview以后使用
        gisView.getAddressOfLocation(114.057771, 22.656049) { loc ->
            mUIHandler.post {
                if (loc != null && loc.isNotEmpty()) {
                    for (i in 0 until loc.size) {
                        val geoLocation = loc[i]
                        if (!TextUtils.isEmpty(geoLocation.address)) {
                            Log.e("getAddressOfLocation", geoLocation.address)
                        }
                    }
                }
            }
        }

    }

}
