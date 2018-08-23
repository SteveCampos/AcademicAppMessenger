package com.consultoraestrategia.messengeracademico.chat.adapters.holder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.OfficialMessage;
import com.consultoraestrategia.messengeracademico.utils.MessageUtils;
import com.consultoraestrategia.messengeracademico.utils.TimeUtils;
import com.vanniktech.emoji.EmojiTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.consultoraestrategia.messengeracademico.entities.OfficialMessage.STATE_CONFIRM;
import static com.consultoraestrategia.messengeracademico.entities.OfficialMessage.STATE_DENY;
import static com.consultoraestrategia.messengeracademico.entities.OfficialMessage.STATE_NO_ACTION;
import static com.consultoraestrategia.messengeracademico.entities.OfficialMessage.STATE_WAITING;

/**
 * Created by @stevecampos on 16/08/2017.
 */

public class MessageTextOfficialEmisorHolder extends RecyclerView.ViewHolder {
    private static final String TAG = MessageTextEmisorHolder.class.getSimpleName();


    @BindView(R.id.txt_day_group)
    TextView txtDayGroup;
    @BindView(R.id.txt_subject)
    AppCompatTextView txtSubject;
    @BindView(R.id.txt_official_title)
    AppCompatTextView txtTitle;
    @BindView(R.id.txt_additional1)
    AppCompatTextView txtAdditional1;
    @BindView(R.id.txt_additional2)
    AppCompatTextView txtAdditional2;
    @BindView(R.id.txt_important_reference)
    AppCompatTextView txtImportantReference;
    @BindView(R.id.cardview)
    CardView cardview;
    @BindView(R.id.message_text)
    EmojiTextView messageText;
    @BindView(R.id.txt_official_emisor)
    TextView txtOfficialEmisor;
    @BindView(R.id.txt_time)
    TextView txtTime;
    @BindView(R.id.txt_action_respone)
    TextView txtActionRespone;
    @BindView(R.id.container_action_buttons)
    LinearLayout containerActionButtons;
    @BindView(R.id.layout_bubble)
    RelativeLayout layoutBubble;
    @BindView(R.id.img_status)
    ImageView imgStatus;
    /*
    @BindView(R.id.img_status)
    public ImageView imgStatus;
    @BindView(R.id.txt_time)
    public TextView txtTime;
    @BindView(R.id.message_text)
    public EmojiTextView messageText;
    @BindView(R.id.layout_bubble)
    public RelativeLayout layoutBubble;
    @BindView(R.id.txt_day_group)
    public TextView txtTimeTitle;*/


    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    public MessageTextOfficialEmisorHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(ChatMessage message, ChatMessage previousMessage, ChatMessageListener listener) {
        Drawable statusDrawable = MessageUtils.getDrawableFromMessageStatus(message.getMessageStatus(), itemView.getContext());
        messageText.setText(message.getMessageText());
        imgStatus.setImageDrawable(statusDrawable);
        txtTime.setText(SIMPLE_DATE_FORMAT.format(message.getTimestamp()));
        setTimeTitleVisibility(message.getTimestamp(), previousMessage == null ? 0 : previousMessage.getTimestamp(), txtDayGroup, itemView.getResources());
        showOfficialMessage(
                message.getOfficialMessage(),
                itemView.getResources(),
                txtSubject,
                txtTitle,
                txtAdditional1,
                txtAdditional2,
                txtImportantReference,
                txtOfficialEmisor,
                txtActionRespone,
                null,
                null);
        MessageTextEmisorHolder.checkStatusAndFire(message, listener);
    }

    public static void showOfficialMessage(OfficialMessage officialMessage,
                                           Resources res,
                                           AppCompatTextView txtSubject,
                                           AppCompatTextView txtTitle,
                                           AppCompatTextView txtAdditional1,
                                           AppCompatTextView txtAdditional2,
                                           AppCompatTextView txtImportantReference,
                                           TextView txtOfficialEmisor,
                                           TextView txtActionRespone,
                                           AppCompatButton btnConfirm, AppCompatButton btnDeny) {
        Log.d(TAG, "showOfficialMessage");
        if (officialMessage != null) {
            officialMessage.load();
            String subject = officialMessage.getSubject();
            String title = officialMessage.getTitle();
            String reference1 = officialMessage.getReference1();
            String reference2 = officialMessage.getReference2();
            String reference3 = officialMessage.getReference3();
            String reference4 = officialMessage.getReference4();
            String importantReference = officialMessage.getImportantReference();
            String officialEmisorName = officialMessage.getOfficialEmisorName();
            String actionType = officialMessage.getActionType();
            int stateAction = officialMessage.getState();

            txtSubject.setText(subject != null ? subject : "Subject");
            txtTitle.setText(title != null ? title : "Title");
            txtAdditional1.setText(reference1 != null ? reference1 : "Adicional Info");
            txtAdditional2.setText(reference2 != null ? reference2 : "Adicional Info 2");
            txtImportantReference.setText(importantReference != null ? importantReference : "Referencia\nImportante");
            txtOfficialEmisor.setText(officialEmisorName != null ? officialEmisorName : "Official Emisor");


            if (btnConfirm != null && btnDeny != null) {
                showStateReceptor(stateAction, btnConfirm, btnDeny, txtActionRespone, res);
            } else {
                showStateEmisor(stateAction, txtActionRespone, res);
            }
        }
    }

    public static void showStateEmisor(int stateAction, TextView txtActionRespone, Resources resources) {
        if (stateAction == STATE_NO_ACTION) {
            hideActionResponse(txtActionRespone);
            return;
        }
        showActionResponse(txtActionRespone);
        setActionResponse(stateAction, txtActionRespone, resources);
    }

    public static void setActionResponse(int stateAction, TextView txtActionRespone, Resources res) {

        String textState;
        int color;
        switch (stateAction) {
            default:
                textState = res.getString(R.string.officialmessage_state_waiting);
                color = ResourcesCompat.getColor(res, R.color.md_grey_500, null);
                break;
            case STATE_CONFIRM:
                textState = res.getString(R.string.officialmessage_state_confirm);
                color = ResourcesCompat.getColor(res, R.color.colorPrimary, null);
                break;
            case STATE_DENY:
                textState = res.getString(R.string.officialmessage_state_deny);
                color = ResourcesCompat.getColor(res, R.color.md_red_900, null);
                break;
        }

        txtActionRespone.setText(textState);
        txtActionRespone.setTextColor(color);
    }

    public static void showStateReceptor(int stateAction, AppCompatButton btnConfirm, AppCompatButton btnDeny, TextView txtActionRespone, Resources resources) {

        if (stateAction == STATE_NO_ACTION) {
            hideActionResponse(txtActionRespone);
            hideButtonActions(btnConfirm, btnDeny);
            return;
        }

        if (stateAction == STATE_WAITING) {
            showButtonActions(btnConfirm, btnDeny);
            hideActionResponse(txtActionRespone);
        } else {
            hideButtonActions(btnConfirm, btnDeny);
            showActionResponse(txtActionRespone);
            setActionResponse(stateAction, txtActionRespone, resources);
        }
    }


    public static void setVisibility(AppCompatButton btnConfirm, AppCompatButton btnDeny, int visibility) {
        btnConfirm.setVisibility(visibility);
        btnDeny.setVisibility(visibility);
    }

    private static void showButtonActions(AppCompatButton btnConfirm, AppCompatButton btnDeny) {
        setVisibility(btnConfirm, btnDeny, View.VISIBLE);
    }

    private static void hideButtonActions(AppCompatButton btnConfirm, AppCompatButton btnDeny) {
        setVisibility(btnConfirm, btnDeny, View.GONE);
    }


    public static void hideActionResponse(TextView textState) {
        textState.setVisibility(View.GONE);
    }

    public static void showActionResponse(TextView textState) {
        textState.setVisibility(View.VISIBLE);
    }


    public static Drawable getDrawableFromMessageStatus(int status, Context context) {
        int drawableResStatus = R.drawable.ic_access_time;
        switch (status) {
            case ChatMessage.STATUS_WRITED:
                drawableResStatus = R.drawable.ic_access_time;
                break;
            case ChatMessage.STATUS_SEND:
                drawableResStatus = R.drawable.ic_single_check;
                break;
            case ChatMessage.STATUS_DELIVERED:
                drawableResStatus = R.drawable.ic_double_check;
                break;
            case ChatMessage.STATUS_READED:
                drawableResStatus = R.drawable.ic_double_check_colored;
                break;
        }
        return ContextCompat.getDrawable(context, drawableResStatus);
    }

    public static void setTimeTitleVisibility(long ts1, long ts2, TextView timeText, Resources resources) {

        if (ts2 == 0) {
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(TimeUtils.formatDate(ts1, resources));
        } else {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTimeInMillis(ts1);
            cal2.setTimeInMillis(ts2);

            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);

            if (sameDay) {
                timeText.setVisibility(View.GONE);
                timeText.setText("");
            } else {
                timeText.setVisibility(View.VISIBLE);
                timeText.setText(TimeUtils.formatDate(ts1, resources));
            }

        }
    }
}
