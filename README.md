# Satellite Data Analysis

This project visualizes vegetation changes caused by the Eaton Fire using satellite imagery from Landsat-8/9. It calculates NDVI values over time and generates a timelapse video showing vegetation loss throughout January 2025.

## Important Installations
- Java 8, Scala 2.12, Apache-Spark, Apache Hadoop, Apache-Maven, ImageMagick, FFmpeg

## Project Overview
- Satellite imagery (Landsat-8/9)
- NDVI (Normalized Difference Vegetation Index)
- Scala + Beast library for spatial data processing
- ImageMagick and FFmpeg for timelapse video creation


## Project Setup
- Go to [Beast](https://bitbucket.org/bdlabucr/beast/src/master/doc/Home.md) site
- To create a new Java/Scala project for Beast using the following command:
```
mvn archetype:generate -B -DgroupId=com.example.beastExample -DartifactId=TaskA-lvl2-project \
    -DarchetypeGroupId=edu.ucr.cs.bdlab -DarchetypeArtifactId=distribution -DarchetypeVersion=0.10.1
```
- Add the following dependency:
  ```
    <dependency>
      <groupId>edu.ucr.cs.bdlab</groupId>
      <artifactId>beast-spark</artifactId>
      <version>0.10.1</version>
    </dependency>
    <dependency>
        <groupId>org.apache.spark</groupId>
        <artifactId>spark-core_2.12</artifactId>
        <version>${spark.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.spark</groupId>
        <artifactId>spark-sql_2.12</artifactId>
        <version>${spark.version}</version>
    </dependency>
        <dependency>
        <groupId>org.locationtech.geotrellis</groupId>
        <artifactId>geotrellis-raster_2.12</artifactId>
        <version>3.6.0</version>
    </dependency>
        <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.36</version>
    </dependency>
  ```
- Add the following plugin:
  ```
      <plugin>
        <groupId>net.alchim31.maven</groupId>
        <artifactId>scala-maven-plugin</artifactId>
        <version>${scala.maven.plugin.version}</version>
        <executions>
          <execution>
            <id>scala-compile-first</id>
            <phase>process-resources</phase>
            <goals>
              <goal>add-source</goal>
              <goal>compile</goal>
            </goals>
          </execution>
          <execution>
            <id>scala-test-compile</id>
            <phase>process-test-resources</phase>
            <goals>
              <goal>testCompile</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <version>3.0.0</version>
        <configuration>
            <mainClass>com.example.beastExample.BeastScala</mainClass>
        </configuration>
      </plugin>

  ```
## Download Satellite Images
- Go to https://earthexplorer.usgs.gov/
- Select Eaton Fire area, California
- Filter by date: Jan 01, 2025 – Jan 31, 2025
- Download Landsat 8-9 C2 L2 Dataset
- Place the Band 4 Band 5 images in the src/main/resources folder

## GeoTIFF Files to PNG Conversion
- Open the Nano Editor
  ```
  nano convert_ndvi_level2.sh
  ```
- Write the following code in the Nano Editor:

``` mkdir -p output/ndvi_tifs_level2/pngs
for file in output/ndvi_tifs_level2/*.TIF; do
    filename=$(basename "$file" .TIF)

    gdal_translate -scale -1 1 0 255 -ot Byte -a_nodata 0 -of PNG "$file" "$outp$
    magick "$output_file" -resize 1920x1080 "$resized_output"
done
```

## PNG to GIF and MP4 Conversion
- Open the Nano Editor
  ```
  nano generate_ndvi_level2_animation.sh
  ```
- GIF animation code:
  ```
  magick -delay 50 -loop 0 output/ndvi_tifs_level2/pngs/*.png output/ndvi_level2_a$
  ```
- MP4 video code:
  ```
  ffmpeg -framerate 2 -pattern_type glob -i "output/ndvi_tifs_level2/pngs/*.png" -$
  ```

  
  

