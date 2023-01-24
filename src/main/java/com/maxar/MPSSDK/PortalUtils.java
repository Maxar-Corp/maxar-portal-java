package com.maxar.MPSSDK;

import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

class PortalUtils {

    public PortalUtils() {
        throw new UnsupportedOperationException("PortalUtils can not be instantiated");
    }

    static double calculateSqKm(String bbox) {
//        39.84387,-105.05608,39.95133,-104.94827

        String[] bboxList = bbox.split(",");
        if (bboxList.length == 4) {
            // Set up the source and target coordinate reference systems
            CoordinateReferenceSystem sourceCRS;
            CoordinateReferenceSystem targetCRS;
            MathTransform transform;
            try {
                targetCRS = CRS.decode("EPSG:3857");
                sourceCRS = CRS.decode("EPSG:4326");
                transform = CRS.findMathTransform(sourceCRS, targetCRS);
            } catch (FactoryException e) {
                throw new RuntimeException(e);
            }

            // Define the bounding box coordinates
            double minX = Double.parseDouble(bboxList[1]);
            double minY = Double.parseDouble(bboxList[0]);
            double maxX = Double.parseDouble(bboxList[3]);
            double maxY = Double.parseDouble(bboxList[2]);

            // Create an envelope for the bounding box
            Envelope envelope = new Envelope(minX, maxX, minY, maxY);

            // Transform the envelope
            Envelope transformedEnvelope;
            try {
                transformedEnvelope = JTS.transform(envelope, transform);
            } catch (TransformException e) {
                throw new RuntimeException(e);
            }

            return transformedEnvelope.getArea() / 1000000;

        }
        return 0;
//        double lat1 = Double.parseDouble(bboxList[0]);
//        double lat2 = Double.parseDouble(bboxList[2]);
//        double lon1 = Double.parseDouble(bboxList[1]);
//        double lon2 = Double.parseDouble(bboxList[3]);
//
//        GeometryFactory geometryFactory = new GeometryFactory();
//        Coordinate[] coordinates = new Coordinate[] {
//            new Coordinate(lon1, lat1),
//            new Coordinate(lon1, lat2),
//            new Coordinate(lon2, lat2),
//            new Coordinate(lon2, lat1),
//            new Coordinate(lon1, lat1),
//        };
//
//
//        LinearRing linearRing = geometryFactory.createLinearRing(coordinates);
//
//        Polygon polygon = geometryFactory.createPolygon(linearRing);
//
//        return polygon.getArea() / 1000000;
    }

    static Response handleRequest(Auth auth, String url, HashMap<String, String> params)
        throws IOException {
        String token = auth.refreshToken();
        //HTTP request
        OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        //Build params from querystring
        if (!params.isEmpty()) {
            for (Map.Entry<String, String> set : params.entrySet()) {
                urlBuilder.addQueryParameter(set.getKey(), set.getValue());
            }
        }
        String URL = urlBuilder.build().toString();
        System.out.println(URL);
        Request getRequest = new Request.Builder()
            .header("Authorization", "Bearer " + token)
            .url(URL)
            .build();
        Response response = client.newCall(getRequest).execute();
        if (response.code() != 200) {
            System.out.printf("error: %s %s", response.code(), response.body());
        }
        return response;
    }


    static void validateImageFormat(Portal portal) throws IllegalArgumentException {
        String[] acceptableFormats = {"jpeg", "png", "geotiff"};
        if (Arrays.asList(acceptableFormats).contains(portal.getImageFormat())) {
            portal.setParamsImageFormat("image/" + portal.getImageFormat());
        } else if (portal.getImageFormat() == null) {
            portal.setParamsImageFormat("image/jpeg");
        }
        else {
            throw new IllegalArgumentException("Format not recognized, please use acceptable format"
                + " for downloading image. Format provided: " + portal.getImageFormat());
        }
    }

    static void validateBbox(String bbox) throws IllegalArgumentException {
        /*
          Function takes in the bbox and validates that it is proper format
          Args:
            bbox =  String of Coordinates separated by comma
            example = "-105.05608, 39.84387, -104.94827, 39.95133"
         */
        String[] bboxList = bbox.split(",");
        if (bboxList.length > 4) {
            throw new IllegalArgumentException("Projection must be exactly 4 coordinates");
        }
        //build map to parse and hold values from bbox
        HashMap<String, Float> bboxData = new HashMap<>();
        try {
            bboxData.put("minY", Float.parseFloat(bboxList[0]));
            bboxData.put("minX", Float.parseFloat(bboxList[1]));
            bboxData.put("maxY", Float.parseFloat(bboxList[2]));
            bboxData.put("maxX", Float.parseFloat(bboxList[3]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Bbox coordinates must be numeric.");
        }
        if (bboxData.get("minY") >= bboxData.get("maxY")) {
            throw new IllegalArgumentException("Improper order of bbox: minY is greater than maxY.");
        }
        if (bboxData.get("minX") >= bboxData.get("maxX")) {
            throw new IllegalArgumentException("Improper order of bbox: minX is greater than maxX.");
        }
        if (bbox.contains("EPSG:4326")) {
            if (180 < Math.abs(bboxData.get("minX")) || 180 < Math.abs(bboxData.get("maxX"))) {
                throw new IllegalArgumentException(String.format("%s%s - X coordinates out of range -180 - "
                    + "180", bboxData.get("minX"), bboxData.get("maxX")));
            }
            if (90 < Math.abs(bboxData.get("minY")) || 90 < Math.abs(bboxData.get("maxY"))) {
                throw new IllegalArgumentException("Y coordinates out of range -90 - 90");
            }
        } else {
            if (20048966.1 < Math.abs(bboxData.get("minX")) || 20048966.1 < Math.abs(bboxData
                .get("maxX"))) {
                throw new IllegalArgumentException("X coordinates out of range -20048966.1 - 20048966.1");
            }
            if (20037508.34 < Math.abs(bboxData.get("minY")) || 20037508.34 < Math.abs(bboxData
                .get("maxY"))) {
                throw new IllegalArgumentException("Y coordinates out of range -20037508.34 - 20037508.34");
            }
        }
    }

    static String processBbox(Portal portal) {

        String[] bboxList = portal.getBbox().split(",");
        if (portal.getSrsname() != null) {
            return String.join(",", bboxList[1],  bboxList[0], bboxList[3],  bboxList[2],
                portal.getSrsname());
        }
        return String.join(",", bboxList[0],  bboxList[1], bboxList[2],  bboxList[3]);

    }

    static Results aoiCoverage(String bbox, String response) throws IOException {
        /*
        Function adds the percentage of the desired feature that is covered by the AOI
        Args:
        bbox: String of Coordinates separated by comma
        response: Response object from a WFS request call
        Returns:
        Updated Results containing aoi coverage
         */
        Gson gson = new Gson();
        Results json = gson.fromJson(response, Results.class);
        GeometryFactory geometryFactory = new GeometryFactory();

        String[] bboxList = bbox.split(",");
        // Create a polygon from the bbox
        double minX = Double.parseDouble(bboxList[1]);
        double minY = Double.parseDouble(bboxList[0]);
        double maxX = Double.parseDouble(bboxList[3]);
        double maxY = Double.parseDouble(bboxList[2]);
        Coordinate[] bboxCoordinates = new Coordinate[] {
            new Coordinate(minX, minY),
            new Coordinate(minX, maxY),
            new Coordinate(maxX, maxY),
            new Coordinate(maxX, minY),
            new Coordinate(minX, minY),
        };
        Polygon bboxPolygon = geometryFactory.createPolygon(bboxCoordinates);

        // Create a polygon for each feature from the supplied coordinate list
        for (int i = 0; i < json.features().length; i++) {
            if (Objects.equals(json.features()[i].getGeometry().type(), "Polygon")) {
                Coordinate[] featureCoordinates = new Coordinate[json.features()[0].getGeometry().coordinates()[0].length];
                for (int j = 0; j < json.features()[0].getGeometry().coordinates()[0].length; j++) {
                    double coord1 = json.features()[0].getGeometry().coordinates()[0][j][0];
                    double coord2 = json.features()[0].getGeometry().coordinates()[0][j][1];
                    featureCoordinates[j] = new Coordinate(coord1, coord2);
                }
                // calculate the area of the ratio between the intersection of the bbox/feature and the area of the feature
                Polygon featurePolygon = geometryFactory.createPolygon(featureCoordinates);
                json.features()[i].setCoverage(String.valueOf(
                    featurePolygon.intersection(bboxPolygon).getArea() / featurePolygon.getArea()));
                json.features()[i].setBboxCoverage(String.valueOf(
                    bboxPolygon.intersection(featurePolygon).getArea() / bboxPolygon.getArea()));
            } else {
                ArrayList<Polygon> multiPolygonList = new ArrayList<>();
                Coordinate[] featureCoordinates = new Coordinate[json.features()[0].getGeometry().coordinates().length];
                for (int j = 0; j < json.features()[0].getGeometry().coordinates().length; j++) {
                    for (int k = 0; k < json.features()[0].getGeometry().coordinates()[0].length; k++) {
                        double coord1 = json.features()[0].getGeometry().coordinates()[j][k][0];
                        double coord2 = json.features()[0].getGeometry().coordinates()[j][k][1];
                        featureCoordinates[j] = new Coordinate(coord1, coord2);
                    }
                    multiPolygonList.add(geometryFactory.createPolygon(featureCoordinates));
                }
                MultiPolygon multiPolygon = geometryFactory.createMultiPolygon(multiPolygonList.toArray(new Polygon[0]));
                json.features()[i].setCoverage(String.valueOf(
                    multiPolygon.intersection(bboxPolygon).getArea() / multiPolygon.getArea()));
                json.features()[i].setBboxCoverage(String.valueOf(
                    bboxPolygon.intersection(multiPolygon).getArea() / bboxPolygon.getArea()));

            }
        }
        return json;
    }

    static String removeCache() {
        /*
            Function assigns random characters to bypass caching
            Returns:
                String build from random characters
         */

        int length = 25;
        boolean useLetters = true;
        boolean useNumbers = false;
        return RandomStringUtils.random(length, useLetters, useNumbers);

    }

    static String handleDownload(Portal portal, Response response) {
        /*
            Function downloads images to users computer
            Args:
                Portal - instance of the Portal class
                Response - response from one of the imagery APIs
            Returns:
                String of the download location
         */
        InputStream file = response.body().byteStream();
        if (portal.isDisplay()) {
            BufferedImage image;
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ImageIcon icon = new ImageIcon(image);
            JLabel label = new JLabel();
            label.setIcon(icon);
            JPanel panel = new JPanel();
            panel.add(label);
            JFrame frame = new JFrame();
            frame.add(panel);
            frame.setVisible(true);
            return "Image displayed";
        }
        String filename = portal.getFileName() != null ? portal.getFileName() : "Download";
        String format;
        if (portal.isCsv()) {
            format = "csv";
        } else if (portal.isShapefile()) {
            format = "zip";
        } else {
            format = portal.getImageFormat();
        }
        if (portal.getDownloadPath() != null) {
            try {
                Paths.get(portal.getDownloadPath());
            } catch (InvalidPathException ipe) {
                throw new IllegalArgumentException(String.format("Path %s not valid.",
                    portal.getDownloadPath()));
            }
            try {
                FileUtils.copyInputStreamToFile(file, new File(String.format("%s\\%s.%s",
                    portal.getDownloadPath(), filename, format)));
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            return "File downloaded to: " + portal.getDownloadPath() + "\\" + filename + "." + format;
        } else {
            try {
                FileUtils.copyInputStreamToFile(file, new File(String.format("%s\\Downloads\\%s.%s",
                    System.getProperty("user.home"), filename, format)));
                System.getProperty("user.home");
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            return String.format("File downloaded to: %s\\Downloads\\%s.%s",
                System.getProperty("user.home"), filename, format);
        }
    }

    static int handleMultithreadDownload(HashMap<String, String> multiThreadingMap, Portal portal)
        throws InterruptedException {
        /*
            Function downloads images to users computer
            Args:
                multiThreadingMap - Map containing a filename and an API call
                Portal - instance of the Portal class
            Returns:
                int - number of failed requests
         */
        //Create a CountDownLatch to block the main thread until all API calls have finished
        CountDownLatch latch = new CountDownLatch(multiThreadingMap.size());

        // Create a fixed thread pool with .getThreadNumber() threads
        Executor executors = Executors.newFixedThreadPool(portal.getThreadNumber());

        //Get auth once for all calls
        String token = portal.getAuth().refreshToken();

        String downloadPath = portal.getDownloadPath();

        //Performance measures for user
        double multiple = Math.floor(multiThreadingMap.size() * 0.25);
        final int[] count = {0};
        AtomicInteger percentage = new AtomicInteger();

        //catch failed calls
        HashMap<String, String> failedRequests = new HashMap<>();
        int attempts = 0;
        //Set up the HTTP client
        OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .build();

        for (Entry<String, String> entry : multiThreadingMap.entrySet()) {
            executors.execute(() -> {
                Request getRequest = new Request.Builder()
                    .header("Authorization", "Bearer " + token)
                    .url(entry.getValue())
                    .build();
                Response response = null;
                try {
                    response = client.newCall(getRequest).execute();
                } catch (IOException e) {
                    //Timeout failures are common. Retry the call once first
                    try {
                        response = client.newCall(getRequest).execute();
                    } catch (IOException ioe) {
                        failedRequests.put(entry.getKey(), entry.getValue());
                        return;
                    }
                } finally {
//                     Decrement the latch count to signal that this API call has finished
                    latch.countDown();
                }
                if (response.code() != 200) {
                    failedRequests.put(entry.getKey(), entry.getValue());
                } else {
                    count[0]++;
                    if (count[0] % multiple == 0) {
                        percentage.getAndIncrement();
                        System.out.println((percentage.get() * 25) + "% complete");
                    }
                    InputStream file = response.body().byteStream();
                    try {
                        FileUtils.copyInputStreamToFile(file, new File(String.format("%s\\%s.%s",
                            downloadPath, entry.getKey(), portal.getImageFormat())));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        if (!failedRequests.isEmpty()) {
            attempts++;
            System.out.printf("Some image requests failed, retrying failed requests, "
                + "retry attempt: %s%n", attempts);
            handleMultithreadDownload(failedRequests, portal);
        }

        // Block the main thread until the latch count reaches zero
        latch.await();
        return failedRequests.size();
    }

    static void createMosaic(String path) throws IOException {
        /*
                Function creates a mosaic of downloaded image tiles from full_res_dowload function
        Args:
            base_dir (string) = Root directory containing image files to be mosaiced
            img_format (string) = Image format of files
            img_size (int) = Size of individual image files, defaults to 1024
            outputdirectory (string) = Directory destination of finished mosaic file
            filename (string) = filename of merged image
        Returns:
            None
         */

        //TODO

        //Load up tiles downloaded from getFullResImage
//        File folder = new File(path);
//        File[] files = folder.listFiles();

        // Create a list to store the GridCoverage2D objects for each GeoTIFF
//        List<File> coverages = new ArrayList<>();
//
//        File outputFile = new File(path + "\\output.geotiff");

        // loop through folder containing geotiff files
//        for (File file : files) {
//            if (Objects.equals(FilenameUtils.getExtension(String.valueOf(file)), "geotiff")) {
//                coverages.add(file);
//            }
//        }

//        GridCoverage2D mosaic = Mosiac.create()
//
//        // Create a coverage processor
//        CoverageProcessor processor = new CoverageProcessor();
//
//        // Set up the parameters for the CoverageMerge operation
//        ParameterValueGroup parameters = processor.getOperation("CoverageMerge").getParameters();
//        parameters.parameter("sources").setValue(coverages);
//
//        // Execute the CoverageMerge operation to merge the input grid coverages into a mosaic
//        GridCoverage2D mosaic = (GridCoverage2D) processor.doOperation(parameters);
//
//
//        // Write the mosaic to a GeoTIFF file
//        GeoTiffFormat format = new GeoTiffFormat();
//
//        GeoTiffWriter writer = (GeoTiffWriter) format.getWriter(outputFile);
//        writer.write(mosaic, null);
    }

    static void cqlChecker(String cqlFilter) throws IllegalArgumentException {
        /*
                Function checks for the validity of a passed in cql filter
                Args:
                    cql_filter: string representation of cql filter
         */

        ArrayList<String> errorArray = new ArrayList<>();
        String[] stringArray = {"featureId", "sourceUnit", "productType",
            "groundSampleDistanceUnit",
            "dataLayer", "product_line_item", "legacyDescription", "colorBandOrder", "assetName",
            "assetType", "legacyId", "factoryOrderNumber", "layer", "crs", "url", "spatialAccuracy",
            "catalogIdentifier", "tileMatrixSet", "tileMatrix", "product_name", "product_id",
            "bandDescription", "bandConfiguration", "fullResolutionInitiatedOrder",
            "legacyIdentifier",
            "processingLevel", "companyName", "orbitDirection", "beamMode", "polarisationMode",
            "polarisationChannel", "antennaLookDirection", "md5Hash", "licenseType", "ceCategory",
            "deletedReason", "productName", "bucketName", "path", "sensorType"};
        String[] stringDateArray = {"acquisitionDate", "ingestDate", "collect_date_min",
            "createdDate", "earliestAcquisitionTime", "latestAcquisitionTime", "lastModifiedDate",
            "deletedDate"};
        String[] floatArray = {"resolutionX", "resolutionY", "minimumIncidenceAngle",
            "maximumIncidenceAngle", "incidenceAngleVariation", "niirs", "ce90Accuracy",
            "groundSampleDistance", "perPixelX", "perPixelY", "CE90Accuracy", "RMSEAccuracy"};
        String[] booleanArray = {"isEnvelopeGeometry", "isMultiPart", "hasCloudlessGeometry"};
        String[] integerArray = {"usageProductId"};
        String[] sourceArray = {"WV01", "WV02", "WV03_VNIR", "WV03", "WV04", "GE01", "QB02", "KS3",
            "KS3A", "WV03_SWIR", "KS5", "RS2", "IK02", "LG01", "LG02"};
        String[] zero360Array = {"sunAzimuth", "offNadirAngle", "sunElevation"};
        String[] zero1Array = {"cloudCover"};
        if (cqlFilter == null) {
            errorArray.add("Filter can not be null");
            throw new IllegalArgumentException("CQL filters Error:" + errorArray);
        }
        if (StringUtils.countMatches(cqlFilter, ")") != StringUtils.countMatches(cqlFilter,
            "(") ||
            cqlFilter.indexOf(")") < cqlFilter.indexOf("(")) {
            errorArray.add("Incorrect parenthesis");
        }
        String comparisons = "<=|>=|<|>|=";
        Pattern pattern = Pattern.compile(comparisons, Pattern.MULTILINE);
        Matcher m = pattern.matcher(cqlFilter);
        if (!m.find()) {
            errorArray.add("No comparison operator e.g. < > =");
        }
        String[] cqlArray = cqlFilter.split("AND|OR");
        for (String s : cqlArray) {
            String[] filter = s.replaceAll("[()]", "").split(comparisons);
            if (Objects.equals(filter[0], "source") && !Arrays.asList(sourceArray)
                .contains(filter[0])) {
                errorArray.add(filter[0] + " should be one of: " + Arrays.asList(sourceArray));
            } else if (Arrays.asList(floatArray).contains(filter[0])) {
                try {
                    Float.parseFloat(filter[1]);
                } catch (NumberFormatException e) {
                    errorArray.add(filter[1] + " is not a float");
                }
            } else if (Arrays.asList(booleanArray).contains(filter[0])) {
                if (!Objects.equals(filter[1], "FALSE") || !Objects.equals(filter[1], "TRUE")) {
                    errorArray.add(filter[1] + " should be TRUE or FALSE");
                }
            } else if (Arrays.asList(integerArray).contains(filter[0])) {
                try {
                    Integer.parseInt(filter[1]);
                } catch (NumberFormatException nfe) {
                    errorArray.add(filter[1] + " is not an integer");
                }
            } else if (Arrays.asList(stringDateArray).contains(filter[0])) {
                String date = filter[1].replaceAll("'", "");
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd",
                    Locale.ENGLISH);
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd "
                    + "hh:mm:ss", Locale.ENGLISH);
                try {
                    LocalDate.parse(date, formatter1);
                } catch (java.time.DateTimeException dte) {
                    try {
                        LocalDate.parse(date, formatter2);
                    } catch (java.time.DateTimeException dte2) {
                        errorArray.add(filter[1] + " not a valid date");
                    }
                }
            } else if (Arrays.asList(zero1Array).contains(filter[0])) {
                try {
                    float flt = Float.parseFloat(filter[1]);
                    if (flt < 0 || flt > 1) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException nfe) {
                    errorArray.add(filter[1] + " must represent a number between 0 and 1");
                }
            } else if (Arrays.asList(zero360Array).contains(filter[0])) {
                try {
                    float flt = Float.parseFloat(filter[1]);
                    if (flt < 0 || flt > 360) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException nfe) {
                    errorArray.add(filter[1] + " must represent a number between 0 and 360");
                }
            } else if (Arrays.asList(stringArray).contains(filter[0])) {
                continue;
            } else {
                errorArray.add(String.format("%s %s is not a valid filter", filter[0], filter[1]));
            }
        }
        if (!errorArray.isEmpty()) {
            throw new IllegalArgumentException("CQL Filter Error: " + errorArray);
        }
    }
}
