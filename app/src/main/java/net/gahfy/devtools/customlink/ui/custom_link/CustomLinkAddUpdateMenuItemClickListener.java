package net.gahfy.devtools.customlink.ui.custom_link;

import android.content.Context;
import android.view.MenuItem;

import net.gahfy.devtools.customlink.R;
import net.gahfy.devtools.customlink.util.view.ErrorViewUtils;

class CustomLinkAddUpdateMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
    private CustomLinkAddUpdatePresenter mPresenter;
    private Context mContext;

    CustomLinkAddUpdateMenuItemClickListener(Context context, CustomLinkAddUpdatePresenter presenter){
        mPresenter = presenter;
        mContext = context;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.menu_done:
                    mPresenter.submitForm();
                    return true;
            }
            return false;
        }
        catch(NullPointerException e){
            ErrorViewUtils.showErrorToast(mContext, R.string.technical_error, e);
            return false;
        }
    }
}
