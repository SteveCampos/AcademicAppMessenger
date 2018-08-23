package com.consultoraestrategia.messengeracademico.edittext;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.consultoraestrategia.messengeracademico.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EdittextDialogFragment extends DialogFragment {

    public static final String TAG = "EdittextDialogFragment";

    @BindView(R.id.textTitle)
    TextView textTitle;
    @BindView(R.id.edt)
    TextInputEditText edt;
    @BindView(R.id.til)
    TextInputLayout til;
    Unbinder unbinder;
    private EditextDialogListener listener;

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_TEXT = "extra_text";
    public static final String EXTRA_HINTRES = "extra_hintres";
    public static final String EXTRA_REQUESTCODE = "extra_request_code";
    public static final String EXTRA_INPUT_TYPE = "extra_input_type";

    public static EdittextDialogFragment newInstance(String text, int inputType, String title, String hintRes, int requestCode) {
        EdittextDialogFragment fragment = new EdittextDialogFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_INPUT_TYPE, inputType);
        args.putString(EXTRA_TEXT, text);
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_HINTRES, hintRes);
        args.putInt(EXTRA_REQUESTCODE, requestCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_edittext, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args == null) return;
        int inputType = args.getInt(EXTRA_INPUT_TYPE);
        String text = args.getString(EXTRA_TEXT);
        String title = args.getString(EXTRA_TITLE);
        String hintRes = args.getString(EXTRA_HINTRES);
        int requestCode = args.getInt(EXTRA_REQUESTCODE);
        setArgs(text, inputType, title, hintRes, requestCode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof EditextDialogListener) {
            listener = (EditextDialogListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void onSubmitClicked() {
        dismiss();
        if (listener != null) {
            listener.onTextSubmit(edt.getText().toString(), requestCode);
        }
    }

    private void onCancelClicked() {
        dismiss();
    }


    private String text;
    private String titleRes;
    private String hintRes;
    private int requestCode = -1;

    private void setArgs(String text, int inputType, String titleRes, String hintRes, int requestCode) {
        this.text = text;

        this.titleRes = titleRes;
        this.hintRes = hintRes;
        this.requestCode = requestCode;

        setText(text);
        setTitle(titleRes);
        setEdtHint(hintRes);
        setInputType(inputType);
    }

    private void setInputType(int inputType) {
        edt.setInputType(inputType);
    }

    private void setText(String text) {
        if (!TextUtils.isEmpty(text)) {
            edt.setText(text);
        }
    }

    private void setTitle(String titleRes) {
        textTitle.setText(titleRes);
    }

    private void setEdtHint(String hintRes) {
        til.setHint(hintRes);
    }

    @OnClick({R.id.bttn_cancel, R.id.bttn_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bttn_cancel:
                onCancelClicked();
                break;
            case R.id.bttn_ok:
                onSubmitClicked();
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
