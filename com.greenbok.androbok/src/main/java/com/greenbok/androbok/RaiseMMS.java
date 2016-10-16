package com.greenbok.androbok;

/**
 * Created by thomas on 16/10/2016.
 */

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class RaiseMMS implements IXposedHookLoadPackage {
    public static final String MY_PACKAGE_NAME = RaiseMMS.class.getPackage()
            .getName();
    public static final String TAG = "MMSHack";

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        // TODO Auto-generated method stub
        if (lpparam.packageName.equals("com.android.mms")) {
            ClassLoader classLoader = lpparam.classLoader;
            XC_MethodReplacement methodreplacer = new XC_MethodReplacement() {
                protected Object replaceHookedMethod(
                        XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
                        throws Throwable {
                    return (int) 999;

                }
            };
            XposedHelpers.findAndHookMethod("com.android.mms.MmsConfig",
                    classLoader, "getMmsMaxRecipient", methodreplacer);
        }

    }

}