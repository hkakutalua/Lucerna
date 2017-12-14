package com.bitpapr.lucerna.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by henrick on 12/13/17.
 */

public class UserSharedPreferences {

    private static final String PREF_USER_PROFILE_IS_CONFIGURED = "PREF_USER_PROFILE_IS_CONFIGURED";

    private Context mContext;

    public UserSharedPreferences(Context context) {
        mContext = context;
    }

    public void setUserProfileConfigured(boolean configured) {
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(PREF_USER_PROFILE_IS_CONFIGURED, configured);
        editor.apply();
    }

    public boolean getUserProfileConfigured() {
        return PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean(PREF_USER_PROFILE_IS_CONFIGURED, false);
    }

    private SharedPreferences.Editor getEditor() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).edit();
    }
}
