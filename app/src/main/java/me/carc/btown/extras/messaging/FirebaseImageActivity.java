package me.carc.btown.extras.messaging;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.camera.CameraActivity;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.TinyDB;
import me.carc.btown.common.interfaces.RecyclerClickListener;
import me.carc.btown.common.interfaces.RecyclerTouchListener;
import me.carc.btown.extras.messaging.viewholders.PhotoViewHolder;

public class FirebaseImageActivity extends BaseActivity {

    private static final String TAG = FirebaseImageActivity.class.getName();

    private static final String MSG_BOARD_CAT_PHOTO     = "PHOTO_BOARD";

    private static final int RESULT_CAMERA_PREVIEW = 102;
    private static final String COLUMN_COUNT = "COLUMN_COUNT"; // use sp to save user preferences
    private static final int COL_SINGLE = 1;
    private static final int COL_GRID = 3;


    private ProgressBar loadingBar;

    @BindView(R.id.cloud_toolbar)
    Toolbar toolbar;

    @BindView(R.id.photoRecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.toggleFab)
    FloatingActionButton toggleFab;

    @BindView(R.id.photo)
    ImageView image;


    /*
        private DrawerLayout mDrawerLayout;
        private ListView mDrawerList;
        private ActionBarDrawerToggle mDrawerToggle;
        private DrawerItemCustomAdapter adapter;

    */
    private static FirebaseRecyclerAdapter<UserPhotograph, PhotoViewHolder> mFirebaseAdapter;


    private final View.OnClickListener navigationOnClickListener =
            new View.OnClickListener() {

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    if (C.HAS_L)
                        finishAfterTransition();
                    else
                        finish();
                }
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_firebase_activity);
        ButterKnife.bind(this);

        loadingBar = (ProgressBar) findViewById(android.R.id.empty);

        // Allow fullscreen - shift the toolbar down below the status bar
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        params.height += getStatusBarHeight();
        toolbar.setLayoutParams(params);

        // change the arrow color
        final Drawable backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back);
        backArrow.setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(backArrow);

        setActionBar(toolbar);

        getActionBar().setTitle(getString(R.string.shared_string_image_uploads));
        toolbar.setNavigationOnClickListener(navigationOnClickListener);

        setupRecyclerView();
    }

    /**
     * Setup the favorites_recyclerview to display the comments
     */
    private void setupRecyclerView() {

        int columnCount = TinyDB.getTinyDB().getInt(COLUMN_COUNT, COL_GRID);
        boolean gridMode = columnCount == COL_GRID;
        final GridLayoutManager layoutManager = new GridLayoutManager(this, columnCount);

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference(MSG_BOARD_CAT_PHOTO);

        if (Commons.isNetworkAvailable(this)) {
            // check the number of items in the msg board
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.d(TAG, "onDataChange: Found " + snapshot.getChildrenCount());
                    loadingBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    loadingBar.setVisibility(View.GONE);
                }
            });
        } else {
            Log.d(TAG, "setupRecyclerView: NOT CONNECTED TO INTERNET");
            loadingBar.setVisibility(View.GONE);

            AlertDialog.Builder offlineNoticeBuilder = new AlertDialog.Builder(this);
            offlineNoticeBuilder.setTitle(getString(R.string.shared_string_image_uploads));
            offlineNoticeBuilder.setIcon(R.drawable.ic_offline);
            offlineNoticeBuilder.setMessage(R.string.network_offline);

            offlineNoticeBuilder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dlg, int which) {
                    dlg.dismiss();
                    finish();
                }
            });
            offlineNoticeBuilder.show();

        }

        mFirebaseAdapter = new FirebaseRecyclerAdapter<UserPhotograph, PhotoViewHolder>(
                UserPhotograph.class,
                R.layout.image_firebase_item,
                PhotoViewHolder.class,
                database) {

            @Override
            protected void populateViewHolder(final PhotoViewHolder viewHolder, UserPhotograph entry, int position) {

                viewHolder.author.setText(entry.getDisplayName());

                StorageReference ref = FirebaseStorage.getInstance().getReference().child(entry.getPhotoUrl());

                Glide.with(FirebaseImageActivity.this)
                        .using(new FirebaseImageLoader())
                        .load(ref)
//                        .placeholder(getBackground())
                        .error(getBackground())
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                viewHolder.photo.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadStarted(Drawable placeholder) {
                                super.onLoadStarted(placeholder);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                viewHolder.photo.setImageDrawable(errorDrawable);
                                Toast.makeText(FirebaseImageActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
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

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerClickListener() {
            @Override
            public void onClick(View view, int pos) {

                try {

                    // TODO: 25/10/2017 launch the image viewer

/*
                    Intent detailIntent = new Intent(FirebaseImageActivity.this, ActivityImageDetail.class);

                    detailIntent.putExtra(C.EXTRA_HOLDER_IMAGE, true);
                    // disable transition as we're downloading the image and transition looks crap with it!
                    detailIntent.putExtra(C.EXTRA_HAS_TRANSITION, false);
                    detailIntent.putExtra(C.EXTRA_IMAGE_TITLE, mFirebaseAdapter.getItem(pos).getDisplayName());
                    detailIntent.putExtra(C.EXTRA_IMAGE_URL, mFirebaseAdapter.getItem(pos).getImageDownloadUrl());

                    String date = U.readableDate(mFirebaseAdapter.getItem(pos).getTimestamp());
                    String extra = mFirebaseAdapter.getItem(pos).getUserName() + ", " + date;
                    detailIntent.putExtra(C.EXTRA_IMAGE_EXTRACT, extra);

                    Bundle options = null;
                    if (C.HAS_L)
                        options = ActivityOptions.makeSceneTransitionAnimation(FirebaseImageActivity.this, image, "imageCover").toBundle();
                    startActivity(detailIntent, options);
*/
                } catch (Exception e) {
                    Log.d(TAG, "onItemClick: " + e.getLocalizedMessage());
                    Toast.makeText(FirebaseImageActivity.this, R.string.operation_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(View view, int pos) {
                // ...
            }
        }));

//        ViewUtils.changeFabIcon(this, toggleFab, gridMode ? R.drawable.ic_column_single : R.drawable.ic_column_multi);
//        toggleFab.setTag(gridMode ? COL_SINGLE : COL_GRID);
    }

    @OnClick(R.id.toggleFab)
    public void onToggleViewLayout(View v) {

        loadingBar.setVisibility(View.VISIBLE);
        if (C.HAS_M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, C.PERMISSION_CAMERA);
            return;
        }
        startActivityForResult(new Intent(FirebaseImageActivity.this, CameraActivity.class), RESULT_CAMERA_PREVIEW);
    }


    void updateUiFab() {

        int scrollPosition = 0;
        if (recyclerView.getLayoutManager() != null)
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        GridLayoutManager layoutManager = null;

        switch ((int) toggleFab.getTag()) {
            case COL_GRID:
                ViewUtils.changeFabIcon(this, toggleFab, R.drawable.ic_column_single);
                layoutManager = new GridLayoutManager(this, COL_GRID);
                TinyDB.getTinyDB().putInt(COLUMN_COUNT, COL_GRID);
                break;

            case COL_SINGLE:
                ViewUtils.changeFabIcon(this, toggleFab, R.drawable.ic_column_multi);
                layoutManager = new GridLayoutManager(this, COL_SINGLE);
                TinyDB.getTinyDB().putInt(COLUMN_COUNT, COL_SINGLE);
                break;

            default:
                Log.d(TAG, "updateUiFab: UNKNOWN TAG");
        }

        if (layoutManager != null) {
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.scrollToPosition(scrollPosition);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cloud_image, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    Log.e(TAG, "onMenuOpened", e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }


    /**
     * Menu click listener
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // reject all calls until the list is populated... easier than enabling / disabling at the moment
        if (loadingBar.getVisibility() == View.GONE) {

            int scrollPosition = 0;
            if (recyclerView.getLayoutManager() != null) {
                scrollPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            }
            GridLayoutManager layoutManager = null;
            switch (item.getItemId()) {
/*
                case R.id.shuffle:
                    TinyDB.getTinyDB().remove(C.TMP_LAST_BROWSE_LOCATION);
                    Collections.shuffle(relevantPhotos);
                    savePhotoList();
                    recyclerView.getAdapter().notifyDataSetChanged();
                    recyclerView.scrollToPosition(0); // reset to start of list
                    break;
*/

                case R.id.single_grid:
                    TinyDB.getTinyDB().putInt(COLUMN_COUNT, 1);
                    layoutManager = new GridLayoutManager(this, 1);
                    break;

                case R.id.multi_grid:
                    TinyDB.getTinyDB().putInt(COLUMN_COUNT, 3);
                    layoutManager = new GridLayoutManager(this, 3);
                    break;
                default:
            }

            if (layoutManager != null) {
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.scrollToPosition(scrollPosition);
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_CAMERA_PREVIEW:
                loadingBar.setVisibility(View.GONE);
                break;
            default:
        }
    }
}
