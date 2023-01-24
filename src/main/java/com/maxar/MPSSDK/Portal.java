package com.maxar.MPSSDK;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Portal {

    private final Auth auth;
    private String bbox;
    private final String filter;
    private final boolean shapefile;
    private final boolean csv;
    private final String featureProfile;
    private final String typename;
    private String srsname;
    private int height;
    private int width;
    private String imageFormat;
    private final String featureId;
    private final String gridOffsets;
    private final int zoomLevel;
    private final boolean download;
    private final String legacyId;
    private final String catalogId;
    private ArrayList<String> bandCombination;
    private final int imageSize;
    private int threadNumber;
    private final boolean mosaic;
    private String fileName;
    private final ArrayList<String> coordinateList;
    private final ArrayList<String> tileCoordinateList;
    private final int numberOfAttempts;
    private final String baseDirectory;
    private String downloadPath;
    private final String requestType;
    private final boolean display;
    private final InputStream picture;
    private String paramsImageFormat;



    Auth getAuth() {
        return auth;
    }

    String getBbox() {
        return bbox;
    }

    String getFilter() {
        return filter;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    boolean isShapefile() {
        return shapefile;
    }

    boolean isCsv() {
        return csv;
    }

    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    String getFeatureProfile() {
        return featureProfile;
    }

    String getTypename() {
        return typename;
    }

    String getSrsname() {
        return srsname;
    }

    void setSrsname(String srsname) {
        this.srsname = srsname;
    }

    int getHeight() {
        return height;
    }

    int getWidth() {
        return width;
    }

    String getImageFormat() {
        return imageFormat;
    }

    String getFeatureId() {
        return featureId;
    }

    String getGridOffsets() {
        return gridOffsets;
    }

    int getZoomLevel() {
        return zoomLevel;
    }

    boolean isDownload() {
        return download;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    String getLegacyId() {
        return legacyId;
    }

    String getCatalogId() {
        return catalogId;
    }

    ArrayList<String> getBandCombination() {
        return bandCombination;
    }

    void setBandCombination(ArrayList<String> bandCombination) {
        this.bandCombination = bandCombination;
    }

    int getImageSize() {
        return imageSize;
    }

    int getThreadNumber() {
        return threadNumber;
    }

    boolean isMosaic() {
        return mosaic;
    }

    String getFileName() {
        return fileName;
    }

    ArrayList<String> getCoordinateList() {
        return coordinateList;
    }

    ArrayList<String> getTileCoordinateList() {
        return tileCoordinateList;
    }

    int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    String getBaseDirectory() {
        return baseDirectory;
    }

    String getDownloadPath() {
        return downloadPath;
    }

    String getRequestType() {
        return requestType;
    }

    boolean isDisplay() {
        return display;
    }

    InputStream getPicture() {
        return picture;
    }

    void setImageFormat(String format) {
        this.imageFormat = format;
    }

    void setFileName(String fileName) {
        this.fileName = fileName;
    }

    String getParamsImageFormat() {
        return this.paramsImageFormat;
    }

    void setParamsImageFormat(String paramsImageFormat) {
        this.paramsImageFormat = paramsImageFormat;
    }

    private Portal(PortalBuilder builder) {
        this.auth = builder.auth;
        this.bbox = builder.bbox;
        this.filter = builder.filter;
        this.shapefile = builder.shapefile;
        this.csv = builder.csv;
        this.featureProfile = builder.featureProfile;
        this.typename = builder.typename;
        this.srsname = builder.srsname;
        this.height = builder.height;
        this.width = builder.width;
        this.imageFormat = builder.imageFormat;
        this.featureId = builder.featureId;
        this.gridOffsets = builder.gridOffsets;
        this.zoomLevel = builder.zoomLevel;
        this.download = builder.download;
        this.legacyId = builder.legacyId;
        this.catalogId = builder.catalogId;
        this.bandCombination = builder.bandCombination;
        this.imageSize = builder.imageSize;
        this.threadNumber = builder.threadNumber;
        this.mosaic = builder.mosaic;
        this.fileName = builder.fileName;
        this.coordinateList = builder.coordinateList;
        this.tileCoordinateList = builder.tileCoordinateList;
        this.numberOfAttempts = builder.numberOfAttempts;
        this.baseDirectory = builder.baseDirectory;
        this.downloadPath = builder.downloadPath;
        this.requestType = builder.requestType;
        this.display = builder.display;
        this.picture = builder.picture;
    }

    //Builder Class
    public static class PortalBuilder {
        private Auth auth;
        private String username;
        private String password;
        private String clientId;
        private String bbox;
        private String filter;
        private boolean shapefile;
        private boolean csv;
        private String featureProfile;
        private String typename;
        private String srsname;
        private int height;
        private int width;
        private String imageFormat;
        private String featureId;
        private String gridOffsets;
        private int zoomLevel;
        private boolean download;
        private String legacyId;
        private String catalogId;
        private ArrayList<String> bandCombination;
        private int imageSize;
        private int threadNumber;
        private boolean mosaic;
        private String fileName;
        private ArrayList<String> coordinateList;
        private ArrayList<String> tileCoordinateList;
        private int numberOfAttempts;
        private String baseDirectory;
        private String downloadPath;
        private String requestType;
        private boolean display;
        private InputStream picture;

        public PortalBuilder username(String username) {
            this.username = username;
            return this;
        }

        public PortalBuilder password(String password) {
            this.password = password;
            return this;
        }

        public PortalBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public PortalBuilder bbox(String bbox) {
            this.bbox = bbox;
            return this;
        }

        public PortalBuilder filter(String filter) {
            this.filter = filter;
            return this;
        }

        public PortalBuilder shapefile() {
            this.shapefile = true;
            return this;
        }

        public PortalBuilder csv() {
            this.csv = true;
            return this;
        }

        public PortalBuilder featureProfile(String featureProfile) {
            this.featureProfile = featureProfile;
            return this;
        }

        public PortalBuilder typename(String typename) {
            this.typename = typename;
            return this;
        }

        public PortalBuilder srsname(String srsname) {
            this.srsname = srsname;
            return this;
        }

        public PortalBuilder height(int height) {
            this.height = height;
            return this;
        }

        public PortalBuilder width(int width) {
            this.width = width;
            return this;
        }

        public PortalBuilder imageFormat(String imageFormat) {
            this.imageFormat = imageFormat;
            return this;
        }

        public PortalBuilder featureId(String featureId) {
            this.featureId = featureId;
            return this;
        }

        public PortalBuilder gridOffsets(String gridOffsets) {
            this.gridOffsets = gridOffsets;
            return this;
        }

        public PortalBuilder zoomLevel(int zoomLevel) {
            this.zoomLevel = zoomLevel;
            return this;
        }

        public PortalBuilder download() {
            this.download = true;
            return this;
        }

        public PortalBuilder legacyId(String legacyId) {
            this.legacyId = legacyId;
            return this;
        }

        public PortalBuilder catalogId(String catalogId) {
            this.catalogId = catalogId;
            return this;
        }

        public PortalBuilder bandCombination(ArrayList<String> bandCombination) {
            this.bandCombination = bandCombination;
            return this;
        }

        public PortalBuilder imageSize(int imageSize) {
            this.imageSize = imageSize;
            return this;
        }

        public PortalBuilder threadNumber(int threadNumber) {
            this.threadNumber = threadNumber;
            return this;
        }

        public PortalBuilder mosaic() {
            this.mosaic = true;
            return this;
        }

        public PortalBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public PortalBuilder coordinateList(ArrayList<String> coordinateList) {
            this.coordinateList = coordinateList;
            return this;
        }

        public PortalBuilder tileCoordinateList(ArrayList<String> tileCoordinateList) {
            this.tileCoordinateList = tileCoordinateList;
            return this;
        }

        public PortalBuilder numberOfAttempts(int numberOfAttempts) {
            this.numberOfAttempts = numberOfAttempts;
            return this;
        }

        public PortalBuilder baseDirectory(String baseDirectory) {
            this.baseDirectory = baseDirectory;
            return this;
        }

        public PortalBuilder downloadPath(String downloadPath) {
            this.downloadPath = downloadPath;
            return this;
        }

        public PortalBuilder requestType(String requestType) {
            this.requestType = requestType;
            return this;
        }

        public PortalBuilder setDisplay() {
            this.display = true;
            return this;
        }

        public PortalBuilder setPicture(InputStream picture) {
            this.picture = picture;
            return this;
        }

        public Portal build() throws IllegalArgumentException {
            HashMap<String,String> credentials = new HashMap<>();
            credentials.put("username", this.username);
            credentials.put("password", this.password);
            credentials.put("clientId", this.clientId);
            this.auth = new Auth(credentials);
            return new Portal(this);
        }
    }

    public String search() throws IOException {
        /*
            Function searches using the wfs method.
            Required:
                bbox (String) = Bounding box of AOI. Comma delimited set of coordinates. (miny,minx,maxy,maxx)
                filter (String) = CQL filter used to refine data of search.
            Optional:
                shapefile (boolean) = Binary of whether to return as shapefile format
                csv (boolean) = Binary of whether to return as a csv format
                typename (string) = The typename of the desired feature type. Defaults to FinishedFeature. Example input
                MaxarCatalogMosaicProducts
                srsname (string) = Desired projection. Defaults to EPSG:4326. Required only if not using default projection
            Returns:
                String Response. Either a list of features or a shapefile of all features and associated metadata.
         */

        Wfs wfs = new Wfs(this);
        if (this.getFilter() != null) {
            PortalUtils.cqlChecker(this.getFilter());
        }
        Response wfsResults = wfs.search();
        if (this.isShapefile() || this.isCsv()) {
             return PortalUtils.handleDownload(this, wfsResults);
        }
        if (this.getBbox() != null) {
            String results = wfsResults.body().string();
            Gson gson = new Gson();
            //Results json = gson.fromJson(wfsResults.body().string(), Results.class);
            Results json = PortalUtils.aoiCoverage(bbox, results);
            return gson.toJson(json.features());
        }
        return wfsResults.body().string();
    }

    public String downloadImage() throws IOException {

        /*
            Function downloads the image using the wms method.
            Args:
                bbox (string) = Bounding box of AOI. Comma delimited set of coordinates. (miny,minx,maxy,maxx)
                height (int) = The vertical number of pixels to return
                width (int) = The horizontal number of pixels to return
                img_format (string) = The format of the response image either jpeg, png or geotiff
                identifier (string) = The feature id
                gridoffsets (string) = The pixel size to be returned in X and Y dimensions
                zoom_level (int) = The zoom level. Used for WMTS
                download (bool) = User option to download band manipulation file locally.
                outputpath (string) = Output path must include output format. Downloaded path default is user home path.
                legacyid (string) = The duc id to download the browse image
            Returns:
                requests response object or downloaded file path
         */
        Wms wms = new Wms(this);
        String result;
        int height = this.getHeight();
        int width = this.getWidth();
        PortalUtils.validateImageFormat(this);
        ///If legacy ID
        if (this.getLegacyId() != null) {
            String legacyId = this.getLegacyId();
            String URL = "https://api.discover.digitalglobe.com/show?id=" + legacyId;
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            Request okRequest = new Request.Builder()
                .url(URL)
                .build();
            Call call = client.newCall(okRequest);
            Response response;
            try {
                response = call.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            InputStream picture = response.body().byteStream();
            return PortalUtils.handleDownload(this, response);

        } else {
            if (this.getBbox() == null || this.getImageFormat() == null || ((Number) height).doubleValue() == 0 || ((Number) width).doubleValue() == 0) {
                throw new IllegalArgumentException("Downloads must have a bbox an image format a height and a width");
            } else {
                PortalUtils.validateBbox(this.getBbox());
                if (width < 0 || width > 8000) {
                    throw new IllegalArgumentException("Invalid value for width parameter (max 8000)");
                }
                if (height < 0 || height > 8000) {
                    throw new IllegalArgumentException("Invalid value for height parameter (max 8000)");
                }
                return wms.returnImage();
            }
        }
    }

    public String downloadBrowseImage() {
        /*
            Function downloads the browse image for the desired legacy id
            Args:
                input_id (string) = The desired input id (Can be feature id or catalog id)
                img_format (string) = The format of the response image either jpeg, png or geotiff
                outputpath (string) = Output path must include output format. Downloaded path default is user home path.
                display (bool) = Display image to user
            Returns:
                Downloaded image location of desired legacy id in desired format
         */
        //Convert feature to legacy ID
        String inputId = this.getFeatureId();
        String legacyId;
        String[] catalogIDs = {"101", "102", "103", "104", "105", "106"};
        // if the id passed in is a cat id or WVO4 Inv id. Return the browse for that id from discover api
        if (Arrays.asList(catalogIDs).contains(inputId.substring(0,3)) ||
            "-inv".equals(inputId.substring(inputId.length() - 4))) {
            legacyId = inputId;
        }
        /* If the id passed in is a feature id. Use our wfs method to return a json and parse out the
            legacy id from the metadata
         */
        else {
            Portal internalPortalSearch = new PortalBuilder()
                .featureId(inputId)
                .build();
            String searchResults;
            try {
                searchResults = internalPortalSearch.search();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Gson gson = new Gson();
            Results json = gson.fromJson(searchResults, Results.class);
            legacyId = json.features()[0].getProperties().legacyIdentifier();
        }
        PortalUtils.validateImageFormat(this);
        String URL = "https://api.discover.digitalglobe.com/show?id=" + legacyId;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request okRequest = new Request.Builder()
            .url(URL)
            .build();
        Call call = client.newCall(okRequest);
        Response response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return PortalUtils.handleDownload(this, response);
    }

    public HashMap<String, String> getTileList() {
        /*
             Function takes in a bbox and zoom level to return a list of WMTS calls that can be used to aquire all the wmts
             tiles. Projection defaults to EPSG:4326
             Args:
                 zoom_level: Integer value of the desired zoom level
                 bbox: String bounding box of AOI. Comma delimited set of coordinates. (miny,minx,maxy,maxx)
             Returns:
                 List of WMTS calls.
         */
        Wmts wmts = new Wmts(this);
        PortalUtils.validateBbox(this.getBbox());
        return wmts.wmtsBboxGetTileList(this);
    }

    public void downloadTiles() throws IOException {
        /*
            Function downloads all tiles within a bbox dependent on zoom level
        Args:
            bbox (string) = Bounding box of AOI. Comma delimited set of coordinates. (miny,minx,maxy,maxx)
            zoomLevel (int) = The zoom level
            imgFormat (string) = The format of the response image either jpeg, png or geotiff
            outputpath (string) = Output path must include output format. Downloaded path default is user download path.
            display (bool) = Display image in IDE (Jupyter Notebooks only)
        Returns:
            Message displaying success and location of downloaded tiles
         */
        Wmts wmts = new Wmts(this);
        String baseFile;
        baseFile = this.getFileName() != null ? this.getFileName() : "download";
        PortalUtils.validateImageFormat(this);
        HashMap<String, String> wmtsList = this.getTileList();
        for (Map.Entry<String, String> set : wmtsList.entrySet()) {
            String[] params = set.getKey().replaceAll("\\[", "")
                .replaceAll("\\]", "").replaceAll(" ", "").split(",");
            Response response = wmts.wmtsGetTile(params[0], params[1], params[2]);
            this.setFileName(String.format("%s_%s_%s_%s", baseFile, params[0], params[1], params[2]));
            PortalUtils.handleDownload(this, response);
        }
    }

    public void downloadImageWithFeatureId() {
        //TODO determine applicability of WCS functions
    }

    public String downloadImageByPixelCount() throws IOException {
        /*
          Args:
            bbox (string) = Bounding box of AOI. Comma delimited set of coordinates. (miny,minx,maxy,maxx)
            height (int) = The vertical number of pixels to return
            width (int) = The horizontal number of pixels to return
            img_format (string) = The format of the response image either jpeg, png or geotiff
            outputpath (string) = Output path must include output format. Downloaded path default is user home path.
            filter (string) = CQL filter used to refine data of search.
            featureprofile (string) = The desired stacking profile. Defaults to account Default
            bands (list[string]) = The desired band combination of 1-4 items. Requires SWIR 8 Band or MS1_MS2
        Returns:
            Downloaded image location of desired bbox dependent on pixel height and width
         */
        Wms wms = new Wms(this);
        if (this.getFilter() != null) {
            PortalUtils.cqlChecker(this.getFilter());
        }
        PortalUtils.validateImageFormat(this);
        PortalUtils.validateBbox(this.getBbox());
        if (this.getWidth() <= 0 || this.width > 8000) {
            throw new IllegalArgumentException("Invalid value for width parameter (max 8000)");
        }
        if (this.getHeight() <= 0 || this.getHeight() > 8000) {
            throw new IllegalArgumentException("Invalid value for height parameter (max 8000)");
        }
        return wms.returnImage();
    }

    public String bandManipulation() throws IOException {
        /*
        Function changes the bands of the feature id passed in.
        Args:
            bbox (string) = Bounding box of AOI. Comma delimited set of coordinates. (miny,minx,maxy,maxx)
            featureid (string) = The id of the image
            band_combination (list[string]) = The desired band combination of 1-4 items.
            height (int) = The vertical number of pixels to return
            width (int) = The horizontal number of pixels to return
            image_format (string) = The file type that you want downloaded.
            outputpath (string) = Output path must include output format. Downloaded path default is user home path.
        Returns:
            download location for file
         */

        //Check bands given against a list of valid bands
        if (this.getFeatureId() == null) {
            throw new IllegalArgumentException("Band check requires a Feature ID");
        }
        String bandCheck = this.search();
        //String[] bandCheckList = {"MS1_MS2", "SWIR 8-Band"};
        String[] bandCheckList = {"WV03_SWIR", "MS1_MS2", "SWIR 8-Band"};
        Gson gson = new Gson();
        Results json = gson.fromJson(bandCheck, Results.class);
        if (!Arrays.asList(bandCheckList).contains(json.features()[0].getProperties().source())) {
            throw new IllegalArgumentException("Product Type for the image must be either "
                + "SWIR 8-band, WV03_SWIR or MS1_MS2");
        }
        String[] bandOptions = {"R", "G", "B", "C", "Y", "RE", "N", "N2", "S1", "S2", "S3", "S4",
            "S5", "S6", "S7"};
        ArrayList<String> bandCombination = this.getBandCombination();
        if (bandCombination.size() == 0 || bandCombination.size() > 4) {
            throw new IllegalArgumentException("The number of bands must be greater than 0 and less than or equal to 4");
        }
        ArrayList<String> bandString = new ArrayList<>();
        for (int i = 0; i < bandCombination.size(); i++) {
            if (Arrays.asList(bandOptions).contains(bandCombination.get(i))) {
                bandString.add(bandCombination.get(i));
            } else {
                throw new IllegalArgumentException(bandCombination + " is not a valid option");
            }
        }
        this.setBandCombination(bandString);
        return this.downloadImageByPixelCount();

    }

    public void getImageFromCsv() {
        /*
            Function reruns requests for images that previously failed, from a csv file
        Args:
            featureid (string) = Feature id of the image
            img_size (int) = Desired pixel resolution (size x size). Defaults to 1024
            outputdirectory (string) = Desired output location for tiles
        Returns:
            None
         */
        Wms wms = new Wms(this);
        wms.rerunFailedImages();
    }

    public void getFullResImage() throws IOException, InterruptedException {
        /*
         Function takes in a feature id and breaks the image up into 1024x1024 tiles, then places a number of calls
        based on multithreading percentages to return a full image strip in multiple tiles
        Args:
            featureid (string) = Feature id of the image
            thread_number (int) = Number of threads given to multithread functionality
            bbox (string) = Bounding box of AOI. Comma delimited set of coordinates. (miny,minx,maxy,maxx)
            mosaic (bool) = Flag if image files are mosaiced
            outputdirectory (string) = Desired output location for tiles
            image_format (string) = Desired image format (png or jpeg)
            filename = filename for output mosaiced image
        Returns:
            None
         */
        if (!(((Number) this.getThreadNumber()).doubleValue() != 0)) {
            throw new IllegalArgumentException("Must provide number of threads to use");
        }
        String wfsRequest = this.search();
        Gson gson = new Gson();
        Results json = gson.fromJson(wfsRequest, Results.class);
        double[][] imageBbox = json.features()[0].getGeometry().coordinates()[0];
        double[] xCoords = new double[imageBbox.length];
        double[] yCoords = new double[imageBbox.length];
        for (int i = 0; i < imageBbox.length; i++) {
            xCoords[i] = imageBbox[i][0];
            yCoords[i] = imageBbox[i][1];
        }
        Arrays.sort(xCoords);
        Arrays.sort(yCoords);
        String srsname = json.features()[0].getProperties().crs();
        double yValue = Objects.equals(srsname, "EPSG:4326") ? 0.0042176 : 468.1536;
        double xValue = Objects.equals(srsname, "EPSG:4326") ? 0.0054932 : 468.1536;
        double minY = yCoords[0];
        double minX = xCoords[0];
        double maxY = yCoords[yCoords.length - 1] + yValue;
        double maxX = xCoords[xCoords.length - 1] + xValue;
        if (this.getBbox() != null) {
            PortalUtils.validateBbox(this.getBbox());
            String[] bboxList = this.getBbox().split(",");
            minY = Math.max(minY, Double.parseDouble(bboxList[0]));
            maxY = Math.min(maxY, Double.parseDouble(bboxList[2])) + yValue;
            minX = Math.max(minX, Double.parseDouble(bboxList[1]));
            maxX = Math.min(maxX, Double.parseDouble(bboxList[3])) + xValue;
        }
        // Generate a Hashmap that splits the bbox into col row tiles
        ArrayList<Double> yList = new ArrayList<>();
        ArrayList<Double> xList = new ArrayList<>();
        while (minY < maxY) {
            yList.add(minY);
            minY += yValue;
        }
        Double[] yListArr = new Double[yList.size()];
        yListArr = yList.toArray(yListArr);
        while (minX < maxX) {
            xList.add(minX);
            minX += xValue;
        }
        Double[] xListArr = new Double[xList.size()];
        xListArr = xList.toArray(xListArr);
        HashMap<String, String> tiles = new HashMap<>();

        if (Objects.equals(srsname, "EPSG:4326")) {
            if (yListArr.length == 1) {
                if (xList.size() == 1) {
                    tiles.put("c0_r0", String.format("%s, %s, %s, %s", yListArr[0], xListArr[0],
                        yListArr[0] + yValue, xListArr[0] + xValue));
                } else {
                    for (int i = 0; i < xListArr.length - 1; i++) {
                        tiles.put(String.format("c%s_r%s", xListArr[i], 0), String.format("%s, %s, %s, %s",
                            yListArr[0], xListArr[i], yListArr[0] + yValue, xListArr[i + 1]));
                    }
                }
            } else if (xListArr.length == 1) {
                for (int i = yListArr.length - 1; i >= 0; i--) {
                    tiles.put(String.format("c%s_r%s", 0, yListArr.length - 2), String.format("%s, %s, %s, %s",
                        yListArr[i], xListArr[0], yListArr[i + 1], xListArr[0] + xValue));
                }
            } else {
                for (int i = yListArr.length - 2; i >= 0; i--) {
                    for (int j = 0; j < xListArr.length - 1; j++) {
                        tiles.put(String.format("c%s_r%s", j, yListArr.length - i - 2),
                            String.format("%s, %s, %s, %s", yListArr[i], xListArr[j], yListArr[i + 1], xListArr[j + 1]));
                    }
                }
            }
        } else {
            if (yListArr.length == 1) {
                if (xList.size() == 1) {
                    tiles.put("c0_r0", String.format("%s, %s, %s, %s", xListArr[0], yListArr[0],
                        xListArr[0] + xValue, yListArr[0] + yValue));
                } else {
                    for (int i = 0; i < xListArr.length - 1; i++) {
                        tiles.put(String.format("c%s_r%s", xListArr[i], 0), String.format("%s, %s, %s, %s",
                            xListArr[i], yListArr[0], xListArr[i + 1], yListArr[0] + yValue));
                    }
                }
            } else if (xListArr.length == 1) {
                for (int i = yListArr.length - 1; i >= 0; i--) {
                    tiles.put(String.format("c%s_r%s", 0, yListArr.length - 2), String.format("%s, %s, %s, %s",
                        xListArr[0], yListArr[i], xListArr[0] + xValue, yListArr[i]));
                }
            } else {
                for (int i = yListArr.length - 2; i >= 0; i--) {
                    for (int j = 0; j < xListArr.length - 1; j++) {
                        tiles.put(String.format("c%s_r%s", j, yListArr.length - i - 2),
                            String.format("%s, %s, %s, %s", xListArr[i], yListArr[j], xListArr[i + 1], yListArr[j + 1]));
                    }
                }
            }
        }

        //This section deletes bboxes that don't cover the image from Tiles
        String wfsResponse = this.search();
        this.setSrsname(srsname);
        this.setHeight(1024);
        this.setWidth(1024);
        String bbox_coverage;
        Iterator<Entry<String, String>> it = tiles.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry<String, String> entry = it.next();
            bbox_coverage = PortalUtils.aoiCoverage(entry.getValue(),
                wfsResponse).features()[0].getCoverage();
            if (Double.parseDouble(bbox_coverage) == 0.0) {
                it.remove();
            }
        }

        //Initiate a custom querystring
        Wms fullResWms = new Wms(this);
        HashMap<String, String> querystring = fullResWms.initQueryString();
        querystring.put("crs", srsname);
        querystring.put("width", "1024");
        querystring.put("height", "1024");
        querystring.put("coverage_cql_filter", String.format("featureId='%s'", this.getFeatureId()));
        if (this.getImageFormat() != null) {
            PortalUtils.validateImageFormat(this);
            querystring.put("format", this.getParamsImageFormat());
        }

        //Build list of API urls from bbox list
        HashMap<String, String> multiThreadingMap = new HashMap<>();
        it = tiles.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry<String, String> entry = it.next();
            querystring.put("bbox", entry.getValue());
            HttpUrl.Builder urlBuilder = HttpUrl.parse(this.getAuth().getApiBaseUrl() + "/geoserver/wms").newBuilder();
            //Build params from querystring
            for (Map.Entry<String, String> set : querystring.entrySet()) {
                urlBuilder.addQueryParameter(set.getKey(), set.getValue());
            }
            multiThreadingMap.put(entry.getKey(), urlBuilder.build().toString());
        }
        System.out.println("Number of tiles: " + multiThreadingMap.size());

        if (this.getDownloadPath() != null) {
            try {
                Paths.get(this.getDownloadPath());
            } catch (InvalidPathException ipe) {
                throw new IllegalArgumentException("Path " + this.getDownloadPath() + " not a valid path");
            }
        } else {
            this.setDownloadPath(String.format("%s\\Downloads\\%s", System.getProperty("user.home"), "Tiles"));
        }

        System.out.println("Starting full image download process...");
        //call util function to handle full download
        int failedDownloads = PortalUtils.handleMultithreadDownload(multiThreadingMap, this);

        System.out.println("Download complete!");
        System.out.println("Number of failed requests: " + failedDownloads);
        System.out.println("Tiles downloaded to " + this.getDownloadPath());


    }
}
