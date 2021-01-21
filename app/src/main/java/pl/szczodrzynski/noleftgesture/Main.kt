package pl.szczodrzynski.noleftgesture

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.XposedHelpers.getIntField
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Main : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam?.packageName != "com.android.systemui")
            return

        findAndHookMethod(
            "com.android.systemui.statusbar.phone.EdgeBackGestureHandler",
            lpparam.classLoader,
            "isWithinTouchRegion",
            Integer.TYPE, Integer.TYPE,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val mEdgeWidth = getIntField(param.thisObject, "mEdgeWidth")
                    val mLeftInset = getIntField(param.thisObject, "mLeftInset")
                    val x = param.args[0] as Int
                    val mIsOnLeftEdge = x <= mEdgeWidth + mLeftInset
                    //XposedBridge.log("EdgeBackGestureHandler mEdgeWidth = $mEdgeWidth, mLeftInset = $mLeftInset, x = $x, mIsOnLeftEdge = $mIsOnLeftEdge")
                    if (mIsOnLeftEdge)
                        param.result = false
                }
            })
    }
}
