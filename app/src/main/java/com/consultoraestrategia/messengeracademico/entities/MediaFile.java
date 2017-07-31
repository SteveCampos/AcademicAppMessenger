package com.consultoraestrategia.messengeracademico.entities;

import android.net.Uri;

import com.consultoraestrategia.messengeracademico.db.MessengerAcademicoDatabase;
import com.google.firebase.database.Exclude;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by @stevecampos on 25/07/2017.
 */

@Table(database = MessengerAcademicoDatabase.class)
@Parcel(analyze = {MediaFile.class})
public class MediaFile extends BaseModel {


    @Exclude
    public Uri localUri;

    @PrimaryKey
    @Column
    public String downloadUri;

    @Column
    public String contentType;
    @Column
    public int height;
    @Column
    public int width;
    @Column
    public int rotate;
    @Column
    public int orientation;
    @Column
    public String name;
    @Column
    public long sizeBytes;

    public MediaFile() {
    }

    public MediaFile(Uri localUri, String downloadUri, String contentType, int height, int width, int rotate, int orientation, String name, long sizeBytes) {
        this.localUri = localUri;
        this.downloadUri = downloadUri;
        this.contentType = contentType;
        this.height = height;
        this.width = width;
        this.rotate = rotate;
        this.orientation = orientation;
        this.name = name;
        this.sizeBytes = sizeBytes;
    }

    public Uri getLocalUri() {
        return localUri;
    }

    public void setLocalUri(Uri localUri) {
        this.localUri = localUri;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("downloadUri", downloadUri);
        result.put("contentType", contentType);
        result.put("width", width);
        result.put("height", height);
        result.put("orientation", orientation);
        result.put("rotate", rotate);
        result.put("sizeBytes", sizeBytes);
        return result;
    }

    @Override
    public String toString() {
        return "localUri: " + localUri + "\n" +
                "downloadUri: " + downloadUri + "\n" +
                "contentType: " + contentType + "\n" +
                "width: " + width + "\n" +
                "height: " + height + "\n" +
                "orientation: " + orientation + "\n" +
                "rotate: " + rotate + "\n" +
                "sizeBytes: " + sizeBytes;
    }
}
