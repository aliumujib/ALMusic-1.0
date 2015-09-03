/*
 * Copyright (C) 2014 Saravan Pantham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alium.ALMPlayer.SettingsActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.alium.ALMPlayer.BlacklistManagerActivity.BlacklistManagerActivity;
import com.alium.ALMPlayer.Helpers.UIElementsHelper;
import com.alium.ALMPlayer.InAppBilling.IabHelper;
import com.alium.ALMPlayer.R;
import com.alium.ALMPlayer.Utils.Common;
import com.alium.ALMPlayer.WelcomeActivity.WelcomeActivity;

/**
 * @author Saravan Pantham
 */
public class SettingsBlacklistsFragment extends PreferenceFragment {

    private Context mContext;
    private Common mApp;
    private View mRootView;
    private Preference blacklistManagerPreference;
    private Preference unblacklistAllPreference;
    public static SettingsActivity____ mSettingsActivity;
    public static Dialog dialogHolder;
    public static String mAccountName = "";
    public static boolean accountPicked = false;
    private IabHelper mHelper;
    static final String ITEM_SKU = "com.jams.music.player.unlock";
    protected boolean mPurchased;
    private ListView mListView;

    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        addPreferencesFromResource(R.xml.setting_blacklists);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = super.onCreateView(inflater, container, savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mApp = (Common) mContext;

        mListView = (ListView) mRootView.findViewById(android.R.id.list);

        blacklistManagerPreference = getPreferenceManager().findPreference("preference_key_blacklist_manager");
        unblacklistAllPreference = getPreferenceManager().findPreference("preference_key_unblacklist_all");

        blacklistManagerPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference arg0) {

                Intent intentPDL = new Intent(mContext, PreferenceDialogLauncherActivity.class);
                intentPDL.putExtra("INDEX", 8);
                startActivity(intentPDL);

                String[] blacklistManagerChoices = { mContext.getResources().getString(R.string.manage_blacklisted_artists),
                        mContext.getResources().getString(R.string.manage_blacklisted_albums),
                        mContext.getResources().getString(R.string.manage_blacklisted_songs),
                        mContext.getResources().getString(R.string.manage_blacklisted_playlists) };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                //Set the dialog title.
                builder.setTitle(R.string.blacklist_manager);
                builder.setItems(blacklistManagerChoices, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle bundle = new Bundle();

                        if (which==0) {
                            bundle.putString("MANAGER_TYPE", "ARTISTS");
                        } else if (which==1) {
                            bundle.putString("MANAGER_TYPE", "ALBUMS");
                        } else if (which==2) {
                            bundle.putString("MANAGER_TYPE", "SONGS");
                        } else if (which==3) {
                            bundle.putString("MANAGER_TYPE", "PLAYLISTS");
                        }

                        dialog.dismiss();
                        Intent intent = new Intent(mContext, BlacklistManagerActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);

                    }

                });

                try {
                    builder.create().show();
                }
                catch (Exception e) {
                    Intent intent = new Intent(mContext, BlacklistManagerActivity.class);
                    startActivity(intent);
                }
                return false;
            }

        });

        unblacklistAllPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference arg0) {
                AsyncUnblacklistAllSongsTask task = new AsyncUnblacklistAllSongsTask();
                task.execute();

                return false;
            }

        });

        applyLollipopActionBar();
        applyKitKatTranslucency();

        return mRootView;
    }

    /**
     * Applies KitKat specific translucency.
     */
    private void applyKitKatTranslucency() {
        if (Build.VERSION.SDK_INT==Build.VERSION_CODES.KITKAT) {

            //Calculate ActionBar and navigation bar height.
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }

            mListView.setBackgroundDrawable(UIElementsHelper.getBackgroundGradientDrawable(mContext));
            mRootView.setPadding(0, actionBarHeight + mApp.getStatusBarHeight(mContext),
                    0, 0);
            mListView.setPadding(10, 0, 10, mApp.getNavigationBarHeight(mContext));
            mListView.setClipToPadding(false);

            //Set the window color.
            getActivity().getWindow().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));

        }

    }

    private void applyLollipopActionBar() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {

            //Calculate ActionBar and navigation bar height.
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }

            mListView.setBackgroundDrawable(UIElementsHelper.getBackgroundGradientDrawable(mContext));
            mRootView.setPadding(0, actionBarHeight + mApp.getStatusBarHeight(mContext),
                    0, 0);
            mListView.setPadding(10, 0, 10, mApp.getNavigationBarHeight(mContext));
            mListView.setClipToPadding(false);

            //Set the window color.
            getActivity().getWindow().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(UIElementsHelper.getGeneralStatusBarBackground((mContext)).getColor());
        }

    }

    class AsyncUnblacklistAllSongsTask extends AsyncTask<String, Void, Void> {

        private ProgressDialog pd;

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            /*try {
                pd = new ProgressDialog(mSettingsActivity);
                pd.setTitle(R.string.reset_blacklist);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage(getResources().getString(R.string.resetting_blacklist));
                pd.show();
            }
            catch (Exception E)
            {
                pd = new ProgressDialog(mSettingsActivity);
                pd.setTitle(R.string.reset_blacklist);
                pd.setMessage(getResources().getString(R.string.resetting_blacklist));
                pd.show();
            }*/


        }

        @Override
        protected Void doInBackground(String... params) {
            getActivity().finish();

            Intent intent = new Intent(mContext, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("REFRESH_MUSIC_LIBRARY", true);
            mContext.startActivity(intent);

            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);

            //pd.dismiss();
            Toast.makeText(mContext, R.string.blacklist_reset, Toast.LENGTH_SHORT).show();
        }



    }

}