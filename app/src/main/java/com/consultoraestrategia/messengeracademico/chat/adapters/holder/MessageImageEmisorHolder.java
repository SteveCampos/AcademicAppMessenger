package com.consultoraestrategia.messengeracademico.chat.adapters.holder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by @stevecampos on 16/06/2017.
 */

public class MessageImageEmisorHolder extends RecyclerView.ViewHolder {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
    @BindView(R.id.imageview)
    ImageView imageview;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.img_status)
    ImageView imgStatus;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.txt_day_group)
    public TextView txtTimeTitle;
    @BindView(R.id.layout_message_container)
    public RelativeLayout layoutContainer;

    public MessageImageEmisorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(final ChatMessage message, ChatMessage previousMessage, Resources resources, final ChatMessageListener listener, Drawable drawable, Context context) {


        MessageTextEmisorHolder.
                setTimeTitleVisibility(message.getTimestamp(), previousMessage == null ? 0 : previousMessage.getTimestamp(), txtTimeTitle, resources);
        MessageImageReceptorHolder.loadImageView(layoutContainer, imageview, message, context);

        imgStatus.setImageDrawable(drawable);
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));

        if (message.getMessageStatus() == ChatMessage.STATUS_WRITED) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }

        //ViewCompat.setTransitionName(imageview, message.getMessageUri());

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onImageClick(message, v);
                }
            }
        });
        MessageTextEmisorHolder.checkStatusAndFire(message, listener);
    }

}
