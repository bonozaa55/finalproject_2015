package com.example.android.location.GameManagement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.location.Activity.MarkerActivity;
import com.example.android.location.R;
import com.example.android.location.Resource.MarkerMode.ImageObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adisorn on 4/6/2015.
 */
public class ZoomViewManager {
    static Context context;
    View zoomLayout;
    ArrayList<int[]> setIndex;
    /**
     * Hold a reference to the current animator, so that it can be canceled mid-way.
     */
    private Animator mCurrentAnimator;

    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    private int mShortAnimationDuration;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ZoomViewManager.context = context;
    }

    public ZoomViewManager(View zoomLayout) {
        this.zoomLayout=zoomLayout;
        initResource();
    }

    public void showZoomView(View v,int resID){
        zoomImageFromThumb(v, resID);
    }


    void initResource(){
        mShortAnimationDuration = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
        setIndex=new ArrayList<>();
        setIndex.add(new int[]{0,4,12,20,24});
        setIndex.add(new int[]{2,10,14,22});
        setIndex.add(new int[]{5,9,15,19});
        setIndex.add(new int[]{1,3,21,23});
        setIndex.add(new int[]{6,7,8,11,13,16,17,18});

    }

    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        final View containerView = zoomLayout.findViewById(R.id.container2);


        final ImageView expandImage = (ImageView) zoomLayout.findViewById(R.id.expanded_image);
        expandImage.setBackgroundResource(imageResId);


        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();


        thumbView.getGlobalVisibleRect(startBounds);
        zoomLayout.findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);


        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }


        thumbView.setAlpha(0f);
        zoomLayout.setVisibility(View.VISIBLE);
        containerView.setVisibility(View.VISIBLE);


        containerView.setPivotX(0f);
        containerView.setPivotY(0f);


        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(containerView, View.X, startBounds.left,
                        finalBounds.left))
                .with(ObjectAnimator.ofFloat(containerView, View.Y, startBounds.top,
                        finalBounds.top))
                .with(ObjectAnimator.ofFloat(containerView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(containerView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;


        final float startScaleFinal = startScale;
        containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel, back to their
                // original values.
                AnimatorSet set = new AnimatorSet();
                set
                        .play(ObjectAnimator.ofFloat(containerView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(containerView, View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(containerView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(containerView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        containerView.setVisibility(View.GONE);
                        zoomLayout.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        containerView.setVisibility(View.GONE);
                        zoomLayout.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
        showImage();
    }

    public static void initialImage(ImageObject t){
        int square=5;
        List<Integer> section = new ArrayList<>();
        for (int number = 0; number < square * square; number++) {
            section.add(number);
        }

        Bitmap fillBMP = BitmapFactory.decodeResource(context.getResources(),t.getImageID());

        final Bitmap circleBitmap = Bitmap.createBitmap(fillBMP.getWidth(),
                fillBMP.getHeight(), Bitmap.Config.ARGB_8888);
        t.setPieceImage(circleBitmap);

        BitmapShader shader = new BitmapShader(fillBMP, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        final Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        final Canvas c = new Canvas(circleBitmap);
        t.setCanvas(c);

        final int[] srcWidth = new int[section.size()], srcHeight = new int[section
                .size()], destWidth = new int[section.size()], destHeight = new int[section
                .size()];

        for (int k = 0; k < section.size(); k++) {
            srcWidth[k] = getSrcWidth(section.get(k), fillBMP.getWidth()/ square, square);
            srcHeight[k] = getSrcHeight(section.get(k), fillBMP.getHeight() / square, square);
            destWidth[k] = getDestWidth(section.get(k), fillBMP.getWidth()/ square, square);
            destHeight[k] = getDestHeight(section.get(k), fillBMP.getHeight() / square, square);
        }
        t.setSrcWidth(srcWidth);
        t.setSrcHeight(srcHeight);
        t.setDesHeight(destHeight);
        t.setDesWidth(destWidth);
        t.setPaint(paint);
    }

    public void showImage() {
        ImageObject imageObject=MarkerActivity.getTouchImageObject();
        if(!imageObject.isGetItem()) {
            for (int i = 0; i < imageObject.getHitCount(); i++) {
                int temp[] = setIndex.get(i);
                int size = temp.length;
                for (int j = 0; j < size; j++) {
                    int index = temp[j];
                    imageObject.getCanvas().drawRect(imageObject.getSrcWidth()[index], imageObject.getSrcHeight()[index],
                            imageObject.getDesWidth()[index], imageObject.getDesHeight()[index], imageObject.getPaint());
                }
            }
            Bitmap temp = imageObject.getPieceImage();
            ImageView t = (ImageView) zoomLayout.findViewById(R.id.expanded_image);
            t.setScaleType(ImageView.ScaleType.FIT_CENTER);
            t.setImageBitmap(temp);
            ImageView t0 = (ImageView) MarkerActivity.getmViewList().get(MarkerActivity.CAMERA_LAYOUT)
                    .findViewById(imageObject.getThumbImageID());
            t0.setScaleType(ImageView.ScaleType.CENTER_CROP);
            t0.setImageBitmap(temp);
        }else {
            ImageView t = (ImageView) zoomLayout.findViewById(R.id.expanded_image);
            t.setBackgroundResource(imageObject.getImageID());
            t.setImageResource(android.R.color.transparent);
        }
        TextView t1= (TextView) zoomLayout.findViewById(R.id.hitcount);
        t1.setText("FLIP COUNT: "+imageObject.getHitCount());
    }

    public static int getSrcWidth(int section, int width, int square) {
        return width * (section % square);
    }

    public static int getSrcHeight(int section, int height, int square) {
        return height * (section / square);
    }

    public static int getDestWidth(int section, int width, int square) {
        return width * ((section % square) + 1);
    }

    public static int getDestHeight(int section, int height, int square) {
        return height * ((section / square) + 1);
    }

}
