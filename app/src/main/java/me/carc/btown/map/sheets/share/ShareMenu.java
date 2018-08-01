package me.carc.btown.map.sheets.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;

import org.osmdroid.util.GeoPoint;

import java.util.LinkedList;
import java.util.List;

import me.carc.btown.App;
import me.carc.btown.R;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.common.Commons;

/**
 * Display share options in bottomsheet
 */
public class ShareMenu {

    private Context mAppContext;
    private GeoPoint latLon;
    private String title;
    private String address;

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
        mAppContext = context;
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
        if(Commons.isNotNull(address) && address.toLowerCase().startsWith("http"))
            list.add(ShareItem.WEB);
        return list;
    }

    public List<ShareItem> getSimpleItems() {
        List<ShareItem> list = new LinkedList<>();
        list.add(ShareItem.CLIPBOARD);
        list.add(ShareItem.QR_CODE);
        if(Commons.isNotNull(address) && address.toLowerCase().startsWith("http"))
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
        ShareMenu menu = new ShareMenu(appContext);
        menu.latLon = latLon;
        menu.title = title;
        menu.address = address;
        ShareMenuDialogFragment.showInstance(menu, latLon == null);
    }

    public void share(ShareItem item) {
        final int zoom = 19; // use maximum zoom - very clear what the poi on the shared map
        String geoUrl = null;
        String httpUrl = null;

        if(Commons.isNotNull(latLon)) {
            geoUrl = MapUtils.buildGeoUrl(latLon.getLatitude(), latLon.getLongitude(), zoom);

//        final String httpUrlGoogle = MapUtils.buildGoogleMapLink(latLon);

            httpUrl = MapUtils.buildOsmMapLink(latLon, zoom);
        }

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
        if(Commons.isNotNull(latLon))
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
                if(Commons.isNotNull(latLon)) {
                    bundle.putFloat("LAT", (float) latLon.getLatitude());
                    bundle.putFloat("LONG", (float) latLon.getLongitude());
                    ShareDialog.sendQRCode(getActivity(), "LOCATION_TYPE", bundle, null);
                } else {
                    ShareDialog.sendQRCode(getActivity(), "TEXT_TYPE", null, address);
                }
                break;

            case WEB:
                ShareDialog.sendMessage(getActivity(), wiki);
                break;
        }

    }
}
