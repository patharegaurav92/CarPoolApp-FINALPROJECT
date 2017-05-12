package edu.student.android.chatappvolley.features;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.student.android.chatappvolley.R;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static edu.student.android.chatappvolley.R.id.flag;

/**
 * Created by Gaurav on 11-05-2017.
 */

public class PendingRidersAdapter extends ArrayAdapter<PendingRiders> {
    public PendingRidersAdapter(Context context, ArrayList<PendingRiders> resource) {
        super(context,0,resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView=convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_pending_requests, parent, false);
        }
        PendingRiders pendingRiders = getItem(position);
        TextView id = (TextView) listItemView.findViewById(R.id.p_ride_id);
        id.setText(pendingRiders.getRide_id());
        id.setTextColor(Color.BLACK);
        TextView email=(TextView) listItemView.findViewById(R.id.p_user_id);
        email.setText(pendingRiders.getUser());
        email.setTextColor(Color.BLACK);
        TextView source=(TextView) listItemView.findViewById(R.id.p_source);
        source.setText(pendingRiders.getSource());
        source.setTextColor(Color.BLACK);
        TextView dest=(TextView) listItemView.findViewById(R.id.p_dest);
        dest.setText(pendingRiders.getDestination());
        dest.setTextColor(Color.BLACK);
        return listItemView;

    }
}
