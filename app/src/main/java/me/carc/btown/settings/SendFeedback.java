package me.carc.btown.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.carc.btown.BuildConfig;
import me.carc.btown.R;
import me.carc.btown.Utils.IntentUtils;
import me.carc.btown.ui.FeedbackDialog;
import me.carc.btown.ui.RatingDialog;

/**
 * Holder for sending feedback info to Firebase
 * Created by bamptonm on 29/10/2017.
 */

public class SendFeedback {
    private static final String TAG = SendFeedback.class.getName();

    public static final int TYPE_FEEDBACK = 0;
    public static final int TYPE_RATE = 1;

    public static final String FIREBASE_MSG_BOARD_FEEDBACK = "FEEDBACK";


    public SendFeedback(Context ctx, int type) {
        if(type == TYPE_FEEDBACK) {
            feedback(ctx);
        }  else if(type == TYPE_RATE) {
            rate(ctx);
        }
    }

    private void feedback(final Context ctx) {

        FeedbackDialog.Builder builder = new FeedbackDialog.Builder(ctx);
        builder.titleTextColor(R.color.black);

        builder.formTitle(ctx.getString(R.string.shared_string_feedback));
        builder.formHint(ctx.getString(R.string.add_your_comment));
        builder.allowEmpty(false);

        // Positive button
        builder.submitBtnText(ctx.getString(R.string.shared_string_send));
        builder.positiveBtnTextColor(R.color.positiveBtnTextColor);
        builder.positiveBtnBgColor(R.drawable.button_selector_positive);
        builder.onSumbitClick(
                new FeedbackDialog.Builder.FeedbackDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        sendFeedBack(feedback);
                        Toast.makeText(ctx, R.string.shared_string_thankyou, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFormCancel() {
                    }
                });
        builder.build().show();
    }


    private void rate(final Context ctx) {
        final RatingDialog ratingDialog = new RatingDialog.Builder(ctx)
                .icon(ContextCompat.getDrawable(ctx, R.mipmap.ic_launcher_skyline_rnd_blue))
                .threshold(3)
                .title(ctx.getString(R.string.ratings_request_title))
                .titleTextColor(R.color.black)
                .formTitle(ctx.getString(R.string.feedback_request_title))
                .formHint(ctx.getString(R.string.feedback_request_hint))
                .formSubmitText(ctx.getString(R.string.rating_dialog_submit))
                .formCancelText(ctx.getString(R.string.rating_dialog_cancel))
                .ratingBarColor(R.color.colorAccent)

                .positiveButtonTextColor(R.color.colorAccent)
                .positiveButtonBackgroundColor(R.drawable.button_selector_positive)

                .negativeButtonTextColor(R.color.colorPrimaryDark)
                .negativeButtonBackgroundColor(R.drawable.button_selector_negative)
                .onThresholdCleared(new RatingDialog.Builder.RatingThresholdClearedListener() {
                    @Override
                    public void onThresholdCleared(RatingDialog dlg, float rating, boolean thresholdCleared) {

                        try {
                            ctx.startActivity(IntentUtils.openPlayStore(ctx));
                        } catch (ActivityNotFoundException ex) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                            builder.setTitle(R.string.shared_string_error);
                            builder.setMessage(R.string.error_playstore_not_found);
                            dlg.show();

                        }
                        dlg.dismiss();
                    }
                })

                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        sendFeedBack(feedback);
                        Toast.makeText(ctx, R.string.shared_string_thankyou, Toast.LENGTH_SHORT).show();
                    }
                }).build();

        ratingDialog.show();
    }


    private static void sendFeedBack(String text) {
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(new Date());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(FIREBASE_MSG_BOARD_FEEDBACK);
        mDatabase.child(date).setValue(text);
        if (BuildConfig.USE_CRASHLYTICS)
            Answers.getInstance().logCustom(new CustomEvent(FIREBASE_MSG_BOARD_FEEDBACK));
    }
}
