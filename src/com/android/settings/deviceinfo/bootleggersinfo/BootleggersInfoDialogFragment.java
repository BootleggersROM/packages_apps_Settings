/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.android.settings.deviceinfo.bootleggersinfo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.SELinux;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.wrapper.PackageManagerWrapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BootleggersInfoDialogFragment  extends InstrumentedDialogFragment {

    private static final String TAG = "bootlegersInfoDialog";
    static final String BOOTLEGGERS_VERSION = "ro.bootleggers.version";
    static final String BOOTLEGGERS_MAINTAINER = "ro.bootleggers.maintainer";
    static final String BOOTLEGGERS_RELEASETYPE = "ro.bootleggers.releasetype";
    static final String BOOTLEGGERS_MUSICODENAME = "ro.bootleggers.songcodename";
    static final String BOOTLEGGERS_MUSICODENAME_URL = "ro.bootleggers.songcodeurl";
    static final String PROPERTY_SELINUX_STATUS = "ro.boot.selinux";
    static final int BOOTLEGGERS_VERSION_TITLE = R.id.bootleggers_buildzip_label;
    static final int BOOTLEGGERS_VERSION_PREFERENCE = R.id.bootleggers_buildzip_value;
    static final int BOOTLEGGERS_DATE_TITLE = R.id.bootleggers_build_date;
    static final int BOOTLEGGERS_DATE_PREFERENCE = R.id.bootleggers_build_value;
    static final int BOOTLEGGERS_MAINTAINER_TITLE = R.id.bootleggers_maintainer_label;
    static final int BOOTLEGGERS_MAINTAINER_PREFERENCE = R.id.bootleggers_maintainer_value;
    static final int BOOTLEGGERS_RELEASE_TITLE = R.id.bootleggers_release_type;
    static final int BOOTLEGGERS_RELEASE_PREFERENCE = R.id.bootleggers_release_value;
    static final int BOOTLEGGERS_MUSICODENAME_TITLE = R.id.bootleggers_musicodename;
    static final int BOOTLEGGERS_MUSICODENAME_PREFERENCE = R.id.bootleggers_musicodename_value;
    static final int SELINUX_STATUS_TITLE = R.id.selinux_status;
    static final int SELINUX_STATUS_PREFERENCE = R.id.selinux_status_value;
    private static final Uri INTENT_BOOTLEG_MUSICODE = Uri.parse(SystemProperties.get(BOOTLEGGERS_MUSICODENAME_URL));


    private View mRootView;
    private String bootleggersVersion;
    private String bootleggersDate;
    private String bootleggersMaintainer;
    private String bootleggersRelease;
    private String bootleggersMusiCodename;
    private String selinuxStatus;

    public static void show(Fragment host) {
        final FragmentManager manager = host.getChildFragmentManager();
        if (manager.findFragmentByTag(TAG) == null) {
            final BootleggersInfoDialogFragment dialog = new BootleggersInfoDialogFragment();
            dialog.show(manager, TAG);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.DIALOG_FIRMWARE_VERSION;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.about_bootleg_title)
                .setPositiveButton(android.R.string.ok, null /* listener */);

        mRootView = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_bootleggers_info, null /* parent */);

        TextView musicode = (TextView) mRootView.findViewById(BOOTLEGGERS_MUSICODENAME_PREFERENCE);
        musicode.setOnClickListener(songUrlClickListener);

        initializeSystemProperties();

        showPreferenceWhenAvaliable(BOOTLEGGERS_VERSION_TITLE,BOOTLEGGERS_VERSION_PREFERENCE, bootleggersVersion);
        showPreferenceWhenAvaliable(BOOTLEGGERS_DATE_TITLE,BOOTLEGGERS_DATE_PREFERENCE, bootleggersDate);
        showPreferenceWhenAvaliable(BOOTLEGGERS_MAINTAINER_TITLE,BOOTLEGGERS_MAINTAINER_PREFERENCE, bootleggersMaintainer);
        showPreferenceWhenAvaliable(BOOTLEGGERS_RELEASE_TITLE,BOOTLEGGERS_RELEASE_PREFERENCE, bootleggersRelease);
        showPreferenceWhenAvaliable(BOOTLEGGERS_MUSICODENAME_TITLE,BOOTLEGGERS_MUSICODENAME_PREFERENCE, bootleggersMusiCodename);
        showPreferenceWhenAvaliable(SELINUX_STATUS_TITLE,SELINUX_STATUS_PREFERENCE, selinuxStatus);
        return builder.setView(mRootView).create();
    }

    public void setText(int viewId, String text) {
        final TextView view = mRootView.findViewById(viewId);
        if (view != null) {
            view.setText(text);
        }
    }

    public void removeSettingFromScreen(int viewId) {
        final View view = mRootView.findViewById(viewId);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

	View.OnClickListener songUrlClickListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
        PackageManagerWrapper mPackageManager = new PackageManagerWrapper(getContext().getPackageManager());
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(INTENT_BOOTLEG_MUSICODE);
        if (mPackageManager.queryIntentActivities(intent, 0).isEmpty()) {
            // Don't send out the intent to stop crash
            Log.w(TAG, "Stop click action on " + BOOTLEGGERS_MUSICODENAME_PREFERENCE + ": "
                    + "queryIntentActivities() returns empty");
            return;
        }
        getContext().startActivity(intent);
		}
	};

    public void showPreferenceWhenAvaliable (int viewTitleId, int viewId, String text) {
        if (!text.isEmpty()) {
            setText(viewId,text);
        } else {
            removeSettingFromScreen(viewId);
            removeSettingFromScreen(viewTitleId);
        }
    }

    public void registerClickListener(int viewId, View.OnClickListener listener) {
        final View view = mRootView.findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    private void initializeSystemProperties() {
        // Time to get the strings
        bootleggersVersion = SystemProperties.get(BOOTLEGGERS_VERSION);
        bootleggersMaintainer = SystemProperties.get(BOOTLEGGERS_MAINTAINER);

        // Build type case for the explaining situation
        Resources res = getResources();
        String buildType = SystemProperties.get(BOOTLEGGERS_RELEASETYPE);
        switch (buildType) {
            case "Shishufied":
                bootleggersRelease = res.getString(R.string.bootleggers_releasetype_skeleton, buildType, res.getString(R.string.bootleg_type_official));
                break;

            case "Unshishufied":
                bootleggersRelease = res.getString(R.string.bootleggers_releasetype_skeleton, buildType, res.getString(R.string.bootleg_type_unofficial));
                break;

            default:
                bootleggersRelease = res.getString(R.string.bootleggers_releasetype_skeleton, buildType, res.getString(R.string.bootleg_type_unknown));
                break;
        }

        //Preparing the date string, taken from com.android.settingslib.DeviceInfoUtils
        String zipbuildate = bootleggersVersion.substring(bootleggersVersion.length() - 15);
        if (!zipbuildate.isEmpty()) {
            try {
                SimpleDateFormat template = new SimpleDateFormat("yyyyMMdd");
                Date buildDate = template.parse(zipbuildate);
                String format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMMMyyyy");
                bootleggersDate = DateFormat.format(format, buildDate).toString();
            } catch (ParseException e) {
                // broken parse; fall through and use the raw string
            }
        }

        //SELinux status check, taken from SELinuxPreferenceController.java
        String selinuxStatusProp =  SystemProperties.get(PROPERTY_SELINUX_STATUS);
        if (selinuxStatusProp != null || SELinux.isSELinuxEnabled()) {
            if (SELinux.isSELinuxEnforced()) {
                selinuxStatus = res.getString(R.string.selinux_status_enforcing);
            } else if (!SELinux.isSELinuxEnforced()) {
                selinuxStatus = res.getString(R.string.selinux_status_permissive);
            } else {
                selinuxStatus = res.getString(R.string.selinux_status_disabled);
            }
        }

        //Musical Codename method to use spacing, as it's a dumb thing and additional props doesn't have support for spaces. So, let's do a method to parse text
        String musicodenameProp = SystemProperties.get(BOOTLEGGERS_MUSICODENAME);
        if (musicodenameProp.contains("niceparse")) {
            String musicFinal = musicodenameProp.substring(musicodenameProp.lastIndexOf(".") + 1);
            bootleggersMusiCodename = musicFinal.replace('_',' ');
        } else {
            //Fallback musical codename
            bootleggersMusiCodename = musicodenameProp;
        }
    }
}
