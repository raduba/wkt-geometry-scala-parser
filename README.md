Well-known text geometry to GeoJSON converter
---

Parse WKT geometry from the input file and write the GeoJSON equivalent to stdout.  
Write the error to stderr in case parsing fails.  
    
Well-known text Geometric objects parser for two dimension geometry supports the following geometries so far:  

    * POINT
    * LINESTRING
    * POLYGON
    * MULTIPOINT
    * MULTILINESTRING
    * MULTIPOLYGON
    * GEOMETRYCOLLECTION
  
All GeoJSON geometries are supported with 2D positions.      
CRS and BoundingBoxes are not yet supported.  
     
#### Build
 To build the native platform binary, use
    
 ```activator clean universal:packageBin```
    

#### Run
* You can unarchive target/universal/wkt-geometry-parser-1.0.zip file that you built previously and run bin/wkt-geometry-parser

```
unzip target/universal/wkt-geometry-parser-1.0.zip
wkt-geometry-parser-1.0/bin/wkt-geometry-parser <WKT_INPUT_FILE> > parsed.geojson        
```

* Or you can run the main file using activator:
```activator run <WKT_INPUT_FILE>```