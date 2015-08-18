package org.songfamily.tiem.nanodegree.app1.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPrefs {
    private static final String SHOW_ONGOING_NOTIFICATIONS = "SHOW_ONGOING_NOTIFICATIONS";
    private static final String COUNTRY_CODE = "COUNTRY_CODE";

    private static final String DEFAULT_COUNTRY_CODE = "US";

    public static void setShowOngoingNotifications(Context context, boolean b) {
        getSharedPrefs(context)
                .edit()
                .putBoolean(SHOW_ONGOING_NOTIFICATIONS, b)
                .apply();
    }

    public static boolean getShowOngoingNotifications(Context context) {
        return getSharedPrefs(context).getBoolean(SHOW_ONGOING_NOTIFICATIONS, true);
    }

    public static void setCountryCode(Context context, String countryCode) {
        getSharedPrefs(context)
                .edit()
                .putString(COUNTRY_CODE, countryCode)
                .apply();
    }

    public static String getCountryCode(Context context) {
        return getSharedPrefs(context).getString(COUNTRY_CODE, DEFAULT_COUNTRY_CODE);
    }

    private static SharedPreferences getSharedPrefs(Context context) {
        return ((Activity) context).getPreferences(Context.MODE_PRIVATE);
    }
}
