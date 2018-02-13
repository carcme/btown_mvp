package me.carc.btown.ui.custom;

import android.graphics.Color;
import android.graphics.Paint;

import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;

/**
 * Created by bamptonm on 17/01/2018.
 */

public class MySimplePointOverlayOptions extends SimpleFastPointOverlayOptions {


    public MySimplePointOverlayOptions() {
        mPointStyle = new Paint();
        mPointStyle.setStyle(Paint.Style.FILL);
        mPointStyle.setColor(Color.parseColor("#ff7700"));

        mSelectedPointStyle = new Paint();
        mSelectedPointStyle.setStrokeWidth(5);
        mSelectedPointStyle.setStyle(Paint.Style.STROKE);
        mSelectedPointStyle.setColor(Color.parseColor("#ffff00"));

        mTextStyle = new Paint();
        mTextStyle.setStyle(Paint.Style.FILL);
        mTextStyle.setColor(Color.parseColor("#222222"));
        mTextStyle.setTextAlign(Paint.Align.CENTER);
        mTextStyle.setTextSize(24);

        mAlgorithm = RenderingAlgorithm.NO_OPTIMIZATION;
        mCircleRadius = 24;
    }


    /**
     * Sets the style for the point overlay, which is applied to all circles.
     * @param style A Paint object.
     * @return The updated {@link MySimplePointOverlayOptions}
     */
    public MySimplePointOverlayOptions setPointStyle(Paint style) {
        mPointStyle = style;
        return this;
    }

    /**
     * Sets the style for the selected point.
     * @param style A Paint object.
     * @return The updated {@link MySimplePointOverlayOptions}
     */
    public MySimplePointOverlayOptions setSelectedPointStyle(Paint style) {
        mSelectedPointStyle = style;
        return this;
    }

    /**
     * Sets the radius of the circles to be drawn.
     * @param radius Radius.
     * @return The updated {@link MySimplePointOverlayOptions}
     */
    public MySimplePointOverlayOptions setRadius(float radius) {
        mCircleRadius = radius;
        return this;
    }

    /**
     * Sets the radius of the selected point's circle.
     * @param radius Radius.
     * @return The updated {@link MySimplePointOverlayOptions}
     */
    public MySimplePointOverlayOptions setSelectedRadius(float radius) {
        mSelectedCircleRadius = radius;
        return this;
    }

    /**
     * Sets whether this overlay is clickable or not. A clickable overlay will automatically select
     * the nearest point.
     * @param clickable True or false.
     * @return The updated {@link MySimplePointOverlayOptions}
     */
    public MySimplePointOverlayOptions setIsClickable(boolean clickable) {
        mClickable = clickable;
        return this;
    }

    /**
     * Sets the grid cell size used for indexing, in pixels. Larger cells result in faster rendering
     * speed, but worse fidelity. Default is 10 pixels, for large datasets (>10k points), use 15.
     * @param cellSize The cell size in pixels.
     * @return The updated {@link MySimplePointOverlayOptions}
     */
    public MySimplePointOverlayOptions setCellSize(int cellSize) {
        mCellSize = cellSize;
        return this;
    }

    /**
     * Sets the rendering algorithm. There are three options:
     * NO_OPTIMIZATION: Slowest option. Draw all points on each draw event.
     * MEDIUM_OPTIMIZATION: Faster. Recalculates the grid index on each draw event.
     *          Not recommended for >10k points. Better UX, but may be choppier.
     * MAXIMUM_OPTIMIZATION: Fastest. Only recalculates the grid on touch up and animation end
     *          , hence much faster display on move. Recommended for >10k points.
     * @param algorithm A {@link MySimplePointOverlayOptions.RenderingAlgorithm}.
     * @return The updated {@link MySimplePointOverlayOptions}
     */
    public MySimplePointOverlayOptions setAlgorithm(MySimplePointOverlayOptions.RenderingAlgorithm algorithm) {
        mAlgorithm = algorithm;
        return this;
    }

    /**
     * Sets the symbol shape for this layer.
     * @param symbol The symbol.
     * @return The updated {@link MySimplePointOverlayOptions}
     */
    public MySimplePointOverlayOptions setSymbol(MySimplePointOverlayOptions.Shape symbol) {
        mSymbol = symbol;
        return this;
    }

    /**
     * Sets the style for the labels.
     * @param textStyle The style.
     * @return The updated {@link MySimplePointOverlayOptions}
     */
    public MySimplePointOverlayOptions setTextStyle(Paint textStyle) {
        mTextStyle = textStyle;
        return this;
    }

    /**
     * Sets the minimum zoom level at which the labels should be drawn. This option is
     * <b>ignored</b> if LabelPolicy is DENSITY_THRESHOLD.
     * @param minZoomShowLabels The zoom level.
     * @return
     */
    public MySimplePointOverlayOptions setMinZoomShowLabels(int minZoomShowLabels) {
        mMinZoomShowLabels = minZoomShowLabels;
        return this;
    }

    /**
     * Sets the threshold (nr. of visible points) after which labels will not be drawn. <b>This
     * option only works when LabelPolicy is DENSITY_THRESHOLD and the algorithm is
     * MAXIMUM_OPTIMIZATION</b>.
     * @param maxNShownLabels The maximum number of visible points
     * @return
     */
    public MySimplePointOverlayOptions setMaxNShownLabels(int maxNShownLabels) {
        mMaxNShownLabels = maxNShownLabels;
        return this;
    }

    /**
     * Sets the policy for displaying point labels. Can be:<br/>
     *     ZOOM_THRESHOLD: Labels are not displayed is current map zoom level is lower than
     *         <code>MinZoomShowLabels</code>
     *     DENSITY_THRESHOLD: Labels are not displayed when the number of visible points is larger
     *         than <code>MaxNShownLabels</code>. <b>This only works for MAXIMUM_OPTIMIZATION</b><br/>
     * @param labelPolicy One of <code>ZOOM_THRESHOLD</code> or <code>DENSITY_THRESHOLD</code>
     * @return
     */
    public MySimplePointOverlayOptions setLabelPolicy(MySimplePointOverlayOptions.LabelPolicy labelPolicy) {
        mLabelPolicy = labelPolicy;
        return this;
    }
}
