package com.example.dell.mycirc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;


public class CircularAnimUtil {

    public static final long PERFECT_MILLS = 618;
    public static final int MINI_RADIUS = 0;
    private static final int FINISH_NONE = 0, FINISH_SINGLE = 1, FINISH_ALL = 3;


    @SuppressLint("NewApi")
    private static void actionVisible(boolean isShow, final View myView, float miniRadius, long durationMills) {
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (isShow)
                myView.setVisibility(View.VISIBLE);
            else
                myView.setVisibility(View.INVISIBLE);
            return;
        }

        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;

        int w = myView.getWidth();
        int h = myView.getHeight();

                int maxRadius = (int) Math.sqrt(w * w + h * h) + 1;

        float startRadius, endRadius;
        if (isShow) {

            startRadius = miniRadius;
            endRadius = maxRadius;
        } else {
                    startRadius = maxRadius;
            endRadius = miniRadius;
        }

        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, startRadius, endRadius);
        myView.setVisibility(View.VISIBLE);
        anim.setDuration(durationMills);


        if (!isShow)
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            });

        anim.start();
    }


    @SuppressLint("NewApi")
    private static void actionOtherVisible(boolean isShow, final View triggerView, final View animView,
                                           float miniRadius, long durationMills) {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (isShow)
                animView.setVisibility(View.VISIBLE);
            else
                animView.setVisibility(View.INVISIBLE);
            return;
        }

        int[] tvLocation = new int[2];
        triggerView.getLocationInWindow(tvLocation);
        final int tvCX = tvLocation[0] + triggerView.getWidth() / 2;
        final int tvCY = tvLocation[1] + triggerView.getHeight() / 2;

        int[] avLocation = new int[2];
        animView.getLocationInWindow(avLocation);
        final int avLX = avLocation[0];
        final int avTY = avLocation[1];

        int triggerX = Math.max(avLX, tvCX);
        triggerX = Math.min(triggerX, avLX + animView.getWidth());

        int triggerY = Math.max(avTY, tvCY);
        triggerY = Math.min(triggerY, avTY + animView.getHeight());

               int avW = animView.getWidth();
        int avH = animView.getHeight();

        int rippleCX = triggerX - avLX;
        int rippleCY = triggerY - avTY;

               int maxW = Math.max(rippleCX, avW - rippleCX);
        int maxH = Math.max(rippleCY, avH - rippleCY);
        final int maxRadius = (int) Math.sqrt(maxW * maxW + maxH * maxH) + 1;

        float startRadius, endRadius;
        if (isShow) {

            startRadius = miniRadius;
            endRadius = maxRadius;
        } else {
                   startRadius = maxRadius;
            endRadius = miniRadius;
        }

        Animator anim = ViewAnimationUtils.createCircularReveal(
                animView, rippleCX, rippleCY, startRadius, endRadius);
        animView.setVisibility(View.VISIBLE);
        anim.setDuration(durationMills);


        if (!isShow)
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animView.setVisibility(View.INVISIBLE);
                }
            });

        anim.start();
    }


    private static void startActivityOrFinish(final int finishType, final Activity thisActivity,
                                              final Intent intent, final Integer requestCode,
                                              final Bundle bundle) {
        if (requestCode == null)
            thisActivity.startActivity(intent);
        else if (bundle == null)
            thisActivity.startActivityForResult(intent, requestCode);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            thisActivity.startActivityForResult(intent, requestCode, bundle);
        } else
            thisActivity.startActivityForResult(intent, requestCode);

        switch (finishType) {
            case FINISH_SINGLE:

                thisActivity.finish();
                break;
            case FINISH_ALL:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    thisActivity.finishAffinity();
                } else
                    thisActivity.finish();
                break;
        }
    }

    @SuppressLint("NewApi")
    private static void actionStarActivity(
            final int finishType, final Activity thisActivity, final Intent intent,
            final Integer requestCode, final Bundle bundle, final View triggerView,
            int colorOrImageRes, long durationMills) {


        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            startActivityOrFinish(finishType, thisActivity, intent, requestCode, bundle);
            return;
        }

        int[] location = new int[2];
        triggerView.getLocationInWindow(location);
        final int cx = location[0] + triggerView.getWidth() / 2;
        final int cy = location[1] + triggerView.getHeight() / 2;
        final ImageView view = new ImageView(thisActivity);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setImageResource(colorOrImageRes);
        final ViewGroup decorView = (ViewGroup) thisActivity.getWindow().getDecorView();
        int w = decorView.getWidth();
        int h = decorView.getHeight();
        decorView.addView(view, w, h);

                int maxW = Math.max(cx, w - cx);
        int maxH = Math.max(cy, h - cy);
        final int finalRadius = (int) Math.sqrt(maxW * maxW + maxH * maxH) + 1;

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        int maxRadius = (int) Math.sqrt(w * w + h * h) + 1;
                if (durationMills == PERFECT_MILLS) {

            double rate = 1d * finalRadius / maxRadius;

            durationMills = (long) (PERFECT_MILLS * Math.sqrt(rate));
        }
        final long finalDuration = durationMills;
                anim.setDuration((long) (finalDuration*0.9));
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (requestCode == null)
                    thisActivity.startActivity(intent);
                else if (bundle == null)
                    thisActivity.startActivityForResult(intent, requestCode);
                else
                    thisActivity.startActivityForResult(intent, requestCode, bundle);

                             thisActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                switch (finishType) {
                    case FINISH_NONE:

                        triggerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Animator anim =
                                        ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius, 0);
                                anim.setDuration(finalDuration);
                                anim.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        try {
                                            decorView.removeView(view);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                anim.start();
                            }
                        }, 1000);
                        break;
                    case FINISH_SINGLE:
                        // finish当前activity
                        thisActivity.finish();
                        break;
                    case FINISH_ALL:

                        thisActivity.finishAffinity();
                        break;
                }

            }
        });
        anim.start();
    }



    public static void show(View myView, float startRadius, long durationMills) {
        actionVisible(true, myView, startRadius, durationMills);
    }


    public static void hide(final View myView, float endRadius, long durationMills) {
        actionVisible(false, myView, endRadius, durationMills);
    }


    public static void startActivityForResult(
            final Activity thisActivity, final Intent intent, final Integer requestCode, final Bundle bundle,
            final View triggerView, int colorOrImageRes, long durationMills) {

        actionStarActivity(FINISH_NONE, thisActivity, intent, requestCode, bundle, triggerView, colorOrImageRes, durationMills);
    }


    public static void startActivityThenFinish(
            final Activity thisActivity, final Intent intent, final boolean isFinishAffinity, final View triggerView,
            int colorOrImageRes, long durationMills) {
        int finishType = isFinishAffinity ? FINISH_ALL : FINISH_SINGLE;
        actionStarActivity(finishType, thisActivity, intent, null, null, triggerView, colorOrImageRes, durationMills);
    }





    public static void startActivityForResult(
            Activity thisActivity, Intent intent, Integer requestCode, View triggerView, int colorOrImageRes) {
        startActivityForResult(thisActivity, intent, requestCode, null, triggerView, colorOrImageRes, PERFECT_MILLS);
    }

    public static void startActivity(
            Activity thisActivity, Intent intent, View triggerView, int colorOrImageRes, long durationMills) {
        startActivityForResult(thisActivity, intent, null, null, triggerView, colorOrImageRes, durationMills);
    }

    public static void startActivity(
            Activity thisActivity, Intent intent, View triggerView, int colorOrImageRes) {
        startActivity(thisActivity, intent, triggerView, colorOrImageRes, PERFECT_MILLS);
    }

    public static void startActivity(Activity thisActivity, Class<?> targetClass, View triggerView, int colorOrImageRes) {
        startActivity(thisActivity, new Intent(thisActivity, targetClass), triggerView, colorOrImageRes, PERFECT_MILLS);
    }

    public static void startActivityThenFinish(Activity thisActivity, Intent intent, View triggerView, int colorOrImageRes) {
                startActivityThenFinish(thisActivity, intent, false, triggerView, colorOrImageRes, PERFECT_MILLS);
    }

    public static void show(View myView) {
        show(myView, MINI_RADIUS, PERFECT_MILLS);
    }

    public static void hide(View myView) {
        hide(myView, MINI_RADIUS, PERFECT_MILLS);
    }

    public static void showOther(View triggerView, View otherView) {
        actionOtherVisible(true, triggerView, otherView, MINI_RADIUS, PERFECT_MILLS);
    }

    public static void hideOther(View triggerView, View otherView) {
        actionOtherVisible(false, triggerView, otherView, MINI_RADIUS, PERFECT_MILLS);
    }

}
