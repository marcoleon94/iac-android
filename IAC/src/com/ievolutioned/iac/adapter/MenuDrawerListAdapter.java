package com.ievolutioned.iac.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ievolutioned.iac.R;
import com.ievolutioned.iac.view.MenuDrawerItem;

import java.util.ArrayList;

/**
 * Menu navigation drawer list adapter. manages the base adapter of the navigation drawer menu
 * Created by Daniel on 23/03/2015.
 */
public class MenuDrawerListAdapter extends BaseAdapter {

    /**
     * Current context
     */
    private Context context;
    /**
     * The navigation drawer item
     */
    private ArrayList<MenuDrawerItem> items;

    /**
     * Instantiates the adapter with the context and the items
     *
     * @param context
     * @param items
     */
    public MenuDrawerListAdapter(Context context, ArrayList<MenuDrawerItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.drawer_list_item_icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.drawer_list_item_title);

        imgIcon.setImageResource(items.get(position).getIcon());
        txtTitle.setText(items.get(position).getTitle());

        return convertView;
    }

}
