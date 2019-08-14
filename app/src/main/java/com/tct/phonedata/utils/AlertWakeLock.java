package com.tct.phonedata.utils;

import android.content.Context;
import android.os.PowerManager;

/*
 Flag Value                 CPU        Screen      Keyboard
 PARTIAL_WAKE_LOCK            On           Off         Off
 SCREEN_DIM_WAKE_LOCK         On           Dim         Off
 SCREEN_BRIGHT_WAKE_LOCK      On           Bright      Off
 FULL_WAKE_LOCK               On           Bright      Bright
 */
public class AlertWakeLock {
    private static final String TAG = "AlertWakeLock";
    private static PowerManager.WakeLock sCpuWakeLock;

    static PowerManager.WakeLock createPartialWakeLock(Context context) {
        // 第一步：获取PowerManager的实例
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (null == pm) {
            return null;
        }
        // 第二步：调用PowerManager中的newWakeLock方法创建一个WakeLock对象
        return pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG); 
        //return pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, TAG);
    }

    public static void acquireCpuWakeLock(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }

        sCpuWakeLock = createPartialWakeLock(context);
        // 第三步：acquire()获取相应的锁
        sCpuWakeLock.acquire();
    }

    public static void releaseCpuLock() {
        if (sCpuWakeLock != null) {
            // 最后：release释放
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }
}
