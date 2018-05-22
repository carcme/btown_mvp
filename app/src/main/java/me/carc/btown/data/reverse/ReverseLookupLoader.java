package me.carc.btown.data.reverse;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import me.carc.btown.common.Commons;
import me.carc.btown.data.results.ReverseResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Reverse address lookup - extend to get all parameters
 * Created by bamptonm on 09/10/2017.
 */

public class ReverseLookupLoader {
    private static final String TAG = ReverseLookupLoader.class.getName();

    private final WeakReference<TextView> imageViewRef;
    private String completeAddress;

    public ReverseLookupLoader(TextView textView, double lat, double lon) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewRef = new WeakReference<>(textView);

        reverseAddressLookup(lat, lon);
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
                        ReverseResult.Address address = response.body().address;

                        completeAddress = address.buildAddress();
                        setImageView(true);
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, "RevereLookupLoader :: " + e.getLocalizedMessage());
                    setImageView(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                setImageView(false);
            }
        });
    }

    private void setImageView(boolean hasAddress) {
        if (imageViewRef != null) {
            final TextView textView = imageViewRef.get();
            if (textView != null) {
                if (hasAddress)
                    textView.setText(Commons.startsWith(completeAddress, ",") ?
                            completeAddress.substring(1).trim() : completeAddress);
                else
                    textView.setVisibility(View.GONE);
            }
        }
    }
}
