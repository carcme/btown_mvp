package me.carc.btown.map.overlays;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;

import java.util.Date;

import me.carc.btown.Utils.ImageUtils;

/**
 * Add features to overlay - modified from CompassView
 * Created by bamptonm on 6/2/17.
 */

public class MyDirectedLocationOverlay extends DirectedLocationOverlay {

    static final public float TIME_DELTA_THRESHOLD = 0.5f;    // maximum time difference between iterations, s
    static final public float ANGLE_DELTA_THRESHOLD = 0.1f;    // minimum rotation change to be redrawn, deg

//    private static final float TIME_DELTA_THRESHOLD = 0.5f;    // maximum time difference between iterations, s
//    private static final float ANGLE_DELTA_THRESHOLD = 0.5f;    // minimum rotation change to be redrawn, deg

    private static final float INERTIA_MOMENT_DEFAULT = 0.1f;    // default physical properties
    private static final float ALPHA_DEFAULT = 10;
    private static final float MB_DEFAULT = 1000;

    private long time1, time2;                // timestamps of previous iterations--used in numerical integration
    private float angle1, angle2, angle0;    // angles of previous iterations
    private float angleLastDrawn;            // last drawn anglular position
    float bearTo;            // last drawn anglular position
    private boolean animationOn = false;    // if animation should be performed
    private boolean usingDirection;
    private Bitmap arrow;

    private float inertiaMoment = INERTIA_MOMENT_DEFAULT;    // moment of inertia
    private float alpha = ALPHA_DEFAULT;    // damping coefficient
    private float mB = MB_DEFAULT;    // magnetic field coefficient


    public MyDirectedLocationOverlay(Context ctx) {
        super(ctx);
    }

    public MyDirectedLocationOverlay(Context ctx, int arrowRes) {
        super(ctx);
        arrow = ImageUtils.drawableToBitmap(ContextCompat.getDrawable(ctx, arrowRes));
        usingDirection = true;

        setDirectionArrow(arrow);

        this.mAccuracyPaint.setStrokeWidth(2);
        this.mAccuracyPaint.setColor(Color.GREEN);
        this.mAccuracyPaint.setAntiAlias(true);
    }

    public void useDirectionIcon(Context ctx, int icon) {
        if(!usingDirection) {
            arrow = ImageUtils.drawableToBitmap(ContextCompat.getDrawable(ctx, icon));
            usingDirection = true;
            setDirectionArrow(arrow);
        }
    }

    public void useStaticIcon(Context ctx, int icon) {
        if(usingDirection) {
            arrow = ImageUtils.drawableToBitmap(ContextCompat.getDrawable(ctx, icon));
            usingDirection = false;
            setDirectionArrow(arrow);
        }
    }

    @Override
    public void draw(final Canvas c, final MapView osmv, final boolean shadow) {
        if (animationOn) {
            if (angleRecalculate(new Date().getTime())) {
                this.setBearing(angle1);
            }
        } else {
            this.setBearing(angle1);
        }
        super.draw(c, osmv, shadow);
    }

    public void rotationUpdate(final float angleNew, final boolean animate) {
        if (animate) {
            if (Math.abs(angle0 - angleNew) > ANGLE_DELTA_THRESHOLD) {
                angle0 = angleNew;
                setBearing(angle0);
            }
            animationOn = true;
        } else {
            angle1 = angleNew;
            angle2 = angleNew;
            angle0 = angleNew;
            angleLastDrawn = angleNew;
            setBearing(angleLastDrawn);
            animationOn = false;
        }
    }

    /**
     * Recalculate angles using equation of dipole circular motion
     *
     * @param    timeNew        timestamp of method invoke
     * @return if there is a need to redraw rotation
     */
    protected boolean angleRecalculate(final long timeNew) {

        // recalculate angle using simple numerical integration of motion equation
        float deltaT1 = (timeNew - time1) / 1000f;
        if (deltaT1 > TIME_DELTA_THRESHOLD) {
            deltaT1 = TIME_DELTA_THRESHOLD;
            time1 = timeNew + Math.round(TIME_DELTA_THRESHOLD * 1000);
        }
        float deltaT2 = (time1 - time2) / 1000f;
        if (deltaT2 > TIME_DELTA_THRESHOLD) {
            deltaT2 = TIME_DELTA_THRESHOLD;
        }

        // circular acceleration coefficient
        float koefI = inertiaMoment / deltaT1 / deltaT2;

        // circular velocity coefficient
        float koefAlpha = alpha / deltaT1;

        // angular momentum coefficient
        float koefk = mB * (float) (Math.sin(Math.toRadians(angle0)) * Math.cos(Math.toRadians(angle1)) -
                (Math.sin(Math.toRadians(angle1)) * Math.cos(Math.toRadians(angle0))));

        float angleNew = (koefI * (angle1 * 2f - angle2) + koefAlpha * angle1 + koefk) / (koefI + koefAlpha);

        // reassign previous iteration variables
        angle2 = angle1;
        angle1 = angleNew;
        time2 = time1;
        time1 = timeNew;

        // if angles changed less then threshold, return false - no need to redraw the view
        if (Math.abs(angleLastDrawn - angle1) < ANGLE_DELTA_THRESHOLD) {
            return false;
        } else {
            angleLastDrawn = angle1;
            return true;
        }
    }
}
