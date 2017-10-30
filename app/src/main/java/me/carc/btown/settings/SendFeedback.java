package me.carc.btown.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.RatingEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.carc.btown.R;
import me.carc.btown.Utils.IntentUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.ui.FeedbackDialog;
import me.carc.btown.ui.RatingDialog;

/**
 * Holder for sending feedback info to Firebase
 * Created by bamptonm on 29/10/2017.
 */

public class SendFeedback {
    private static final String TAG = C.DEBUG + Commons.getTag();

    public static final int TYPE_FEEDBACK = 0;
    public static final int TYPE_RATE = 1;

    public static final String FIREBASE_MSG_BOARD_FEEDBACK = "FEEDBACK";


    private Context mContext;

    public SendFeedback(Context ctx, int type) {
        mContext = ctx;
        if(type == TYPE_FEEDBACK) {
            feedback();
        }  else if(type == TYPE_RATE) {
            rate();
        }
    }

    private void feedback() {

        FeedbackDialog.Builder builder = new FeedbackDialog.Builder(mContext);
        builder.titleTextColor(R.color.black);

        builder.formTitle(mContext.getString(R.string.shared_string_feedback));
        builder.formHint(mContext.getString(R.string.add_your_comment));
        builder.allowEmpty(false);

        // Positive button
        builder.submitBtnText(mContext.getString(R.string.shared_string_send));
        builder.positiveBtnTextColor(R.color.positiveBtnTextColor);
        builder.positiveBtnBgColor(R.drawable.button_selector_positive);
        builder.onSumbitClick(
                new FeedbackDialog.Builder.FeedbackDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        sendFeedBack(feedback);
                    }

                    @Override
                    public void onFormCancel() {
                    }
                });
        builder.build().show();
    }


    private void rate() {
        final RatingDialog ratingDialog = new RatingDialog.Builder(mContext)
                .icon(ContextCompat.getDrawable(mContext, R.mipmap.ic_launcher_rnd))
                .threshold(3)
                .title(mContext.getString(R.string.ratings_request_title))
                .titleTextColor(R.color.black)
                .formTitle(mContext.getString(R.string.feedback_request_title))
                .formHint(mContext.getString(R.string.feedback_request_hint))
                .formSubmitText(mContext.getString(R.string.rating_dialog_submit))
                .formCancelText(mContext.getString(R.string.rating_dialog_cancel))
                .ratingBarColor(R.color.colorAccent)

                .positiveButtonTextColor(R.color.colorAccent)
                .positiveButtonBackgroundColor(R.drawable.button_selector_positive)

                .negativeButtonTextColor(R.color.colorPrimaryDark)
                .negativeButtonBackgroundColor(R.drawable.button_selector_negative)
                .onThresholdCleared(new RatingDialog.Builder.RatingThresholdClearedListener() {
                    @Override
                    public void onThresholdCleared(RatingDialog dlg, float rating, boolean thresholdCleared) {

                        try {
                            mContext.startActivity(IntentUtils.openPlayStore(mContext));
                        } catch (ActivityNotFoundException ex) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
                    }
                }).build();

        ratingDialog.show();
    }


    public static void sendFeedBack(String text) {
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).format(new Date());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(FIREBASE_MSG_BOARD_FEEDBACK);
        mDatabase.child(date).setValue(text);
        Answers.getInstance().logRating(new RatingEvent().putCustomAttribute(FIREBASE_MSG_BOARD_FEEDBACK, text));
    }
}
