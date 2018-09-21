package com.consultoraestrategia.messengeracademico.main;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.consultoraestrategia.messengeracademico.InjectorUtils;
import com.consultoraestrategia.messengeracademico.base.BasePresenter;
import com.consultoraestrategia.messengeracademico.base.BasePresenterImpl;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.UseCase;
import com.consultoraestrategia.messengeracademico.UseCaseHandler;
import com.consultoraestrategia.messengeracademico.base.actionMode.BasePresenterActionModeImpl;
import com.consultoraestrategia.messengeracademico.entities.Chat;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.entities.GlobalSettings;
import com.consultoraestrategia.messengeracademico.importData.events.ImportDataEvent;
import com.consultoraestrategia.messengeracademico.lib.EventBus;
import com.consultoraestrategia.messengeracademico.main.domain.usecase.DeleteChat;
import com.consultoraestrategia.messengeracademico.main.domain.usecase.ListenForUserMessages;
import com.consultoraestrategia.messengeracademico.main.event.MainEvent;
import com.consultoraestrategia.messengeracademico.main.ui.MainView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.consultoraestrategia.messengeracademico.notification.MyFirebaseInstanceIdService.USER_TOPIC;

/**
 * Created by stevecampos on 27/07/2017.
 */

public class MainPresenterImpl extends BasePresenterActionModeImpl<MainView, Chat> implements MainPresenter {

    private static final String TAG = MainPresenterImpl.class.getSimpleName();

    private final ListenForUserMessages listenForUserMessages;
    //private final EventBus eventBus;
    private final ConnectionInteractor connectionInteractor;
    private final Long timestamp;

    private boolean forwardToAnotherActivity = false;
    private final FirebaseUser mainUser;

    public MainPresenterImpl(UseCaseHandler useCaseHandler, ListenForUserMessages listenForUserMessages, EventBus eventBus, ConnectionInteractor connectionInteractor, Long timestamp, FirebaseUser mainUser, Resources res) {
        super(useCaseHandler, res, eventBus);
        this.listenForUserMessages = listenForUserMessages;
        //this.eventBus = eventBus;
        this.connectionInteractor = connectionInteractor;
        this.timestamp = timestamp;
        this.mainUser = mainUser;
    }

    @Override
    public void setExtras(Bundle extras) {
        super.setExtras(extras);
    }

    @Override
    protected String getTag() {
        return null;
    }

    @Override
    protected BasePresenter<MainView> getPresenterImpl() {
        return this;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        //eventBus.register(this);
        view.checkGooglePlayServicesAvailable();
        suscribeToNotifications(mainUser);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        forwardToAnotherActivity = false;
        connectionInteractor.setOnline();
        view.checkGooglePlayServicesAvailable();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        if (!forwardToAnotherActivity) {
            connectionInteractor.setOffline();
        }
    }


    @Override
    public void listenForMessages() {
        Log.d(TAG, "listenForAllUserMessages");
        handler.execute(
                listenForUserMessages,
                new ListenForUserMessages.RequestValues(),
                new UseCase.UseCaseCallback<ListenForUserMessages.ResponseValue>() {
                    @Override
                    public void onSuccess(ListenForUserMessages.ResponseValue response) {
                    }

                    @Override
                    public void onError() {

                    }
                });
        suscribeToNotifications(mainUser);
    }

    @Override
    public void suscribeToNotifications(FirebaseUser mainUser) {
        Log.d(TAG, "suscribeToNotifications");
        FirebaseMessaging.getInstance()
                .subscribeToTopic(USER_TOPIC + mainUser.getUid());

    }

    @Override
    public void onActionImportClicked() {
        showToolbarProgress();
        if (view != null) {
            view.startImportDataJobService();
        }
    }

    @Override
    public void onActionDeleteClicked() {
        Log.d(TAG, "onActionDeleteClicked: ");
        List<Chat> chatsToDelete = getItemsSelected();
        for (Chat chat :
                chatsToDelete) {

            handler.execute(
                    new DeleteChat(InjectorUtils.getChatRepository()),
                    new DeleteChat.RequestValues(chat),
                    new UseCase.UseCaseCallback<DeleteChat.ResponseValue>() {
                        @Override
                        public void onSuccess(DeleteChat.ResponseValue response) {
                            Chat chat = response.getChat();
                            removeChat(chat);
                            removeItemFromSelectedMode(chat);
                        }

                        @Override
                        public void onError() {
                            showMessage(R.string.error_removing_chat);
                        }
                    }
            );
        }
    }

    @Override
    public void onServerMenuClicked() {
        Log.d(TAG, "onServerMenuClicked");
        if (view == null) return;
            List<GlobalSettings> serverList = new ArrayList<>();
            serverList.add(GlobalSettings.getNewInstance(GlobalSettings.Servers.LOCAL));
            serverList.add(GlobalSettings.getNewInstance(GlobalSettings.Servers.PRUEBAS));
            serverList.add(GlobalSettings.getNewInstance(GlobalSettings.Servers.PRODUCCION));
            serverList.add(GlobalSettings.getNewInstance(GlobalSettings.Servers.OTRO));
            String serverUrl = GlobalSettings.getServerUrl();
            int positionSelected = -1;
            if (!TextUtils.isEmpty(serverUrl)) {
                for (int i = 0; i < serverList.size(); i++) {
                    String url = serverList.get(i).getUrlServer();
                    if (serverUrl.equals(url)) {
                        positionSelected = i;
                        break;
                    }
                }
            }

            view.showListSingleChooser(res.getString(R.string.dialog_title_serverlist), serverList, positionSelected);
    }

    @Override
    public void onSelectedServidor(GlobalSettings objectSelected, int selectedPosition) {
            if (objectSelected.getNombre().equals(GlobalSettings.Servers.OTRO.getNombre())) {
                showEdtDialog();
                return;
            }

            boolean success = objectSelected.save();
            if (!success) {
                showImportantMessage(res.getString(R.string.error_server_url_saving));
            }

    }

    @Override
    public void onCustomUrlOk(String url) {
        GlobalSettings.Servers servers = GlobalSettings.Servers.OTRO;
        GlobalSettings otro = GlobalSettings.getNewInstance(servers);
        otro.setUrlServer(url + servers.getPath());
        otro.save();
    }

    private void showEdtDialog() {
        if (view != null) view.showEdtDialog();

    }

    private void removeChat(Chat chat) {
        if (view != null) view.removeChat(chat);
    }

    @Subscribe
    public void onEventMainThread(MainEvent event) {
        Log.d(TAG, "onEventMainThread MainEvent: " + event);
        switch (event.getType()) {
            case MainEvent.TYPE_LAUNCH_CHAT:
                launchChat(event.getContact());
                break;
            case MainEvent.TYPE_FIRE_NOTIFICATION:
                fireNotification(event.getMessage());
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(ImportDataEvent event) {
        Log.d(TAG, "onEventMainThread ImportDataEvent: " + event);
        switch (event.getType()) {
            case ImportDataEvent.OnImportFinish:
                onImportDataFinished();
                break;
            case ImportDataEvent.OnImportError:
                onImportDataError();
                break;
        }

    }

    private void showToolbarProgress() {
        if (view != null) {
            view.showToolbarProgress();
        }
    }

    private void hideToolbarProgress() {
        if (view != null) {
            view.hideToolbarProgress();
        }
    }

    private void onImportDataError() {
        hideToolbarProgress();
        showMessage(R.string.activity_importdata_error);
    }

    private void onImportDataFinished() {
        hideToolbarProgress();
        //showMessage(R.string.activity_importdata_success);
        reloadContacts();
    }

    private void reloadContacts() {
        if (view != null) {
            view.reloadContacts();
        }
    }

    private void fireNotification(ChatMessage message) {
        Log.d(TAG, "fireNotification");
        if (view != null) {
            int retval = timestamp.compareTo(message.getTimestamp());
            Log.d(TAG, "retveal: " + retval);
            if (retval <= 0) {
                //The messages timestamp is greather than the timestamp when the presenter was created!
                //then the message needs to convert into a notification fire!
                view.fireNotification(message);
            }
        }

    }

    private void launchChat(Contact contact) {
        Log.d(TAG, "launchChat");
        if (view != null) {
            forwardToAnotherActivity = true;
            view.startChat(contact);
        }
    }


    @Override
    protected void updateItem(Chat item) {
        updateChat(item);
    }

    @Override
    protected int getMenuActionMode() {
        return R.menu.menu_main_action_mode;
    }

    private void updateChat(Chat chat) {
        if (view != null) view.updateChat(chat);
    }

    @Override
    public void onClick(Chat chat, View view) {
        if (!isInSelectedMode()) {
            Contact emisor = chat.getEmisor();
            Contact receptor = chat.getReceptor();
            if (mainUser == null || emisor == null || receptor == null) {
                return;
            }
            if (mainUser.getUid().equals(emisor.getUid())) {
                launchChat(chat.getReceptor());
            } else if (mainUser.getUid().equals(receptor.getUid())) {
                launchChat(chat.getEmisor());
            }
            return;
        }

        super.onClick(chat, view);
    }
}
