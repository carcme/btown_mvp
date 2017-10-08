package me.carc.btownmvp.map.sheets;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.carc.btownmvp.MapActivity;
import me.carc.btownmvp.R;
import me.carc.btownmvp.Utils.FragmentUtil;
import me.carc.btownmvp.Utils.MapUtils;
import me.carc.btownmvp.Utils.ViewUtils;
import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.data.model.ReverseResult;
import me.carc.btownmvp.data.model.RouteResult;
import me.carc.btownmvp.data.reverse.ReverseApi;
import me.carc.btownmvp.data.reverse.ReverseServiceProvider;
import me.carc.btownmvp.map.interfaces.MyClickListener;
import me.carc.btownmvp.map.sheets.model.RouteInfo;
import me.carc.btownmvp.map.sheets.model.adpater.RouteInstructionsAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Bottom Sheet Dialog for points of interest
 * Created by bamptonm on 31/08/2017.
 */
public class RouteDialog extends BottomSheetDialogFragment {
    public interface RouteDialogCallback {
        void onRouteChange(RouteInfo info);
        void onRouteClose();
    }

    public static final String ID_TAG = "RouteDialog";
    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String INFO = "INFO";
    public static final String RESULT = "RESULT";

    private BottomSheetBehavior behavior;
    private RouteInfo routeInfo;
    private RouteInstructionsAdapter mAdapter;
    private Unbinder unbinder;

    @BindView(R.id.routeRecyclerView)
    RecyclerView routeRecyclerView;

    @BindView(R.id.routeCar)
    TextView routeCar;
    @BindView(R.id.routeWalk)
    TextView routeWalk;
    @BindView(R.id.routeTrain)
    TextView routeTrain;

    @BindView(R.id.routeCancelBtn)
    TextView routeCancelBtn;
    @BindView(R.id.routeListBtn)
    TextView routeListBtn;
//    @BindView(R.id.routeGoBtn)
//    TextView routeGoBtn;

    @BindView(R.id.routeTime)
    TextView routeTime;
    @BindView(R.id.routeDistance)
    TextView routeDistance;
    @BindView(R.id.routeElevation)
    TextView routeElevation;
    @BindView(R.id.routeElevationText)
    TextView routeElevationText;
    @BindView(R.id.routeElevationIcon)
    ImageView routeElevationIcon;

/*
    @BindView(R.id.routeAccent)
    TextView routeAccent;
    @BindView(R.id.routeDecent)
    TextView routeDecent;
*/

    @BindView(R.id.routeDeparture)
    TextView routeDeparture;
    @BindView(R.id.routeDestination)
    TextView routeDestination;


    public static boolean showInstance(final MapActivity mapActivity, RouteInfo info, RouteResult gHopperRes) {

        try {
            Bundle bundle = new Bundle();
            bundle.putParcelable(INFO, info);
            bundle.putParcelable(RESULT, gHopperRes);

            RouteDialog fragment = new RouteDialog();
            fragment.setArguments(bundle);
            fragment.show(mapActivity.getSupportFragmentManager(), ID_TAG);

        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCB = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                Log.d(TAG, "onStateChanged: STATE_HIDDEN");
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            Log.d(TAG, "onSlide: " + slideOffset);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.RoutingDialog);
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        View rootView = View.inflate(getContext(), R.layout.sheet_route_base_layout, null);
        unbinder = ButterKnife.bind(this, rootView);


        dialog.setContentView(rootView);

        Bundle args = getArguments();
        if (args == null)
            dismiss();

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) rootView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            this.behavior = (BottomSheetBehavior) behavior;
            this.behavior.setBottomSheetCallback(mBottomSheetCB);
            this.behavior.setHideable(false);
        }

        try {
            assert args != null;
            routeInfo = args.getParcelable(INFO);
            RouteResult routeResult = args.getParcelable(RESULT);
            assert routeResult != null;
            RouteResult.Paths path = routeResult.getPath();

            if (Commons.isNull(path))
                return;

            new PopulateView(path).run();


        } catch (NullPointerException e) {
            e.printStackTrace();

            // something went wrong, close the dialog
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 200);
        }
    }

    public void show() {
        getDialog().show();
    }

    public void hide() {
        getDialog().hide();
    }

    public void closeRoute() {
        dismiss();
    }


    @Override
    public void onStart() {
        super.onStart();
        this.behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onDestroy() {
//        if(Commons.isNotNull(unbinder))
        unbinder.unbind();
        super.onDestroy();
    }

    @OnClick(R.id.routeListBtn)
    void onToggleList() {
        if (routeRecyclerView.getVisibility() == View.GONE)
            routeRecyclerView.setVisibility(View.VISIBLE);
        else
            routeRecyclerView.setVisibility(View.GONE);
    }

    @OnClick(R.id.routeCar)
    void onCarClick() {
        changeRouteMode(RouteInfo.Vehicle.CAR);
    }

    @OnClick(R.id.routeWalk)
    void onWalkClick() {
        changeRouteMode(RouteInfo.Vehicle.WALK);
    }

    @OnClick(R.id.routeTrain)
    void onTrainClick() {
    }

    @OnClick(R.id.routeCancelBtn)
    void onClose() {
        RouteDialogCallback callback = callback();
        if(Commons.isNotNull(callback)) callback.onRouteClose();
    }



    private void changeRouteMode(RouteInfo.Vehicle vehicle) {
        if (Commons.isNotNull(routeInfo)) {

            resetVehicleIcons();

            routeInfo.setVehicle(vehicle);
            RouteDialogCallback callback = callback();
            if(Commons.isNotNull(callback)) callback.onRouteChange(routeInfo);
        }
    }

    private void resetVehicleIcons() {
        // reset the vehicle icon colors
        ViewUtils.changeTextViewIconColour(getActivity(), routeCar, R.color.routeVehicleUnselecttColor);
        ViewUtils.changeTextViewIconColour(getActivity(), routeWalk, R.color.routeVehicleUnselecttColor);
        ViewUtils.changeTextViewIconColour(getActivity(), routeTrain, R.color.routeVehicleUnselecttColor);
    }


    @Nullable
    private RouteDialogCallback callback() {
        return FragmentUtil.getCallback(this, RouteDialogCallback.class);
    }


    private class PopulateView implements Runnable {

        RouteResult.Paths path;

        PopulateView(RouteResult.Paths path) {
            this.path = path;
        }

        @Override
        public void run() {
            populateRecycler();
        }

        private void populateRecycler() {

            double elevation = path.descend - path.ascend;
            int elevationRes;
            if (elevation < 0) {
                elevationRes = R.drawable.ic_expand_down;
                routeElevationText.setText(getResources().getText(R.string.altitude_descent));
            } else {
                elevationRes = R.drawable.ic_expand_up;
                routeElevationText.setText(getResources().getText(R.string.altitude_ascent));
            }

            routeElevationIcon.setImageDrawable(ContextCompat.getDrawable(getActivity(), elevationRes));
            routeElevation.setText(MapUtils.getFormattedAlt(elevation));
/*
            routeAccent.setText(MapUtils.getFormattedAlt(path.ascend));
            routeDecent.setText(MapUtils.getFormattedAlt(path.descend));
*/
            routeTime.setText(MapUtils.getFormattedDuration((int) path.time / C.TIME_ONE_SECOND));
            routeDistance.setText(MapUtils.getFormattedDistance(path.distance));

            routeDeparture.setText(routeInfo.getAddressFrom());

            if (Commons.isNull(routeInfo.getAddressTo())) {
                reverseAddressLookup(routeInfo.getTo().getLatitude(), routeInfo.getTo().getLongitude());
            } else
                routeDestination.setText(routeInfo.getAddressTo());

            resetVehicleIcons();
            switch (routeInfo.getVehicle()) {
                case R.string.vehicle_car:
                    ViewUtils.changeTextViewIconColour(getActivity(), routeCar, R.color.routeVehicleHighlightColor);
                    break;
                case R.string.vehicle_walk:
                    ViewUtils.changeTextViewIconColour(getActivity(), routeWalk, R.color.routeVehicleHighlightColor);
                    break;
                case R.string.vehicle_train:
                    ViewUtils.changeTextViewIconColour(getActivity(), routeTrain, R.color.routeVehicleHighlightColor);
                    break;
            }

            // Set TO color
            ViewUtils.changeTextViewIconColour(getActivity(), routeDestination, R.color.routeToColor);

            // Set FROM color
            ViewUtils.changeTextViewIconColour(getActivity(), routeDeparture, R.color.routeFromColor);

            // Set control button colors
            ViewUtils.changeTextViewIconColour(getActivity(), routeCancelBtn, R.color.black);
            ViewUtils.changeTextViewIconColour(getActivity(), routeListBtn, R.color.black);


            mAdapter = new RouteInstructionsAdapter(path.instructions, new MyClickListener() {
                @Override
                public void OnClick(View v, int position) {
                    Toast.makeText(getActivity(), "Position:" + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void OnLongClick(View v, int position) {
                    Toast.makeText(getActivity(), "Position:" + position, Toast.LENGTH_SHORT).show();
                }
            });

            routeRecyclerView.setNestedScrollingEnabled(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            routeRecyclerView.setLayoutManager(layoutManager);
            routeRecyclerView.setAdapter(mAdapter);

        }
    }

    private void reverseAddressLookup(double lat, double lon) {
        ReverseApi service = ReverseServiceProvider.get();
        Call<ReverseResult> reverseCall = service.reverse(lat, lon);
        reverseCall.enqueue(new Callback<ReverseResult>() {
            @SuppressWarnings({"ConstantConditions"})
            @Override
            public void onResponse(@NonNull Call<ReverseResult> call, @NonNull Response<ReverseResult> response) {

                try {
                    if (Commons.isNotNull(response.body().address)) {
                        ReverseResult.Address addressResult = response.body().address;
                        String address = addressResult.buildAddress();
                        routeInfo.setAddressTo(address);
                        routeDestination.setText(address);

                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "setupDialog ERROR: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }
}
