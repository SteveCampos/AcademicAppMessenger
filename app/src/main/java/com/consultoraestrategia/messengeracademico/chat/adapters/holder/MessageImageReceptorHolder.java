package com.consultoraestrategia.messengeracademico.chat.adapters.holder;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.target.Target;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

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

    public MessageImageReceptorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(final ChatMessage message, final ChatMessageListener listener, Context context) {
        Log.d(TAG, "MessageTextReceptorHolder bind");
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));

        loadImageView(Uri.parse(message.getMessageUri()), context);
        /*
        Glide.with(context)
                .load(Uri.parse(message.getMessageUri()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(imageview);*/

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

    private void loadImageView(final Uri uri, final Context context) {
        if (imageview.getHeight() == 0) {
            // wait for layout, same as Glide's SizeDeterminer does
            imageview.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    // re-query viewTreeObserver, because the one used to add the listener may be dead: http://stackoverflow.com/a/29172475/253468
                    imageview.getViewTreeObserver().removeOnPreDrawListener(this);
                    // call the same method, but now we can be sure getHeight() has a value
                    loadImageView(uri, context);
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


}
