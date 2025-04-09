# Create GIF animation
magick -delay 50 -loop 0 output/ndvi_tifs_level2/pngs/*.png output/ndvi_level2_animation.gif

# Create MP4 video from PNGs 
ffmpeg -framerate 2 -pattern_type glob -i "output/ndvi_tifs_level2/pngs/*.png" -vf "scale=1920:1080" -c:v libx264 -r 30 -pix_fmt yuv420p output/ndvi_level2_animation.mp4

