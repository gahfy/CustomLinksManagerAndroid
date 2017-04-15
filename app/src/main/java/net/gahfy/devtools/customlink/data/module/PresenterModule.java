package net.gahfy.devtools.customlink.data.module;

import android.support.annotation.NonNull;

import net.gahfy.devtools.customlink.data.injection.CustomScope;
import net.gahfy.devtools.customlink.ui.custom_link.CustomLinkAddUpdatePresenter;
import net.gahfy.devtools.customlink.ui.custom_link.CustomLinkListPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Module which provides presenters
 */
@Module  @CustomScope
public class PresenterModule {
    /**
     * Provides a CustomLinkListPresenter.
     * @return a CustomLinkListPresenter
     */
    @Provides @NonNull
    CustomLinkListPresenter provideCustomLinkListPresenter(){
        return new CustomLinkListPresenter();
    }

    /**
     * Provides a CustomLinkAddUpdatePresenter.
     * @return a CustomLinkAddUpdatePresenter
     */
    @Provides @NonNull
    CustomLinkAddUpdatePresenter provideCustomLinkAddUpdatePresenter(){
        return new CustomLinkAddUpdatePresenter();
    }
}
