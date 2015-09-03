package com.alium.ALMPlayer.Drawers;

/**
 * Created by aliumujib on 8/4/15.
 */
public class NavigationDrawerItem {
        private int icon;
        private String title;
        public NavigationDrawerItem(){
            super();
        }

        public NavigationDrawerItem(int icon, String title) {
            super();
            this.icon = icon;
            this.title = title;
        }

        public int getIcon()
        {
            return icon;
        }

        public String getTitle()
        {
            return title;
        }
}
