package com.example.ioclibrary;

import android.app.Activity;
import android.view.View;

/**
 * Created by liuxh on 2017/7/25.
 */

public class ViewFindHelper {
    private Activity mActivity;
    private View mView;

    public ViewFindHelper(Activity activity) {
        mActivity=activity;
    }

    public ViewFindHelper(View view) {
        mView=view;
    }
    public View findViewById(int viewId){
        return mActivity!=null?mActivity.findViewById(viewId):mView.findViewById(viewId);
    }
}
