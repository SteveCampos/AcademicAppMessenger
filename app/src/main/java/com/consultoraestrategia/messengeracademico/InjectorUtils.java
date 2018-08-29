package com.consultoraestrategia.messengeracademico;

import com.consultoraestrategia.messengeracademico.data.ChatRepository;
import com.consultoraestrategia.messengeracademico.data.source.local.ChatLocalDataSource;
import com.consultoraestrategia.messengeracademico.data.source.remote.ChatRemoteDataSource;
import com.consultoraestrategia.messengeracademico.domain.FirebaseChat;
import com.consultoraestrategia.messengeracademico.postEvent.ChatListPostEventImpl;
import com.consultoraestrategia.messengeracademico.postEvent.ChatPostEventImpl;
import com.consultoraestrategia.messengeracademico.storage.ChatDbFlowStorage;
import com.consultoraestrategia.messengeracademico.storage.ChatStorageImpl;
import com.google.firebase.auth.FirebaseAuth;

public class InjectorUtils {

    public static ChatRepository getChatRepository() {
        return ChatRepository.getInstance(
                new ChatLocalDataSource(new ChatStorageImpl(new ChatDbFlowStorage()), FirebaseAuth.getInstance().getCurrentUser()),
                new ChatRemoteDataSource(FirebaseChat.getInstance(), com.consultoraestrategia.messengeracademico.domain.FirebaseUser.getInstance(), FirebaseAuth.getInstance().getCurrentUser()),
                ChatListPostEventImpl.getInstance(),
                ChatPostEventImpl.getInstance(),
                FirebaseAuth.getInstance().getCurrentUser());
    }
}
