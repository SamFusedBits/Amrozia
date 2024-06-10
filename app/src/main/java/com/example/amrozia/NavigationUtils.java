package com.example.amrozia;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import com.example.amrozia.Activity.AboutUsActivity;
import com.example.amrozia.Activity.BlogActivity;
import com.example.amrozia.Activity.FaqActivity;
import com.example.amrozia.Activity.GiftCardActivity;
import com.example.amrozia.Activity.ShippingReturnsActivity;

public class NavigationUtils {

    private static final String TAG = "NavigationUtils";

    public static void navigateTo(MenuItem item, Activity activity) {
        Log.d(TAG, "navigateTo called with item ID: " + item.getItemId());
        Intent intent = null;
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            intent = new Intent(activity, HomeActivity.class);
        } else if (id == R.id.nav_shop) {
            intent = new Intent(activity, ShopActivity.class);
        } else if (id == R.id.nav_ship) {
            intent = new Intent(activity, ShippingReturnsActivity.class);
        } else if (id == R.id.nav_contact) {
            intent = new Intent(activity, ContactUsActivity.class);
        } else if (id == R.id.nav_blog) {
            intent = new Intent(activity, BlogActivity.class);
        } else if (id == R.id.nav_faq) {
            intent = new Intent(activity, FaqActivity.class);
        } else if (id == R.id.nav_about) {
            intent = new Intent(activity, AboutUsActivity.class);
        } else if (id == R.id.nav_gift) {
            intent = new Intent(activity, GiftCardActivity.class);
        }

        if (intent != null) {
            activity.startActivity(intent);
            Log.d(TAG, "Starting activity with intent: " + intent.toString());
            activity.finish();
        } else {
            Log.d(TAG, "Intent is null, not starting activity");
        }
    }
}
