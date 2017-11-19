package me.carc.btown.extras.messaging.viewholders;

import android.support.annotation.Keep;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.carc.btown.R;

/**
 * Move the holder outside of the class - Firebase limitation. This must be public
 * Created by bamptonm on 09/08/2017.
 */
@Keep
public class PhotoViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    public TextView author;
    public ImageView photo;

    public PhotoViewHolder(View v) {
        super(v);
        mView = v;
        author = itemView.findViewById(R.id.author);
        photo = itemView.findViewById(R.id.photo);
    }
}
