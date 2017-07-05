package com.consultoraestrategia.messengeracademico.entities;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.util.Log;

import com.bumptech.glide.Glide;

/**
 * Created by @stevecampos on 19/04/2017.
 */

public class Notification {
    private static final String TAG = "NotificationEntity";
    private String action;
    private String contentTitle;
    private String contentText;
    private Bitmap largeIcon;
    private String largeIconUri;
    private int smallIcon;

    public Notification() {
    }

    public Notification(String contentTitle, String contentText, Bitmap largeIcon, @DrawableRes int smallIcon) {
        this.contentTitle = contentTitle;
        this.contentText = contentText;
        this.largeIcon = largeIcon;
        this.smallIcon = smallIcon;
    }

    public Notification(String contentTitle, String contentText, Bitmap largeIcon, String largeIconUri, int smallIcon) {
        this.contentTitle = contentTitle;
        this.contentText = contentText;
        this.largeIcon = largeIcon;
        this.largeIconUri = largeIconUri;
        this.smallIcon = smallIcon;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }


    public Bitmap getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(Bitmap largeIcon) {
        this.largeIcon = largeIcon;
    }

    public int getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon(int smallIcon) {
        this.smallIcon = smallIcon;
    }

    public String getLargeIconUri() {
        return largeIconUri;
    }

    public void setLargeIconUri(String largeIconUri) {
        this.largeIconUri = largeIconUri;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
