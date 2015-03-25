package com.ievolutioned.iac.view;

/**
 * Menu drawer item for a list
 * Created by Daniel on 23/03/2015.
 */
public class MenuDrawerItem {

    public static final int ICON_DEFAULT = -1;

    private int icon;
    private String title;

    /**
     * Instantiates a menu drawer item
     */
    public MenuDrawerItem() {
    }

    /**
     * Instantiates a menu drawer item with icon and title
     * @param icon
     * @param title
     */
    public MenuDrawerItem(int icon, String title) {
        this.setTitle(title);
        this.setIcon(icon);
    }

    /**
     * Instantiates a menu drawer item with and title
     * @param title
     */
    public MenuDrawerItem(String title) {
        this.setTitle(title);
        this.setIcon(ICON_DEFAULT);
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
