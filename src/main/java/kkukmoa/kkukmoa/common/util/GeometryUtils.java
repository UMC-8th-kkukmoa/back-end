package kkukmoa.kkukmoa.common.util;

import org.locationtech.jts.geom.*;

public class GeometryUtils {
    private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

    public static Point createPoint(double longitude, double latitude) {
        return GF.createPoint(new Coordinate(longitude, latitude));
    }
}
