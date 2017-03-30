package fetch.supermarkt.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import fetch.supermarkt.R;
import fetch.supermarkt.model.Request;

/**
 * Created by sande on 30/03/2017.
 */

public class RequestListAdapter extends ArrayAdapter<Request> {

    public RequestListAdapter(Context context, int resource, List<Request> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.a_request_list_item, null);
        }

        Request p = getItem(position);

        if (p != null) {

        }
        return v;
    }

}
