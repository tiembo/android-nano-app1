package org.songfamily.tiem.nanodegree.app1.helpers;

import android.content.Context;
import android.preference.PreferenceManager;

import org.songfamily.tiem.nanodegree.app1.R;

public class MySharedPrefs {
    public static boolean getShowOngoingNotifications(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_notifcation_controls),
                        Boolean.parseBoolean(context.getString(R.string.pref_country_code_default)));
    }

    public static String getCountryCode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.pref_country_code),
                        context.getString(R.string.pref_country_code_default));
    }
}
