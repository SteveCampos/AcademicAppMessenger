package com.consultoraestrategia.messengeracademico.postEvent;

import com.consultoraestrategia.messengeracademico.entities.ChatMessage;

/**
 * Created by @stevecampos on 24/04/2017.
 */

public interface ChatPostEvent {
    void post(ChatMessage message);
}
