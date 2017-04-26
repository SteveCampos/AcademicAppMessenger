package com.consultoraestrategia.messengeracademico.lib;

/**
 * Created by @stevecampos on 17/02/2017.
 */

public interface EventBus {
    void register(Object suscriber);
    void unregister(Object suscriber);
    void post(Object event);
}
