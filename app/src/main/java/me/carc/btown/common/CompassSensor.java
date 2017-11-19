package me.carc.btown.common;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.List;

import me.carc.btown.Utils.MapUtils;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Calculate the direction of the device
 * Created by bamptonm on 19/09/2017.
 */

public class CompassSensor implements SensorEventListener {

    private Context mContext;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private boolean isEnabled;

    private float[] mGravity;
    private float[] mGeomagnetic;


    private CompassSensor.Callback mCallback = null;

    public interface Callback {
        void onAccuracyChanged(Sensor sensor, int accuracy);

        void onAngleCalculation(float degree);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public CompassSensor(Context context, Callback callback) {
        this.mContext = context;
        this.mCallback = callback;
        initSensors();
    }

    private void getSensorList() {
        SensorManager sensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        StringBuilder strLog = new StringBuilder();
        int iIndex = 1;
        for (Sensor item : sensors) {
            strLog.append(iIndex + ".");
            strLog.append(" Sensor Type - " + item.getType() + "\r\n");
            strLog.append(" Sensor Name - " + item.getName() + "\r\n");
            strLog.append(" Sensor Version - " + item.getVersion() + "\r\n");
            strLog.append(" Sensor Vendor - " + item.getVendor() + "\r\n");
            strLog.append(" Maximum Range - " + item.getMaximumRange() + "\r\n");
            strLog.append(" Minimum Delay - " + item.getMinDelay() + "\r\n");
            strLog.append(" Power - " + item.getPower() + "\r\n");
            strLog.append(" Resolution - " + item.getResolution() + "\r\n");
            strLog.append("\r\n");
            iIndex++;
        }
        System.out.println(strLog.toString());
    }

    public static boolean hasCompass(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) {
            if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                return true;
        }
        return false;
    }

    private void initSensors() {
        getSensorList();
        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void enableSensors() {
        isEnabled = true;
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void disableSensors() {
        isEnabled = false;
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values.clone();
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values.clone();
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                float degree = (float) (Math.toDegrees(orientation[0]));
                degree = MapUtils.normalizeDegree(degree);

                mCallback.onAngleCalculation(degree);
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        mCallback.onAccuracyChanged(sensor, accuracy);
    }
}
