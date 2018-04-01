package com.example.ioclibrary;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by liuxh on 2017/7/25.
 */

public class ViewUtils {

    public static void inject(Activity activity){
        inject(new ViewFindHelper(activity),activity);
    }

    public static void inject(View view){
        inject(new ViewFindHelper(view),view);
    }

    public static void inject(View view,Object object){
        inject(new ViewFindHelper(view),object);
    }

    private static void inject(ViewFindHelper viewFindHelper,Object object){
        injectField(viewFindHelper,object);
        injectEvent(viewFindHelper,object);
    }

    private static void injectEvent(ViewFindHelper viewFindHelper, Object object) {
        //获取类的所有方法
        Class<?> cls = object.getClass();
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            OnClick onClick = declaredMethod.getAnnotation(OnClick.class);
            if (onClick!=null){
                //获取注解OnClick里的值
                int[] viewIds = onClick.value();
                for (int viewId : viewIds) {
                    //找到view
                    View view = viewFindHelper.findViewById(viewId);
                    //设置点击事件
                    if (view!=null){
                        CheckNet checkNet = declaredMethod.getAnnotation(CheckNet.class);
                        boolean isCheckNet =(checkNet != null);
                        String remindMsg=null;
                        if (checkNet!=null){
                            remindMsg = checkNet.remindMsg();
                        }
                        view.setOnClickListener(new DeclaredOnClickListener(declaredMethod,object,isCheckNet,remindMsg));

                    }
                }


            }
        }

    }
    private static class DeclaredOnClickListener implements View.OnClickListener {
        private Method mMethod;
        private Object mObject;
        private boolean mIsCheckNet;
        private String mRemindMsg;
        public DeclaredOnClickListener(Method declaredMethod, Object object,boolean checkNet,String remindMsg) {
            mMethod=declaredMethod;
            mObject=object;
            mIsCheckNet=checkNet;
            mRemindMsg=remindMsg;
        }

        @Override
        public void onClick(View v) {
            if (mIsCheckNet){
                //检查网络
                boolean isNetConnected = networkAvailable(v.getContext());
                if (!isNetConnected){
                    Toast.makeText(v.getContext(),mRemindMsg,Toast.LENGTH_LONG).show();
                    return;
                }
            }

            //执行方法
            mMethod.setAccessible(true);
            try {
                mMethod.invoke(mObject,v);
            }catch (Exception e) {
                e.printStackTrace();
                try {
                    Object[] objects=new Object[]{};
                    mMethod.invoke(mObject,objects);
                }catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static void injectField(ViewFindHelper viewFindHelper, Object object) {
        //获取类的所有属性
        Class<?> clz = object.getClass();
        Field[] declaredFields = clz.getDeclaredFields();
        //获取注解ViewById里的值
        for (Field declaredField : declaredFields) {
            ViewById viewById = declaredField.getAnnotation(ViewById.class);
            if (viewById!=null){
                int viewId = viewById.value();
                    //找到View
                    View view = viewFindHelper.findViewById(viewId);
                if (view!=null) {
                    //动态注入找到的view
                    declaredField.setAccessible(true);
                    try {
                        declaredField.set(object, view);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }

    /**
     * 判断当前网络是否可用
     */
    private static boolean networkAvailable(Context context) {
        // 得到连接管理器对象
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
