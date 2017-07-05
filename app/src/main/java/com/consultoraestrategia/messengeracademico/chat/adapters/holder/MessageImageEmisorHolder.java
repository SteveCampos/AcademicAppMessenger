package com.consultoraestrategia.messengeracademico.chat.adapters.holder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

public class MessageImageEmisorHolder extends RecyclerView.ViewHolder {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
    @BindView(R.id.imageview)
    ImageView imageview;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.layout_bubble)
    RelativeLayout layoutBubble;
    @BindView(R.id.img_status)
    ImageView imgStatus;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    public MessageImageEmisorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(final ChatMessage message, final ChatMessageListener listener, Drawable drawable, Context context) {

        loadImageView(Uri.parse(message.getMessageUri()), context);

        imgStatus.setImageDrawable(drawable);
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));

        if (message.getMessageStatus() == ChatMessage.STATUS_WRITED) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
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
                    .override(imageview.getWidth(), Target.SIZE_ORIGINAL)
                    .bitmapTransform(new FitCenter(context), new RoundedCornersTransformation(context, 8, 0, RoundedCornersTransformation.CornerType.ALL))
                    .into(imageview);
        }
    }

    private float dpFromPx(final float px) {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    private float pxFromDp(final float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }


}
