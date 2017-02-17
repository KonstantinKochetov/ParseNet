package org.kscience.networkWithParse.ui;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.kscience.networkWithParse.adapters.UserAdapter;
import org.kscience.networkWithParse.utils.ParseConstants;
import org.kscience.networkWithParse.R;

import java.util.List;

public class FriendsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected ParseRelation<ParseUser> mFriendRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    protected GridView mGridView;

    public static FriendsFragment newInstance(int sectionNumber) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false);
        mGridView = (GridView)rootView.findViewById(R.id.friendsGrid);
        TextView emptyTextView = (TextView)rootView.findViewById(android.R.id.empty);
        mGridView.setEmptyView(emptyTextView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = mFriendRelation.getQuery();

        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                if (e == null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    if (mGridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    } else {//if adapter already exists, then refill
                        ((UserAdapter)mGridView.getAdapter()).refill(mFriends);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
        TextView empty = (TextView) getView().findViewById(android.R.id.empty);
        empty.setText("Add friends with menu button!:)");
        empty.setTextColor(Color.WHITE);
        empty.setTextSize(35);
        Typeface myTypefaceTitle = Typeface.createFromAsset(getActivity().getAssets(), "GreatVibes-Regular.ttf");
        empty.setTypeface(myTypefaceTitle);
        empty.setGravity(Gravity.CENTER);
    }
}
