/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.android.settings.display;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;

import com.gzr.wolvesden.preference.CustomSeekBarPreference;
import com.android.settings.core.PreferenceController;
import com.android.settings.core.instrumentation.MetricsFeatureProvider;
import com.android.settings.overlay.FeatureFactory;

import static com.android.internal.logging.nano.MetricsProto.MetricsEvent.ACTION_AMBIENT_DISPLAY;

public class DozeAutoBrightnessPreferenceController extends PreferenceController implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_AMBIENT_DOZE_AUTO_BRIGHTNESS = "ambient_doze_auto_brightness";
    private static final String KEY_AMBIENT_DOZE_CUSTOM_BRIGHTNESS = "ambient_doze_custom_brightness";

    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private PreferenceScreen mScreen;

    public DozeAutoBrightnessPreferenceController(Context context) {
        super(context);
        mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override
    public String getPreferenceKey() {
        return KEY_AMBIENT_DOZE_AUTO_BRIGHTNESS;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (KEY_AMBIENT_DOZE_AUTO_BRIGHTNESS.equals(preference.getKey())) {
            mMetricsFeatureProvider.action(mContext, ACTION_AMBIENT_DISPLAY);
        }
        return false;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        mScreen = screen;
        super.displayPreference(screen);
    }

    @Override
    public void updateState(Preference preference) {
        SwitchPreference mAmbientDozeAutoBrightness =
                (SwitchPreference) preference;
        boolean defaultAmbientDozeAutoBrighthness = mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_allowAutoBrightnessWhileDozing);
        boolean isAmbientDozeAutoBrighthness = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.AMBIENT_DOZE_AUTO_BRIGHTNESS, defaultAmbientDozeAutoBrighthness ? 1 : 0,
                UserHandle.USER_CURRENT) == 1;
        mAmbientDozeAutoBrightness.setChecked(isAmbientDozeAutoBrighthness);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value = (Boolean) newValue;
        Settings.System.putIntForUser(mContext.getContentResolver(),
                Settings.System.AMBIENT_DOZE_AUTO_BRIGHTNESS, value ? 1 : 0, UserHandle.USER_CURRENT);

        //disable the option if auto brightness on doze is enabled
        CustomSeekBarPreference ambientDozeCustomBrightness =
                (CustomSeekBarPreference) mScreen.findPreference(KEY_AMBIENT_DOZE_CUSTOM_BRIGHTNESS);
        if (ambientDozeCustomBrightness != null)
                ambientDozeCustomBrightness.setEnabled(!value);
        return true;
    }

    @Override
    public boolean isAvailable() {
        String name = Build.IS_DEBUGGABLE ? SystemProperties.get("debug.doze.component") : null;
        if (TextUtils.isEmpty(name)) {
            name = mContext.getResources().getString(
                    com.android.internal.R.string.config_dozeComponent);
        }
        return !TextUtils.isEmpty(name);
    }
}
