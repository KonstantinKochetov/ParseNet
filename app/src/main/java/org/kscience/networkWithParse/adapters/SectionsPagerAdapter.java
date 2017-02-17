package org.kscience.networkWithParse.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.kscience.networkWithParse.R;
import org.kscience.networkWithParse.ui.FriendsFragment;
import org.kscience.networkWithParse.ui.InboxFragment;

import java.util.Locale;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;
    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return InboxFragment.newInstance(position);
            case 1:
                return FriendsFragment.newInstance(position);
        }
        return null;

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
        }
        return null;
    }

    public int getIcon(int position) {
        switch (position) {
            case 0:/*inboxTab*/
                return R.mipmap.ic_mail_outline_white_24dp;
            case 1:/*FriendsTab*/
                return R.mipmap.ic_face_white_24dp;
        }
        return R.mipmap.ic_mail_outline_white_24dp;/*default (will not be used), fail safe catch*/
    }
}
