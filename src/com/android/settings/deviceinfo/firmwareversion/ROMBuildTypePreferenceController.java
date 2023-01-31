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
import android.text.TextUtils;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class ROMBuildTypePreferenceController  extends BasePreferenceController {

    private static final String TAG = "ROMBuildTypePreferenceController";
    private static final String BOOTLEGGERS_RELEASETYPE = "ro.bootleggers.releasetype";

    public ROMBuildTypePreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        String buildType = SystemProperties.get(BOOTLEGGERS_RELEASETYPE);
        String bootlegReleaseType = buildType.split("-")[0];
        switch (bootlegReleaseType) {
            case "Shishufied":
                return mContext.getString(R.string.bootleggers_releasetype_skeleton, buildType, mContext.getString(R.string.bootleg_type_official));

            case "Unshishufied":
                return mContext.getString(R.string.bootleggers_releasetype_skeleton, buildType, mContext.getString(R.string.bootleg_type_unofficial));

            default:
                return mContext.getString(R.string.bootleggers_releasetype_skeleton, buildType, mContext.getString(R.string.bootleg_type_unknown));
        }
    }
}

