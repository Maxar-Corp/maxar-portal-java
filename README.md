# Maxar Portal Services SDK

## Table of Contents
- [Overview](#overview)
- [Installation Instructions](#installation-instructions)
- [Authentication](#authentication)
- [Workflow](#workflow)
    - [Example](#example)
    - [Search](#search)
    - [Download Image](#download-image)
    - [Get Tile List](#get-tile-list)
    - [Download Tiles](#download-tiles)
    - [Band Manipulation](#band-manipulation)
    - [Get Full Resolution Image](#get-full-resolution-image)
- [Builder Definitions](#authentication)

## Overview
---

The Java MPS package uses a builder pattern to build and send specific RESTful API calls. The SDK supports the following Open Geospatial Consortium standard protocols: WFS, WMS, WMTS.

[Javadoc](TBDIncludeJavadocLink.com)

## Installation Instructions
---

TBD

## Authentication
---

Access to Maxar API resources requires valid credentials. The package looks for a credentials file located in the users home directory. Users should have a file here titled .MPS-config and should resemble the following exactly:
```
[mps]
user_name=myuser@maxar.com
user_pasword=mySuperSecretPassword
client_id=my-client-id
```
Replace these values with the credentials provided to you by Maxar. <br />

Credentials can also be supplied by passing them in as builder arguments when the call is instantiated using .username() .password() and .clientID()

## Workflow
---
### Example
The following is an example workflow to make a simple wfs call with a bounding box and a filter using the .search() function

```
public class Main {
    public static void main(String[] args) {
        Portal wfsCall = new PortalBuilder()
            .bbox("39.84387,-105.05608,39.95133,-104.94827")
            .filter("(acquisitionDate>='2022-01-01')AND(cloudCover<0.20)")
            .build()

        System.out.println(wfsCall.search())
    }

}
```

The search function performs a WFS request that returns a string containing a GeoJSON formatted list of features that match the paramerts sent in the call. 

Example response recieved from the server for the above call
```
{
   "type":"FeatureCollection",
   "features":[
      {
         "type":"Feature",
         "id":"7dea6ffc-e4b3-a507-f7e7-af315d32da29",
         "geometry":{
            "type":"Polygon",
            "coordinates":[...]           ]
         },
         "geometry_name":"featureGeometry",
         "properties":{
            "featureId":"7dea6ffc-e4b3-a507-f7e7-af315d32da29",
            "cloudCover":0,
            "sunAzimuth":159.64929,
            "sunElevation":24.48628,
            "offNadirAngle":26.880003,
            "groundSampleDistance":0.38,
            etc...
         }
      },
      {...}
   ],
   "totalFeatures":"unknown",
   "numberReturned":4,
   "timeStamp":"2023-01-18T16:51:58.818Z",
   "crs":{
      "type":"name",
      "properties":{
         "name":"urn:ogc:def:crs:EPSG::4326"
      }
   }
}
```

## Making a call
Because the sdk utilizes a builder pattern, the parameters you want to add to the call can be chained on the Portal class instantiation. Once the Portal is built, it contains methods that correspond to the different available functionality for making and returning OGC calls.

## Search
**portal.search()** <br/>
Performs a WFS search.<br/>
Return WFS results in json, or shapefile / csv if indicated <br/>
Builder parameters: <br/>
[.bbox()](#bounding-box) <br/>
[.filter()](#filter) <br/>
[.srsname()](#srsname) <br/>
[.shapefile()](#shapefile) <br/>
[.csv()](#csv) <br/>
[.featureId()](#featureid) <br/>
[.requestType()](#request-type) <br/>

Example Call
```
public class Main {
    public static void main(String[] args) {

        //Build the call
        Portal wfsCall = new PortalBuilder()
            .bbox("4828455.4171,-11686562.3554,4830614.7631,-11684030.3789")
            .filter("(acquisitionDate>='2022-01-01')AND(cloudCover<0.20)")
            .srsname("ESPG:3857")
            .shapefile()
            .build();

        //Make the call
        String wfsResults = wfsCall.search();

        //Print the results
        System.out.println(wfsResults);
    }

}
```

## Download Image
**portal.downloadImage()** <br/>
Downloads the requested image to the users machine using WMS <br/>
Returns location the image was downloaded to <br/>
Builder parameters: <br/>
[.bbox()](#bounding-box) <br/>
[.filter()](#filter) <br/>
[.srsname()](#srsname) <br/>
[.height()](#height) <br/>
[.width()](#width) <br/>
[.imageFormat()](#image-format) <br/>
[.downloadPath()](#download-path) <br/>
[.fileName()](#file-name) <br/>
[.legacyId()](#legacy-id) <br/>

Example Call
```
public class main {

    public static void main(String[] args) {
        
        //Build the call
        Portal wmsCall = new PortalBuilder()
            .bbox("4828455.4171,-11686562.3554,4830614.7631,-11684030.3789")
            .filter("(acquisitionDate>='2022-01-01')AND(cloudCover<0.20)")
            .srsname("ESPG:3857")
            .height(1000)
            .width(1000)
            .imageFormat("png")
            .filename("test")
            .downloadPath("C:/Users/user/Desktop/Images")
            .build();

        //Make the call
        String downloadLocation = wmsCall.downloadImage();

        //View the response
        System.out.println(downloadLocation);
    }

}
```

## Get Tile List
**portal.getTileList()** <br/>
Returns a Hashmap<String, String> of WMTS calls that can be used to return all of the tiles in a given AOI. The key is a String list containing a tiles row, column, and zoom level. The value is the associated api call. If you want to download all tiles and do not care about the individual calls, use the .downloadTiles() method instead <br/>
Builder Parameters: <br/>
[.bbox()](#bounding-box) <br/>
[.srsname()](#srsname) <br/>
[.zoomLevel()](#zoom-level) <br/>

Example Call
```
public class main {

    public static void main(String[] args) {
        
        //Build the call
        Portal wmtsCall = new PortalBuilder()
            .bbox("4828455.4171,-11686562.3554,4830614.7631,-11684030.3789")
            .zoomLevel(12)
            .build();

        //Make the call
        Hashmap<String, String> wmtsApiCallList = wmtsCall.getTileList();

        //View the response
        wmtsApiCallList.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue());
        })
    }
}
```

## Download Tiles
**portal.downloadTIles()** <br/>
Downloads all the tiles in a given bounding box at a given zoom level. Returns a message indicating success or failures and the location of the downloaded tiles. A base file name can be added with the .fileName() method. This file name will be appended with a tile's row column and zoom level <br />
Builder Parameters: <br/>
[.bbox()](#bounding-box) <br/>
[.srsname()](#srsname) <br/>
[.zoomLevel()](#zoom-level) <br/>
[.imageFormat()](#image-format) <br/>
[.fileName()](#file-name) <br/>
[.downloadPath()](#download-path) <br/>

Example Call
```
public class main {

    public static void main(String[] args) {
        
        //Build the call
        Portal wmtsCall = new PortalBuilder()
            .bbox("4828455.4171,-11686562.3554,4830614.7631,-11684030.3789")
            .zoomLevel(12)
            .imageFormat("geotiff")
            .downloadPath("C:\\Users\\user\\Desktop\\Seattle")
            .fileName("seattle_tile")
            .build();

        //Make the call
        String results = wmtsCall.downloadTiles();

        //View the response
        SYstem.out.println(results);
    }
}
```

## Band Manipulation
//TODO learn what this does <br/>
Builder Parameters: <br/>
[.bbox()](#bounding-box) <br/>
[.srsname()](#srsname) <br/>
[.featureId()](#featureid)<br/>
[.bandCombination()](#band-combination)<br/>
[.height()](#height) <br/>
[.width()](#width) <br/>
[.imageFormat()](#image-format) <br/>
[.downloadPath()](#download-path) <br/>
[.fileName()](#file-name) <br/>

Example Call
```
public class main {

    public static void main(String[] args) {
        
        //Build the call
        Portal bandListCall = new PortalBuilder()
            .bbox("4828455.4171,-11686562.3554,4830614.7631,-11684030.3789")
            .srsname("ESPG:3857")
            .featureId("7dea6ffce4b3a507f7e7af315d32da29")
            .bandCombination({"R", "G", "B"})
            .height(512)
            .width(512)
            .imageFormat("png")
            .filename("test")
            .downloadPath("C:/Users/user/Desktop/Images")
            .build();

        //Make the call
        String downloadLocation = bandListCall.bandCombination();

        //View the response
        System.out.println(downloadLocation);
    }

}
```

## Get Full Resolution Image
This method downloads the full scale resolution of a desired AOI. Utilizes multithreading to try to speed up the download process. Full resolution images are broken into tiles and downloaded as seperate full resolution images. The number of threads must be set by the user. 50 is a good starting point. The more threads in use the quicker the download process however the more unstable. If you are getting a number of failed returns, terminate the process, lower the number of threads and try again. <br/>
Builder Parameters: <br/>
[.featureId()](#featureid)<br/>
[.threadNumber()](#thread-number)<br/>
[.bbox()](#bounding-box) <br/>
[.srsname()](#srsname) <br/>
[.imageFormat()](#image-format) <br/>
[.downloadPath()](#download-path) <br/>
[.fileName()](#file-name) <br/>

### Username
.username("username")
Used if not using a .MPS-config file.

### Password
.password("password")
Used if not using a .MPS-config file.

### ClientId
.clientId("clientid")
Used if not using a .MPS-config file.

### Bounding box 
.bbox("39.84387,-105.05608,39.95133,-104.94827") <br>
Accepts a string containing the bbox in yx order. All calls default to an EPSG:4326 projection. To use a different one utilize the .srsname() method.

### Filter
.filter(cloudCover<0.20) <br>
Accepts a String containing a cql filter. When combining filters, each cql filter should be surrounded with parenthesis and sperated by an AND or OR combiner. These filter combinations can be continually combined using standard logic order of operations.  eg: <br/>
"(acquisitionDate>='2021-10-05')AND(cloudCover<0.20)" <br/>
"(acquisitionDate>='2022-11-01')OR((acquisitionDate>='2021-10-05')AND(cloudCover<0.20))"
For full documentation on cql filter usage visit [Maxar cql docs](putwebsitehere.com)

### SRSName
.srsname("EPSG:3857")
Accepts a string containing the desired projection if not the default EPSG:4326

### Shapefile
.shapefile()
Boolean. No arguments. False unless set with .shapefile() Used to indicate to a WFS call that the server should return a shapefile rather than json. 

### CSV
.csv()
Boolean. No arguments. False unless set with .csv() Used to indicate to a WFS call that the server should return a csv file rather than json. 

### FeatureID
.featureId("57d0e26239dde11463d31ff0893ce9ca")
Accepts a string containing the specific feature ID you are looking for. The featureId overrides the .filter method. It will not throw an error if a filter is passed, however the filter will be ignored. 

### Request Type
.requestType("DescribeFeatureType")
Accepts a string containing the type of request you want to make. Defualts to "GetFeature" in WFS calls, GetMap in WMS calls and GetTile in WMTS calls

### Height
.height(100)
Accepts an integer representing the desired height of the image in pixels. Max 8000. NOTE: 8000px calls may not work due to many factors including servier load, user machine capabilities, and latency. For large calls it is recommended the user break the calls up. 

### Width
.width(100)
Accepts an integer representing the desired width of the image in pixels. Max 8000. NOTE: 8000px calls may not work due to many factors including servier load, user machine capabilities, and latency. For large calls it is recommended the user break the calls up. 

### Image Format
.imageFormat("jpeg") <br/>
Accepts a string of the desired format. Supported formats jpeg, geotiff, png.

### Download Path
.downloadPath("C:\\\images\\\Denver") <br/>
Accepts a string of the full path of the location to download responses to. Defaults to the users Downloads directory. If a folder that does not exist is declared, then the folder will be created. 

### File Name
.fileName("filename") <br>
Accepts a string to represent the desired filename. Defualts to Download.filename

### Legacy ID
.legacyId("01B75DA34C") <br/>
Accepts a string representing the legacy identifier you are searching for. Downloads the full image therefore height and width are ignore if they are passed in. Image searches using legacy ID point to api.discover.digitalglobe.com

### Band Combination
.bandCombination({"R", "G", "B"}) <br/>
Accepts an ArrayList of Strings containing between 1 - 4 band options. 

### Thread Number
.threadNumber(50)<br/>
Accepts an integer value representing the number of threds to be used for full res download multithreading.