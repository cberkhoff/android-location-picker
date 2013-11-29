package cl.cberkhoff.locationpicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CBerkhoff on 09-10-13.
 */
public class LocationAdapter extends ArrayAdapter<Location> {

    private final List<Location> originalLocations;
    private ParentIdFilter filter;
    private int count;

    class ViewHolder{
        TextView name;
    }

    public LocationAdapter(Context context, LocationReader lr) {
        super(context, android.R.layout.simple_list_item_1);
        originalLocations = lr.readLocations();
        for(Location l : originalLocations){
            add(l);
        }
        this.count = originalLocations.size();
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);

            h = new ViewHolder();
            h.name = (TextView) convertView.findViewById(android.R.id.text1);

            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        h.name.setText(getItem(position).getName());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new ParentIdFilter();
        }
        return filter;
    }

    private class ParentIdFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final FilterResults fr = new FilterResults();
            final ArrayList<Location> filteredLocations = new ArrayList<Location>();

            for(Location l : originalLocations){
                if(l.getParentId() != null && (l.getParentId()+"").equals(constraint)){
                    filteredLocations.add(l);
                }
            }

            fr.count = filteredLocations.size();
            fr.values = filteredLocations;

            return fr;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            final ArrayList<Location> values = (ArrayList<Location>) results.values;
            for(Location l : values){
                add(l);
            }
            count = values.size();
            notifyDataSetChanged();
        }
    }
}
