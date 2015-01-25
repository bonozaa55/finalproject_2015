package com.example.android.location.Interface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.example.android.location.R;

import java.util.HashMap;

/**
 * Created by Adisorn on 1/23/2015.
 */
public class CustomView  extends ImageView {
        public CustomView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        public CustomView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public CustomView(Context context) {
            super(context);
            init();
        }

        private HashMap<String, Bitmap> mStore = new HashMap<String, Bitmap>();

        boolean drawGlow = false;
        //this is the pixel coordinates of the screen
        float glowX = 0;
        float glowY = 0;
        //this is the radius of the circle we are drawing
        float radius = 150;
        //this is the paint object which specifies the color and alpha level
//of the circle we draw
        Paint paint = new Paint();

        {
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            //paint.setAlpha(50);
        };

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        if(visibility==VISIBLE) {
            drawGlow=true;
        }
        if(visibility==GONE) {
            drawGlow=false;
        }
        super.onVisibilityChanged(changedView, visibility);
    }
/*
    @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
           if (drawGlow)
                canvas.drawCircle(glowX, glowY, radius, paint);
        }*/

    public float getGlowX() {
        return glowX;
    }

    public void setGlowX(float glowX) {
        this.glowX = glowX;
    }

    public float getGlowY() {
        return glowY;
    }

    public void setGlowY(float glowY) {
        this.glowY = glowY;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bmp = mStore.get("0");
        if(bmp!=null) {
            canvas.drawBitmap(bmp, glowX-128, glowY-128, null);
        }

        super.onDraw(canvas);
    }

    public void init() {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.monster1);
        mStore.put("0", bmp);
    }


}
