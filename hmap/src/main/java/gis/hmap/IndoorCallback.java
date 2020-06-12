package gis.hmap;

/**
 * Created by Ryan on 2019/5/18.
 */

public interface IndoorCallback {
    @Deprecated
    void done();//建议使用indoorSuccess
    void indoorSuccess(String roomCode);//长编码
    void indoorSuccess(IndoorMapData indoorMapData);//室内数据
}
