package jlab.TipsToLoseWeight.Activity.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import jlab.TipsToLoseWeight.Activity.Utils.Tip;
import jlab.TipsToLoseWeight.Activity.Utils.TipManager;
import jlab.TipsToLoseWeight.Activity.Utils.Utils;
import jlab.TipsToLoseWeight.R;

/*
 * Created by Javier on 22/03/2020.
 */

public class GridTipsView extends GridView implements AbsListView.OnScrollListener {

    private int last, first, antFirst;
    public boolean scrolling = false;
    private TipAdapter mAdapter;
    private TipManager dessertManager;

    public GridTipsView(Context context) {
        super(context);
        dessertManager = new TipManager(context);
        mAdapter = new TipAdapter(context);
        setAdapter(mAdapter);
    }

    public GridTipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dessertManager = new TipManager(context);
        mAdapter = new TipAdapter(context);
        setAdapter(mAdapter);
    }

    public GridTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dessertManager = new TipManager(context);
        mAdapter = new TipAdapter(context);
        setAdapter(mAdapter);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE)
            scrolling = false;
        scrolling = scrollState == SCROLL_STATE_FLING;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        antFirst = first != firstVisibleItem ? first : antFirst;
        first = firstVisibleItem;
        last = firstVisibleItem + visibleItemCount - 1;
    }

    public void setOnGetSetViewListener (final LayoutInflater inflater, final int itemWidth, final int itemHeight,
                                         final OnTipClickListener onTipClickListener) {
        this.mAdapter.setonGetSetViewListener(new TipAdapter.OnGetSetViewListener() {
            @Override
            public View getView(ViewGroup parent, Tip resource, int position) {
                return inflater.inflate(R.layout.grid_details_tip, parent, false);
            }

            @Override
            public void setView(final View view, final Tip resource, int position) {
                TextView tvTitle = view.findViewById(R.id.tvTipTitle),
                        tvDescription = view.findViewById(R.id.tvTipDescription);
                tvTitle.setText(resource.getTitle());
                tvDescription.setText(resource.getDescription());
                ViewGroup.LayoutParams newParams = view.getLayoutParams();
                newParams.height = itemHeight;
                newParams.width = itemWidth;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ImageView imageView = (ImageView) view.findViewById(R.id.ivTipListImage);
                        final Bitmap image = dessertManager.getImage(resource.getTipId());
                        Utils.runnerOnUIThread.run(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(image);
                            }
                        });
                    }
                }).start();

                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTipClickListener.onClick(resource);
                    }
                });
            }
        });
    }

    public void loadContent() {
        mAdapter.clear();
        mAdapter.addAll(dessertManager.getAllDetails());
    }

    @Override
    public int getFirstVisiblePosition() {
        return first;
    }

    public interface OnTipClickListener {
        void onClick (Tip tip);
    }
}