package com.consultoraestrategia.messengeracademico.chat.adapters.holder;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.target.Target;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.MediaFile;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by @stevecampos on 16/06/2017.
 */

public class MessageImageReceptorHolder extends RecyclerView.ViewHolder {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
    private static final String TAG = MessageImageReceptorHolder.class.getSimpleName();
    @BindView(R.id.imageview)
    ImageView imageview;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.txt_day_group)
    public TextView txtTimeTitle;
    @BindView(R.id.layout_message_container)
    public RelativeLayout layoutContainer;


    public MessageImageReceptorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(final ChatMessage message, ChatMessage previousMessage, final ChatMessageListener listener, Context context) {
        Log.d(TAG, "MessageTextReceptorHolder bind");
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));

        loadImageView(layoutContainer, imageview, message, context);

        MessageTextEmisorHolder.
                setTimeTitleVisibility(message.getTimestamp(), previousMessage == null ? 0 : previousMessage.getTimestamp(), txtTimeTitle, context.getResources());

        if (listener != null && message.getMessageStatus() != ChatMessage.STATUS_READED) {
            listener.onMessageReaded(message);
        }

        ViewCompat.setTransitionName(imageview, message.getMessageUri());
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onImageClick(message, v);
                }
            }
        });
    }


    public static void loadImageView(final ImageView imageview, final Uri uri, final Context context) {
        if (imageview.getHeight() == 0) {
            // wait for layout, same as Glide's SizeDeterminer does
            imageview.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    // re-query viewTreeObserver, because the one used to add the listener may be dead: http://stackoverflow.com/a/29172475/253468
                    imageview.getViewTreeObserver().removeOnPreDrawListener(this);
                    // call the same method, but now we can be sure getHeight() has a value
                    loadImageView(imageview, uri, context);
                    return true; // == allow drawing
                }
            });
        } else {
            Glide
                    .with(imageview.getContext())
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .override(Target.SIZE_ORIGINAL, imageview.getHeight())
                    .bitmapTransform(new FitCenter(context), new RoundedCornersTransformation(context, 8, 0, RoundedCornersTransformation.CornerType.ALL))
                    .into(imageview);
        }
    }

    public static void changeViewDimensions(Resources resources, View view, MediaFile mediaFile) {
        if (mediaFile != null) {
            mediaFile.load();

            int actualWidth = mediaFile.getWidth();
            int actualHeight = mediaFile.getHeight();
            Log.d(TAG, "actualWidth: " + actualWidth);
            Log.d(TAG, "actualHeight: " + actualHeight);
            if (actualHeight == 0 || actualWidth == 0) {
                return;
            }
            int maxWidth = resources.getDimensionPixelSize(R.dimen.image_default_size);
            Log.d(TAG, "maxWidht: " + maxWidth);
            if (actualWidth > maxWidth) {
                actualHeight = (actualHeight * maxWidth) / actualWidth;
                actualWidth = maxWidth;
            }
            Log.d(TAG, "conversion...");
            Log.d(TAG, "actualWidth: " + actualWidth);
            Log.d(TAG, "actualHeight: " + actualHeight);

            view.getLayoutParams().height = actualHeight; // OR
            view.getLayoutParams().width = actualWidth;
        }
    }

    public static void loadImageView(RelativeLayout layoutContainer, ImageView imageview, ChatMessage message, Context context) {
        Log.d(TAG, "loadImageview message: " + message.toString());

        if (message.getMediaFile() == null) {
            loadImageView(imageview, Uri.parse(message.getMessageUri()), context);
            return;
        }

        changeViewDimensions(context.getResources(), layoutContainer, message.getMediaFile());

        if (message.getMediaFile() != null && message.getMediaFile().getDownloadUri() != null) {
            Glide
                    .with(imageview.getContext())
                    .load(Uri.parse(message.getMediaFile().getDownloadUri()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    //.override(actualWidth, actualHeight)
                    .bitmapTransform(new FitCenter(context), new RoundedCornersTransformation(context, 8, 8, RoundedCornersTransformation.CornerType.ALL))
                    .into(imageview);
        }
    }

}
