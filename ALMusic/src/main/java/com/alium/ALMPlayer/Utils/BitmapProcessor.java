package com.alium.ALMPlayer.Utils;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by aliumujib on 8/3/15.
 */
public class BitmapProcessor {

    public int getDominantColor(Bitmap bitmap)
    {
        if(null == bitmap)
        {
            return Color.TRANSPARENT;
        }

        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;
        int alphaBucket = 0;

        boolean hasAlpha = bitmap.hasAlpha();
        int pixelCount = bitmap.getWidth()*bitmap.getHeight();

        int[] pixels = new int[pixelCount];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int y=0, h = bitmap.getHeight(); y<h; y++)
        {
            for(int x=0 , w = bitmap.getWidth(); x<w; x++)
            {
                int color = pixels[x+y*w]; //x+y*width
                redBucket += (color >> 16)& 0xFF; // Color.red
                greenBucket += (color >> 8) & 0xFF; //color.green
                blueBucket += (color & 0xFF); //Color.blue

                if(hasAlpha) alphaBucket += (color >>> 24); //Color.alpha
            }
        }

        int finalColor = Color.argb(
                255, redBucket/ pixelCount, greenBucket/pixelCount, blueBucket/pixelCount);

        if(finalColor!=Color.TRANSPARENT)
        {
            return finalColor;
        }
        else return Color.RED;
    }
}
