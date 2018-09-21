package com.consultoraestrategia.messengeracademico.chat.adapters.holder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.base.SelectableViewHolder;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.utils.MessageUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by @stevecampos on 16/06/2017.
 */

public class MessageRubroEmisorHolder extends SelectableViewHolder<ChatMessage> {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    @BindView(R.id.txt_day_group)
    TextView txtDayGroup;
    @BindView(R.id.img_status)
    ImageView imgStatus;
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

    public MessageRubroEmisorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(final ChatMessage message, ChatMessage previousMessage, final ChatMessageListener listener) {

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickRubro(message, v);
                }
            }
        });
        super.bind(message, listener);


        MessageUtils.
                setTimeTitleVisibility(message.getTimestamp(), previousMessage == null ? 0 : previousMessage.getTimestamp(), txtDayGroup, itemView.getResources());
        //MessageImageReceptorHolder.loadImageView(bgImg, img, message);

        Drawable statusDrawable = MessageUtils.getDrawableFromMessageStatus(message.getMessageStatus(), itemView.getContext());

        imgStatus.setImageDrawable(statusDrawable);
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));

        if (message.getMessageStatus() == ChatMessage.STATUS_WRITED) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
        MessageTextEmisorHolder.checkStatusAndFire(message, listener);
    }

}
