package com.maxar.MPSSDK;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import javax.sound.sampled.Port;
import okhttp3.Response;

class Wms {

    private final Auth auth;
    private final String baseUrl;
    private final String version;
    private final HashMap<String, String> querystring;
    private final Portal portal;

    Wms(Portal portal) {

        this.portal = portal;
        this.auth = portal.getAuth();
        this.baseUrl = auth.getApiBaseUrl() + "/geoserver/wms";
        this.version = auth.getVersion();
        this.querystring = this.initQueryString();
    }

    String returnImage() throws IOException {

        /*
            Function finds the imagery matching a bbox or feature id
            args:
                bbox = String bounding box of AOI. Comma delimited set of coordinates. (miny,minx,maxy,maxx)
                filter = CQL filter used to refine data of search.
                height = Integer value representing the vertical number of pixels to return
                width = Integer value representing the horizontal number of pixels to return
                layers = String representing the called upon layer. Defaults to 'DigitalGlobe:Imagery'
                format = String of the format of the response image either jpeg, png or geotiff
                featureprofile = String of the desired stacking profile. Defaults to account Default
            Returns:
                requests response object of desired image
         */

        String bbox = portal.getBbox();
        String filter = portal.getFilter();
        PortalUtils.validateImageFormat(portal);
        this.querystring.put("format", portal.getParamsImageFormat());
        // Check if srsname was provided. If not, default to EPSG:4326
        String srsname = portal.getSrsname() != null ? portal.getSrsname() : "EPSG:4326";
        this.querystring.put("crs", srsname);

        if (bbox != null) {
            PortalUtils.validateBbox(bbox);
            String bboxList = PortalUtils.processBbox(portal);
            this.querystring.put("bbox", bboxList);
        } else {
            throw new IllegalArgumentException("Search function must have a BBOX.");
        }
        if (filter != null) {
            PortalUtils.cqlChecker(filter);
            this.querystring.put("cql_filter", filter);
        }
        if (((Number) portal.getHeight()).doubleValue() != 0) {
            this.querystring.put("height", String.valueOf(portal.getHeight()));
        }
        if (((Number) portal.getWidth()).doubleValue() != 0) {
            this.querystring.put("width", String.valueOf(portal.getWidth()));
        }
        Response response = PortalUtils.handleRequest(this.auth, this.baseUrl, this.querystring);
        return PortalUtils.handleDownload(portal, response);

    }

    void rerunFailedImages() {

        /*
            Function reruns requests for images that previously failed, from a csv file
        Args:
            featureid (string) = Feature id of the image
            img_size (int) = Desired pixel resolution (size x size). Defaults to 1024
            outputdirectory (string) = Desired output location for tiles
        Returns:
            None
         */
        String width = (((Number) portal.getWidth()).doubleValue() != 0) ? String.valueOf(
            portal.getWidth()) : "1024";
        String height = (((Number) portal.getHeight()).doubleValue() != 0) ? String.valueOf(
            portal.getHeight()) : "1024";
        this.querystring.put("width", width);
        this.querystring.put("height", height);
        this.querystring.put("coverage_cql_filter", String.format("featureId='%s'", portal.getFeatureId()));
        PortalUtils.validateImageFormat(portal);
        this.querystring.put("format", portal.getParamsImageFormat());

        ArrayList<String> failedRequests = new ArrayList<>();
        String token = auth.refreshToken();
        String homeDirectory = portal.getDownloadPath() != null ? portal.getDownloadPath() :
            System.getProperty("user.home");
        String csvFile = homeDirectory + "/failed_tiles.csv";
        String line = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String subBbox1 = data[0];
                String subBbox2 = data[1];
                String subBbox3 = data[2];
                String subBbox4 = data[3];
                String subGridCellLocation = data[4];
                String subBbox = subBbox1 + ", " + subBbox2 + ", " + subBbox3 + ", " + subBbox4;
                this.querystring.put("bbox", subBbox);
                Response repsonse = PortalUtils.handleRequest(auth, this.baseUrl, this.querystring);
                if (repsonse.code() != 200) {
                    System.out.println("Request failed for image " + subGridCellLocation);
                    failedRequests.add(line);
                } else {
                    PortalUtils.handleDownload(portal, repsonse);
                    System.out.println("Request success for image " + subGridCellLocation);
                }
            }
        }
        catch (FileNotFoundException e) {
            throw new IllegalArgumentException("failed_tiles.csv not found in user home directory");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File file = new File(csvFile);
        if (file.exists()) {
            file.delete();
        }
       if (failedRequests.size() > 0) {
           try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
               for (String failedRequest : failedRequests) {
                   String[] data = line.split(",");
                   pw.println(failedRequest);
               }
           } catch (IOException e) {
               throw new RuntimeException(e);
           }
       }
    }

    HashMap<String, String> initQueryString() {
        HashMap<String, String> queryString = new HashMap<>();
        queryString.put("service", "WMS");
        queryString.put("request", "GetMap");
        queryString.put("version", "1.3.0");
        queryString.put("crs", "EPSG:4326");
        queryString.put("height", "512");
        queryString.put("width", "512");
        queryString.put("layers", "Maxar:Imagery");
        queryString.put("SDKversion", this.version);

        return queryString;
    }

}
