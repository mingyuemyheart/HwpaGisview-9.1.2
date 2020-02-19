package gis.hmap;

import java.util.Map;

/**
 * 获取ivas数据回调
 */
public interface IVASMappingListener {
    void onIVASMappingSuccess(Map<String, IVASMappingData> iVasMapping);
    void onIVASMappingFailed(String msg);
}
