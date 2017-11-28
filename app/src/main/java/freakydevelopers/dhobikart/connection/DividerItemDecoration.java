package freakydevelopers.dhobikart.connection;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by PURUSHOTAM on 10/21/2016.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;

    public DividerItemDecoration(int mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = mVerticalSpaceHeight;
    }
}
