package me.carc.btownmvp.map.sheets.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;

import org.osmdroid.util.GeoPoint;

import java.util.LinkedList;
import java.util.List;

import me.carc.btownmvp.App;
import me.carc.btownmvp.R;
import me.carc.btownmvp.Utils.MapUtils;
import me.carc.btownmvp.common.Commons;

/**
 * Display share options in bottomsheet
 */
public class ShareMenu {

    private Context mAppContext;
    private GeoPoint latLon;
    private String title;
    private String address;

    private static final String KEY_SHARE_MENU_LATLON = "key_share_menu_latlon";
    private static final String KEY_SHARE_MENU_POINT_TITLE = "key_share_menu_point_title";

    public enum ShareItem {
        MESSAGE(FontAwesome.Icon.faw_envelope, R.string.shared_string_send),
        CLIPBOARD(FontAwesome.Icon.faw_clipboard, R.string.shared_string_copy),
        GEO(FontAwesome.Icon.faw_globe, R.string.share_geo),
        QR_CODE(FontAwesome.Icon.faw_qrcode, R.string.shared_string_qr_code),
        WEB(FontAwesome.Icon.faw_link, R.string.shared_string_internet);

        final FontAwesome.Icon iconResourceId;
        final int titleResourceId;

        ShareItem(FontAwesome.Icon iconResourceId, int titleResourceId) {
            this.iconResourceId = iconResourceId;
            this.titleResourceId = titleResourceId;
        }

        public FontAwesome.Icon getIconResourceId() {
            return iconResourceId;
        }

        public int getTitleResourceId() {
            return titleResourceId;
        }
    }

    private ShareMenu(Context context) {
        mAppContext= context;
    }

    public AppCompatActivity getActivity() {
        return ((App) mAppContext).getCurrentActivity();
    }


    public List<ShareItem> getItems() {
        List<ShareItem> list = new LinkedList<>();
        list.add(ShareItem.MESSAGE);
        list.add(ShareItem.CLIPBOARD);
        list.add(ShareItem.GEO);
        list.add(ShareItem.QR_CODE);
        if(address.toLowerCase().startsWith("http"))
            list.add(ShareItem.WEB);
        return list;
    }

    public GeoPoint getLatLon() {
        return latLon;
    }

    public String getTitle() {
        return title;
    }

    public static void show(GeoPoint latLon, String title, String address, Context appContext) {

        AppCompatActivity activity = ((App) appContext).getCurrentActivity();

        ShareMenu menu = new ShareMenu(appContext);

        menu.latLon = latLon;
        menu.title = title;
        menu.address = address;

        ShareMenuDialogFragment.showInstance(menu);
    }

    public void share(ShareItem item) {
        final int zoom = 17;// ((MapActivity) mActivity).getMapView().getZoomLevel();

        final String geoUrl = MapUtils.buildGeoUrl(latLon.getLatitude(), latLon.getLongitude(), zoom);

        final String httpUrlGoogle = "http://maps.google.com/?q=" + ((float) latLon.getLatitude()) + "," + ((float) latLon.getLongitude());

        final String httpUrl = "https://www.openstreetmap.org/#map=" + zoom + "/" + ((float) latLon.getLatitude()) + "/" + ((float) latLon.getLongitude());

        StringBuilder sb = new StringBuilder();
        if (!Commons.isEmpty(title)) {
            sb.append(title).append("\n");
        }
        if (!Commons.isEmpty(address) && !address.equals(title) && !address.equals(getActivity().getString(R.string.no_address_found))) {
            sb.append(address).append("\n");
        }
        String wiki = sb.toString();

        // Add Location
        sb.append(getActivity().getString(R.string.shared_string_location)).append(": ");
        sb.append(geoUrl).append("\n").append(httpUrl);
        String sms = sb.toString();

        switch (item) {

            case MESSAGE:
                ShareDialog.sendMessage(getActivity(), sms);
                break;

            case CLIPBOARD:
                ShareDialog.sendToClipboard(getActivity(), sms);
                break;

            case GEO:
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUrl));
                getActivity().startActivity(mapIntent);
                break;

            case QR_CODE:
                Bundle bundle = new Bundle();
                bundle.putFloat("LAT", (float) latLon.getLatitude());
                bundle.putFloat("LONG", (float) latLon.getLongitude());
                ShareDialog.sendQRCode(getActivity(), "LOCATION_TYPE", bundle, null);
                break;

            case WEB:
                ShareDialog.sendMessage(getActivity(), wiki);
                break;

        }

    }

    public void saveMenu(Bundle bundle) {
        bundle.putSerializable(KEY_SHARE_MENU_LATLON, latLon);
        bundle.putString(KEY_SHARE_MENU_POINT_TITLE, title);
    }
}
