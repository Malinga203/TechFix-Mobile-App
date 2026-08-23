package com.techfix.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.activities.AdminDashboardActivity;
import com.techfix.app.activities.TechnicianDashboardActivity;
import com.techfix.app.userauthentication.activities.LoginActivity;
import com.techfix.app.userauthentication.models.User;
import com.techfix.app.userauthentication.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =====================================================
        // WINDOW
        // =====================================================

        Window window = getWindow();

        window.setStatusBarColor(
                getColor(R.color.techfix_primary_dark)
        );

        window.setNavigationBarColor(
                getColor(R.color.techfix_primary_dark)
        );


        // =====================================================
        // SPLASH UI
        // =====================================================

        setContentView(R.layout.activity_splash);


        // =====================================================
        // SESSION
        // =====================================================

        sessionManager = new SessionManager(this);


        // =====================================================
        // VIEWS
        // =====================================================

        ImageView logo =
                findViewById(R.id.imgSplashLogo);

        TextView title =
                findViewById(R.id.tvSplashTitle);

        TextView subtitle =
                findViewById(R.id.tvSplashSubtitle);

        ProgressBar loader =
                findViewById(R.id.splashLoader);


        // =====================================================
        // INITIAL STATES
        // =====================================================

        logo.setAlpha(0f);
        logo.setScaleX(0.60f);
        logo.setScaleY(0.60f);
        logo.setRotation(-8f);

        title.setAlpha(0f);
        title.setTranslationY(35f);

        subtitle.setAlpha(0f);
        subtitle.setTranslationY(25f);

        loader.setAlpha(0f);


        // =====================================================
        // 1. LOGO ANIMATION
        // =====================================================

        ObjectAnimator logoAlpha =
                ObjectAnimator.ofFloat(
                        logo,
                        "alpha",
                        0f,
                        1f
                );

        ObjectAnimator logoScaleX =
                ObjectAnimator.ofFloat(
                        logo,
                        "scaleX",
                        0.60f,
                        1f
                );

        ObjectAnimator logoScaleY =
                ObjectAnimator.ofFloat(
                        logo,
                        "scaleY",
                        0.60f,
                        1f
                );

        ObjectAnimator logoRotation =
                ObjectAnimator.ofFloat(
                        logo,
                        "rotation",
                        -8f,
                        0f
                );


        AnimatorSet logoAnimation =
                new AnimatorSet();

        logoAnimation.playTogether(
                logoAlpha,
                logoScaleX,
                logoScaleY,
                logoRotation
        );

        logoAnimation.setDuration(900L);

        logoAnimation.setInterpolator(
                new OvershootInterpolator(1.25f)
        );


        // =====================================================
        // 2. TITLE ANIMATION
        // =====================================================

        ObjectAnimator titleAlpha =
                ObjectAnimator.ofFloat(
                        title,
                        "alpha",
                        0f,
                        1f
                );

        ObjectAnimator titleMove =
                ObjectAnimator.ofFloat(
                        title,
                        "translationY",
                        35f,
                        0f
                );


        AnimatorSet titleAnimation =
                new AnimatorSet();

        titleAnimation.playTogether(
                titleAlpha,
                titleMove
        );

        titleAnimation.setDuration(500L);

        titleAnimation.setInterpolator(
                new DecelerateInterpolator()
        );


        // =====================================================
        // 3. SUBTITLE ANIMATION
        // =====================================================

        ObjectAnimator subtitleAlpha =
                ObjectAnimator.ofFloat(
                        subtitle,
                        "alpha",
                        0f,
                        1f
                );

        ObjectAnimator subtitleMove =
                ObjectAnimator.ofFloat(
                        subtitle,
                        "translationY",
                        25f,
                        0f
                );


        AnimatorSet subtitleAnimation =
                new AnimatorSet();

        subtitleAnimation.playTogether(
                subtitleAlpha,
                subtitleMove
        );

        subtitleAnimation.setDuration(500L);

        subtitleAnimation.setInterpolator(
                new DecelerateInterpolator()
        );


        // =====================================================
        // 4. LOADER
        // =====================================================

        ObjectAnimator loaderAnimation =
                ObjectAnimator.ofFloat(
                        loader,
                        "alpha",
                        0f,
                        1f
                );

        loaderAnimation.setDuration(300L);


        // =====================================================
        // 5. FINAL LOGO PULSE
        // =====================================================

        ObjectAnimator pulseX =
                ObjectAnimator.ofFloat(
                        logo,
                        "scaleX",
                        1f,
                        1.055f,
                        1f
                );

        ObjectAnimator pulseY =
                ObjectAnimator.ofFloat(
                        logo,
                        "scaleY",
                        1f,
                        1.055f,
                        1f
                );


        AnimatorSet pulseAnimation =
                new AnimatorSet();

        pulseAnimation.playTogether(
                pulseX,
                pulseY
        );

        pulseAnimation.setDuration(650L);

        pulseAnimation.setInterpolator(
                new DecelerateInterpolator()
        );


        // =====================================================
        // TEXT + LOADER TOGETHER
        // =====================================================

        AnimatorSet textAnimation =
                new AnimatorSet();

        textAnimation.playTogether(
                titleAnimation,
                subtitleAnimation,
                loaderAnimation
        );


        // =====================================================
        // COMPLETE ANIMATION
        //
        // 900ms logo
        // 500ms text
        // 650ms pulse
        //
        // Total ~2.05 seconds
        // =====================================================

        AnimatorSet fullAnimation =
                new AnimatorSet();

        fullAnimation.playSequentially(
                logoAnimation,
                textAnimation,
                pulseAnimation
        );


        // =====================================================
        // IMPORTANT
        //
        // Animation finish wena exact moment eke next screen.
        // Extra Handler delay naha.
        // =====================================================

        fullAnimation.addListener(
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        routeUser();
                    }
                }
        );


        fullAnimation.start();
    }


    // =========================================================
    // ROUTE USER
    // =========================================================

    private void routeUser() {

        // -----------------------------------------------------
        // NOT LOGGED IN
        // Splash -> Login DIRECT
        // -----------------------------------------------------

        if (!sessionManager.isLoggedIn()) {

            openActivity(LoginActivity.class);

            return;
        }


        String role = sessionManager.getRole();


        // -----------------------------------------------------
        // ADMIN
        // -----------------------------------------------------

        if (User.ROLE_ADMIN.equals(role)) {

            openActivity(AdminDashboardActivity.class);

            return;
        }


        // -----------------------------------------------------
        // TECHNICIAN
        // -----------------------------------------------------

        if (User.ROLE_TECHNICIAN.equals(role)) {

            openTechnicianDashboard();

            return;
        }


        // -----------------------------------------------------
        // CUSTOMER
        // -----------------------------------------------------

        if (User.ROLE_CUSTOMER.equals(role)) {

            openActivity(MainActivity.class);

            return;
        }


        // -----------------------------------------------------
        // INVALID SESSION
        // -----------------------------------------------------

        sessionManager.logout();

        openActivity(LoginActivity.class);
    }


    // =========================================================
    // TECHNICIAN
    // =========================================================

    private void openTechnicianDashboard() {

        int technicianId =
                sessionManager.getTechnicianId();


        if (technicianId <= 0) {

            sessionManager.logout();

            openActivity(LoginActivity.class);

            return;
        }


        Intent intent =
                new Intent(
                        SplashActivity.this,
                        TechnicianDashboardActivity.class
                );


        intent.putExtra(
                TechnicianDashboardActivity.EXTRA_TECHNICIAN_ID,
                technicianId
        );


        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );


        // Start without Android transition animation
        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(
                        this,
                        0,
                        0
                );


        startActivity(
                intent,
                options.toBundle()
        );


        finish();
    }


    // =========================================================
    // OPEN ACTIVITY
    // =========================================================

    private void openActivity(Class<?> targetActivity) {

        Intent intent =
                new Intent(
                        SplashActivity.this,
                        targetActivity
                );


        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );


        /*
         * Disable Android's Activity transition.
         *
         * Splash animation end ->
         * next Activity immediately.
         */
        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(
                        this,
                        0,
                        0
                );


        startActivity(
                intent,
                options.toBundle()
        );


        finish();
    }
}