package com.example.android.location.Interface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.android.location.R;

/**
 * Created by Adisorn on 4/23/2015.
 */


public class PageIndicator extends View {
    int totalNoOfDots;
    int activeDot;
    int dotSpacing;
    int horizontalSpace = 5;
    Bitmap activeDotBitmap;
    Bitmap normalDotBitmap;
    int x=0;

    private Paint paint;
    public PageIndicator(Context context) {
        super(context);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        activeDotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_active);
        normalDotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_deactive);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        activeDotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_active);
        normalDotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_deactive);

    }

    public PageIndicator(Context context, AttributeSet attrs , int defStyle) {
        super(context,attrs,defStyle);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        activeDotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_active);
        normalDotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_deactive);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawDot(canvas);
        super.onDraw(canvas);

    }


    private void drawDot(Canvas canvas){
        for(int i=0;i<totalNoOfDots;i++){
            if(i==activeDot){
                canvas.drawBitmap(activeDotBitmap, x, 0, paint);
            }else{
                canvas.drawBitmap(normalDotBitmap, x, 3, paint);
            }
            x=x+activeDotBitmap.getWidth()+horizontalSpace+dotSpacing;
        }
    }





    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = totalNoOfDots*(activeDotBitmap.getWidth()+horizontalSpace+ getDotSpacing());
        width = resolveSize(width, widthMeasureSpec);
        int height = activeDotBitmap.getHeight();
        height = resolveSize(height, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public void refersh(){
        x = 0;
        invalidate();
    }


    public int getTotalNoOfDots() {
        return totalNoOfDots;
    }

    public void setTotalNoOfDots(int totalNoOfDots) {
        this.totalNoOfDots = totalNoOfDots;
        x=0;
        invalidate();
    }

    public int getActiveDot() {
        return activeDot;
    }

    public void setActiveDot(int activeDot) {
        this.activeDot = activeDot;
        x=0;
        invalidate();
    }

    public int getDotSpacing() {
        return dotSpacing;
    }

    public void setDotSpacing(int dotSpacing) {
        this.dotSpacing = dotSpacing;
        x=0;
        invalidate();
    }

}
