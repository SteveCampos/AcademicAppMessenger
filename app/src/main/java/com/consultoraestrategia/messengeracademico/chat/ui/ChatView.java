package com.consultoraestrategia.messengeracademico.chat.ui;

import com.consultoraestrategia.messengeracademico.BaseView;
import com.consultoraestrategia.messengeracademico.chat.ChatPresenter;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;

import java.util.List;

/**
 * Created by @stevecampos on 9/03/2017.
 */

public interface ChatView extends BaseView<ChatPresenter> {


    void showProgress();

    void hideProgress();

    void showReceptor(Contact receptor);

    void addMessages(List<ChatMessage> messages);

    void addMessage(ChatMessage message);

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
}
