package com.consultoraestrategia.messengeracademico.infoRubro.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.infoRubro.InfoRubroPresenter;
import com.consultoraestrategia.messengeracademico.infoRubro.InfoRubroPresenterImpl;
import com.consultoraestrategia.messengeracademico.infoRubro.adapter.InfoRubroTableViewAdapter;
import com.consultoraestrategia.messengeracademico.infoRubro.adapter.holder.CornerHolder;
import com.consultoraestrategia.messengeracademico.infoRubro.domain.useCase.TransformarJsonRubroObjeto;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Alumno;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Cell;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Column;
import com.consultoraestrategia.messengeracademico.infoRubro.entities.Row;
import com.evrencoskun.tableview.TableView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jse on 15/09/2018.
 */

public class IndoRubroFragment extends DialogFragment implements InfoRubroView {

    @BindView(R.id.text_alumn_name)
    TextView textAlumnName;
    @BindView(R.id.img_alumn_profile)
    CircleImageView imgAlumnProfile;
    @BindView(R.id.edt_puntos)
    TextInputEditText edtPuntos;
    @BindView(R.id.til_puntos)
    TextInputLayout tilPuntos;
    @BindView(R.id.edt_nota)
    TextInputEditText edtNota;
    @BindView(R.id.til_nota)
    TextInputLayout tilNota;
    @BindView(R.id.edt_desempenio)
    TextInputEditText edtDesempenio;
    @BindView(R.id.til_desempenio)
    TextInputLayout tilDesempenio;
    @BindView(R.id.edt_logro)
    TextInputEditText edtLogro;
    @BindView(R.id.til_logro)
    TextInputLayout tilLogro;
    @BindView(R.id.table)
    TableView table;
    @BindView(R.id.txt_contador)
    TextView txtContador;
    @BindView(R.id.text_alumn_lastname)
    TextView textAlumnLastname;
    @BindView(R.id.root)
    ConstraintLayout root;
    @BindView(R.id.txt_rubrica)
    TextView txtRubrica;
    @BindView(R.id.txt_curso)
    TextView txtCurso;
    private Unbinder unbinder;
    private InfoRubroTableViewAdapter adapter;
    private InfoRubroPresenter presenter;

    public static final String BUNDLE_JSON_RUBRO = "jsonRubro";

    public static IndoRubroFragment newInstance(String jsonRubro) {
        Bundle args = new Bundle();
        args.putString(BUNDLE_JSON_RUBRO, jsonRubro);
        IndoRubroFragment fragment = new IndoRubroFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eval_rubrica_bidimencionale_grupales, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.getDialog().requestWindowFeature(STYLE_NO_TITLE);
        super.onViewCreated(view, savedInstanceState);
        setupAdapter();

        setupPresenter();
    }

    private void setupPresenter() {
        presenter = new InfoRubroPresenterImpl(new TransformarJsonRubroObjeto());
        setPresenter(presenter);
    }

    private void setupAdapter() {
        this.adapter = new InfoRubroTableViewAdapter(getContext());
        table.setAdapter(adapter);
        table.setIgnoreSelectionColors(false);
        table.setHasFixedWidth(false);
        table.setIgnoreSelectionColors(true);
    }

    @Override
    public void setPresenter(InfoRubroPresenter presenter) {
        presenter.setExtras(getArguments());
        presenter.attachView(this);
        presenter.onViewCreated();
    }

    @Override
    public void showTableView(List<List<Cell>> cellListList, List<Column> columnList, List<Row> rowList) {
        adapter = new InfoRubroTableViewAdapter(getActivity());
        table.setAdapter(adapter);
        table.setHasFixedWidth(false);
        table.setIgnoreSelectionColors(true);
        CornerHolder cornerHolder = adapter.getViewHolder();
        if (cornerHolder != null) cornerHolder.bind("Hi ;)");
        adapter.setAllItems(columnList, rowList, cellListList);
    }

    @Override
    public void setPuntos(String puntos) {
        edtPuntos.setText(puntos);
    }

    @Override
    public void setAlumno(Alumno alumno) {
        textAlumnName.setText(alumno.getNombre());
        textAlumnLastname.setText(alumno.getApellido());
        Glide.with(this)
                .load(alumno.getUrlImg())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_info_outline_black_24dp)
                .into(imgAlumnProfile);
    }

    @Override
    public void setDesempenio(String desempenio) {
        edtDesempenio.setText(desempenio);
    }

    @Override
    public void setNota(String nota) {
        edtNota.setText(nota);
    }

    @Override
    public void setLogro(String logro) {
        edtLogro.setText(logro);
    }

    @Override
    public void setNombreRubrica(String nombreRubrica) {
        txtRubrica.setText(nombreRubrica);
    }

    @Override
    public void setNombreCurso(String nombreCurso) {
        txtCurso.setText(nombreCurso);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        this.getDialog().getWindow().
                setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @OnClick(R.id.btnRetroceder)
    public void onViewClicked() {
        dismiss();
    }
}
