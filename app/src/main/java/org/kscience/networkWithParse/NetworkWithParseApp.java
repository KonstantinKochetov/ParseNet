package org.kscience.networkWithParse;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import org.kscience.networkWithParse.utils.ParseConstants;

public class NetworkWithParseApp extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
    //need this for push notifications
    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }

}

