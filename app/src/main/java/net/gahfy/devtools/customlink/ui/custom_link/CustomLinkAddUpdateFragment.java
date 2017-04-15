package net.gahfy.devtools.customlink.ui.custom_link;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;

import net.gahfy.devtools.customlink.R;
import net.gahfy.devtools.customlink.data.model.CustomLink;
import net.gahfy.devtools.customlink.ui.activity.ContainerActivity;
import net.gahfy.devtools.customlink.ui.base.BaseFragment;
import net.gahfy.devtools.customlink.util.view.ErrorViewUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CustomLinkAddUpdateFragment extends BaseFragment implements CustomLinkAddUpdateView {
    @Inject
    public CustomLinkAddUpdatePresenter mPresenter;
    @BindView(R.id.lyt_txt_custom_link_uri)
    TextInputLayout lytTxtCustomLinkUri;
    @BindView(R.id.txt_custom_link_title)
    EditText txtCustomLinkTitle;
    @BindView(R.id.txt_custom_link_uri)
    EditText txtCustomLinkUri;
    private Unbinder mUnbinder;

    private CustomLink mCustomLink;
    private int mPosition;
    private CustomLinkListPresenter mParentPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_link_add_update, container, false);

        mUnbinder = ButterKnife.bind(this, view);
        getComponent().inject(this);
        try {
            mPresenter.bind(this);
        }
        catch(NullPointerException e){
            ErrorViewUtils.showErrorToast(getContext(), R.string.technical_error, e);
        }
        txtCustomLinkUri.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    mPresenter.onUriTextChanged(s.toString());
                }
                catch(NullPointerException e){
                    ErrorViewUtils.showErrorToast(getContext(), R.string.technical_error, e);
                }
            }
        });
        return view;
    }

    @Override
    public void setForm(String title, String uri){
        txtCustomLinkTitle.setText(title);
        txtCustomLinkUri.setText(uri);
        txtCustomLinkUri.post(new Runnable() {
            @Override
            public void run() {
                EditText focusedEditText;
                if(!txtCustomLinkTitle.getText().toString().equals("") || !txtCustomLinkTitle.getText().toString().equals("")) {
                    focusedEditText = txtCustomLinkUri;
                }
                else {
                    focusedEditText = txtCustomLinkTitle;
                }
                focusedEditText.requestFocus();
                focusedEditText.setSelection(focusedEditText.getText().length());
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(focusedEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        txtCustomLinkTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_NEXT){
                    txtCustomLinkUri.requestFocus();
                    txtCustomLinkUri.setSelection(txtCustomLinkUri.getText().length());
                }
                return false;
            }
        });

        txtCustomLinkUri.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    try {
                        mPresenter.submitForm();
                    }
                    catch(NullPointerException e){
                        ErrorViewUtils.showErrorToast(getContext(), R.string.technical_error, e);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public ContainerActivity getContainerActivity() {
        if(getActivity() instanceof ContainerActivity)
            return (ContainerActivity) getActivity();
        return null;
    }

    @Override
    public void onDestroyView(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtCustomLinkUri.getWindowToken(), 0);
        mUnbinder.unbind();
        try {
            mPresenter.unbind();
        }
        catch(NullPointerException e){
            FirebaseCrash.report(e);
        }
        mCustomLink = null;
        mParentPresenter = null;
        super.onDestroyView();
    }

    public void setCustomLink(CustomLink customLink, int position){
        mCustomLink = customLink;
        mPosition = position;
    }

    @Override
    public CustomLink getCustomLink() {
        return mCustomLink;
    }

    @Override
    public int getPosition(){
        return mPosition;
    }

    @Override
    public CustomLinkListPresenter getParentPresenter(){
        return mParentPresenter;
    }

    public void setParentPresenter(CustomLinkListPresenter parentPresenter) {
        mParentPresenter = parentPresenter;
    }

    @Override
    public String getCustomLinkTitle() {
        return txtCustomLinkTitle.getText().toString();
    }

    @Override
    public String getCustomLinkUri() {
        return txtCustomLinkUri.getText().toString();
    }

    @Override
    public void setError(String error) {
        lytTxtCustomLinkUri.setError(error);
    }
}
