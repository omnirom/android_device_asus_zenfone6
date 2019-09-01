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
import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
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
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class AudioSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener, LifecycleObserver/*, OnPause, OnResume*/ {

    public static final String KEY_SETTINGS_PREFIX = "device_setting_";
    public static final String KEY_OUTDOOR_MODE = "outdoor_mode";
    public static final String URI_FOR_OUTDOOR_MODE = "audio_wizard_outdoor_mode";
    private static final Boolean DEBUG = Boolean.valueOf(Build.TYPE.equals("userdebug"));

    private final int DEFAULT_VALUE = 0;
    private final int OUTDOOR_MODE_ON = 2;
    private int mActiveRouteNumber = -1;

    private Context mContext = null;
    private Preference mPreference;
    private SettingObserver mSettingObserver;
    private TwoStatePreference mOutdoorMode;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.audio_mode, rootKey);

        mOutdoorMode = (TwoStatePreference) findPreference(KEY_OUTDOOR_MODE);
        mOutdoorMode.setChecked(Settings.System.getInt(getContext().getContentResolver(),
        URI_FOR_OUTDOOR_MODE, DEFAULT_VALUE) == OUTDOOR_MODE_ON);

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mOutdoorMode) {
            Settings.System.putInt(getContext().getContentResolver(), URI_FOR_OUTDOOR_MODE, mOutdoorMode.isChecked() ? OUTDOOR_MODE_ON : DEFAULT_VALUE);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

    class SettingObserver extends ContentObserver {
        Uri deviceUri = System.getUriFor("settings_key_audio_wizard_device");
        Uri headsetEffectUri = System.getUriFor("settings_key_audio_wizard_headset_effect");
        Uri modeUri = System.getUriFor("settings_key_audio_wizard_mode");
        Uri ourdoorUri = System.getUriFor("audio_wizard_outdoor_mode");

        public SettingObserver() {
            super(new Handler());
        }

        public void register(ContentResolver cr) {
            cr.registerContentObserver(this.deviceUri, false, this);
            cr.registerContentObserver(this.ourdoorUri, false, this);
            cr.registerContentObserver(this.modeUri, false, this);
            cr.registerContentObserver(this.headsetEffectUri, false, this);
        }

        public void unregister(ContentResolver cr) {
            cr.unregisterContentObserver(this);
        }

        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.i("AudioSettings", "onChange");
            AudioSettings.this.updateState(AudioSettings.this.mPreference);
        }
    }

    /*public AudioSettings(Context context, Lifecycle lifecycle) {
        this.mSettingObserver = new SettingObserver();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }*/

    /*public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        this.mPreference = screen.findPreference(getPreferenceKey());
    }*/

    public void updateState(Preference preference) {
        if (isEnable()) {
            preference.setEnabled(true);
        } else {
            preference.setEnabled(false);
        }
        CharSequence summary = getSummaryString();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("updateState summary = ");
        stringBuilder.append(summary);
        Log.i("AudioSettings", stringBuilder.toString());
        if (TextUtils.isEmpty(summary)) {
            preference.setSummary((CharSequence) "");
        } else {
            preference.setSummary(summary);
        }
    }

    public boolean isAvailable() {
        return hasAudioWizard();
    }

    public String getPreferenceKey() {
        return "audiowizard_entry";
    }

    private boolean hasAudioWizard() {
        boolean result = false;
        try {
            result = this.mContext.getPackageManager().getApplicationInfo("com.asus.maxxaudio.audiowizard", 0) != null;
        } catch (NameNotFoundException e) {
            if (DEBUG.booleanValue()) {
                Log.w("AudioSettings", " AudioWizard cannot be found!!! (maybe it is not installed)");
                result = false;
            }
        } catch (Exception e2) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(" ERROR: Unknown ERROR!!! (error=");
            stringBuilder.append(e2.getMessage());
            stringBuilder.append(") ...");
            Log.e("AudioSettings", stringBuilder.toString(), e2);
            result = false;
        }
        if (getSystemPropertieInt("ro.vendor.asus.aw.settingentry", 0) == 0) {
            return false;
        }
        return result;
    }

    public static int getSystemPropertieInt(String inputPropName, int defaultValue) {
        int ret;
        if (SystemProperties.getInt(inputPropName, -500) == -500) {
            ret = SystemProperties.getInt(inputPropName.replace(".vendor", ""), defaultValue);
        } else {
            ret = SystemProperties.getInt(inputPropName, defaultValue);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getSystemPropertieInt(), return ret = ");
        stringBuilder.append(ret);
        Log.d("AsusTelephonyUtils", stringBuilder.toString());
        return ret;
    }

    private boolean isEnable() {
        boolean z = false;
        try {
            ApplicationInfo service = this.mContext.getPackageManager().getApplicationInfo("com.asus.maxxaudio", 0);
            ApplicationInfo ui = this.mContext.getPackageManager().getApplicationInfo("com.asus.maxxaudio.audiowizard", 0);
            boolean serviceEnable = service.enabled;
            boolean UIenable = ui.enabled;
            if (serviceEnable && UIenable) {
                z = true;
            }
            return z;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //~ public void onResume() {
        //~ if (this.mSettingObserver != null) {
            //~ this.mSettingObserver.register(this.mContext.getContentResolver());
        //~ }
    //~ }

    //~ public void onPause() {
        //~ if (this.mSettingObserver != null) {
            //~ this.mSettingObserver.unregister(this.mContext.getContentResolver());
        //~ }
    //~ }

    private String getSummaryString() {
        String summary = "";
        this.mActiveRouteNumber = System.getInt(this.mContext.getContentResolver(), "settings_key_audio_wizard_device", -1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getSummaeyString() mActiveRouteNumber = ");
        stringBuilder.append(this.mActiveRouteNumber);
        Log.i("AudioSettings", stringBuilder.toString());
        if (this.mActiveRouteNumber == -1) {
            Log.i("AudioSettings", "getSummaeyString() active route is unknown, return");
            return summary;
        }
        int mode = System.getInt(this.mContext.getContentResolver(), "settings_key_audio_wizard_mode", -1);
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("getSummaeyString() mode = ");
        stringBuilder2.append(mode);
        Log.i("AudioSettings", stringBuilder2.toString());
        if (this.mActiveRouteNumber != 2) {
            int headsetEffect = System.getInt(this.mContext.getContentResolver(), "settings_key_audio_wizard_headset_effect", -1);
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("getSummaeyString() headsetEffect = ");
            stringBuilder3.append(headsetEffect);
            Log.d("AudioSettings", stringBuilder3.toString());
            if (headsetEffect != -1) {
                summary = String.format(this.mContext.getResources().getString(R.string.audiowizard_summary_headset_effect_and_mode), new Object[]{getHeadsetEffectName(headsetEffect), getModeName(mode)});
            }
        } else if (System.getInt(this.mContext.getContentResolver(), "audio_wizard_outdoor_mode", 1) == 2) {
            summary = this.mContext.getString(R.string.outdoor_mode_title);
        } else {
            summary = getModeName(mode);
        }
        return summary;
    }

    private String getHeadsetEffectName(int effect) {
        String headsetEffectName = "";
        switch (effect) {
            case 0:
                return this.mContext.getString(R.string.audio_wide);
            case 1:
                return this.mContext.getString(R.string.audio_in_front);
            case 2:
                return this.mContext.getString(R.string.audio_traditional);
            default:
                return headsetEffectName;
        }
    }

    private String getModeName(int mode) {
        String modeName = "";
        if (mode == 1) {
            return this.mContext.getString(R.string.mode_normal);
        }
        if (mode == 101) {
            return this.mContext.getString(R.string.custom_name);
        }
        switch (mode) {
            case 4:
                return this.mContext.getString(R.string.mode_classical);
            case 5:
                return this.mContext.getString(R.string.mode_dance);
            case 6:
                return this.mContext.getString(R.string.mode_jazz);
            case 7:
                return this.mContext.getString(R.string.mode_pop);
            case 8:
                return this.mContext.getString(R.string.mode_rock);
            case 9:
                return this.mContext.getString(R.string.mode_vocal);
            default:
                return modeName;
        }
    }
}
