package org.kscience.networkWithParse.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.kscience.networkWithParse.adapters.MessageAdapter;
import org.kscience.networkWithParse.utils.ParseConstants;
import org.kscience.networkWithParse.R;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends ListFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected TextView mEmpty;

    public static InboxFragment newInstance(int sectionNumber) {
        InboxFragment fragment = new InboxFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public InboxFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        /*for refreshing a fragment*/
        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipeRefresh1, R.color.swipeRefresh2, R.color.swipeRefresh3, R.color.swipeRefresh4);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView empty = (TextView) this.getListView().getEmptyView();
        empty.setText("You don't have any messages yet");
        empty.setTextColor(Color.WHITE);
        empty.setTextSize(35);
        Typeface myTypefaceTitle = Typeface.createFromAsset(getActivity().getAssets(), "GreatVibes-Regular.ttf");
        empty.setTypeface(myTypefaceTitle);
        empty.setGravity(Gravity.CENTER);
        retrieveMessages();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

       ParseObject message =  mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);

        Uri fileUri = Uri.parse(file.getUrl());

        if (messageType.equals(ParseConstants.TYPE_IMAGE)) {

            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);

        } else {

            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }
        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);

        if(ids.size() == 1) {
            message.deleteInBackground();
        } else {
            ids.remove(ParseUser.getCurrentUser().getObjectId());
            ArrayList<String> idsToRemove = new ArrayList<>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
            message.saveInBackground();
        }
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            retrieveMessages();
        }
    };

    private void retrieveMessages() {
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {

                //for refresh
                if (mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if(e==null) {
                    mMessages = messages;
                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        i++;
                    }
                    if(getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(getActivity(), mMessages);
                        setListAdapter(adapter);
                    } else {
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }
                }

            }
        });
    }

}
