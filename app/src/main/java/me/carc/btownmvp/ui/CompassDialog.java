package me.carc.btownmvp.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.carc.btownmvp.MapActivity;
import me.carc.btownmvp.R;
import me.carc.btownmvp.Utils.MapUtils;
import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.Commons;

/**
 * Show a rotating compass dialog page
 * Created by bamptonm on 6/3/17.
 */

public class CompassDialog extends DialogFragment {

    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String ID_TAG = "CompassDialog";
    private static final String TITLE = "TITLE";
    private static final String SUBTITLE = "SUBTITLE";
    private static final String MY_LAT = "MY_LAT";
    private static final String MY_LNG = "MY_LNG";
    private static final String POI_LAT = "POI_LAT";
    private static final String POI_LNG = "POI_LNG";

    @BindView(R.id.compassTitle) TextView title;
    @BindView(R.id.compassType) TextView type;
    @BindView(R.id.compassArrow) CompassView arrow;
    @BindView(R.id.compassDistance) TextView compassDistance;

    private GeoPoint poiLocation;
    private Unbinder unbinder;


    public static boolean showInstance(final MapActivity mapActivity, final String title,
                                       final String subTitle, final GeoPoint start, final GeoPoint end) {

        try {
            Bundle bundle = new Bundle();

            if(title != null) {
                bundle.putString(TITLE, title);
            }
            if(subTitle != null) {
                bundle.putString(SUBTITLE, subTitle);
            }

            if (start != null) {
                bundle.putDouble(MY_LAT, start.getLatitude());
                bundle.putDouble(MY_LNG, start.getLongitude());
            }
            if (end != null) {
                bundle.putDouble(POI_LAT, end.getLatitude());
                bundle.putDouble(POI_LNG, end.getLongitude());
            }
            CompassDialog fragment = new CompassDialog();
            fragment.setArguments(bundle);
            fragment.show(mapActivity.getSupportFragmentManager(), ID_TAG);

            return true;

        } catch (RuntimeException e) {
            return false;
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compass_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if(args != null) {

            double lat = args.getDouble(MY_LAT, Double.NaN);
            double lng = args.getDouble(MY_LNG, Double.NaN);
            GeoPoint myLocation = null;
            if (!Double.isNaN(lat) && !Double.isNaN(lng))
                myLocation = new GeoPoint(lat, lng);

            lat = args.getDouble(POI_LAT, Double.NaN);
            lng = args.getDouble(POI_LNG, Double.NaN);
            poiLocation = null;
            if (!Double.isNaN(lat) && !Double.isNaN(lng))
                poiLocation = new GeoPoint(lat, lng);

            title.setText(args.getString(TITLE));
            type.setText(args.getString(SUBTITLE));

            if(myLocation != null && poiLocation!= null)
                arrow.rotationFromLocations(myLocation, poiLocation, true);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { dismiss(); }
            });

        } else
            dismiss();

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.Dialog_Fullscreen);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    public void updatePoiDirection(GeoPoint myLocation, float dir){
        if(arrow != null) {
            arrow.rotationUpdate(dir, true);

            double d = MapUtils.getDistance(myLocation, poiLocation.getLatitude(), poiLocation.getLongitude());
            compassDistance.setText(MapUtils.getFormattedDistance(d));
        }
    }
}