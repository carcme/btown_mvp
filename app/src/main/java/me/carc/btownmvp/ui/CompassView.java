package me.carc.btownmvp.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import org.osmdroid.util.GeoPoint;

import java.util.Date;

/**
 * Class CompassView extends Android ImageView to perform cool, real-life animation of objects such
 * compass needle in magnetic field. Rotation is performed relative to the center of image.
 *
 * It uses angular motion equation of magnetic dipole in magnetic field to implement such animation.
 * To vary behaviour (damping, oscillation, responsiveness and so on) set various physical
 * properties.
 *
 * Use `setPhysical()` to vary physical properties. Use `rotationUpdate()` to change angle of
 * "magnetic field" at which image should rotate.
 */

public class CompassView extends android.support.v7.widget.AppCompatImageView {

    static final public float TIME_DELTA_THRESHOLD = 0.25f;    // maximum time difference between iterations, s
    static final public float ANGLE_DELTA_THRESHOLD = 0.1f;    // minimum rotation change to be redrawn, deg

    static final public float INERTIA_MOMENT_DEFAULT = 0.1f;    // default physical properties
    static final public float ALPHA_DEFAULT = 10;
    static final public float MB_DEFAULT = 1000;

    long time1, time2;                // timestamps of previous iterations--used in numerical integration
    float angle1, angle2, angle0;    // angles of previous iterations
    float angleLastDrawn;            // last drawn anglular position
    float bearTo;            // last drawn anglular position
    boolean animationOn = false;    // if animation should be performed

    float inertiaMoment = INERTIA_MOMENT_DEFAULT;    // moment of inertia
    float alpha = ALPHA_DEFAULT;    // damping coefficient
    float mB = MB_DEFAULT;    // magnetic field coefficient

    /**
     * Constructor inherited from ImageView
     */
    public CompassView(Context context) {
        super(context);
    }

    /**
     * Constructor inherited from ImageView
     */
    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor inherited from ImageView
     */
    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * onDraw override.
     * If animation is "on", view is invalidated after each redraw,
     * to perform recalculation on every loop of UI redraw
     */
    @Override
    public void onDraw(Canvas canvas) {
        if (animationOn) {
            if (angleRecalculate(new Date().getTime())) {
                this.setRotation(angle1);
            }
        } else {
            this.setRotation(angle1);
        }
        super.onDraw(canvas);
        if (animationOn) {
            this.invalidate();
        }
    }

    /**
     * Use this to set physical properties.
     * Negative values will be replaced by default values
     *
     * @param inertiaMoment Moment of inertia (default 0.1)
     * @param alpha         Damping coefficient (default 10)
     * @param mB            Magnetic field coefficient (default 1000)
     */
    public void setPhysical(float inertiaMoment, float alpha, float mB) {
        this.inertiaMoment = inertiaMoment >= 0 ? inertiaMoment : INERTIA_MOMENT_DEFAULT;
        this.alpha = alpha >= 0 ? alpha : ALPHA_DEFAULT;
        this.mB = mB >= 0 ? mB : MB_DEFAULT;
    }

    public float getLastAngle(){
        if (bearTo < 0) {
            bearTo = bearTo + 360;
        }
        return bearTo;
    }

    /**
     * DUPLICATE IN MapUtils.bearingBetweenLocations(getLocationPoint(), location);
     */
    public void rotationFromLocations(final GeoPoint a, final GeoPoint b, final boolean animate) {

        double PI = 3.14159;
        double lat1 = a.getLatitude() * PI / 180;
        double long1 = a.getLongitude() * PI / 180;
        double lat2 = b.getLatitude() * PI / 180;
        double long2 = b.getLongitude() * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        bearTo = (float)brng;
        rotationUpdate((float)brng, animate);
    }

    /**
     * Use this to set new "magnetic field" angle at which image should rotate
     *
     * @param    angleNew    new magnetic field angle, deg., relative to vertical axis.
     * @param    animate        true, if image shoud rotate using animation, false to set new rotation
     * instantly
     */
    public void rotationUpdate(final float angleNew, final boolean animate) {
        if (animate) {
            if (Math.abs(angle0 - angleNew) > ANGLE_DELTA_THRESHOLD) {
                angle0 = angleNew;
                this.invalidate();
            }
            animationOn = true;
        } else {
            angle1 = angleNew;
            angle2 = angleNew;
            angle0 = angleNew;
            angleLastDrawn = angleNew;
            this.invalidate();
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