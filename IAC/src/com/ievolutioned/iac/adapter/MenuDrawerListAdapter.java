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
 */
public class MenuDrawerListAdapter extends BaseAdapter {
    /**
     * Current context
     */
    private Context context;
    private LayoutInflater mInflater;
    /**
     * The navigation drawer item
     */
    private ArrayList<MenuDrawerItem> items;

    /**
     * Instantiates the adapter with the context and the items
     *
     * @param con
     * @param items_list
     */
    public MenuDrawerListAdapter(Context con, ArrayList<MenuDrawerItem> items_list) {
        context = con;
        items = items_list;
        mInflater = LayoutInflater.from(context);
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
    public View getView(int position, View view, ViewGroup parent) {
        HelperMenu h;

        if (view == null) {
            view = mInflater.inflate(R.layout.drawer_list_item, parent, false);
            h = new HelperMenu();
            h.imgIcon = (ImageView) view.findViewById(R.id.drawer_list_item_icon);
            h.txtTitle = (TextView) view.findViewById(R.id.drawer_list_item_title);
            view.setTag(h);
        }else{
            h = (HelperMenu) view.getTag();
        }

        if (items.get(position).getIcon() != MenuDrawerItem.ICON_DEFAULT)
            h.imgIcon.setImageResource(items.get(position).getIcon());

        h.txtTitle.setText(items.get(position).getTitle());

        return view;
    }

    static class HelperMenu{
        public ImageView imgIcon;
        public TextView txtTitle;
    }
}
