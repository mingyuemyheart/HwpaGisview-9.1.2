package gis.hmap;

import com.supermap.imobilelite.maps.Point2D;
import com.supermap.services.components.commontypes.Rectangle2D;

import java.util.List;

/**
 * Created by Ryan on 2019/6/4.
 */

public class IndoorMapData {
    public String buildingId;
    public String floorId;
    public String roomCode;
    public List<List<Point2D>> buildingGeometry;
    public Rectangle2D buildingBounds;
    public List<ModelData> rooms;

    public IndoorMapData(String buildingId, String floorId, String roomCode, List<List<Point2D>> buildingGeometry, Rectangle2D buildingBounds, List<ModelData> rooms) {
        this.buildingId = buildingId;
        this.floorId = floorId;
        this.roomCode = roomCode;
        this.buildingGeometry = buildingGeometry;
        this.buildingBounds = buildingBounds;
        this.rooms = rooms;
    }
}
