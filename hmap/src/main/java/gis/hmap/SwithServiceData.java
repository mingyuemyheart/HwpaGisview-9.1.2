package gis.hmap;

import com.supermap.services.components.commontypes.Feature;

/**
 * 切换服务，获取真实GisServer地址和园区信息
 */
public class SwithServiceData {

    public double lat;
    public double lng;
    public String parkId;
    public String gisServer;
    public String mapType;
    public String inputUrl;
    public String divide;//区分内外网，内网0，外网1

    public SwithServiceData(Feature feature) {
        for (int i = 0; i < feature.fieldNames.length; i++) {
            if (feature.fieldNames[i].equalsIgnoreCase("SMX"))
                lng = Double.parseDouble(feature.fieldValues[i]);
            else if (feature.fieldNames[i].equalsIgnoreCase("SMY"))
                lat = Double.parseDouble(feature.fieldValues[i]);
            else if (feature.fieldNames[i].equalsIgnoreCase("PARKID"))
                parkId = feature.fieldValues[i];
            else if (feature.fieldNames[i].equalsIgnoreCase("URL"))
                gisServer = feature.fieldValues[i] + "/services";
            else if (feature.fieldNames[i].equalsIgnoreCase("MAPTYPE"))
                mapType = feature.fieldValues[i];
            else if (feature.fieldNames[i].equalsIgnoreCase("INPUTURL"))
                inputUrl = feature.fieldValues[i];
            else if (feature.fieldNames[i].equalsIgnoreCase("DIVIDE"))
                divide = feature.fieldValues[i];
        }
    }
}
