/*
 * Copyright (C) 2019 The LineageOS Project
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

package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.os.SystemProperties;
import android.text.format.DateFormat;
import android.text.TextUtils;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ROMBuildDatePreferenceController  extends BasePreferenceController {

    private static final String TAG = "ROMBuildDatePreferenceController";
    private static final String BOOTLEGGERS_VERSION = "ro.bootleggers.version";

    public ROMBuildDatePreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        //Preparing the date string, taken from com.android.settingslib.DeviceInfoUtils
        String bootleggersVersion = SystemProperties.get(BOOTLEGGERS_VERSION);
        String zipbuildate = bootleggersVersion.substring(bootleggersVersion.length() - 15);
        if (!zipbuildate.isEmpty()) {
            try {
                SimpleDateFormat template = new SimpleDateFormat("yyyyMMdd");
                Date buildDate = template.parse(zipbuildate);
                String format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMMMyyyy");
                return DateFormat.format(format, buildDate).toString();
            } catch (ParseException e) {
                return mContext.getString(R.string.unknown);
            }
        }
        return mContext.getString(R.string.unknown);
    }
}

