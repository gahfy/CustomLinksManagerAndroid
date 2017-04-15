package net.gahfy.devtools.customlink.ui.custom_link;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gahfy.devtools.customlink.R;
import net.gahfy.devtools.customlink.ui.base.BaseFragment;
import net.gahfy.devtools.customlink.ui.activity.ContainerActivity;
import net.gahfy.devtools.customlink.util.view.ErrorViewUtils;
import net.gahfy.devtools.customlink.util.view.RecyclerViewUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CustomLinkListFragment extends BaseFragment implements CustomLinkListView {
    @BindView(R.id.rv_custom_link_list)
    RecyclerView rvCustomLinkList;

    @BindView(R.id.fab_add_custom_link)
    FloatingActionButton fabAddCustomLink;

    @BindView(R.id.lbl_empty_state)
    TextView lblEmptyState;

    @Inject
    CustomLinkListPresenter mPresenter;

    Unbinder mUnbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_link_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        getComponent().inject(this);
        try {
            mPresenter.bind(this);
        }
        catch(NullPointerException e){
            ErrorViewUtils.showErrorToast(getContext(), R.string.technical_error, e);
        }

        fabAddCustomLink.setRippleColor(ContextCompat.getColor(getContext(), R.color.fabRipple));

        return view;
    }

    @Override
    public void setEmptyStateVisibility(boolean visible){
        lblEmptyState.setVisibility(visible ? View.VISIBLE : View.GONE);
    }


    @OnClick(R.id.fab_add_custom_link)
    public void onAddButtonClick(){
        try {
            mPresenter.onAddButtonClick();
        }
        catch(NullPointerException e){
            ErrorViewUtils.showErrorToast(getContext(), R.string.technical_error, e);
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mUnbinder.unbind();
        try {
            mPresenter.unbind();
        }
        catch(NullPointerException e){
            // Do nothing (we are leaving the view, no matter)
        }
    }

    @Override
    public void setAdapter(CustomLinkListAdapter customLinkListAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvCustomLinkList.setLayoutManager(linearLayoutManager);
        rvCustomLinkList.addItemDecoration(new RecyclerViewUtils.DividerItemDecoration(getActivity(), R.drawable.recyclerview_divider));
        rvCustomLinkList.setAdapter(customLinkListAdapter);
    }

    @Override
    public ContainerActivity getContainerActivity() {
        if(getActivity() instanceof ContainerActivity)
            return (ContainerActivity) getActivity();
        return null;
    }
}
