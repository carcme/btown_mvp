package me.carc.btown.extras.messaging.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.carc.btown.R;
import me.carc.btown.ui.custom.CapitalisedTextView;

/**
 * Move the holder outside of the class - Firebase limitation. This must be public
 * Created by bamptonm on 09/08/2017.
 */
public class MessageViewHolder extends RecyclerView.ViewHolder {
    public TextView user;
    public ImageView userImage;
    public TextView date;
    public CapitalisedTextView message;

    public MessageViewHolder(View v) {
        super(v);
        user = itemView.findViewById(R.id.user);
        date = itemView.findViewById(R.id.date);
        userImage = itemView.findViewById(R.id.userImage);
        message = itemView.findViewById(R.id.message);
    }
}
