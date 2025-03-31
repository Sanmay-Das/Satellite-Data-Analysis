package com.example.beastExample

import geotrellis.raster._
import geotrellis.raster.io.geotiff._
import org.apache.spark.SparkConf
import org.apache.spark.beast.{CRSServer, SparkSQLRegistration}
import org.apache.spark.sql.SparkSession
import edu.ucr.cs.bdlab.beast.io.SpatialFileRDD
import geotrellis.raster.DoubleArrayTile
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import java.io.File

/**
 * Scala examples for Beast
 */
object BeastScala {
  def main(args: Array[String]): Unit = {
    // Initialize Spark context

    val conf = new SparkConf().setAppName("Task A Lvl 2 project")
    // Set Spark master to local if not already set
    if (!conf.contains("spark.master"))
      conf.setMaster("local[*]")

    // Start the CRSServer and store the information in SparkConf
    val sparkSession: SparkSession = SparkSession.builder().config(conf).getOrCreate()
    val sparkContext = sparkSession.sparkContext
    CRSServer.startServer(sparkContext)
    SparkSQLRegistration.registerUDT
    SparkSQLRegistration.registerUDF(sparkSession)

    try {
      // Import Beast features
      import edu.ucr.cs.bdlab.beast._
      // TODO Insert your code here
         val inputDir = "src/main/resources/"
      val outputDir = "output/ndvi_tifs_level2/"
      new File(outputDir).mkdirs()

      val fileList = Option(new File(inputDir).listFiles()).getOrElse(Array.empty)
      val files = fileList.map(_.getName)

      // Level-2 B4 and B5 file naming convention uses _SR_B4.TIF and _SR_B5.TIF
      val b4Files = files.filter(_.contains("_SR_B4.TIF"))
      val b5Files = files.filter(_.contains("_SR_B5.TIF"))

      val matchedPairs = b4Files.flatMap { b4File =>
        val parts = b4File.split("_")
        if (parts.length > 4) {
          val pathRow = parts(2)
          val date = parts(3)
          val matchingB5 = b5Files.find(b5File => b5File.contains(pathRow) && b5File.contains(date))

          matchingB5.map(b5File => (new File(inputDir, b4File).getPath, new File(inputDir, b5File).getPath, date, pathRow))
        } else None
      }

      if (matchedPairs.nonEmpty) {
        matchedPairs.foreach { case (redBandPath, nirBandPath, date, pathRow) =>
          try {
            // Load Red and NIR bands
            val redBand = SinglebandGeoTiff(redBandPath).tile.convert(FloatConstantNoDataCellType)
              .mapDouble(v => if (v == -9999) Float.NaN else v / 10000.0) 

            val nirBand = SinglebandGeoTiff(nirBandPath).tile.convert(FloatConstantNoDataCellType)
              .mapDouble(v => if (v == -9999) Float.NaN else v / 10000.0) 

            // NDVI Computation and handling of NoData values
            val ndvi = nirBand.combineDouble(redBand) { (nir, red) =>
            if (nir.isNaN || red.isNaN || (nir + red) == 0) Float.NaN  
            else (nir - red) / (nir + red)
            }

            // Save NDVI result as GeoTIFF
            val ndviGeoTiff = SinglebandGeoTiff(ndvi, SinglebandGeoTiff(redBandPath).extent, SinglebandGeoTiff(redBandPath).crs)
            ndviGeoTiff.write(new File(outputDir, s"NDVI_${date}_$pathRow.TIF").getPath)

            println(s"NDVI computed and saved: NDVI_${date}_$pathRow.TIF")
          } catch {
            case e: Exception =>
              println(s"Error processing files: $redBandPath and $nirBandPath -> ${e.getMessage}")
          }
        }
      } else {
        println("No matching B4 and B5 files found, skipping computation.")
      }
    } finally {
      sparkSession.stop()
    }
  }
}