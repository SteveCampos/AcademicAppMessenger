package com.consultoraestrategia.messengeracademico.infoRubro;

import android.os.Bundle;

import com.consultoraestrategia.messengeracademico.infoRubro.domain.useCase.TransformarJsonRubroObjeto;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Row;
import com.consultoraestrategia.messengeracademico.infoRubro.ui.IndoRubroFragment;
import com.consultoraestrategia.messengeracademico.infoRubro.ui.InfoRubroView;
import com.consultoraestrategia.messengeracademico.infoRubro.domain.wrapper.RubroUIFIrebase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jse on 15/09/2018.
 */

public class InfoRubroPresenterImpl implements InfoRubroPresenter {
    private InfoRubroView view;
    String json;
    private RubroUIFIrebase rubroUIFIrebase;
    private TransformarJsonRubroObjeto transformarJsonRubroObjeto;

    public InfoRubroPresenterImpl(TransformarJsonRubroObjeto transformarJsonRubroObjeto) {
        this.transformarJsonRubroObjeto = transformarJsonRubroObjeto;
    }

    @Override
    public void setExtras(Bundle extras) {
        json = extras.getString(IndoRubroFragment.BUNDLE_JSON_RUBRO);
    }

    @Override
    public void attachView(InfoRubroView view) {
        this.view = view;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {
        showTableView();
    }

    private void showTableView() {
        transformarJsonRubroObjeto.excute(new TransformarJsonRubroObjeto.Request(json),
                new TransformarJsonRubroObjeto.Callback() {
                    @Override
                    public void onSuccess(TransformarJsonRubroObjeto.Response response) {
                        if(view!=null)view.showTableView(response.getCellListList(), response.getColumnList(), response.getRowList());
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onViewCreated() {

    }

    @Override
    public void onDestroyView() {
        view = null;
    }
}
