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

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alium.ALMPlayer.R;
import com.alium.ALMPlayer.Utils.Common;

/**
 * @author Saravan Pantham
 */
public class SettingsAboutFragment extends PreferenceFragment {

    private Context mContext;
    private Common mApp;
    private View mRootView;
    private Preference licensesPreference;
    private Preference contactUsPreference;

    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        addPreferencesFromResource(R.xml.settings_contact_lisences);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = super.onCreateView(inflater, container, savedInstanceState);
        mContext = getActivity().getApplicationContext();
        mApp = (Common) mContext;

        licensesPreference = getPreferenceManager().findPreference("preference_key_licenses");
        contactUsPreference = getPreferenceManager().findPreference("preference_key_contact_us");


        licensesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference arg0) {

                Intent intent = new Intent(mContext, PreferenceDialogLauncherActivity.class);
                intent.putExtra("INDEX", 12);
                startActivity(intent);

                return false;
            }

        });

        contactUsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference arg0) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "aliuabdulmujib@yahoo.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ALMusic Player Support");
                startActivity(Intent.createChooser(emailIntent, "Send email"));

                return false;
            }

        });

        return mRootView;
    }

}
