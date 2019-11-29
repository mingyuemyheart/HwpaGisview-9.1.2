package gis.gisdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import gis.hmap.GeoLocation;
import gis.hmap.GeoServiceCallback;
import gis.hmap.GisView;
import gis.hmap.IVASMappingData;
import gis.hmap.IVASMappingListener;
import gis.hmap.MapLoadedEvent;
import gis.hmap.MapLoadedListener;
import gis.hmap.QueryWorkspaceListener;

public class BlankActivity extends Activity {

    private Handler mainHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        initMap();

    }

    private void initMap() {
        final Button button = (Button) findViewById(R.id.btnIntent);
        button.setVisibility(View.GONE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BlankActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        GisView.setGisServer("http://mcloud-uat.huawei.com/mcloud/mag/FreeProxyForText/BTYQ_json");//华为平安园区
        GisView.setLocDecoder(true, new IVASMappingListener() {
            @Override
            public void onIVASMappingSuccess(final List<IVASMappingData> iVasMapping) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < iVasMapping.size(); i++) {
                            Log.e("onIVASMappingSuccess"+i, iVasMapping.get(i).ivasBuildingId);
                        }
                        Toast.makeText(BlankActivity.this, "获取IVAS数据成功", Toast.LENGTH_SHORT).show();
                        button.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onIVASMappingFailed(final String msg) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("onIVASMappingFailed", msg);
                        Toast.makeText(BlankActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });//获取IVAS数据

        //获取对应经纬度的园区信息
        GisView.queryWorkspace(114.0576151013374200, 22.6573017046106460, new QueryWorkspaceListener() {
            @Override
            public void onQueryWorkspace(final GeoLocation[] loc) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loc != null && loc.length > 0) {
                            for (int i = 0; i < loc.length; i++) {
                                GeoLocation geoLocation = loc[i];
                                if (!TextUtils.isEmpty(geoLocation.address)) {
                                    Log.e("switchWorkspace", geoLocation.address);
                                }
                            }
                        }
                    }
                });
            }
        });

        GisView gisView = (GisView) findViewById(R.id.gisView);
        gisView.loadMap(5, new double[]{22.6573017046106460, 114.0576151013374200});
        gisView.addMapLoadedListener(new MapLoadedListener() {
            @Override
            public void OnMapLoaded(MapLoadedEvent me) {
                Toast.makeText(BlankActivity.this, "地图加载完成", Toast.LENGTH_SHORT).show();
            }
        });

        //poi查询，获取对应经纬度下园区信息、地理位置信息等，需要实例化gisview以后使用
        gisView.getAddressOfLocation(114.0576151013374200, 22.6573017046106460, new GeoServiceCallback() {
            @Override
            public void onQueryAddressFinished(final GeoLocation[] loc) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loc != null && loc.length > 0) {
                            for (int i = 0; i < loc.length; i++) {
                                GeoLocation geoLocation = loc[i];
                                if (!TextUtils.isEmpty(geoLocation.address)) {
                                    Log.e("getAddressOfLocation", geoLocation.address);
                                }
                            }
                        }
                    }
                });
            }
        });

    }

}
