package gis.gisdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import gis.hmap.Common
import gis.hmap.GisView
import kotlinx.android.synthetic.main.activity_blank.*

class BlankActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blank)
        initMap()
    }

    private fun initMap() {
        Common.setLogEnable(true)

        btnTest.setOnClickListener {
            startActivity(Intent(this@BlankActivity, MainActivity::class.java))
            Common.setUGCV5(false)
            GisView.setGisServer("http://mcloud-uat.huawei.com/mcloud/mag/FreeProxyForText/BTYQ_json")//华为平安园区
        }

        btnProduce.setOnClickListener {
            startActivity(Intent(this@BlankActivity, MainActivity::class.java))
            Common.setUGCV5(false)
            GisView.setGisServer("http://w3m.huawei.com/mcloud/mag/FreeProxyForText/BTYQ_json")//生产环境
        }

        btnWA.setOnClickListener {
            startActivity(Intent(this@BlankActivity, MainActivity::class.java))
            Common.setUGCV5(true)
            GisView.setGisServer("http://192.168.1.101:8090/iserver/services")
        }

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
//                        val externLocData = GisView.decodeLocLocation(this@BlankActivity, "a760a2b8-e982-43c4-a9d4-e1214d4e1290", 1)
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
//
//        //该方法由于超图没有适配android24及以上
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
//            GisView.enableAutoClearCache(true)
//        }
//
//        gisView.addMapLoadedListener { Toast.makeText(this@BlankActivity, "地图加载完成", Toast.LENGTH_SHORT).show() }

    }

}
