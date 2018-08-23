package com.consultoraestrategia.messengeracademico.chat.adapters.holder;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.target.Target;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.base.SelectableViewHolder;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.MediaFile;
import com.consultoraestrategia.messengeracademico.utils.MessageUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by @stevecampos on 16/06/2017.
 */

public class MessageImageReceptorHolder extends SelectableViewHolder<ChatMessage> {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
    private static final String TAG = MessageImageReceptorHolder.class.getSimpleName();

    @BindView(R.id.txt_day_group)
    TextView txtDayGroup;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.bg_img)
    FrameLayout bgImg;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.layout_message)
    ConstraintLayout layoutMessage;


    @Override
    protected View getBgRegion() {
        return layoutMessage;
    }

    public MessageImageReceptorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(final ChatMessage message, ChatMessage previousMessage, final ChatMessageListener listener) {
        Log.d(TAG, "MessageTextReceptorHolder bind");
        super.bind(message, listener);
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));

        loadImageView(bgImg, img, message);

        MessageUtils.
                setTimeTitleVisibility(message.getTimestamp(), previousMessage == null ? 0 : previousMessage.getTimestamp(), txtDayGroup, itemView.getResources());

        if (listener != null && message.getMessageStatus() != ChatMessage.STATUS_READED) {
            listener.onMessageReaded(message);
        }

        img.setOnClickListener(new View.OnClickListener() {
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

    public static void loadImageView(FrameLayout layout, ImageView imageview, ChatMessage message) {
        Log.d(TAG, "loadImageview message: " + message.toString());

        if (message.getMediaFile() == null) {
            loadImageView(imageview, Uri.parse(message.getMessageUri()), imageview.getContext());
            return;
        }

        changeViewDimensions(imageview.getResources(), layout, message.getMediaFile());

        if (message.getMediaFile() != null && message.getMediaFile().getDownloadUri() != null) {

            String downloadUrl = message.getMediaFile().getDownloadUri();
            Log.d(TAG, "loadImageView: downloadUrl: " + downloadUrl);
            /*if (downloadUrl.contains("crmepruebas.firebaseapp.com/api/table?")){
                downloadUrl = "https://crmepruebas.firebaseapp.com/api/table?values=[%22Indicadores%22,%22%25%22,%22Muy%20Contento-19%22,%22Contento-15%22,%22Neutral-12%22,%22Triste-9%22,%22Muy%20Triste-6%22,%22ESTABLECE%20RELACIONES%22,%22base%22,%22%22,%22x%22,%22%22,%22%22,%22%22,%22EXPRESA%22,%22base%22,%22%22,%22x%22,%22%22,%22%22,%22%22,%22Disposición%20de%20Valorar%20%22,%22enfoque%22,%22%22,%22x%22,%22%22,%22%22,%22%22,%22Disposición%20de%20Elección%20Voluntaria%22,%22enfoque%22,%22%22,%22x%22,%22%22,%22%22,%22%22]&columns=7&title=Matemática&nombre=Blanco%20Calla,%20Juan%20daniel&puntos=15.0&desempenio=0.0&logro=%20Cumplió";
            }*/
            Log.d(TAG, "loadImageView: download Url converted: " + downloadUrl);
            Uri uri = Uri.parse(downloadUrl);

            Glide
                    .with(imageview.getContext())
                    .load(uri)
                    //.load(Uri.parse("https://crmepruebas.firebaseapp.com/api/table?values=[%2212399949090956789%22,%22%22,%22C-13%22,%22AD-4%22,%22B-3%22,%22A-2%22,%22Comprueba%22,%22enfoque%22,%22x%22,%22%22,%22%22,%22%22,%22Comprueba%20Expresiones%22,%22%22,%22%22,%22x%22,%22%22,%22%22,%22Comprueba%22,%2225%22,%22%22,%22%22,%22x%22,%22%22,%22Compara%20numeros%22,%22trans%22,%22%22,%22%22,%22x%22,%22%22,%22Resuelve%20Problemas%22,%2225%22,%22%22,%22%22,%22%22,%22x%22]&columns=6"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    //.override(actualWidth, actualHeight)
                    .bitmapTransform(new FitCenter(imageview.getContext()), new RoundedCornersTransformation(imageview.getContext(), 8, 4, RoundedCornersTransformation.CornerType.ALL))
                    .into(imageview);
        }
    }

}
