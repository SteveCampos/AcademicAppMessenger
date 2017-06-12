package com.consultoraestrategia.messengeracademico;

/**
 * Created by @stevecampos on 15/05/2017.
 */

public interface BaseView<T extends BasePresenter> {
    void setPresenter(T presenter);
}
