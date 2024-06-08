package com.example.amrozia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amrozia.CartActivity;
import com.example.amrozia.ContactUsActivity;
import com.example.amrozia.MainActivity;
import com.example.amrozia.ProfileActivity;
import com.example.amrozia.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class MoreActivity extends AppCompatActivity {
    YouTubePlayerView youTubePlayerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        setupPageNavigation();

        // Set the onClickListeners for the cart buttons to go back to the home page
        LinearLayout profileButton = findViewById(R.id.profile_btn);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the cart activity
                Intent intent = new Intent(MoreActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        LinearLayout homeButton = findViewById(R.id.home_btn);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the cart activity
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        LinearLayout cartButton = findViewById(R.id.cart_btn);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the cart activity
                Intent intent = new Intent(MoreActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        // Initialize YouTubePlayerView and add lifecycle observer to ensure proper management of the player's lifecycle
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        // Add a YouTubePlayerListener to the YouTubePlayerView to handle events, such as when the player is ready
        // and load a specific video with its video ID ("NtUabc_D3JA") when the player is ready
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "NtUabc_D3JA";
                youTubePlayer.loadVideo(videoId, 0);
            }
        });
        
    }

    private void setupPageNavigation() {
        findViewById(R.id.nav_contact).setOnClickListener(v -> navigateTo(ContactUsActivity.class));
        findViewById(R.id.nav_blog).setOnClickListener(v -> navigateTo(BlogActivity.class));
        findViewById(R.id.nav_faq).setOnClickListener(v -> navigateTo(FaqActivity.class));
        findViewById(R.id.nav_about).setOnClickListener(v -> navigateTo(AboutUsActivity.class));
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(MoreActivity.this, activityClass);
        startActivity(intent);
    }


     /* Called when the activity is being destroyed. Ensures proper cleanup and releases
     * resources associated with the YouTube player view to prevent memory leaks.*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }
}
