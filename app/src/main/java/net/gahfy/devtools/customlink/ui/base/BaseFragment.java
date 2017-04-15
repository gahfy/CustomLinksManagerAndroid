package net.gahfy.devtools.customlink.ui.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import net.gahfy.devtools.customlink.DevToolsApplication;
import net.gahfy.devtools.customlink.data.component.BaseViewComponent;
import net.gahfy.devtools.customlink.data.component.DaggerBaseViewComponent;
import net.gahfy.devtools.customlink.data.module.PresenterModule;

public abstract class BaseFragment extends Fragment implements BaseView {
    private BaseActivity mBaseActivity;

    public BaseFragment(){
        super();
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof BaseActivity)
            mBaseActivity = (BaseActivity) context;
        else
            throw new RuntimeException("BaseFragment must be attached to BaseActivity");
    }

    protected BaseViewComponent getComponent(){
        return DaggerBaseViewComponent.builder()
                .presenterModule(new PresenterModule())
                .build();
    }

    @Override
    @Nullable
    public DevToolsApplication getDevToolsApplication() {
        return mBaseActivity == null ? null : mBaseActivity.getDevToolsApplication();
    }

    @Override
    public void setTitle(String title) {
        if(mBaseActivity != null)
            mBaseActivity.setTitle(title);
    }
}
