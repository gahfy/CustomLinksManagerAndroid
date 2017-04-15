package net.gahfy.devtools.customlink.ui.custom_link;

import net.gahfy.devtools.customlink.data.model.CustomLink;
import net.gahfy.devtools.customlink.ui.base.BaseView;
import net.gahfy.devtools.customlink.ui.activity.ContainerActivity;

interface CustomLinkAddUpdateView extends BaseView {
    CustomLink getCustomLink();
    void setForm(String title, String uri);
    ContainerActivity getContainerActivity();
    int getPosition();
    CustomLinkListPresenter getParentPresenter();
    String getCustomLinkTitle();
    String getCustomLinkUri();
    void setError(String error);
}
