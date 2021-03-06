package com.visualize;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.pm.PackageManager;
import com.visualize.AudioVisuals.AudioInputReader;
import com.visualize.AudioVisuals.VisualizerView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import static android.support.v7.preference.R.attr.key;


public class VisualizerActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88;
    private VisualizerView mVisualizerView;
    private AudioInputReader mAudioInputReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        mVisualizerView = (VisualizerView) findViewById(R.id.activity_visualizer);
        //defaultSetup();
        setupSharedPreferences();
        setupPermissions();
    }

    /**
     * //My technique to change the settings smoothly without using sharedPreference change listener
     *
     * @Override protected void onPostResume() {
     * setupSharedPreferences();
     * super.onPostResume();
     * }
     **/
    private void setupSharedPreferences() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mVisualizerView.setShowBass(sharedPreferences
                .getBoolean(getString(R.string.pref_show_bass_key), getResources().getBoolean(R.bool.pref_show_bass_default)));
        // mVisualizerView.setShowBass(true);
        mVisualizerView.setShowMid(sharedPreferences
                .getBoolean(getString(R.string.pref_show_mid_range_key), getResources().getBoolean(R.bool.pref_show_mid_range_default)));
        mVisualizerView.setShowTreble(sharedPreferences
                .getBoolean(getString(R.string.pref_show_treble_key),getResources().getBoolean(R.bool.pref_show_treble_default)));
        mVisualizerView.setMinSizeScale(1);
        mVisualizerView.setColor(getString(R.string.pref_color_red_value));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void loadColorFromPreferences(SharedPreferences sharedPreferences)
    {
        mVisualizerView.setColor(sharedPreferences.getString(getString(R.string.pref_color_key),getString(R.string.pref_color_red_value)));
    }
    /**
     * Below this point is code you do not need to modify; it deals with permissions
     * and starting/cleaning up the AudioInputReader
     **/

    /**
     * onPause Cleanup audio stream
     **/
    @Override
    protected void onPause() {
        super.onPause();
        if (mAudioInputReader != null) {
            mAudioInputReader.shutdown(isFinishing());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAudioInputReader != null) {
            mAudioInputReader.restart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.visualizer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * App Permissions for Audio
     **/
    private void setupPermissions() {
        // If we don't have the record audio permission...
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // And if we're on SDK M or later...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Ask again, nicely, for the permissions.
                String[] permissionsWeNeed = new String[]{Manifest.permission.RECORD_AUDIO};
                requestPermissions(permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE);
            }
        } else {
            // Otherwise, permissions were granted and we are ready to go!
            mAudioInputReader = new AudioInputReader(mVisualizerView, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission was granted! Start up the visualizer!
                    mAudioInputReader = new AudioInputReader(mVisualizerView, this);

                } else {
                    Toast.makeText(this, "Permission for audio not granted. Visualizer can't run.", Toast.LENGTH_LONG).show();
                    finish();
                    // The permission was denied, so we can show a message why we can't run the app
                    // and then close the app.
                }
            }
            // Other permissions could go down here

        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.pref_show_bass_key)))
        {
            mVisualizerView.setShowBass(sharedPreferences.getBoolean(s,getResources().getBoolean(R.bool.pref_show_bass_default)));
        }
       else if (s.equals(getString(R.string.pref_show_mid_range_key)))
        {
            mVisualizerView.setShowMid(sharedPreferences.getBoolean(s,getResources().getBoolean(R.bool.pref_show_mid_range_default)));
        }
        else if (s.equals(getString(R.string.pref_show_treble_key)))
        {
            mVisualizerView.setShowTreble(sharedPreferences.getBoolean(s,getResources().getBoolean(R.bool.pref_show_treble_default)));
        }
        else  if (s.equals(getString(R.string.pref_color_key)))
        {
            loadColorFromPreferences(sharedPreferences);
        }

    }
}
    // TODO (1) Create a new Empty Activity named SettingsActivity; make sure to generate the
    // activity_settings.xml layout file as well and add the activity to the manifest

    // TODO (2) Add a new resource folder called menu and create visualizer_menu.xml
    // TODO (3) In visualizer_menu.xml create a menu item with a single item. The id should be
    // "action_settings", title should be saved in strings.xml, the item should never
    // be shown as an action, and orderInCategory should be 100

    // TODO (5) Add the menu to the menu bar
    // TODO (6) When the "Settings" menu item is pressed, open SettingsActivity


