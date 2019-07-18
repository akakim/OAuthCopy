package com.dreamsecurity.oauth;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import androidx.browser.customtabs.CustomTabsIntent;

public class CustsomTabBrowererActivity extends Activity {

    String url = "https://paul.kinlan.me/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // open a Chrome Custom Tab
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();


        // Configure the color
        builder.setToolbarColor( 0x00ff00 );

        builder.setStartAnimations(this, android.R.anim.slide_out_right, android.R.anim.slide_in_left);
        builder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);


        customTabsIntent.launchUrl(this, Uri.parse(url));


    }
}
