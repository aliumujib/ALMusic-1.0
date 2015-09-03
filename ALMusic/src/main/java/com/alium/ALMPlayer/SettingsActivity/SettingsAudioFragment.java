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
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.media.audiofx.BassBoost;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alium.ALMPlayer.EqualizerActivity.EqualizerActivity;
import com.alium.ALMPlayer.Helpers.UIElementsHelper;
import com.alium.ALMPlayer.R;
import com.alium.ALMPlayer.Utils.Common;

/**
 * @author Saravan Pantham
 */
public class SettingsAudioFragment extends PreferenceFragment {

    private Context mContext;
    private Common mApp;
    private View mRootView;

    private Preference headphonesUnplugActionPreference;
    private Preference crossfadeTracksPreference;
    private Preference crossfadeTracksDurationPreference;
    private Preference equalizerPreference;

    //Preference Manager.
    private SharedPreferences sharedPreferences;
    private PreferenceManager preferenceManager;

    private ListView mListView;

    public static SettingsActivity mSettingsActivity;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        addPreferencesFromResource(R.xml.settings_audio);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = super.onCreateView(inflater, container, savedInstanceState);

        mContext = getActivity().getApplicationContext();
        mApp = (Common) mContext;

        //Initialize SharedPreferences.
        sharedPreferences = mApp.getSharedPreferences("com.jams.music.player", Context.MODE_PRIVATE);

        headphonesUnplugActionPreference = getPreferenceManager().findPreference("preference_key_headphones_unplug_action");
        crossfadeTracksPreference = getPreferenceManager().findPreference("preference_key_crossfade_tracks");
        crossfadeTracksDurationPreference = getPreferenceManager().findPreference("preference_key_crossfade_tracks_duration");
        equalizerPreference = getPreferenceManager().findPreference("preference_key_equalizer_toggle");

        headphonesUnplugActionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference arg0) {

                //Get the current selection.
                int currentSelection;
                if (sharedPreferences.getString("UNPLUG_ACTION", "DO_NOTHING").equals("DO_NOTHING")) {
                    currentSelection = 0;
                } else {
                    currentSelection = 1;
                }

                String[] unplugActionsArray = mContext.getResources().getStringArray(R.array.headphones_unplug_actions);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.headphones_unplug_action);
                builder.setSingleChoiceItems(unplugActionsArray, currentSelection, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        Toast.makeText(mContext, R.string.changes_saved, Toast.LENGTH_SHORT).show();
                        if (which == 0) {
                            sharedPreferences.edit().putString("UNPLUG_ACTION", "DO_NOTHING").commit();
                            if (sharedPreferences.getBoolean("SERVICE_RUNNING", false) == true) {
                                mApp.getService().unregisterReceiver(mApp.getService().getHeadsetPlugReceiver());
                            }
                        } else {
                            sharedPreferences.edit().putString("UNPLUG_ACTION", "PAUSE_MUSIC_PLAYBACK").commit();
                            if (sharedPreferences.getBoolean("SERVICE_RUNNING", false) == true) {
                                mApp.getService().registerHeadsetPlugReceiver();
                            }

                        }

                    }

                });

                builder.create().show();

                return false;
            }

        });

        crossfadeTracksPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                int currentSelection;
                if (sharedPreferences.getBoolean(Common.CROSSFADE_ENABLED, false) == true) {
                    currentSelection = 0;
                } else {
                    currentSelection = 1;
                }

                String[] enableDisableArray = mContext.getResources().getStringArray(R.array.enable_disable);

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.crossfade_tracks);
                builder.setSingleChoiceItems(enableDisableArray, currentSelection, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        Toast.makeText(mContext, R.string.changes_saved, Toast.LENGTH_SHORT).show();
                        if (which == 0) {
                            sharedPreferences.edit().putBoolean(Common.CROSSFADE_ENABLED, true).commit();

                            //Enable crossfade for the current queue.
                            if (mApp.isServiceRunning() && mApp.getService().getHandler() != null) {
                                mApp.getService().getHandler().post(mApp.getService().startCrossFadeRunnable);
                            }

                        } else {
                            sharedPreferences.edit().putBoolean(Common.CROSSFADE_ENABLED, false).commit();

                            //Disable crossfade for the current queue.
                            if (mApp.isServiceRunning() && mApp.getService().getHandler() != null) {
                                mApp.getService().getHandler().removeCallbacks(mApp.getService().startCrossFadeRunnable);
                                mApp.getService().getHandler().removeCallbacks(mApp.getService().crossFadeRunnable);
                            }

                        }

                    }

                });

                builder.create().show();
                return false;
            }

        });

        crossfadeTracksDurationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mSettingsActivity);
                builder.setTitle(R.string.crossfade_duration);

                RelativeLayout dialogView = (RelativeLayout) mSettingsActivity.getLayoutInflater().inflate(R.layout.dialog_crossfade_duration, null);
                final TextView durationText = (TextView) dialogView.findViewById(R.id.crossfade_duration_text);
                final SeekBar durationSeekBar = (SeekBar) dialogView.findViewById(R.id.crossfade_duration_seekbar);

                int currentSeekBarDuration = sharedPreferences.getInt(Common.CROSSFADE_DURATION, 5);
                durationSeekBar.setMax(14);
                durationSeekBar.setProgress(currentSeekBarDuration);
                durationText.setText(currentSeekBarDuration + " secs");

                durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        sharedPreferences.edit().putInt(Common.CROSSFADE_DURATION, (progress + 1)).commit();
                        durationText.setText((progress + 1) + " secs");

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub

                    }

                });

                builder.setView(dialogView);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                        Toast.makeText(mContext, R.string.changes_saved, Toast.LENGTH_SHORT).show();

                    }

                });

                builder.create().show();
                return false;
            }

        });

        equalizerPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                boolean currentSetting = sharedPreferences.getBoolean("EQUALIZER_ENABLED", true);
                int intCurrentSetting = -1;
                if (currentSetting == true)
                    intCurrentSetting = 0;
                else
                    intCurrentSetting = 1;

                AlertDialog.Builder builder = new AlertDialog.Builder(mSettingsActivity);
                builder.setTitle(R.string.equalizer);
                builder.setSingleChoiceItems(R.array.enable_disable, intCurrentSetting, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        boolean isEnabled;
                        if (which == 0)
                            isEnabled = true;
                        else
                            isEnabled = false;

                        sharedPreferences.edit().putBoolean("EQUALIZER_ENABLED", isEnabled).commit();
                        Toast.makeText(mContext, R.string.changes_saved, Toast.LENGTH_SHORT).show();

                        if (Build.PRODUCT.contains("HTC") && which == 0) {
                            showHTCEqualizerIssueDialog();
                        }

                        if (sharedPreferences.getBoolean("SERVICE_RUNNING", false) == true && which == 0) {
                            mApp.getService().initAudioFX();
                        } else if (sharedPreferences.getBoolean("SERVICE_RUNNING", false) == true && which == 1) {
                            try {
                                mApp.getService().getEqualizerHelper().releaseEQObjects();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        dialog.dismiss();
                    }

                    private void showHTCEqualizerIssueDialog() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(R.string.htc_devices);
                        builder.setMessage(R.string.htc_devices_equalizer_issue);
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }

                        });

                        builder.create().show();

                    }

                });

                builder.create().show();
                return false;
            }

        });

        applyKitKatTranslucency();
        applyLollipopActionBar();

        return mRootView;
    }


    private void applyKitKatTranslucency() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

            //Calculate ActionBar and navigation bar height.
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }

            //Set the window color.
            getActivity().getWindow().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));

        }

    }

    private void applyLollipopActionBar() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {

            //Calculate ActionBar and navigation bar height.
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }

            mListView.setBackgroundColor(0xFFEEEEEE);
            mRootView.setPadding(0, actionBarHeight + mApp.getStatusBarHeight(mContext),
                    0, 0);
            mListView.setPadding(10, 0, 10, mApp.getNavigationBarHeight(mContext));
            mListView.setClipToPadding(false);

            //Set the window color.
            getActivity().getWindow().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));

        }

    }
}
