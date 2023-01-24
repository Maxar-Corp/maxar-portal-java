package com.maxar.MPSSDK;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class Wcs {

    private final Auth auth;
    private final Portal portal;
    private final String baseUrl;
    private final String version;
    private final HashMap<String, String> querystring;


    Wcs(Portal portal) {
        this.portal = portal;
        this.auth = portal.getAuth();
        this.baseUrl = auth.getApiBaseUrl() + "/deliveryservice/wcsaccess";
        this.version = auth.getVersion();
        this.querystring = this.initQuerystring();
    }

    String returnImage() {

        Portal portal = this.portal;
        String bbox = portal.getBbox();
        String filter = portal.getFilter();
        String token = this.auth.refreshToken();
        this.querystring.put("boundingbox", bbox);
        this.querystring.put("identifier", portal.getFeatureId());
        this.querystring.put("gridoffsets", String.valueOf(portal.getGridOffsets()));
        if (bbox.contains("EPSG:3857")) {
            this.querystring.put("gridcrs", "urn:ogc:def:crs:EPSG::3857");
            this.querystring.put("gridbasecrs", "urn:ogc:def:crs:EPSG::3857");
            bbox = PortalUtils.processBbox(portal);
            this.querystring.put("boundingbox", bbox);
        }
        if (filter != null) {
            try {
                PortalUtils.cqlChecker(filter);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace(System.out);
            }
            this.querystring.put("coverage_cql_filter", filter);
        }
        if (portal.getImageFormat() != null) {
            this.querystring.put("format", portal.getImageFormat());
        }
        if (portal.getFeatureProfile() != null) {
            this.querystring.put("featureprofile", portal.getFeatureProfile());
        }
        //HTTP request
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(this.baseUrl).newBuilder();
        //Build params from querystring
        for (Map.Entry<String, String> set : this.querystring.entrySet()) {
            urlBuilder.addQueryParameter(set.getKey(), set.getValue());
        }
        String URL = urlBuilder.build().toString();
        Request getRequest = new Request.Builder()
            .header("Authorization", "Bearer " + token)
            .url(URL)
            .build();
        Response response;
        try {
            response = client.newCall(getRequest).execute();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (response.code() != 200) {
            System.out.printf("error: %s %s", response.code(), response.body());
        }
        try {
            return response.body().string();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    String parseCoverage(String coverage) {
//        ArrayList<String> write;
//
//    }

    private HashMap<String, String> initQuerystring() {
        HashMap<String, String> querystring = new HashMap<>();
        querystring.put("service", "WCS");
        querystring.put("request", "GetCoverage");
        querystring.put("version", "1.3.0");
        querystring.put("gridcrs", "urn:ogc:def:crs:EPSG::4326");
        querystring.put("SDKversion", this.version);
        return querystring;
    }
}
