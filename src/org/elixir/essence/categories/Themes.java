/*
 * Copyright (C) 2014-2016 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elixir.essence.categories;

import android.content.Context;
import android.content.ContentResolver;
import android.content.om.IOverlayManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import android.graphics.Color;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import org.json.JSONException;
import org.json.JSONObject;

import static android.provider.Settings.Secure.CUSTOM_SIGNAL_STYLE;

public class Themes extends SettingsPreferenceFragment 
	implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "Themes";
    private Context mContext;
    private static final String KEY_SIGNAL_STYLES = "custom_signal_styles";
    private static final String BARS_STYLE = "com.tenx.systemui.signalbar_h";

    private ListPreference mSignalStyles;
    private IOverlayManager mOverlayService;
    private String Disable;
    private String Enable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.themes);
        mContext = getActivity();

        mOverlayService = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();

        mSignalStyles = (ListPreference) findPreference(KEY_SIGNAL_STYLES);
        mSignalStyles.setValue(String.valueOf(Settings.Secure.getInt(resolver, CUSTOM_SIGNAL_STYLE, 0)));
        mSignalStyles.setSummary(mSignalStyles.getEntry());
        mSignalStyles.setOnPreferenceChangeListener(this);

    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mSignalStyles) {
            Settings.Secure.putInt(getActivity().getContentResolver(), CUSTOM_SIGNAL_STYLE, Integer.parseInt((String) newValue));
            int current = Settings.Secure.getInt(resolver, CUSTOM_SIGNAL_STYLE, 0);
            mSignalStyles.setValue((String) newValue);
            mSignalStyles.setSummary(mSignalStyles.getEntry());
            if (current == 0) {
                RROManager(BARS_STYLE, false);
            } else if (current == 1) {
                RROManager(BARS_STYLE, true);
            }
        }
        return false;
    }

    public void RROManager(String name, boolean  status) {
        Log.w(TAG, name);
        Log.w(TAG, String.valueOf(status));
        try {
            mOverlayService.setEnabled(name, status, UserHandle.USER_CURRENT);
          } catch (RemoteException re) {
                Log.e(TAG, String.valueOf(re));
        }
    }
}
