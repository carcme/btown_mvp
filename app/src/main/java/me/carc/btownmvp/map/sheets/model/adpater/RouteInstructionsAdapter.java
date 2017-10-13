package me.carc.btownmvp.map.sheets.model.adpater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.carc.btownmvp.map.interfaces.MyClickListener;
import me.carc.btownmvp.R;
import me.carc.btownmvp.Utils.MapUtils;
import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.Commons;
import me.carc.btownmvp.data.model.RouteResult;

/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class RouteInstructionsAdapter extends RecyclerView.Adapter<RouteInstructionsAdapter.ViewHolder> {

    private static final String TAG = C.DEBUG + Commons.getTag();
    private final ArrayList<RouteResult.Paths.Instructions> list;
    public MyClickListener onClickListener;


    public RouteInstructionsAdapter(ArrayList<RouteResult.Paths.Instructions> instuctions, MyClickListener listener ) {
        this.list = instuctions;
        onClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sheet_route_direction_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int pos) {

        holder.stepDistance.setText(MapUtils.getFormattedDistance(list.get(pos).distance));
        holder.stepDuration.setText(MapUtils.getFormattedDuration((int)list.get(pos).time / C.TIME_ONE_SECOND));
        holder.directionIcon.setImageResource(R.drawable.ic_arrow_back);
        holder.directions.setText(list.get(pos).text);


        // Click Handlers
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.OnClick(v, holder.getAdapterPosition());
            }
        });
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClickListener.OnLongClick(v, holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView stepDistance;
        TextView stepDuration;
        ImageView directionIcon;
        TextView directions;

        public ViewHolder(View v) {
            super(v);

            view = v;
            stepDistance = (TextView) v.findViewById(R.id.stepDistance);
            stepDuration = (TextView) v.findViewById(R.id.stepDuration);
            directions = (TextView) v.findViewById(R.id.directions);
            directionIcon = (ImageView) v.findViewById(R.id.directionIcon);
        }
    }
}
