package net.gahfy.devtools.customlink.data.component;

import net.gahfy.devtools.customlink.data.module.DatabaseModule;
import net.gahfy.devtools.customlink.ui.custom_link.CustomLinkAddUpdatePresenter;
import net.gahfy.devtools.customlink.ui.custom_link.CustomLinkListPresenter;

import dagger.Component;

/**
 * This class is the component for presenters. It injects the database module in the specified
 * presenters in the inject methods.
 * @see DatabaseModule
 */
@Component(modules = {DatabaseModule.class})
public interface PresenterComponent {
    /**
     * Injects the component modules in the specified CustomLinkListPresenter.
     * @param customLinkListPresenter the CustomLinkListPresenter in which to inject the component
     *                                modules
     */
    void inject(CustomLinkListPresenter customLinkListPresenter);

    /**
     * Injects the component modules in the specified CustomLinkAddUpdatePresenter.
     * @param customLinkAddUpdatePresenter the CustomLinkAddUpdatePresenter in which to inject the
     *                                     component modules
     */
    void inject(CustomLinkAddUpdatePresenter customLinkAddUpdatePresenter);
}
