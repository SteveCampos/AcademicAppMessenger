package com.consultoraestrategia.messengeracademico.base;

import android.support.annotation.StringRes;

import com.consultoraestrategia.messengeracademico.edittext.EditextDialogListener;

/**
 * Created by @stevecampos on 15/05/2017.
 */

public interface BaseView<T extends BasePresenter> extends EditextDialogListener {

    void setPresenter(T presenter);

    void showProgress();

    void hideProgress();

    void showMessage(@StringRes int message);

    void showMessage(CharSequence message);

    void showImportantMessage(CharSequence message);

    void showFinalMessage(CharSequence message);

    void showEdittextDialog(String text, int inputType, String title, String hint, int requestCode);
}
