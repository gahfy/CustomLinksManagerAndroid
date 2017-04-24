package net.gahfy.devtools.customlink.ui.custom_link;

import android.content.Intent;

import net.gahfy.devtools.customlink.ui.activity.ContainerActivity;
import net.gahfy.devtools.customlink.ui.base.BaseView;

interface CustomLinkListView extends BaseView {
    void setAdapter(CustomLinkListAdapter customLinkListAdapter);
    void startActivity(Intent intent);
    ContainerActivity getContainerActivity();
    void setEmptyStateVisibility(boolean visible);
}
