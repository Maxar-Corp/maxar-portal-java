package com.maxar.MPSSDK;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public class Wmts {

    private final Auth auth;
    private final String baseUrl;
    private final String version;
    private final HashMap<String, String> querystring;
    private final Portal portal;

    public Wmts(Portal portal) {
        this.portal = portal;
        this.auth = portal.getAuth();
        this.baseUrl = auth.getApiBaseUrl() + "/geoserver/gwc/service/wmts";
        this.version = auth.getVersion();
        this.querystring = this.initQueryString();
    }

    public Response wmtsGetTile(String tilerow, String tilecol, String zoomLevel) throws IOException {
        /*
            Function executes the wmts call and returns a response object of the desired tile
        Args:
            tilerow: String value of the tile row.
            tilecol: String value of the tile column
            zoomLevel: String value of the desired zoom level
        Returns:
            WMTS tiles for the input data
         */

        this.querystring.put("TileMatrix", this.querystring.get("TileMatrixSet") + ":" +
            zoomLevel);
        this.querystring.put("tilerow", tilerow);
        this.querystring.put("tilecol", tilecol);
        this.querystring.put("format", portal.getParamsImageFormat());
        this.querystring.put("request", "GetTile");
        return PortalUtils.handleRequest(this.auth, this.baseUrl, this.querystring);
    }

    public HashMap<String, String> wmtsBboxGetTileList(Portal portal) {
        /*
             Function takes in a bbox and zoom level to return a list of WMTS calls that can be used to acquire all the wmts
             tiles. Projection defaults to EPSG:4326
             Args:
                 zoomLevel: Integer value of the desired zoom level
                 bbox: String bounding box of AOI. Comma delimited set of coordinates. (miny,minx,maxy,maxx)
             Returns:
                 List of WMTS calls.
         */
        if (((Number) portal.getZoomLevel()).doubleValue() == 0) {
            throw new IllegalArgumentException("Must provide a zoom level");
        }
        PortalUtils.validateBbox(portal.getBbox());
        String[] bboxList = portal.getBbox().split(",");
        double minX = Double.parseDouble(bboxList[1]);
        double minY = Double.parseDouble(bboxList[0]);
        double maxX = Double.parseDouble(bboxList[3]);
        double maxY = Double.parseDouble(bboxList[2]);

        HashMap<String, Integer> results;
        results = this.wmtsConvert(minY, minX, portal.getZoomLevel(), portal.getSrsname());
        long minTileRow = results.get("tileRow");
        long minTileCol = results.get("tileCol");
        results = this.wmtsConvert(maxY, maxX, portal.getZoomLevel(), portal.getSrsname());
        long maxTileRow = results.get("tileRow");
        long maxTileCol = results.get("tileCol");

        if (maxTileRow < minTileRow) {
            //swap variable assignments
            maxTileRow = maxTileRow ^ minTileRow ^ (minTileRow = maxTileRow);
        }
        if (maxTileCol < minTileCol) {
            //swap variable assignments
            maxTileCol = maxTileCol ^ minTileCol ^ (minTileCol = maxTileCol);
        }

        HashMap<String, String> wmtsCallList = new HashMap<>();
        for (long i = minTileCol; i < maxTileCol + 1; i++) {
            for (long j = minTileRow; j < maxTileRow + 1; j++) {
                this.querystring.put("TileMatrixSet", portal.getSrsname());
                this.querystring.put("TileMatrix", this.querystring.get("TileMatrixSet") + ":" +
                    portal.getZoomLevel());
                this.querystring.put("tileRow", String.valueOf(i));
                this.querystring.put("tileCol", String.valueOf(j));
                HttpUrl.Builder urlBuilder = HttpUrl.parse(this.baseUrl).newBuilder();
                //Build params from querystring
                for (Map.Entry<String, String> set : this.querystring.entrySet()) {
                    urlBuilder.addQueryParameter(set.getKey(), set.getValue());
                }
                String call = urlBuilder.build().toString();
                ArrayList<String> rowCol = new ArrayList<>();
                rowCol.add(this.querystring.get("tileRow"));
                rowCol.add(this.querystring.get("tileCol"));
                rowCol.add(String.valueOf(portal.getZoomLevel()));
                wmtsCallList.put(String.valueOf(rowCol), call);
            }
        }
        return wmtsCallList;
    }

    public HashMap<String, Integer> wmtsConvert(double latY, double longX, int zoomLevel, String crs) {
    /*
                Function converts a lat long position to the tile column and row needed to return WMTS imagery over the area
        Args:
            zoomLevel: Integer value of the desired zoom level
            laty: Integer value of the latitude
            longx: Integer value of the desired longitude
        Returns:
            String values of the Tile Row and the Tile Column
     */
        if (Objects.equals(crs, "4326")) {
            /*
                GetCapablities call structure changed from SW2 to SW3, hardcoded TileMatrixSets
                instead of restructuring the XML parser
                fill tileMatrixSet 0 - 21 ->
                first value: {0: {"MatrixWidth": 2, "MatrixHeight": 1}}
                final value: {21: {"MatrixWidth": 4194304, "MatrixHeight": 2097152}}
            */
            HashMap<Integer, HashMap<String, Integer>> tileMatrixSet = new HashMap<>();
            int value1 = 1;
            for (int i = 0; i <= 21; i++) {
                int finalValue1 = value1;
                tileMatrixSet.put(i, new HashMap<String, Integer>() {{
                    put("MatrixWidth", finalValue1 * 2);
                    put("MatrixHeight", finalValue1);
                }});
                value1 = value1 * 2;
            }

            Integer matrixWidth = tileMatrixSet.get(zoomLevel).get("MatrixWidth");
            Integer matrixHeight = tileMatrixSet.get(zoomLevel).get("MatrixHeight");
            if (matrixWidth == null || matrixHeight == null) {
                throw new IllegalArgumentException("Unable to determine Matrix dimensions from input coordinates");
            }
            HashMap<String, Integer> results = new HashMap<>();
            results.put("tileRow" , (int) Math.round((longX + 180) * (matrixWidth / 360.0)));
            results.put("tileCol" , (int) Math.round((90 - latY) * (matrixHeight / 180.0)));
            return results;
        }
        else {
            CoordinateReferenceSystem sourceCRS;
            CoordinateReferenceSystem targetCRS;
            double[] point = {latY, longX};
            double[] pointTransformed = new double[2];
            MathTransform transform;
            try {
                targetCRS = CRS.decode("EPSG:3857");
                sourceCRS = CRS.decode("EPSG:4326");
                transform = CRS.findMathTransform(sourceCRS, targetCRS);
                transform.transform(point, 0, pointTransformed, 0, 1);
            } catch (FactoryException | TransformException e) {
                throw new RuntimeException(e);
            }
            double latRads = Math.toRadians(pointTransformed[0]);
            double n = Math.pow(2, portal.getZoomLevel());
            double xTile = (longX + 180 )/ (360 * n);
            double yTile = (1 - Math.asin(Math.tan(latRads)) / Math.PI) / 2 * n;
            HashMap<String, Integer> results = new HashMap<>();
            results.put("tileRow" , (int) xTile);
            results.put("tileCol" , (int) yTile);
            return results;
        }
    }

    private HashMap<String, String> initQueryString() {
        HashMap<String, String> queryString = new HashMap<>();
        queryString.put("service", "WMTS");
        queryString.put("request", "GetTile");
        queryString.put("TileMatrixSet", "EPSG:4326");
        queryString.put("Layer", "Maxar:Imagery");
        queryString.put("version", "1.1.0");
        queryString.put("SDKversion", this.version);

        return queryString;
    }

}
