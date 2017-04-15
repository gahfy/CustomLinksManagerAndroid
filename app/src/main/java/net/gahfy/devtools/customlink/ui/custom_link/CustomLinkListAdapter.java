package net.gahfy.devtools.customlink.ui.custom_link;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gahfy.devtools.customlink.R;
import net.gahfy.devtools.customlink.data.model.CustomLink;
import net.gahfy.devtools.customlink.util.view.ErrorViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


class CustomLinkListAdapter extends RecyclerView.Adapter<CustomLinkListAdapter.ViewHolder> {
    private Cursor mCustomLinkListCursor;
    private int mCursorSize;
    private CustomLinkListPresenter mViewPresenter;

    CustomLinkListAdapter(CustomLinkListPresenter viewPresenter){
        super();
        mCursorSize = 0;
        mViewPresenter = viewPresenter;
    }

    void setData(Cursor customLinkListCursor, int cursorSize){
        mCustomLinkListCursor = customLinkListCursor;
        mCursorSize = cursorSize;
    }

    void unbind(){
        mCustomLinkListCursor = null;
        mViewPresenter = null;
    }

    int getPosition(CustomLink customLink){
        mCustomLinkListCursor.moveToFirst();
        do{
            CustomLink currentCustomLink = CustomLink.cursorPositionedToObject(mCustomLinkListCursor);
            if(currentCustomLink.equals(customLink)){
                return mCustomLinkListCursor.getPosition();
            }
        } while(mCustomLinkListCursor.moveToNext());
        return -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_link_list, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.ltItemCustomLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ItemViewTag viewTag = (ItemViewTag) v.getTag();
                    mViewPresenter.onItemClick(viewTag.getCustomLink());
                }
                catch(NullPointerException e){
                    ErrorViewUtils.showErrorToast(v.getContext(), R.string.technical_error, e);
                }
            }
        });
        viewHolder.ltMenuContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = (PopupMenu) v.getTag();
                popupMenu.show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCustomLinkListCursor.moveToPosition(position);
        CustomLink customLink = CustomLink.cursorPositionedToObject(mCustomLinkListCursor);
        ItemViewTag itemViewTag = new ItemViewTag(customLink, position);

        SpannableString customLinkUriSpan = new SpannableString(customLink.getCustomLinkUri().toString());
        customLinkUriSpan.setSpan(new UnderlineSpan(), 0, customLinkUriSpan.length(), 0);

        if(customLink.getCustomLinkTitle() != null) {
            holder.lblCustomLinkTitle.setVisibility(View.VISIBLE);
            holder.lblCustomLinkTitle.setText(customLink.getCustomLinkTitle());
        }
        else {
            holder.lblCustomLinkTitle.setVisibility(View.GONE);
        }
        holder.lblCustomLinkUri.setText(customLinkUriSpan);

        holder.inflatePopupMenu(mViewPresenter, customLink.getIdAsInt());
        holder.setCustomLinkItemMenuItemClickListener(new CustomLinkItemMenuItemClickListener(holder.getContext(), mViewPresenter));

        holder.customLinkItemMenuItemClickListener.setData(customLink);

        holder.ltItemCustomLink.setTag(itemViewTag);
        holder.ltMenuContainer.setTag(holder.popupMenu);
    }

    @Override
    public long getItemId(int position){
        mCustomLinkListCursor.moveToPosition(position);
        CustomLink customLink = CustomLink.cursorPositionedToObject(mCustomLinkListCursor);
        return customLink.getId();
    }

    @Override
    public int getItemCount() {
        return mCursorSize;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.lt_item_custom_link)
        RelativeLayout ltItemCustomLink;

        @BindView(R.id.lt_menu_container)
        RelativeLayout ltMenuContainer;

        @BindView(R.id.lbl_custom_link_uri)
        TextView lblCustomLinkUri;

        @BindView(R.id.lbl_custom_link_title)
        TextView lblCustomLinkTitle;

        PopupMenu popupMenu;
        CustomLinkItemMenuItemClickListener customLinkItemMenuItemClickListener;
        private Context mContext;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
        }

        Context getContext(){
            return mContext;
        }

        void inflatePopupMenu(CustomLinkListPresenter customLinkListPresenter, int customLinkId){
            popupMenu = new PopupMenu(ltMenuContainer.getContext(), ltMenuContainer);
            MenuInflater inflater = popupMenu.getMenuInflater();
            try {
                if (customLinkListPresenter.isCustomLinkNotified(customLinkId)) {
                    inflater.inflate(R.menu.custom_link_item_notified_menu, popupMenu.getMenu());
                } else {
                    inflater.inflate(R.menu.custom_link_item_not_notified_menu, popupMenu.getMenu());
                }
            }
            catch(NullPointerException e){
                inflater.inflate(R.menu.custom_link_item_not_notified_menu, popupMenu.getMenu());
            }
        }

        void setCustomLinkItemMenuItemClickListener(CustomLinkItemMenuItemClickListener customLinkItemMenuItemClickListener) {
            this.customLinkItemMenuItemClickListener = customLinkItemMenuItemClickListener;
            popupMenu.setOnMenuItemClickListener(this.customLinkItemMenuItemClickListener);
        }
    }

    private static class ItemViewTag{
        CustomLink mCustomLink;
        int mPosition;

        ItemViewTag(CustomLink customLink, int position){
            mCustomLink = customLink;
            mPosition = position;
        }

        CustomLink getCustomLink() {
            return mCustomLink;
        }
    }
}
