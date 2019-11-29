package gis.hmap;

import com.supermap.services.components.commontypes.Feature;

public class IVASMappingData {
    public double lat;
    public double lng;
    public String roomCode;
    public String buildingId;
    public String[] floorList;
    public String ivasBuildingId;
    public int[] ivasFloorList;
    public String[] fields;
    public String[] values;

    public IVASMappingData(Feature feature, boolean useTest) {
        fields = feature.fieldNames;
        values = feature.fieldValues;
        for (int i=0; i<feature.fieldNames.length; i++) {
            if (feature.fieldNames[i].equalsIgnoreCase("SMX"))
                lng = Double.parseDouble(feature.fieldValues[i]);
            else if (feature.fieldNames[i].equalsIgnoreCase("SMY"))
                lat = Double.parseDouble(feature.fieldValues[i]);
            else if (feature.fieldNames[i].equalsIgnoreCase("ROOMCODE"))
                roomCode = feature.fieldValues[i];
            else if (feature.fieldNames[i].equalsIgnoreCase("BUILDINGID"))
                buildingId = feature.fieldValues[i];
            else if (feature.fieldNames[i].equalsIgnoreCase("FLOORLIST"))
                floorList = feature.fieldValues[i].split(",");
            else if (feature.fieldNames[i].equalsIgnoreCase("IVASPRODUCTIONBUILDINGID") && useTest == false)
                ivasBuildingId = feature.fieldValues[i];
            else if (feature.fieldNames[i].equalsIgnoreCase("IVASTESTBUILDINGID") && useTest)
                ivasBuildingId = feature.fieldValues[i];
            else if (feature.fieldNames[i].equalsIgnoreCase("IVASFLOORLIST")) {
                String[] lst = feature.fieldValues[i].split(",");
                if (lst != null) {
                    ivasFloorList = new int[lst.length];
                    for (int j = 0; j < lst.length; j++)
                        ivasFloorList[j] = Integer.parseInt(lst[j]);
                }
            }
        }
    }
}
