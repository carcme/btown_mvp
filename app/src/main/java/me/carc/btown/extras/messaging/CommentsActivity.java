package me.carc.btown.extras.messaging;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.BuildConfig;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.facebook.Profile;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.AndroidUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.extras.messaging.viewholders.MessageViewHolder;
import me.carc.btown.ui.custom.MyCustomLayoutManager;

public class CommentsActivity extends BaseActivity {


    public static final String EXTRA_MESSAGE_BOARD_CAT = "EXTRA_MESSAGE_BOARD_CAT";
    public static final String EXTRA_MESSAGE_BOARD_ID  = "EXTRA_MESSAGE_BOARD_ID";
    public static final String EXTRA_MESSAGE_BOARD_ITEM  = "EXTRA_MESSAGE_BOARD_ITEM";

    public static final String MSG_BOARD_CAT_COMMENTS  = "COMMENT_BOARD";
    public static final String MSG_BOARD_CAT_DISCUSS   = "DISCUSS_BOARD";
    public static final String MSG_BOARD_CAT_RATING    = "RATING_BOARD";

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 100;

    public static final String MESSAGE_SENT_EVENT = "message_sent";
    public static final String RATING_SENT_EVENT  = "rating_sent";

    public static final String ANSWERS_CUSTOM_KEY = "CommentsActivity";


    private String mUsername;
    private String mPhotoUrl;
    public FirebaseRecyclerAdapter<CommentMessage, MessageViewHolder> mFirebaseAdapter;

    private DatabaseReference database;
    private static String msgCategory;
    private static String msgBoard;
    private static String msgCollection = "";


    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.messageRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.messageEditText)
    EditText msgEditText;

    @BindView(R.id.sendButton)
    Button sendMsgButton;

    @BindView(R.id.first_comment)
    TextView firstCommentTV;

    @BindView(R.id.first_comment_image)
    ImageView firstCommentImage;

    @BindView(R.id.fcm_toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_firebase_activity);
        ButterKnife.bind(this);

        // stop keypad from opening automatically
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Get incoming intent to id the correct comments board
        Intent incomingIntent = getIntent();
        if (incomingIntent.hasExtra(EXTRA_MESSAGE_BOARD_ID)) {
            // is this a COMMENT or a DISCUSSION post
            msgCategory = incomingIntent.getStringExtra(EXTRA_MESSAGE_BOARD_CAT);
            // Comments board - get the story title
            msgBoard = incomingIntent.getStringExtra(EXTRA_MESSAGE_BOARD_ID);

            // Comments board - get the collection name
/*
            if (incomingIntent.hasExtra(C.EXTRA_MESSAGE_BOARD_ITEM))
                msgCollection = incomingIntent.getStringExtra(C.EXTRA_MESSAGE_BOARD_ITEM);
            else
                msgCollection = "";
*/
            initToolbar();
            loggedIn();

        } else
            finish();
    }

    /**
     * Init the toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(msgBoard);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    /**
     * User is logged in
     */
    private void loggedIn() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Profile facebookUser = Profile.getCurrentProfile();

        if (Commons.isNotNull(firebaseUser)) {
            mUsername = firebaseUser.getDisplayName();
            mPhotoUrl = firebaseUser.getPhotoUrl().toString();
        } else if (Commons.isNotNull(facebookUser)) {
            mUsername = facebookUser.getName();
            mPhotoUrl = facebookUser.getProfilePictureUri(16, 16).toString();
        } else {
            progressBar.setVisibility(View.GONE);
            showAlertDialog(R.string.shared_string_error, R.string.login_error, -1);
            Answers.getInstance().logCustom(new CustomEvent(C.ANSWERS_ERROR).putCustomAttribute(ANSWERS_CUSTOM_KEY, "Login Error"));
            return;
        }
        setupRecyclerView();
        initCommentsFeature();
    }

    /**
     * set up the controls for posting
     */
    private void initCommentsFeature() {
        // limit the length of the comment
        msgEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        msgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /*nohting here*/ }

            @Override
            public void afterTextChanged(Editable s) { /*nohting here*/ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0)
                    sendMsgButton.setEnabled(true);
                else
                    sendMsgButton.setEnabled(false);
            }
        });

        msgEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO)
                    sendMsg();
                return false;
            }
        });

        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
                AndroidUtils.hideSoftKeyboard(CommentsActivity.this, view);
            }
        });
    }

    /**
     * Setup the recyclerview to display the comments
     */
    private void setupRecyclerView() {
        final MyCustomLayoutManager layoutManager = new MyCustomLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        if (msgCollection.isEmpty())
            database = FirebaseDatabase.getInstance().getReference(msgCategory).child(msgBoard);
        else
            database = FirebaseDatabase.getInstance().getReference(msgCategory).child(msgCollection).child(msgBoard);

        if (Commons.isNetworkAvailable(this)) {
            // check the number of items in the msg board
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    long count = snapshot.getChildrenCount();
                    if (count > 0) {
                        firstCommentTV.setVisibility(View.GONE);
                        firstCommentImage.setVisibility(View.GONE);
                    } else {
                        firstCommentTV.setVisibility(View.VISIBLE);
                        firstCommentTV.setText(R.string.first_comment_notification);
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) { /*EMPTY*/ }
            });
        } else {
            firstCommentTV.setText(R.string.sync_comment_later);
            firstCommentTV.setVisibility(View.VISIBLE);
            firstCommentImage.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(ProgressBar.INVISIBLE);
        }

        mFirebaseAdapter = new FirebaseRecyclerAdapter<CommentMessage, MessageViewHolder>(
                CommentMessage.class,
                R.layout.comments_firebase_item,
                MessageViewHolder.class,
                database) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, CommentMessage msg, int position) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.message.setText(msg.getText());
                viewHolder.user.setText(msg.getName());
                viewHolder.date.setText(Commons.readableDate(msg.getDate()));

                if (msg.getPhotoUrl() == null) {
                    Drawable drawable = ContextCompat.getDrawable(CommentsActivity.this, R.drawable.ic_user);
                    drawable.setColorFilter(ContextCompat.getColor(CommentsActivity.this, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                    viewHolder.userImage.setImageDrawable(drawable);
                } else
                    Glide.with(CommentsActivity.this)
                            .load(msg.getPhotoUrl())
                            .into(viewHolder.userImage);
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int msgCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (msgCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mFirebaseAdapter);

        // show usage hint if 1st time using comments (or debugging)
        if(!db.getBoolean("COMMENTS_SHOW_HINT") || C.DEBUG_ENABLED) {
            showAlertDialog(R.string.comment_usage_hint_title, R.string.comment_usage_hint, -1, R.drawable.ic_menu_share);
            if(BuildConfig.DEBUG)
                Toast.makeText(this, "Show usage hint if 1st time using comments (or debugging)", Toast.LENGTH_SHORT).show();
            db.putBoolean("COMMENTS_SHOW_HINT", true);
        }
    }

    /**
     * Post the msg to the specific msg board
     */
    private void sendMsg() {
        CommentMessage commentMessage = new CommentMessage(msgEditText.getText().toString(), mUsername, mPhotoUrl);

        database.push().setValue(commentMessage);
        msgEditText.setText("");
        firstCommentTV.setVisibility(View.GONE);
        firstCommentImage.setVisibility(View.GONE);

        Answers.getInstance().logShare(new ShareEvent()
                .putContentId("ANSWERS_CUSTOM_KEY")
                .putContentName("Comment")
                .putContentType(msgBoard));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
