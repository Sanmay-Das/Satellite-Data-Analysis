mkdir -p output/ndvi_tifs_level2/pngs
# Convert Level-2 NDVI GeoTIFFs to PNGs
for file in output/ndvi_tifs_level2/*.TIF; do
    filename=$(basename "$file" .TIF)  # Extract filename without extension
    output_file="output/ndvi_tifs_level2/pngs/${filename}.png"

    # Convert GeoTIFF to PNG with correct scaling
    gdal_translate -scale -1 1 0 255 -ot Byte -a_nodata 0 -of PNG "$file" "$output_file"
    magick "$output_file" -resize 1920x1080 "$resized_output"    
done
