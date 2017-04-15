package net.gahfy.devtools.customlink.data.component;

import net.gahfy.devtools.customlink.data.module.PresenterModule;
import net.gahfy.devtools.customlink.ui.custom_link.CustomLinkAddUpdateFragment;
import net.gahfy.devtools.customlink.ui.custom_link.CustomLinkListFragment;

import dagger.Component;

/**
 * This class is the component for base views. It injects the PresenterModule in the specified base
 * views in the inject methods.
 * @see PresenterModule
 */
@Component(modules = {PresenterModule.class})
public interface BaseViewComponent {
    /**
     * Injects the component modules in the specified CustomLinkListFragment.
     * @param customLinkListFragment the CustomLinkListFragment in which to inject the component
     *                               modules
     */
    void inject(CustomLinkListFragment customLinkListFragment);

    /**
     * Injects the component modules in the specified CustomLinkAddUpdateFragment.
     * @param customLinkAddUpdateFragment the CustomLinkAddUpdateFragment in which to inject the
     *                                    component modules
     */
    void inject(CustomLinkAddUpdateFragment customLinkAddUpdateFragment);
}
