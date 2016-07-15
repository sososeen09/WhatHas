package com.longge.whathas.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.longge.whathas.R;
import com.longge.whathas.entity.PicEntity;
import com.longge.whathas.net.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by long on 2016/7/13.
 */
public class PrettyGirlsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private List<PicEntity> mList;
    private static final int TYPE_NORMAL_ITEM = 0;//普通条目
    private static final int TYPE_FOOTER_ITEM = 1; //加载更多条目


    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 1;
    //正在加载中
    public static final int LOADING_MORE = 2;
    //默认为0
    private int load_more_status = 0;
    private onItemClickListener mOnItemClickListener;


    public PrettyGirlsRecyclerAdapter(Context context, List<PicEntity> list) {
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL_ITEM) {
            final View view = View.inflate(mContext, R.layout.item_meinv_main, null);
            final RecyclerView.ViewHolder holder = new MainRecyclerHolder(view);
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                    }
                });
            }
            return holder;
        } else {
            View view = View.inflate(mContext, R.layout.item_footer_view, null);
            return new FooterRecyclerHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_NORMAL_ITEM) {
            MainRecyclerHolder mainRecyclerHolder = (MainRecyclerHolder) holder;
            PicEntity picEntity = mList.get(position);
            mainRecyclerHolder.mTvItemTitle.setText(picEntity.title);
            ImageLoader.getInstance()
                       .disPlayPic(mContext, mainRecyclerHolder.mIvItemPic, picEntity.imagePath);
        } else if (itemViewType == TYPE_FOOTER_ITEM) {
            FooterRecyclerHolder footerRecyclerHolder = (FooterRecyclerHolder) holder;
            switch (load_more_status) {
                case LOADING_MORE:
                    footerRecyclerHolder.mTextViewFooter.setVisibility(View.GONE);
                    footerRecyclerHolder.mProgressBarFooter.setVisibility(View.VISIBLE);
                    break;
                case PULLUP_LOAD_MORE:
                    footerRecyclerHolder.mTextViewFooter.setVisibility(View.VISIBLE);
                    footerRecyclerHolder.mProgressBarFooter.setVisibility(View.GONE);
                    break;
            }

        }

    }

    @Override
    public int getItemCount() {
        if (mList.size() == 0) {
            return 0;
        }
        return mList.size() + 1;//包含了一个FooterView
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER_ITEM;
        } else {
            return TYPE_NORMAL_ITEM;
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (getItemViewType(position) == TYPE_FOOTER_ITEM)
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() + 1 == getItemCount()) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    public void setMoreStatus(int status) {
        load_more_status = status;
        notifyDataSetChanged();
    }

    public class MainRecyclerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_item_pic)
        ImageView mIvItemPic;
        @BindView(R.id.tv_item_title)
        TextView mTvItemTitle;

        public MainRecyclerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class FooterRecyclerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pb_item_footer)
        ProgressBar mProgressBarFooter;
        @BindView(R.id.tv_item_footer)
        TextView mTextViewFooter;

        public FooterRecyclerHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setonItemClickListener(onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(View view, int pos);
    }
}
