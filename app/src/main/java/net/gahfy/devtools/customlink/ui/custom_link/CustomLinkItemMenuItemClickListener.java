package net.gahfy.devtools.customlink.ui.custom_link;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;

import net.gahfy.devtools.customlink.R;
import net.gahfy.devtools.customlink.data.model.CustomLink;
import net.gahfy.devtools.customlink.util.view.ErrorViewUtils;

class CustomLinkItemMenuItemClickListener implements PopupMenu.OnMenuItemClickListener{
    private CustomLinkListPresenter mPresenter;
    private CustomLink mCustomLink;
    private Context mContext;

    CustomLinkItemMenuItemClickListener(Context context, CustomLinkListPresenter presenter) {
        mPresenter = presenter;
        mContext = context;
    }

    public void setData(CustomLink customLink){
        mCustomLink = customLink;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        try {
            switch (menuItem.getItemId()) {
                case R.id.menu_ongoing_notification:
                    mPresenter.notifyCustomLink(mCustomLink);
                    return true;
                case R.id.menu_cancel_notification:
                    mPresenter.cancelNotification(mCustomLink);
                    return true;
                case R.id.menu_edit:
                    mPresenter.update(mCustomLink);
                    return true;
                case R.id.menu_delete:
                    mPresenter.deleteCustomLink(mCustomLink);
                    return true;
                default:
            }
            return false;
        }
        catch(NullPointerException e){
            ErrorViewUtils.showErrorToast(mContext, R.string.technical_error, e);
            return false;
        }
    }
}
