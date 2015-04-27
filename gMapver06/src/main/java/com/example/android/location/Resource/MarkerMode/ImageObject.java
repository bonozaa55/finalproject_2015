package com.example.android.location.Resource.MarkerMode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.android.location.GameManagement.ZoomViewManager;
import com.metaio.sdk.jni.Vector3d;

/**
 * Created by Adisorn on 4/7/2015.
 */
public class ImageObject {
    Bitmap pieceImage;
    Paint paint;
    String modelFilePath;
    int hitCount=0,imageID,thumbImageID,blurImageID,cos;
    float modelSize;
    Vector3d offset;
    boolean isGetItem=false;
    Canvas canvas;
    int srcWidth[]=new int[25];
    int srcHeight[]=new int[25];
    int desWidth[]=new int[25];
    int desHeight[]=new int[25];

    public static ImageObject createImageObject(int imageID,int blurImageID,int thumbImageID,float modelSize,String modelFilePath
            ,int cos,Vector3d offset){
        ImageObject t=new ImageObject(imageID,blurImageID,thumbImageID,modelSize,modelFilePath,cos,offset);
        ZoomViewManager.initialImage(t);
        return t;
    }

    public ImageObject(int imageID,int blurImageID,int thumbImageID,float modelSize
            ,String modelFilePath,int cos,Vector3d offset) {
        this.imageID = imageID;
        this.modelFilePath=modelFilePath;
        this.modelSize=modelSize;
        this.thumbImageID=thumbImageID;
        this.blurImageID=blurImageID;
        this.cos=cos;
        this.offset=offset;
    }

    public Vector3d getOffset() {
        return offset;
    }

    public void setOffset(Vector3d offset) {
        this.offset = offset;
    }

    public float getModelSize() {
        return modelSize;
    }

    public void setModelSize(float modelSize) {
        this.modelSize = modelSize;
    }

    public int getCos() {
        return cos;
    }

    public void setCos(int cos) {
        this.cos = cos;
    }

    public String getModelFilePath() {
        return modelFilePath;
    }

    public void setModelFilePath(String modelFilePath) {
        this.modelFilePath = modelFilePath;
    }

    public int getBlurImageID() {
        return blurImageID;
    }

    public void setBlurImageID(int blurImageID) {
        this.blurImageID = blurImageID;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public int getThumbImageID() {
        return thumbImageID;
    }

    public void setThumbImageID(int thumbImageID) {
        this.thumbImageID = thumbImageID;
    }

    public int[] getSrcWidth() {
        return srcWidth;
    }

    public void setSrcWidth(int[] srcWidth) {
        this.srcWidth = srcWidth;
    }

    public int[] getSrcHeight() {
        return srcHeight;
    }

    public void setSrcHeight(int[] srcHeight) {
        this.srcHeight = srcHeight;
    }

    public int[] getDesWidth() {
        return desWidth;
    }

    public void setDesWidth(int[] desWidth) {
        this.desWidth = desWidth;
    }

    public int[] getDesHeight() {
        return desHeight;
    }

    public void setDesHeight(int[] desHeight) {
        this.desHeight = desHeight;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public Bitmap getPieceImage() {
        return pieceImage;
    }

    public void setPieceImage(Bitmap pieceImage) {
        this.pieceImage = pieceImage;
    }

    public int getHitCount() {
        return hitCount;
    }

    public void setHitCount(int hitCount) {
        this.hitCount = hitCount;
    }

    public boolean isGetItem() {
        return isGetItem;
    }

    public void setIsGetItem(boolean isGetItem) {
        this.isGetItem = isGetItem;
    }
}
