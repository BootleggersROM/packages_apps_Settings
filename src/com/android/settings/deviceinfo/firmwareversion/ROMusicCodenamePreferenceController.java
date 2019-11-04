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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;

public class ROMusicCodenamePreferenceController  extends BasePreferenceController {

    private static final String TAG = "ROMusicCodenamePreferenceController";
    private static final String BOOTLEGGERS_MUSICODENAME = "ro.bootleggers.songcodename";
    private static final String BOOTLEGGERS_MUSICODENAME_URL = "ro.bootleggers.songcodeurl";
    private static final Uri INTENT_BOOTLEG_MUSICODE = Uri.parse(SystemProperties.get(BOOTLEGGERS_MUSICODENAME_URL));

    public ROMusicCodenamePreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        //Musical Codename method to use spacing, as it's a dumb thing and additional props doesn't have support for spaces. So, let's do a method to parse text
        String musicodenameProp = SystemProperties.get(BOOTLEGGERS_MUSICODENAME);
        if (musicodenameProp.contains("niceparse")) {
            String musicFinal = musicodenameProp.substring(musicodenameProp.lastIndexOf(".") + 1);
            return musicFinal.replace('_',' ');
        } else {
            //Fallback musical codename
            return musicodenameProp;
        }
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        if (Utils.isMonkeyRunning()) {
            return false;
        }
        PackageManager mPackageManager = mContext.getPackageManager();
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(INTENT_BOOTLEG_MUSICODE);
        if (mPackageManager.queryIntentActivities(intent, 0).isEmpty()) {
            // Don't send out the intent to stop crash
            Log.w(TAG, "queryIntentActivities() returns empty");
            return true;
        }
        mContext.startActivity(intent);
        return true;
    }
}

