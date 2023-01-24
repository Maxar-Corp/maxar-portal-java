package com.maxar.MPSSDK;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

class Wfs {

    private final Auth auth;
    private final String baseUrl;
    private final String version;
    private final HashMap<String, String> querystring;
    private final Portal portal;

    Wfs(Portal portal) {
        this.portal = portal;
        this.auth = this.portal.getAuth();
        this.baseUrl = this.auth.getApiBaseUrl() + "/geoserver/wfs";
        this.version = this.auth.getVersion();
        this.querystring = this.initQueryString();
    }

    Response search() throws IOException {
        /*
                Function searches using the wfs method.
        Args:
            typename = String of the typename. Defaults to 'FinishedFeature'. Example input 'MaxarCatalogMosaicProducts'
            bbox = String bounding box of AOI. Comma delimited set of coordinates. (miny,minx,maxy,maxx)
            filter = CQL filter used to refine data of search.
            outputformat = String of the format of the response object. Defaults to json.
            featureprofile = String of the desired stacking profile. Defaults to account Default
            typename = String of the typename. Defaults to FinishedFeature. Example input MaxarCatalogMosaicProducts
            srsname (string) = Desired projection. Defaults to EPSG:4326
        Returns:
            Response object of the search
     */

        Portal portal = this.portal;
        String bbox = portal.getBbox();
        String filter = portal.getFilter();
        String featureID = portal.getFeatureId();

        // If featureID was provided, format into filter
        if (featureID != null) {
            filter = String.format("featureId='%s'", featureID);
        }

        // Check if srsname was provided. If not, default to EPSG:4326
        String srsname = portal.getSrsname();
        srsname = (srsname != null) ? portal.getSrsname() : "EPSG:4326";
        this.querystring.put("srsname", srsname);

        //Logic to determine cql_filter and bbox parameters based on user selections
        if (bbox != null) {
            PortalUtils.validateBbox(bbox);
            String bboxList = PortalUtils.processBbox(portal);
            if (filter != null) {
                this.combineBboxAndFilter(bboxList, filter, srsname);
            } else {
                this.querystring.put("bbox", bboxList);
            }
        } else if (filter != null) {
            this.querystring.put("cql_filter", filter);
        } else {
            throw new IllegalArgumentException("Search function must have a BBOX or a Filter");
        }
        if (portal.getRequestType() != null) {
            this.querystring.put("request", portal.getRequestType());
            this.querystring.remove("outputFormat");
        }

        if (portal.isShapefile()) {
            this.querystring.put("outputFormat", "shape-zip");
        } else if (portal.isCsv()) {
            this.querystring.put("outputFormat", "csv");
        }

        this.querystring.put(PortalUtils.removeCache(), PortalUtils.removeCache());
        //send request
        return PortalUtils.handleRequest(this.auth, this.baseUrl, this.querystring);

    }

    private HashMap<String, String> initQueryString() {
        HashMap<String, String> queryString = new HashMap<>();
        queryString.put("service", "WFS");
        queryString.put("request", "GetFeature");
        queryString.put("typenames", "Maxar:FinishedFeature");
        queryString.put("outputFormat", "json");
        queryString.put("version", "1.1.0");
        queryString.put("SDKversion", this.version);

        return queryString;
    }

    private void combineBboxAndFilter(String bbox, String filter, String srsname) {
        if (!Objects.equals(srsname, "EPSG:4326")) {
            String[] bboxList = bbox.split(",");
            bboxList[4] = String.format("'%s'", srsname);
            bbox = StringUtils.join(bboxList, ",");
        }
        String bboxGeometry = String.format("BBOX(featureGeometry,%s)", bbox);
        String combinedFilter = bboxGeometry + "AND(" + filter + ")";
        this.querystring.put("cql_filter", combinedFilter);
    }

}
