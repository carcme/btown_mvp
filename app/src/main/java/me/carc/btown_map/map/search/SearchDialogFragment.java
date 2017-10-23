package me.carc.btown_map.map.search;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import me.carc.btown_map.App;
import me.carc.btown_map.R;
import me.carc.btown_map.Utils.AndroidUtils;
import me.carc.btown_map.common.C;
import me.carc.btown_map.common.Commons;
import me.carc.btown_map.common.TinyDB;
import me.carc.btown_map.map.search.fragments.QuickSearchCategoriesListFragment;
import me.carc.btown_map.map.search.fragments.QuickSearchFavoriteListFragment;
import me.carc.btown_map.map.search.fragments.QuickSearchHistoryListFragment;
import me.carc.btown_map.map.search.fragments.QuickSearchListFragment;
import me.carc.btown_map.map.search.fragments.QuickSearchMainListFragment;
import me.carc.btown_map.map.search.list.SearchFragmentPagerAdapter;
import me.carc.btown_map.map.search.model.Place;
import me.carc.btown_map.ui.custom.LockableViewPager;

import static android.view.View.GONE;

/**
 * Created by Carc.me on 30.08.16.
 * Search dialog - show categories and history
 */
public class SearchDialogFragment extends DialogFragment implements ISearch.View {
    public interface SearchListener {
        void searchItemSelected(Place poi);

        void showPlaceItem(Place poi);

        void showFromDatabase(int dbType, Place poi);

        void doWikiLookup();
    }

    SearchListener cbSearchListener;

    @SuppressWarnings("unused")
    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String ID_TAG = "SearchDialogFragment";

    private ISearch.Presenter presenter;
    private Unbinder unbinder;

    private static final String QUICK_SEARCH_SHOW_START_HIDDEN = "QUICK_SEARCH_SHOW_START_HIDDEN";
    private static final String MAP_CENTER = "MAP_CENTER";
    private static final String MY_LOCATION = "MY_LOCATION";


    public static final int SEARCH_ITEM_POI_CATEGORIES = 0;
    //    public static final int POI_CAT_ITEM = 1;
    public static final int SEARCH_ITEM_MAIN = 2;
    public static final int SEARCH_ITEM_HISTORY = 3;
    public static final int SEARCH_ITEM_FAVORITE = 4;

    public static final int SORT_TIME = 1;
    public static final int SORT_DATE = 2;

    private TinyDB db;

    private GeoPoint myLocation = null;

    private String searchQuery;
    private boolean show;
    private String toolbarTitle;
    private boolean toolbarVisible;

    private QuickSearchMainListFragment mainSearchFragment;
    private QuickSearchHistoryListFragment historySearchFragment;
    private QuickSearchCategoriesListFragment categoriesSearchFragment;
    private QuickSearchFavoriteListFragment favoriteSearchFragment;

    @BindView(R.id.search_toolbar)
    Toolbar searchToolbar;

    @BindView(R.id.search_pager)
    LockableViewPager viewPager;

    @BindView(R.id.search_tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.searchEditText)
    EditText searchEditText;

    @BindView(R.id.searchProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.clearButton)
    ImageButton clearButton;

    @BindView(R.id.settingButton)
    ImageButton settingButton;

    @BindView(R.id.bookmarkBtn)
    ImageButton bookmarkBtn;


    @BindView(R.id.tab_toolbar_layout)
    View tabToolbarView;

    @BindView(R.id.tabs_view)
    View tabsView;

    @BindView(R.id.search_view)
    View searchView;


    @Override
    public void onSetSearchHint(String hint) {
        searchEditText.setHint(hint);
    }

    @Override
    public void onSetClearButtonIcon(int id) {
        clearButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), id));
    }

    @Override
    public void setPresenter(ISearch.Presenter presenter) {
        this.presenter = presenter;
    }

    public static boolean showInstance(final Context appContext, boolean show, final GeoPoint mapCenter, final GeoPoint myLocation) {

        AppCompatActivity activity = ((App) appContext).getCurrentActivity();

        try {
            Bundle bundle = new Bundle();
            bundle.putBoolean(QUICK_SEARCH_SHOW_START_HIDDEN, show);

            if (Commons.isNotNull(mapCenter)) bundle.putParcelable(MAP_CENTER, mapCenter);
            if (Commons.isNotNull(myLocation)) bundle.putParcelable(MY_LOCATION, myLocation);

            SearchDialogFragment fragment = new SearchDialogFragment();
            fragment.setArguments(bundle);
            fragment.show(activity.getSupportFragmentManager(), ID_TAG);

            return true;

        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.Dialog_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);

        presenter = new SearchDialogPresenter(getActivity(), this);

        Bundle args = getArguments();
        if (Commons.isNotNull(args)) {
            show = args.getBoolean(QUICK_SEARCH_SHOW_START_HIDDEN, true);
            GeoPoint mapCenter = args.getParcelable(MAP_CENTER);
            myLocation = args.getParcelable(MY_LOCATION);
            presenter.setLocations(mapCenter, myLocation);
        }

        if (searchQuery == null)
            searchQuery = "";


        setupUI();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.setSearchHint(searchEditText.length());
        updateClearButtonVisibility(true);
        addMainSearchFragment();
    }

    public void addMainSearchFragment() {
        mainSearchFragment = (QuickSearchMainListFragment) Fragment.instantiate(getActivity(), QuickSearchMainListFragment.class.getName());
        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
        childFragTrans.replace(R.id.search_view, mainSearchFragment);
        childFragTrans.commit();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setShowsDialog(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            cbSearchListener = (SearchListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TourListener callbacks");
        }
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        cbSearchListener = null;
        super.onDetach();
    }

    public void show() {
        getDialog().show();
        updateClearButtonVisibility(true);
        progressBar.setVisibility(GONE);
        Toast.makeText(getActivity(), R.string.search_no_results_found, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("unused")
    public void hide() {
        updateClearButtonVisibility(true);
        getDialog().hide();
    }

    public void closeSearch() {
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!show)
            closeSearch();
        presenter.setSearchHint(searchEditText.length());
        updateClearButtonVisibility(true);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setupUI() {
        final Drawable backArrow = ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_back);
        backArrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.almostBlack), PorterDuff.Mode.SRC_ATOP);
        searchToolbar.setNavigationIcon(backArrow);
        searchToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearch();
            }
        });

        SearchFragmentPagerAdapter pagerAdapter = new SearchFragmentPagerAdapter(getActivity(), getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(
                new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int pos) {
                        updateOverflowButtonVisibility(pos);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                }
        );

        clearButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_times));
        clearButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               if (searchEditText.getText().length() > 0) {
                                                   searchEditText.setText("");
                                                   presenter.setSearchHint(searchEditText.length());
                                               }
                                           }
                                       }
        );

        if (Commons.isNull(db))
            db = TinyDB.getTinyDB();
    }

    public void itemLongPress(int type, int position) {

        Place place;
        switch (type) {
            case SEARCH_ITEM_FAVORITE:
                place = (Place) favoriteSearchFragment.getAdapter().getItem(position);
                break;

            case SEARCH_ITEM_HISTORY:
                place = (Place) historySearchFragment.getAdapter().getItem(position);
                break;

            default:
                throw new RuntimeException("Unhandled Type");
        }
        presenter.showLongPressSelectionDialog(type, place);
    }

    @OnClick(R.id.bookmarkBtn)
    void showWikiReadingList() {
        presenter.onShowWikiReadingList();
    }


    @OnClick(R.id.settingButton)
    void onSettingOverflow() {
        PopupMenu popupMenu = new PopupMenu(getActivity(), settingButton);
        popupMenu.inflate(R.menu.menu_search_overflow);
        popupMenu.setOnMenuItemClickListener(menuListener);
        popupMenu.show();
    }


    private PopupMenu.OnMenuItemClickListener menuListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            int index = viewPager.getCurrentItem();

            switch (item.getItemId()) {
                case R.id.menu_sort_date:
                    if (Commons.isNotNull(favoriteSearchFragment) && index == 1) {
                        favoriteSearchFragment.sortList(SORT_DATE);
                    } else if (Commons.isNotNull(historySearchFragment) && index == 2) {
                        historySearchFragment.sortList(SORT_DATE);
                    }
                    return true;

                case R.id.menu_sort_time:
                    if (Commons.isNotNull(favoriteSearchFragment) && index == 1) {
                        favoriteSearchFragment.sortList(SORT_TIME);
                    } else if (Commons.isNotNull(historySearchFragment) && index == 2) {
                        historySearchFragment.sortList(SORT_TIME);
                    }
                    return true;

                case R.id.menu_show_all:
                    if (Commons.isNotNull(favoriteSearchFragment) && index == 1) {
                        if (favoriteSearchFragment.getAdapter().getCount() > 0)
                            cbSearchListener.showFromDatabase(SEARCH_ITEM_FAVORITE, null);

                    } else if (Commons.isNotNull(historySearchFragment) && index == 2) {
                        if (historySearchFragment.getAdapter().getCount() > 0)
                            cbSearchListener.showFromDatabase(SEARCH_ITEM_HISTORY, null);
                    }
                    return true;

                default:
                    return false;
            }
        }
    };


    @OnTextChanged(R.id.searchEditText)
    void searchEditChange(CharSequence s) {
        String newQueryText = s.toString();
        presenter.setSearchHint(searchEditText.length());
        updateClearButtonVisibility(true);
        updateTabbarVisibility(newQueryText.length() == 0);

        if (!searchQuery.equalsIgnoreCase(newQueryText)) {
            searchQuery = newQueryText;
            if (!Commons.isEmpty(searchQuery))
                presenter.runMainSearch(searchQuery);
        }
    }

    public void hideKeyboard() {
        if (searchEditText.hasFocus()) {
            AndroidUtils.hideSoftKeyboard(getActivity(), searchEditText);
        }
    }

    @SuppressWarnings("unused")
    public void restoreToolbar() {
        if (toolbarVisible) {
            if (Commons.isNotNull(toolbarTitle)) {
                showToolbar(toolbarTitle);
            } else {
                showToolbar();
            }
        }
    }

    public void showToolbar() {
        showToolbar(getText());
    }

    public void showToolbar(String title) {
        toolbarVisible = true;
        toolbarTitle = title;
    }

    public String getText() {
        return searchEditText.getText().toString();
    }

    @SuppressWarnings("unused")
    public boolean isSearchHidden() {
        return !show;
    }

    @Override
    public void onShowProgressBar(boolean show) {
        if (show) {
            updateClearButtonVisibility(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            updateClearButtonVisibility(true);
            progressBar.setVisibility(GONE);
        }
    }

    @Override
    public void updateListAdapter(int type, List<Place> places) {
        switch (type) {

            case SEARCH_ITEM_MAIN:
                mainSearchFragment.updateListAdapter(places);
                break;

            case SEARCH_ITEM_POI_CATEGORIES:
                categoriesSearchFragment.updateListAdapter(places);
                break;

            case SEARCH_ITEM_HISTORY:
                if (Commons.isNotNull(places)) {
                    historySearchFragment.updateListAdapter(places);
                }
                break;

            case SEARCH_ITEM_FAVORITE:
                if (Commons.isNotNull(places)) {
                    favoriteSearchFragment.updateListAdapter(places);
                }
                break;
        }
    }


    @Override
    public void updateClearButtonVisibility(boolean show) {
        if (show) clearButton.setVisibility(searchEditText.length() > 0 ? View.VISIBLE : GONE);
        else clearButton.setVisibility(View.GONE);
    }

    public void updateOverflowButtonVisibility(int pos) {
        if (pos == 0)
            settingButton.setVisibility(View.GONE);
        else
            settingButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void updateTabbarVisibility(boolean show) {
        if (show && tabsView.getVisibility() == GONE) {
            tabToolbarView.setVisibility(View.VISIBLE);
            tabsView.setVisibility(View.VISIBLE);
            searchView.setVisibility(GONE);
        } else if (!show && tabsView.getVisibility() == View.VISIBLE) {
            tabToolbarView.setVisibility(GONE);
            tabsView.setVisibility(GONE);
            searchView.setVisibility(View.VISIBLE);
        }
    }


    public void onSearchListFragmentResume(QuickSearchListFragment searchListFragment) {
        switch (searchListFragment.getType()) {
            case HISTORY:
                historySearchFragment = (QuickSearchHistoryListFragment) searchListFragment;
                presenter.loadHistory();
                break;

            case FAVORITE:
                favoriteSearchFragment = (QuickSearchFavoriteListFragment) searchListFragment;
                presenter.loadFavorite();
                break;

            case CATEGORIES:
                categoriesSearchFragment = (QuickSearchCategoriesListFragment) searchListFragment;
                presenter.loadCategories();
                break;

            case MAIN:
                if (!Commons.isEmpty(searchQuery)) {
                    searchEditText.setText(searchQuery);
                    searchEditText.setSelection(searchQuery.length());
                }
                break;
        }

        searchListFragment.updateLocation(myLocation, null);
    }

    @SuppressWarnings("unused")
    public int setTint(int imageRes, int color) {
        Drawable drawable = ContextCompat.getDrawable(getActivity().getApplicationContext(), imageRes);
        ColorFilter filter = new LightingColorFilter(Color.WHITE, ContextCompat.getColor(getActivity(), color));
        drawable.setColorFilter(filter);
        return imageRes;
    }

    public void completeQueryWithObject(Place place) {

        AndroidUtils.hideSoftKeyboard(getActivity(), searchEditText);

        onShowProgressBar(true);

        if (place.getPlaceId() == SEARCH_ITEM_POI_CATEGORIES) {
            if (place.getName().equals("Wikipedia"))
                cbSearchListener.doWikiLookup();
            else
                cbSearchListener.searchItemSelected(place);

        } else if (place.getPlaceId() == SEARCH_ITEM_HISTORY) {
//            cbSearchListener.showPlaceItem(place);
            cbSearchListener.showFromDatabase(place.getPlaceId(), place);

        } else if (place.getPlaceId() == SEARCH_ITEM_MAIN) {
            presenter.addToHistory(place);
            cbSearchListener.showPlaceItem(place);

        } else if (place.getPlaceId() == SEARCH_ITEM_FAVORITE) {
//            cbSearchListener.showFavoriteItem(place);
            cbSearchListener.showFromDatabase(place.getPlaceId(), place);
        }
    }

    @SuppressWarnings("unused")
    public void updatePoiDirection(float dir, GeoPoint myLocation) {
        int index = viewPager.getCurrentItem();
        if (Commons.isNotNull(favoriteSearchFragment) && index == 1) {
            favoriteSearchFragment.updateLocation(myLocation, dir);
        } else if (Commons.isNotNull(historySearchFragment) && index == 2) {
            favoriteSearchFragment.updateLocation(myLocation, dir);
        }

    }
}
