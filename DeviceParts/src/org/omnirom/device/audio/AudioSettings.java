/*
* Copyright (C) 2019 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device.audio;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.Resources;
import android.content.Intent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.os.SystemProperties;
import androidx.preference.PreferenceFragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import android.provider.Settings;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;

import org.omnirom.device.R;

public class AudioSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "AudioWizardControllerOmni";
    private static final String KEY_SETTINGS_PREFIX = "device_setting_";
    public static final String KEY_AUDIOWIZARD = "audiowizard_entry";
    private static final String KEY_OUTDOOR_MODE = "outdoor_mode";
    private static final String PACKAGE_NAME = "com.asus.maxxaudio.audiowizard";
    private static final String SERVICE_NAME = "com.asus.maxxaudio";
    private static final String SETTINGS_KEY_AUDIO_WIZARD_DEVICE = "settings_key_audio_wizard_device";
    private static final String SETTINGS_KEY_AUDIO_WIZARD_HEADSET_EFFECT = "settings_key_audio_wizard_headset_effect";
    private static final String SETTINGS_KEY_AUDIO_WIZARD_MODE = "settings_key_audio_wizard_mode";
    private static final String SETTINGS_KEY_AUDIO_WIZARD_OUTDOOR_MODE = "audio_wizard_outdoor_mode";

    private static final Boolean DEBUG = Boolean.valueOf(Build.TYPE.equals("userdebug"));
    private static final int CUSTOM_MODE_ONE_ID = 101;
    private static final int MODE_CLASSICAL = 4;
    private static final int MODE_DANCE = 5;
    private static final int MODE_JAZZ = 6;
    private static final int MODE_NORMAL = 1;
    private static final int MODE_POP = 7;
    private static final int MODE_ROCK = 8;
    private static final int MODE_VOCAL = 9;
    private static final int SETTINGS_AUDIO_WIZARD_OUTDOOR_MODE_OFF = 1;
    private static final int SETTINGS_AUDIO_WIZARD_OUTDOOR_MODE_OFF_PANEL = 0;
    private static final int SETTINGS_AUDIO_WIZARD_OUTDOOR_MODE_ON = 2;
    private static final int SETTINGS_DFLT_AUDIO_WIZARD_OUTDOOR_MODE = 1;
    private static final int STEREO_MODE_FRONT = 1;
    private static final int STEREO_MODE_PURE = -1;
    private static final int STEREO_MODE_TRADITIONAL = 2;
    private static final int STEREO_MODE_TURNON = 3;
    private static final int STEREO_MODE_WIDE = 0;

    private final int DEFAULT_VALUE = 0;
    private final int OUTDOOR_MODE_ON = 2;
    private int mActiveRouteNumber = -1;

    private TwoStatePreference mOutdoorMode;
    private Preference mWizard;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.audio_mode, rootKey);
        final PreferenceScreen prefScreen = getPreferenceScreen();

        mOutdoorMode = (TwoStatePreference) findPreference(KEY_OUTDOOR_MODE);
        mOutdoorMode.setChecked(Settings.System.getInt(getContext().getContentResolver(),
        SETTINGS_KEY_AUDIO_WIZARD_OUTDOOR_MODE, 0) == 2);

        mWizard = prefScreen.findPreference(KEY_AUDIOWIZARD);
        mWizard.setEnabled(isEnable());

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mOutdoorMode) {
            Settings.System.putInt(getContext().getContentResolver(), SETTINGS_KEY_AUDIO_WIZARD_OUTDOOR_MODE, mOutdoorMode.isChecked() ? 2 : 1);
            updateState();
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

    public void updateState() {
        String summaryString = getSummaryString();
        StringBuilder sb = new StringBuilder();
        sb.append("updateState summary = ");
        sb.append(summaryString);
        Log.i("AudioWizardControllerOmni", sb.toString());
        if (!TextUtils.isEmpty(summaryString)) {
            mWizard.setSummary((CharSequence) summaryString);
        } else {
            mWizard.setSummary((CharSequence) "");
        }
    }

    private boolean isEnable() {
        boolean z = false;
        try {
            ApplicationInfo service = getContext().getPackageManager().getApplicationInfo("com.asus.maxxaudio", 0);
            ApplicationInfo ui = getContext().getPackageManager().getApplicationInfo("com.asus.maxxaudio.audiowizard", 0);
            boolean serviceEnable = service.enabled;
            boolean UIenable = ui.enabled;
            if (serviceEnable && UIenable) {
                z = true;
                if (z = true) {
                    updateState();
                }
            }
            return z;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getSummaryString() {
        Resources res = getResources();
        String str;
        this.mActiveRouteNumber = System.getInt(getContext().getContentResolver(), SETTINGS_KEY_AUDIO_WIZARD_DEVICE, -1);
        StringBuilder sb = new StringBuilder();
        sb.append("getSummaeyString() mActiveRouteNumber = ");
        sb.append(this.mActiveRouteNumber);
        String str2 = "AudioWizardControllerOmni";
        Log.i(str2, sb.toString());
        String str3 = "";
        if (this.mActiveRouteNumber == -1) {
            System.putInt(getContext().getContentResolver(), SETTINGS_KEY_AUDIO_WIZARD_DEVICE, 1);
        }
        int i = System.getInt(getContext().getContentResolver(), SETTINGS_KEY_AUDIO_WIZARD_MODE, -1);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("getSummaeyString() mode = ");
        sb2.append(i);
        Log.i(str2, sb2.toString());
        //~ if (this.mActiveRouteNumber == -1) {
            //~ System.putInt(getContext().getContentResolver(), SETTINGS_KEY_AUDIO_WIZARD_MODE, 1);
        //~ }
        if (this.mActiveRouteNumber == 2) {
            if (System.getInt(getContext().getContentResolver(), SETTINGS_KEY_AUDIO_WIZARD_OUTDOOR_MODE, 1) == 2) {
                str = res.getString(R.string.outdoor_mode_title);
            } else {
                str = getModeName(i);
            }
            str3 = str;
        } else {
            int i2 = System.getInt(getContext().getContentResolver(), SETTINGS_KEY_AUDIO_WIZARD_HEADSET_EFFECT, -1);
            StringBuilder sb3 = new StringBuilder();
            sb3.append("getSummaeyString() headsetEffect = ");
            sb3.append(i2);
            Log.d(str2, sb3.toString());
            if (i2 != -1) {
                str3 = String.format(res.getString(R.string.audiowizard_summary_headset_effect_and_mode), new Object[]{getHeadsetEffectName(i2), getModeName(i)});
            }
        }
        return str3;
    }

    private String getHeadsetEffectName(int i) {
        Resources res = getResources();
        if (i == STEREO_MODE_WIDE) {
            return res.getString(R.string.audio_wide);
        }
        if (i != STEREO_MODE_FRONT) {
            return i != STEREO_MODE_TRADITIONAL ? "" : res.getString(R.string.audio_traditional);
        }
        return res.getString(R.string.audio_in_front);
    }

    private String getModeName(int mode) {
        Resources res = getResources();
        String modeName = "";
        if (mode == MODE_NORMAL) {
            return res.getString(R.string.mode_normal);
        }
        if (mode == CUSTOM_MODE_ONE_ID) {
            return res.getString(R.string.custom_name);
        }
        switch (mode) {
            case MODE_CLASSICAL:
                return res.getString(R.string.mode_classical);
            case MODE_DANCE:
                return res.getString(R.string.mode_dance);
            case MODE_JAZZ:
                return res.getString(R.string.mode_jazz);
            case MODE_POP:
                return res.getString(R.string.mode_pop);
            case MODE_ROCK:
                return res.getString(R.string.mode_rock);
            case MODE_VOCAL:
                return res.getString(R.string.mode_vocal);
            default:
                return modeName;
        }
    }
}
