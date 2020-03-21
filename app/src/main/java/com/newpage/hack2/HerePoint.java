package com.newpage.hack2;

import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.routing.Waypoint;

public class HerePoint {

    public Waypoint waypoint;
    public MapMarker mapMarker;

    public String description;

    HerePoint(GeoCoordinates geos, String description, MapImage image) {
        this.description = description;
        waypoint = new Waypoint(geos);
        mapMarker = new MapMarker(geos);
        mapMarker.addImage(image, new MapMarkerImageStyle());
    }

}
