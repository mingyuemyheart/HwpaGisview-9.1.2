package gis.hmap;

/**
 * 获取ivas数据回调
 */
public interface SwithServiceListener {
    void switchServiceSuccess(SwithServiceData data);
    void switchServiceFailed();
}
