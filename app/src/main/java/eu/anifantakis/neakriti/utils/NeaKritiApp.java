package eu.anifantakis.neakriti.utils;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.squareup.picasso.Picasso;

import java.io.File;

import eu.anifantakis.neakriti.BuildConfig;
import eu.anifantakis.neakriti.R;
import okhttp3.OkHttpClient;

//import com.jakewharton.picasso.OkHttp3Downloader;

public class NeaKritiApp extends Application {
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    public SimpleExoPlayer mRadioPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        initFirebaseRemoteConfig();
        applyFirebaseConfiguration();

        sAnalytics = GoogleAnalytics.getInstance(this);
        setupPicasso();
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }
        return sTracker;
    }

    public SimpleExoPlayer getRadioPlayer(){
        // TODO: Add MediaPlayer to support Radio stream on devices running API level 15.
        if (mRadioPlayer == null){
            TrackSelector trackSelector = new DefaultTrackSelector(
                    new AdaptiveTrackSelection.Factory(
                            new DefaultBandwidthMeter()
                    )
            );

            mRadioPlayer = ExoPlayerFactory.newSimpleInstance(
                    getApplicationContext(),
                    trackSelector
            );
            //mRadioPlayer.addListener(this);

            String userAgent = Util.getUserAgent(getApplicationContext(), "rssreadernk");

            MediaSource source = new ExtractorMediaSource.Factory(new OkHttpDataSourceFactory(
                    new OkHttpClient(),
                    userAgent,
                    null
            )).createMediaSource(Uri.parse(AppUtils.RADIO_STATION_URL));


            mRadioPlayer.prepare(source);
            //sRadioPlayer.setPlayWhenReady(true);
        }
        return mRadioPlayer;
    }

    private void setupPicasso() {
        // Source: https://gist.github.com/iamtodor/eb7f02fc9571cc705774408a474d5dcb
        OkHttpClient okHttpClient1 = new OkHttpClient.Builder()
                /*
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());

                        int days=2;
                        long cacheTime = 60 * 60 * 24 * days;

                        return originalResponse.newBuilder().header("Cache-Control", "max-age=" + (cacheTime))
                                .build();
                    }
                })
                .cache(new Cache(getCacheDir(), Integer.MAX_VALUE))
                */
                .build();

        Picasso picasso = new Picasso
                .Builder(this)
                //.downloader(new OkHttp3Downloader(okHttpClient1))
                .build();


        Picasso.setSingletonInstance(picasso);

        File[] files=getCacheDir().listFiles();
        Log.d("FILES IN CACHE", ""+files.length);

        // indicator for checking picasso caching - need to comment out on release
        //picasso.setIndicatorsEnabled(true);
    }

    public FirebaseRemoteConfig mFirebaseRemoteConfig;

    /**
     * Initializations to the firebase remote configuration and application of the "actual"  configuration to the application
     */
    private void initFirebaseRemoteConfig(){
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
    }

    private String getFireBaseString(String field){
        String value = mFirebaseRemoteConfig.getString(field);
        value = value.replace("&amp;", "&");
        return value;
    }

    /**
     * Applies current configuration from firebase defaults or cloud settings
     */
    private void applyFirebaseConfiguration(){
        AppUtils.URL_BASE = getFireBaseString("URL_BASE");
        AppUtils.RADIO_STATION_URL = getFireBaseString("RADIO_STATION_URL");
        AppUtils.TV_STATION_URL = getFireBaseString("TV_STATION_URL");

    }
}
