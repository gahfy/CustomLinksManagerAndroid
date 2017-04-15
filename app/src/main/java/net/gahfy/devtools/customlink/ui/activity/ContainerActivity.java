package net.gahfy.devtools.customlink.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import net.gahfy.devtools.customlink.R;
import net.gahfy.devtools.customlink.data.model.CustomLink;
import net.gahfy.devtools.customlink.ui.base.BaseActivity;
import net.gahfy.devtools.customlink.ui.base.BaseFragment;
import net.gahfy.devtools.customlink.ui.custom_link.CustomLinkAddUpdateFragment;
import net.gahfy.devtools.customlink.ui.custom_link.CustomLinkListFragment;
import net.gahfy.devtools.customlink.ui.custom_link.CustomLinkListPresenter;

public class ContainerActivity extends BaseActivity {
    private enum EnterMode{
        POP_FROM_BOTTOM
    }

    private FragmentManager mFragmentManager;
    private MenuItem.OnMenuItemClickListener mOnMenuItemClickListener;

    private Menu mMenu;

    private boolean isMenuDoneVisible = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.global_menu, mMenu);
        mMenu.findItem(R.id.menu_done).setVisible(isMenuDoneVisible);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        return (mOnMenuItemClickListener != null && mOnMenuItemClickListener.onMenuItemClick(menuItem))
            || super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_container);

        mFragmentManager = getSupportFragmentManager();
        if(mFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            addFragment(new CustomLinkListFragment(), false, null);
        }
    }

    public void setMenuDoneVisible(boolean visible){
        if(mMenu != null)
            mMenu.findItem(R.id.menu_done).setVisible(visible);
        isMenuDoneVisible = visible;
    }

    public void setMenuItemClickListener(MenuItem.OnMenuItemClickListener onMenuItemClickListener){
        mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void addUpdateFragment(CustomLink customLink, int position, CustomLinkListPresenter parentPresenter){
        CustomLinkAddUpdateFragment customLinkAddUpdateFragment = new CustomLinkAddUpdateFragment();
        customLinkAddUpdateFragment.setCustomLink(customLink, position);
        customLinkAddUpdateFragment.setParentPresenter(parentPresenter);
        addFragment(customLinkAddUpdateFragment, true, EnterMode.POP_FROM_BOTTOM);
    }

    private void addFragment(BaseFragment baseFragment, boolean addToBackStack, EnterMode enterMode){
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if(enterMode != null){
            switch(enterMode){
                case POP_FROM_BOTTOM:
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom,
                            R.anim.enter_from_bottom, R.anim.exit_to_bottom);
            }
        }
        fragmentTransaction.add(R.id.fragment_container, baseFragment);
        if(addToBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
