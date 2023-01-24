package com.maxar.MPSSDK;


// record to hold serialized json results from API calls
public record Results(String type, Features[] features, String totalFeatures, int numberReturned, String timeStamp,
                      Crs crs) {

    public class Features {

        private String type;
        private String id;
        private Geometry geometry;
        private String geometry_name;
        private Properties properties;
        private String coverage;
        private String bboxCoverage;

        public Features(String type, String id, Geometry geometry, String geometry_name,
            Properties properties, String coverage, String bboxCoverage) {
            this.type = type;
            this.id = id;
            this.geometry = geometry;
            this.geometry_name = geometry_name;
            this.properties = properties;
            this.coverage = coverage;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        public String getGeometry_name() {
            return geometry_name;
        }

        public void setGeometry_name(String geometry_name) {
            this.geometry_name = geometry_name;
        }

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }

        public String getCoverage() {
            return coverage;
        }

        public void setCoverage(String coverage) {
            this.coverage = coverage;
        }

        public String getBboxCoverage() {
            return bboxCoverage;
        }

        public void setBboxCoverage(String bboxCoverage) {
            this.bboxCoverage = bboxCoverage;
        }

        public record Geometry(String type, double[][][] coordinates) {

        }

        public record Properties(String featureId, String cloutCover, String sunAzimuth,
                                 String sunElevation, String offNadirAngle, String groundSampleDistance,
                                 String groundSampleDistanceUnit, String source, String bandDescription,
                                 String isEnvelopeGeometry, Object centroid, String dataLayer,
                                 String legacyDescription, String bandConfiguration, String fullResolutionInitiatedOrder,
                                 String legacyIdentifier, String crs, String acquisitionDate,
                                 String resolutionX, String resolutionY, String createdDate,
                                 String processingLevel, String earliestAcquisitionTime, String latestAcquisitionTime,
                                 String companyName, String orbitDirection, String beamMode,
                                 String polarisationMode, String polarisationChannel, String antennaLookDirection,
                                 String minimumIncidenceAngle, String maximumIncidenceAngle, String incidenceAngleVariation,
                                 String md5Hash, String licenseType, String isMultiPart, String ceCategory,
                                 String niirs, String lastModifiedDate, String hasCloudlessGeometry,
                                 String deletedDate, String deletedReason, String productName,
                                 String usageProductId, String ce90Accuracy, String bucketName, String path,
                                 String sensorType) {

        }
        }

    public record Crs(String type, Object properties) {

    }

}
