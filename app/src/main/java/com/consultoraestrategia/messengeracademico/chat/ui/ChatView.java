package com.consultoraestrategia.messengeracademico.chat.ui;

import com.consultoraestrategia.messengeracademico.base.BaseView;
import com.consultoraestrategia.messengeracademico.base.actionMode.BaseActionModeView;
import com.consultoraestrategia.messengeracademico.base.actionMode.SelectMode;
import com.consultoraestrategia.messengeracademico.chat.ChatPresenter;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

/**
 * Created by @stevecampos on 9/03/2017.
 */

public interface ChatView extends BaseActionModeView<ChatPresenter, ChatMessage> {

    void showReceptor(Contact receptor);

    void addMessages(List<ChatMessage> messages);

    void addMessage(ChatMessage message);

    void updateMessage(ChatMessage message);

    void sendMessageText();

    void showConnection(Connection connection);



    void showReceptorAction(Contact receptor, String action);

    void startPickImageActivity();

    void showError(String error);

    void showVoiceIcon();

    void showSendIcon();

    void hideKeboard();

    void showKeyboard();

    void toggleMenuWithEffect();

    void finishActivity();

    void showFatalError(String error);

    void showAcademicInformation(String data);

    void showDialogToConfirmOfficialMessage(ChatMessage message);

    void showDialogToEnsureDenyOfficialMessage(ChatMessage message);

    void dismissNotification(int id);

    void showButtomToScroll(int count);

    void addMoreMessages(List<ChatMessage> messages);

    void showContactInPhone(String phoneNumber);

    void copyText(String textToCopy);

    void removeMessage(ChatMessage message);

    void showFullScreenImg(String messageUri);

    void showInfoRubro(String messageText);
}
