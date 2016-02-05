package aquery.com.aquery;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * A class to create a Drawable that is an intermediary between 2 drawables
 */
public class MergeDrawable extends Drawable {
    private Drawable d1;
    private Drawable d2;

    private float w = 0;
    private float alpha = 1;

    /**
     * Constructor of MergeDrawable
     * @param d1
     * The first drawable
     * @param d2
     * The second drawable
     */
    public MergeDrawable(Drawable d1, Drawable d2) {
        this(d1,d2, 0.5f);
    }
    /**
     * Constructor of MergeDrawable
     * @param d1
     * The first drawable
     * @param d2
     * The second drawable
     * @param weight
     * The weighting coefficient, between 0 (draw d1 only) and 1 (draw d2 only).
     * Default is 0.5
     */
    public MergeDrawable(Drawable d1, Drawable d2, float weight) {
        this.d1 = d1;
        this.d2 = d2;
        w = weight;
    }

    private int getAlpha(Drawable d) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            return d.getAlpha();
        return 255;
    }

    @Override
    public void draw(Canvas canvas) {
        if (d1 != null) {
            int maxA1 = getAlpha(d1);
            d1.setAlpha(Math.round(maxA1*alpha*(1-w)));
            d1.setBounds(canvas.getClipBounds());
            d1.draw(canvas);
            d1.setAlpha(maxA1);
        }
        if (d2 != null) {
            int maxA2 = getAlpha(d2);
            d2.setAlpha(Math.round(maxA2*alpha * w));
            d2.setBounds(canvas.getClipBounds());
            d2.draw(canvas);
            d2.setAlpha(maxA2);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha/255f;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (d1 != null)
            d1.setColorFilter(cf);
        if (d2 != null)
            d2.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    /**
     * Sets a new weighting coefficient for the drawable
     * @param weight
     * The new weighting coefficient, between 0 (draw d1 only) and 1 (draw d2 only)
     */
    public void setWeight(float weight) {
        w = weight;
        invalidateSelf();
    }
}
