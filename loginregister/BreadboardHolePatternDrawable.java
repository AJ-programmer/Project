package com.example.loginregister;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class BreadboardHolePatternDrawable extends Drawable {
    private Paint paint;
    private int holeSize = 6; // Diameter of holes in pixels
    private int holeSpacing = 20; // Spacing between holes in pixels
    private int rows = 10;
    private int cols = 20;

    public BreadboardHolePatternDrawable() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Calculate starting points to center the grid
        int startX = (width - (cols * holeSpacing)) / 2;
        int startY = (height - (rows * holeSpacing)) / 2;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float centerX = startX + col * holeSpacing + holeSpacing / 2;
                float centerY = startY + row * holeSpacing + holeSpacing / 2;
                canvas.drawCircle(centerX, centerY, holeSize / 2f, paint);
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}