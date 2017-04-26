package com.consultoraestrategia.messengeracademico.entities;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by @stevecampos on 19/04/2017.
 */

public class NotificationInbox extends Notification {

    private String bigContentTitle;
    private String summaryText;
    private List<String> lines = new ArrayList<>();


    public NotificationInbox() {
    }

    public NotificationInbox(String contentTitle, String contentText, Bitmap largeIcon, @DrawableRes int smallIcon) {
        super(contentTitle, contentText, largeIcon, smallIcon);
    }

    public NotificationInbox(String contentTitle, String contentText, Bitmap largeIcon, @DrawableRes int smallIcon, String bigContentTitle, String summaryText, List<String> lines) {
        super(contentTitle, contentText, largeIcon, smallIcon);
        this.bigContentTitle = bigContentTitle;
        this.summaryText = summaryText;
        this.lines = lines;
    }

    public String getBigContentTitle() {
        return bigContentTitle;
    }

    public void setBigContentTitle(String bigContentTitle) {
        this.bigContentTitle = bigContentTitle;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public void addLine(String line) {
        lines.add(line);
    }
}
