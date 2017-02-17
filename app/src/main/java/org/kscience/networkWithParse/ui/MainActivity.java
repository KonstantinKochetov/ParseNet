package org.kscience.networkWithParse.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseAnalytics;

import com.parse.ParseUser;

import org.kscience.networkWithParse.utils.ParseConstants;
import org.kscience.networkWithParse.R;
import org.kscience.networkWithParse.adapters.SectionsPagerAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int RECORD_VIDEO_REQUEST = 1;
    public static final int PICK_PHOTO_REQUEST = 2;
    public static final int PICK_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;


    public static final int FILE_SIZE_LIMIT = 1024*1024*10;

    protected Uri mMediaUri;

    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0:

                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    if(mMediaUri==null) {

                        Toast.makeText(MainActivity.this, R.string.error_external_storage, Toast.LENGTH_LONG).show();
                    } else {
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);

                        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
                    }
                    break;

                case 1:
                    Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    if(mMediaUri==null) {

                        Toast.makeText(MainActivity.this, R.string.error_external_storage, Toast.LENGTH_LONG).show();
                    } else {
                        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                        videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                        videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                        startActivityForResult(videoIntent, RECORD_VIDEO_REQUEST);
                    }
                    break;
                case 2:
                    Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);

                    choosePhotoIntent.setType("image/*");
                    startActivityForResult(choosePhotoIntent, PICK_PHOTO_REQUEST);
                    break;
                case 3:
                    Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseVideoIntent.setType("video/*");

                    Toast.makeText(MainActivity.this, R.string.video_file_size_warning, Toast.LENGTH_LONG).show();
                    startActivityForResult(chooseVideoIntent, PICK_VIDEO_REQUEST);
                    break;
            }
        }

        private Uri getOutputMediaFileUri(int mediaType) {
            if (isExternalStorageAvailable()) {
                String appName = MainActivity.this.getString(R.string.app_name);
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName);
                if (!mediaStorageDir.exists()) {
                    if(!mediaStorageDir.mkdirs()) {
                        Log.e(TAG, "Failed to create directory");
                        return null;
                    }
                }

                File mediaFile;
                Date now = new Date();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHss", Locale.US).format(now);
                String path = mediaStorageDir.getPath() + File.separator;
                if (mediaType == MEDIA_TYPE_IMAGE) {
                    mediaFile = new File (path + "IMG_" + timestamp + ".jpg");
                } else if (mediaType == MEDIA_TYPE_VIDEO) {
                    mediaFile = new File (path + "VID_" + timestamp + ".mp4");
                } else {
                    return null;
                }
                Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

                return Uri.fromFile(mediaFile);
            } else {
                return null;
            }
        }

        private boolean isExternalStorageAvailable() {
            String state = Environment.getExternalStorageState();

            if (state.equals(Environment.MEDIA_MOUNTED)) {
                return true;
            } else {
                return false;
            }
        }
    };

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();
        } else {
            Log.i(TAG, currentUser.getUsername());
        }

        final ActionBar actionBar = getSupportActionBar();
        //icon in the action bar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.mipmap.background_texture));

        TextView TextViewNewFont = new TextView(MainActivity.this);
        RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        TextViewNewFont.setLayoutParams(layoutparams);
        TextViewNewFont.setText("NetParse");
        TextViewNewFont.setTextColor(Color.WHITE);
        TextViewNewFont.setTextSize(27);
        // Assests folder font folder path
        String font = "GreatVibes-Regular.ttf";
        // Load Typeface font url String ExternalFontPath
        Typeface FontLoaderTypeface = Typeface.createFromAsset(getAssets(), font);
        TextViewNewFont.setTypeface(FontLoaderTypeface);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(TextViewNewFont);

        mSectionsPagerAdapter = new SectionsPagerAdapter(this,getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon(mSectionsPagerAdapter.getIcon(i))/*setting icon for word Inbox or Friends*/
                            .setTabListener(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST) {
                if (data == null) {
                    Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
                }
                else {
                    mMediaUri = data.getData();
                }
                Log.i(TAG, "Media URI: " + mMediaUri);
                if (requestCode == PICK_VIDEO_REQUEST) {
                    int fileSize = 0;
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    }
                    catch (FileNotFoundException e) {
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                        return;
                    }
                    catch (IOException e) {
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();

                        return;
                    }
                    finally {
                        try {
                            inputStream.close();
                        } catch (IOException e) { /*Leaving blank cuz we don't need to handle the exception*/}
                    }
                    if (fileSize >= FILE_SIZE_LIMIT) {
                        Toast.makeText(this, R.string.error_file_size_too_large, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }
            Intent recipientsIntent = new Intent(this, RecipientsActivity.class);
            recipientsIntent.setData(mMediaUri);

            String fileType;
            if (requestCode == PICK_PHOTO_REQUEST || requestCode == TAKE_PHOTO_REQUEST) {
                fileType = ParseConstants.TYPE_IMAGE;
            }
            else {
                fileType = ParseConstants.TYPE_VIDEO;
            }
            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
            startActivity(recipientsIntent);
        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }
    }
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
//TODO:below
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMediaUri!=null) {
        String Uri = mMediaUri.toString();
        outState.putString("Uri", Uri);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mMediaUri!=null) {
            String Uri = savedInstanceState.getString("Uri");
            mMediaUri = mMediaUri.parse(Uri);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();
        switch(id) {
            case R.id.action_logout:
                    ParseUser.logOut();
                    navigateToLogin();
                    break;
            case R.id.action_edit_friends:
                    Intent intent = new Intent(this, EditFriendsActivity.class);
                    startActivity(intent);
                    break;

            case R.id.action_camera:
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setItems(R.array.camera_choices, mDialogListener);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

}
