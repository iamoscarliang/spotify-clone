package com.oscarliang.spotifyclone.core.ui.util;

import android.graphics.Bitmap;

import androidx.palette.graphics.Palette;

import java.util.List;

public class PaletteLoader {

    private PaletteLoader() {
    }

    public static int loadColor(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
        if (vibrantSwatch == null) {
            // In case palette fail to find a proper color
            // from bitmap, manually find the color with
            // the largest population from all swatch
            List<Palette.Swatch> swatches = palette.getSwatches();
            Palette.Swatch maxSwatch = null;
            int maxPopulation = 0;
            for (Palette.Swatch swatch : swatches) {
                int current = swatch.getPopulation();
                if (current > maxPopulation) {
                    maxPopulation = current;
                    maxSwatch = swatch;
                }
            }
            if (maxSwatch == null) {
                throw new IllegalStateException("Vibrant swatch not found");
            }
            vibrantSwatch = maxSwatch;
        }
        return vibrantSwatch.getRgb();
    }

}