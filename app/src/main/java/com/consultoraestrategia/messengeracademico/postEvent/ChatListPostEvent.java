package com.consultoraestrategia.messengeracademico.postEvent;

import com.consultoraestrategia.messengeracademico.entities.Chat;

/**
 * Created by @stevecampos on 24/04/2017.
 */

public interface ChatListPostEvent {
    void post(Chat chat);
}
