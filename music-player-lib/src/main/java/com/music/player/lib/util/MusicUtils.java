package com.music.player.lib.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.bean.MusicAlarmSetting;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.model.MusicGlideCircleTransform;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

/**
 * 409
 * 2018/3/922
 */

public class MusicUtils {

    private static final String TAG = "MusicUtils";
    private static volatile MusicUtils mInstance;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    public static MusicUtils getInstance() {
        if(null==mInstance){
            synchronized (MusicUtils.class) {
                if (null == mInstance) {
                    mInstance = new MusicUtils();
                }
            }
        }
        return mInstance;
    }

    private MusicUtils(){}

    /**
     * ????????????
     100% ??? FF
     95% ??? F2
     90% ??? E6
     85% ??? D9
     80% ??? CC
     75% ??? BF
     70% ??? B3
     65% ??? A6
     60% ??? 99
     55% ??? 8C
     50% ??? 80
     45% ??? 73
     40% ??? 66
     35% ??? 59
     30% ??? 4D
     25% ??? 40
     20% ??? 33
     15% ??? 26
     10% ??? 1A
     5% ??? 0D
     0% ??? 00
     * @param context
     */
    @SuppressLint("CommitPrefEdits")
    public synchronized void initSharedPreferencesConfig(Context context) {
        if(null==mSharedPreferences){
            mSharedPreferences = context.getSharedPreferences(context.getPackageName()
                    + MusicConstants.SP_KEY_NAME, Context.MODE_MULTI_PROCESS);
            mEditor = mSharedPreferences.edit();
        }
    }

    public boolean putString(String key,String value){
        if(null!=mEditor){
            mEditor.putString(key,value);
            mEditor.commit();
            return true;
        }
        return false;
    }

    public boolean putInt(String key,int value){
        if(null!=mEditor){
            mEditor.putInt(key,value);
            mEditor.commit();
            return true;
        }
        return false;
    }

    public boolean putLong(String key,long value){
        if(null!=mEditor){
            mEditor.putLong(key,value);
            mEditor.commit();
            return true;
        }
        return false;
    }

    public boolean putBoolean(String key,boolean value){
        if(null!=mEditor){
            mEditor.putBoolean(key,value);
            mEditor.commit();
            return true;
        }
        return false;
    }

    public boolean putFloat(String key,float value){
        if(null!=mEditor){
            mEditor.putFloat(key,value);
            mEditor.commit();
            return true;
        }
        return false;
    }

    public boolean putStringSet(String key,Set<String> value){
        if(null!=mEditor){
            mEditor.putStringSet(key,value);
            mEditor.commit();
            return true;
        }
        return false;
    }

    public String getString(String key){
        return getString(key,"");
    }

    public String getString(String key,String defaultValue){
        if(null!=mSharedPreferences){
            return mSharedPreferences.getString(key,defaultValue);
        }
        return "";
    }


    public int getInt(String key){
        return getInt(key,0);
    }

    public int getInt(String key,int defaultValue){
        if(null!=mSharedPreferences){
            return mSharedPreferences.getInt(key,defaultValue);
        }
        return 0;
    }

    public long getLong(String key){
        return getLong(key,0);
    }

    public long getLong(String key,long defaultValue){
        if(null!=mSharedPreferences){
            return mSharedPreferences.getLong(key,defaultValue);
        }
        return 0;
    }

    public float getFloat(String key){
        return getFloat(key,0);
    }

    public float getFloat(String key,float defaultValue){
        if(null!=mSharedPreferences){
            return mSharedPreferences.getFloat(key,defaultValue);
        }
        return 0;
    }

    public boolean getBoolean(String key){
        return getBoolean(key,false);
    }

    public boolean getBoolean(String key,boolean defaultValue){
        if(null!=mSharedPreferences){
            return mSharedPreferences.getBoolean(key,defaultValue);
        }
        return false;
    }

    public Set<String> getStringSet(String key){
        return getStringSet(key,null);
    }

    public Set<String> getStringSet(String key,Set<String> defaultValue){
        if(null!=mSharedPreferences){
            return mSharedPreferences.getStringSet(key,defaultValue);
        }
        return null;
    }

    /**
     * ?????? min ??? max??????????????????,?????? min max
     * @param min ?????????
     * @param max ?????????
     * @return ???min-max???????????????????????????
     */
    public int getRandomNum(int min,int max) {
        return min + (int)(Math.random() * max);
    }

    /**
     * ???????????????
     * @param seconds ?????????
     * @return ???????????????String????????????
     * ???????????????????????????
     */
    public String stringForTime(long seconds) {
        if(seconds<=0) return "?????????";
        if(seconds >= 24 * 60 * 60 ) return "24??????";
        if(seconds<3600){//??????????????????????????????????????????????????????
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return minutes+":"+remainingSeconds;
        }else{
            //???????????????????????????
            long hours = seconds/60/60;
            long minutes =(seconds-60*60)/60;//??????=???????????????????????????????????????
            long remainingSeconds = seconds % 60;
            return hours+":"+minutes+":"+remainingSeconds;
        }
    }

    /**
     * ???????????????
     * @param timeMs ????????????
     * @return ???????????????String????????????
     */
    public String stringForAudioTime(long timeMs) {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long totalSeconds = timeMs / 1000;
        int seconds = (int) (totalSeconds % 60);
        int minutes = (int) ((totalSeconds / 60) % 60);
        int hours = (int) (totalSeconds / 3600);
        StringBuilder stringBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * ??????????????????????????????
     * @param timeMs ????????????
     * @return ???????????????String????????????
     */
    public String stringHoursForTime(long timeMs) {
        if(timeMs<=0) return "?????????";
        if(timeMs >= 24 * 60 * 60 ) return "24??????";
        if(timeMs<3600){//??????????????????????????????
            return timeMs/60+"??????";
        }else{
            long hours = timeMs/60/60;
            long minutes =(timeMs-60*60)/60;
            return hours+"??????"+minutes+"??????";
        }
    }

    public boolean isJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * ???dp?????????px
     *
     * @param dp dp????????????
     * @return px??????
     */
    public float dpToPx(Context context,float dp) {
        return dp * context.getApplicationContext().getResources().getDisplayMetrics().density;
    }

    public int dpToPxInt(Context context,float dp) {
        return (int) (dpToPx(context,dp) + 0.5f);
    }

    /**
     * filterBitmap
     * @param bitmap ??????
     * @param filterColor color
     * @return ?????????Bitmap
     */
    public Bitmap filterBitmap(Bitmap bitmap,int filterColor){
        if(null==bitmap){
            return null;
        }
        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.RGB_565);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bmp);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap,0,0,paint);
        canvas.drawColor(filterColor);
        return bmp;
    }

    /**
     * Bitmap??????????????????
     * @param bitmap ??????
     * @param screenWidth ?????????
     * @param screenHeight ?????????
     * @param radius ??????>=1 ???????????????
     * @param filterColor ???????????????
     * @return Drawable
     */
    public Bitmap getForegroundBitmap(Bitmap bitmap,int screenWidth,int screenHeight,int radius,int filterColor) {
        if(radius<=0) radius=8;
        if(null!=bitmap&&bitmap.getWidth()>0){
            //???????????????????????????????????????????????????????????????
            final float widthHeightSize = (float) (screenWidth * 1.0 / screenHeight * 1.0);
            int cropBitmapWidth = (int) (widthHeightSize * bitmap.getHeight());
            int cropBitmapWidthX = (int) ((bitmap.getWidth() - cropBitmapWidth) / 2.0);
            try {
                //??????????????????
                Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropBitmapWidthX, 0, cropBitmapWidth, bitmap.getHeight());
                //????????????
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, bitmap.getWidth()
                        / 50, bitmap.getHeight() / 50, false);
                //?????????
                Bitmap blurBitmap = doBlur(scaleBitmap, radius, true);
                return filterBitmap(blurBitmap, filterColor);
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }catch (RuntimeException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Bitmap??????????????????
     * @param bitmap
     * @param screenWidth
     * @param screenHeight
     * @param radius ??????>=1 ???????????????
     * @param filterColor ???????????????
     * @return Drawable
     */
    public Drawable getForegroundDrawable(Bitmap bitmap,int screenWidth,int screenHeight,int radius,int filterColor) {
        if(radius<=0) radius=8;
        if(null!=bitmap&&bitmap.getWidth()>0){
            //???????????????????????????????????????????????????????????????
            final float widthHeightSize = (float) (screenWidth * 1.0 / screenHeight * 1.0);
            int cropBitmapWidth = (int) (widthHeightSize * bitmap.getHeight());
            int cropBitmapWidthX = (int) ((bitmap.getWidth() - cropBitmapWidth) / 2.0);
            try {
                //??????????????????
                Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropBitmapWidthX, 0, cropBitmapWidth, bitmap.getHeight());
                //????????????
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, bitmap.getWidth()
                        / 50, bitmap.getHeight() / 50, false);
                //?????????
                final Bitmap blurBitmap = doBlur(scaleBitmap, radius, true);
                final Drawable foregroundDrawable = new BitmapDrawable(blurBitmap);
                //????????????????????????????????????????????????????????????
                foregroundDrawable.setColorFilter(filterColor, PorterDuff.Mode.MULTIPLY);
                return foregroundDrawable;
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }catch (RuntimeException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * ????????????????????????????????????
     * @param context
     * @param musicPicRes
     * @return Bitmap
     */
    private Bitmap getForegroundBitmap(Context context,int musicPicRes) {
        int screenWidth = MusicUtils.getInstance().getScreenWidth(context);
        int screenHeight =  MusicUtils.getInstance().getScreenHeight(context);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(context.getResources(), musicPicRes, options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        if (imageWidth < screenWidth && imageHeight < screenHeight) {
            return BitmapFactory.decodeResource(context.getResources(), musicPicRes);
        }
        int sample = 2;
        int sampleX = imageWidth / MusicUtils.getInstance().getScreenWidth(context);;
        int sampleY = imageHeight / MusicUtils.getInstance().getScreenHeight(context);

        if (sampleX > sampleY && sampleY > 1) {
            sample = sampleX;
        } else if (sampleY > sampleX && sampleX > 1) {
            sample = sampleY;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = sample;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeResource(context.getResources(), musicPicRes, options);
    }

    public Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }
        if (radius < 1) {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    //??????????????????
    public int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    //??????????????????
    public int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * ??????????????????
     * @return ???????????????
     */
    public List<MusicAlarmSetting> createAlarmSettings() {
        List<MusicAlarmSetting> alarmSettings=new ArrayList<>();
        MusicAlarmSetting alarmSetting1=new MusicAlarmSetting("10", MusicConstants.MUSIC_ALARM_MODEL_10);
        alarmSettings.add(alarmSetting1);
        MusicAlarmSetting alarmSetting2=new MusicAlarmSetting("15", MusicConstants.MUSIC_ALARM_MODEL_15);
        alarmSettings.add(alarmSetting2);
        MusicAlarmSetting alarmSetting3=new MusicAlarmSetting("30", MusicConstants.MUSIC_ALARM_MODEL_30);
        alarmSettings.add(alarmSetting3);
        MusicAlarmSetting alarmSetting4=new MusicAlarmSetting("60", MusicConstants.MUSIC_ALARM_MODEL_60);
        alarmSettings.add(alarmSetting4);
        return alarmSettings;
    }

    /**
     * ????????????
     * @param content ?????????
     * @param maxLength ????????????
     * @return ????????????????????????
     */

    public String subString(String content, int maxLength) {
        if(TextUtils.isEmpty(content)){
            return content;
        }
        if(content.length()<=maxLength){
            return content+" ";
        }
        return content.substring(0,11)+"...";
    }

    /**
     * ???????????????????????????
     * @param audioInfos ?????????
     * @param musicID ??????ID
     * @return ??????
     */
    public int getCurrentPlayIndex(List<?> audioInfos, long musicID) {
        if(null==audioInfos){
            audioInfos= MusicPlayerManager.getInstance().getCurrentPlayList();
        }
        if(null!=audioInfos&&audioInfos.size()>0){
            List<BaseAudioInfo> audioInfoList= (List<BaseAudioInfo>) audioInfos;
            for (int i = 0; i < audioInfoList.size(); i++) {
                if(musicID==audioInfoList.get(i).getAudioId()){
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * ?????????????????????????????????????????????
     * @param audioInfos ?????????
     * @param musicID ??????ID
     * @return  ???????????????????????????
     */
    public int getCurrentPlayIndexInThis(List<?> audioInfos, long musicID) {
        if(musicID<=0){
            return 0;
        }
        if(null!=audioInfos&&audioInfos.size()>0){
            List<BaseAudioInfo> audioInfoList= (List<BaseAudioInfo>) audioInfos;
            for (int i = 0; i < audioInfoList.size(); i++) {
                if(audioInfoList.get(i).getAudioId()==musicID){
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * ?????????????????????
     * @param context ?????????
     * @return ????????????
     */
    public String getPackageName(Context context) {
        //????????????pid
        int pid = android.os.Process.myPid();
        //???????????????
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //??????????????????
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//??????????????????
                return info.processName;//????????????
        }
        return "";
    }

    public String getNotNullStr(String activityName) {
        if(TextUtils.isEmpty(activityName)) return "";
        return activityName;
    }

    /**
     * ?????????????????????
     * @param context ?????????
     * @return ???????????????
     */
    public int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object o = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = (Integer) field.get(o);
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * ?????????????????????????????????
     * @param context ?????????
     * @return ?????????????????????
     */
    public int getNavigationHeight(Context context){
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * ??????????????????????????????
     * @param context ?????????
     * @return ???????????????????????????
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else {
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * ?????????????????????????????????
     * @return ???true???????????????
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    /**
     * MD5??????
     * @param byteStr ?????????????????????
     * @return ?????? byteStr???md5???
     */
    public String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer("");
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
//            return Base64.encodeToString(byteArray,Base64.NO_WRAP);
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

    /**
     * ??????app??????md5???,??????keytool -list -keystore D:\Desktop\app_key??????keytool -printcert
     * file D:\Desktop\CERT.RSA????????????md5?????????
     */
    public String getAppSignToMd5(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    getPackageName(context), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            String signStr = encryptionMD5(sign.toByteArray());
            return signStr;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * ?????????????????????????????????????????????
     * ??????????????????
     * @param animationMillis
     * @return TranslateAnimation
     */
    public TranslateAnimation animationFromBottomToLocation(long animationMillis) {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                2.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(animationMillis);
        animation.setInterpolator(new LinearInterpolator());
        return animation;
    }

    /**
     * ?????????????????????????????????????????????
     * ??????????????????
     * @param animationMillis
     * @return TranslateAnimation
     */
    public TranslateAnimation animationFromLocationToBottom(long animationMillis) {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 2.0f);
        animation.setDuration(animationMillis);
        animation.setInterpolator(new LinearInterpolator());
        return animation;
    }

    /**
     * ??????????????????????????????
     * @param context
     * @return ????????????????????????
     */
    public ArrayList<BaseAudioInfo> queryLocationMusics(Context context) {
        ArrayList<BaseAudioInfo> audioInfos=null;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA },
                MediaStore.Audio.Media.MIME_TYPE + "=? or "
                        + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[] { "audio/mpeg", "audio/x-ms-wma" }, null);
        if (null!=cursor&&cursor.moveToFirst()) {
            audioInfos = new ArrayList<>();
            do {
                if(!TextUtils.isEmpty(cursor.getString(9))){
                    BaseAudioInfo audioInfo = new BaseAudioInfo();
                    // ?????????
                    //audioInfo.setaudioName(cursor.getString(1));
                    // ?????????
                    audioInfo.setAudioName(cursor.getString(2));
//                song.setPinyin(Pinyin.toPinyin(title.charAt(0)).substring(0, 1).toUpperCase());
                    // ??????
                    audioInfo.setAudioDurtion(cursor.getInt(3));
                    // ?????????
                    audioInfo.setNickname(cursor.getString(4));
                    // ?????????
                    audioInfo.setAudioAlbumName(cursor.getString(5));
                    // ?????? cursor.getString(6)
                    // ????????????
                    if ("audio/mpeg".equals(cursor.getString(7).trim())) {
                        audioInfo.setAudioType("mp3");
                    } else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
                        audioInfo.setAudioType("wma");
                    }
                    // ???????????? cursor.getString(8)
                    // ????????????
                    audioInfo.setAudioPath(cursor.getString(9));
                    audioInfos.add(audioInfo);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return audioInfos;
    }

    /**
     * ?????????????????????????????????
     * @param audioInfo
     * @return ??????????????????
     */
    public String getMusicFrontPath(BaseAudioInfo audioInfo) {
        if(null==audioInfo){
            return null;
        }
        //??????????????????????????????
        if(TextUtils.isEmpty(audioInfo.getAudioPath())){
            return TextUtils.isEmpty(audioInfo.getAudioCover())?audioInfo.getAvatar():audioInfo.getAudioCover();
        }
        if(audioInfo.getAudioPath().startsWith("http:")||audioInfo.getAudioPath().startsWith("https:")){
            return TextUtils.isEmpty(audioInfo.getAudioCover())?audioInfo.getAvatar():audioInfo.getAudioCover();
        }else{
            //??????????????????
            return audioInfo.getAudioPath();
        }
    }

    /**
     * ?????????????????????????????????????????????
     * @param context
     * @param musicCover ????????????
     * @param filePath ????????????
     * @param frontBgSize ?????????????????????(??????)
     * @param frontCoverSize ?????????????????????(??????)
     * @param jukeBoxBgCover ?????????????????????
     * @param defaultCover ????????????
     */
    public void setMusicComposeFront(final Context context, final ImageView musicCover, final String filePath,
                              final float frontBgSize, final float frontCoverSize, final int jukeBoxBgCover, final int defaultCover) {
        if(null==context||null==musicCover||null==filePath){
            return;
        }
        //HTTP || HTTPS
        if(filePath.startsWith("http:")|| filePath.startsWith("https:")){
            Logger.d(TAG,"setMusicComposeFront-->HTTP || HTTPS");
            Glide.with(context)
                    .load(filePath)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .transform(new MusicGlideCircleTransform(context))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                            if(null!=musicCover){
                                if(null==bitmap){
                                    bitmap = BitmapFactory.decodeResource(context.getResources(),defaultCover);
                                    bitmap=drawRoundBitmap(bitmap);
                                }
                                if(null!=bitmap){
                                    LayerDrawable discDrawable = composeJukeBoxDrawable(context,bitmap,
                                            frontBgSize,frontCoverSize,jukeBoxBgCover);
                                    if(null!=discDrawable){
                                        musicCover.setImageDrawable(discDrawable);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),defaultCover);
                            if(null!=bitmap){
                                bitmap=drawRoundBitmap(bitmap);
                                if(null!=bitmap){
                                    LayerDrawable discDrawable = composeJukeBoxDrawable(context,bitmap,
                                            frontBgSize,frontCoverSize,jukeBoxBgCover);
                                    if(null!=discDrawable){
                                        musicCover.setImageDrawable(discDrawable);
                                    }
                                }
                            }
                        }
                    });
        }else{
            Logger.d(TAG,"setMusicCover-->File");
            long startMillis = System.currentTimeMillis();
            //File
            Bitmap bitmap;
            bitmap = MusicImageCache.getInstance().getBitmap(filePath);
            //?????????????????????????????????????????????
            if(null==bitmap){
                bitmap=MusicImageCache.getInstance().createBitmap(filePath);
            }
            //???????????????????????????
            if(null==bitmap){
                bitmap = BitmapFactory.decodeResource(context.getResources(), defaultCover);
            }
            bitmap=drawRoundBitmap(bitmap);
            LayerDrawable discDrawable = composeJukeBoxDrawable(context,bitmap,frontBgSize,frontCoverSize,jukeBoxBgCover);
            long endMillis = System.currentTimeMillis();
            Logger.d(TAG,"?????????????????????????????????"+(endMillis-startMillis));
            if(null!=discDrawable){
                musicCover.setImageDrawable(discDrawable);
            }
        }
    }

    /**
     * ?????????????????????
     * @param context
     * @param musicCover ????????????
     * @param bitmap ????????????
     * @param frontBgSize ?????????????????????(??????)
     * @param frontCoverSize ?????????????????????(??????)
     * @param jukeBoxBgCover ?????????????????????
     * @param defaultCover ??????????????????
     */
    public void setMusicComposeFront(final Context context, final ImageView musicCover,Bitmap bitmap
                              ,final float frontBgSize, final float frontCoverSize, final int jukeBoxBgCover, final int defaultCover){
        if(null!=context&&null!=musicCover){
            if(null==bitmap){
                bitmap = BitmapFactory.decodeResource(context.getResources(), defaultCover);
            }
            LayerDrawable discDrawable = composeJukeBoxDrawable(context,bitmap,frontBgSize,frontCoverSize,jukeBoxBgCover);
            if(null!=discDrawable){
                musicCover.setImageDrawable(discDrawable);
            }
        }
    }

    /**
     * ????????????????????????????????????????????????????????????
     * @param context
     * @param bitmap ??????????????????
     * @param frontJukeBoxScale ????????????????????????
     * @param frontCoverScale ??????????????????
     * @param jukeBoxBgCover ??????????????????
     * @return LayerDrawable
     */
    public LayerDrawable composeJukeBoxDrawable(Context context, Bitmap bitmap, float frontJukeBoxScale,
                                                float frontCoverScale, int jukeBoxBgCover) {
        if(frontJukeBoxScale<=0||frontCoverScale<=0){
            return null;
        }
        int screenWidth = getScreenWidth(context);
        //??????????????????
        int jukeBoxCoverBgSize = (int) (screenWidth * frontJukeBoxScale);
        //????????????
        int jukeBoxCoverFgSize = (int) (screenWidth * frontCoverScale);
        //???????????????????????????????????????
        Bitmap bgBitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                context.getResources(), jukeBoxBgCover), jukeBoxCoverBgSize, jukeBoxCoverBgSize, true);
        BitmapDrawable bgDiscDrawable = new BitmapDrawable(bgBitmapDisc);
        //????????????
        Bitmap finalBitmap = scalePicSize(jukeBoxCoverFgSize,bitmap);
        RoundedBitmapDrawable roundMusicDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), finalBitmap);
        //?????????
        bgDiscDrawable.setAntiAlias(true);
        roundMusicDrawable.setAntiAlias(true);
        Drawable[] drawables = new Drawable[2];
        drawables[0] = bgDiscDrawable;
        drawables[1] = roundMusicDrawable;
        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        int musicPicMargin = (int) ((frontJukeBoxScale - frontCoverScale) * screenWidth / 2);
        //?????????????????????????????????????????????????????????
        layerDrawable.setLayerInset(1, musicPicMargin, musicPicMargin, musicPicMargin, musicPicMargin);
        return layerDrawable;
    }

    /**
     * ?????????????????????????????????
     * @param musicPicSize
     * @param bitmap
     * @return Bitmap
     */
    private Bitmap scalePicSize(int musicPicSize, Bitmap bitmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        int imageWidth = bitmap.getWidth();
        int sample = imageWidth / musicPicSize;
        int dstSample = 1;
        if (sample > dstSample) {
            dstSample = sample;
        }
        options.inJustDecodeBounds = false;
        //?????????????????????
        options.inSampleSize = dstSample;
        //????????????????????????
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return Bitmap.createScaledBitmap(bitmap, musicPicSize, musicPicSize, true);
    }

    /**
     * ?????????????????????
     * @param bitmap
     * @return Bitmap
     */
    public Bitmap drawRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * ?????????????????????
     * @param filename
     * @param currentKey
     * @return ???????????????
     */
    public String formatSearchContent(String filename, String currentKey) {
        if(TextUtils.isEmpty(currentKey)||TextUtils.isEmpty(filename)){
            return filename;
        }
        return filename.replace(currentKey, "<font color='#8000ff'>" + currentKey + "</font>");
    }
    /**
     * ???????????????
     *
     * @param context
     * @param mEditText
     */
    public void openKeybord(Context context,EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * ???????????????
     *
     * @param context
     * @param mEditText
     */
    public void closeKeybord(Context context,EditText mEditText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * ?????????????????????
     * @return ????????????
     */
    public String createRootPath(Context context) {
        String cacheRootPath = "";
        //SD?????????????????????SD??????????????????????????????????????????????????????????????????????????????
        if (isSdCardAvailable()) {
            // /sdcard/Android/data/<application package>/cache
            if(null!=context.getExternalCacheDir()){
                cacheRootPath = context.getExternalCacheDir().getPath();//SD???????????????????????????
            }
            //??????????????????????????????????????????????????????
        } else {
            // /data/data/<application package>/cache
            if(null!=context.getCacheDir()){
                cacheRootPath = context.getCacheDir().getPath();//??????????????????????????????
            }else{
                File cacheDirectory = getCacheDirectory(context, null);
                if(null!=cacheDirectory){
                    cacheRootPath=cacheDirectory.getAbsolutePath();
                }
            }
        }
        return cacheRootPath;
    }

    /**
     * ??????????????????????????????
     * @param context
     * @return ????????????
     */
    public String getCacheDir(Context context) {
        String cacheRootPath = null;
        if(null!=context.getCacheDir()){
            cacheRootPath= context.getCacheDir().getPath();
        } else if(null!=context.getFilesDir()){
            cacheRootPath=context.getFilesDir().getPath();
        }else if(isSdCardAvailable()){
            if(null!=context.getExternalCacheDir()){
                cacheRootPath = context.getExternalCacheDir().getPath();//SD???????????????????????????
            }
        }else{
            File cacheDirectory = getCacheDirectory(context, null);
            if(null!=cacheDirectory){
                cacheRootPath=cacheDirectory.getAbsolutePath();
            }
        }
        return cacheRootPath;
    }

    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    /**
     * ?????????????????????
     *
     * @param file
     * @return ?????????????????????????????????""
     */
    public String createFile(File file) {
        try {
            if (file.getParentFile().exists()) {

                file.createNewFile();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());
                file.createNewFile();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * ?????????????????????
     *
     * @param dirPath
     * @return ?????????????????????????????????""
     */
    public String createDir(String dirPath) {
        try {
            File file = new File(dirPath);
            if (file.getParentFile().exists()) {

                file.mkdir();
                return file.getAbsolutePath();
            } else {
                createDir(file.getParentFile().getAbsolutePath());

                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirPath;
    }

    /**
     * ??????????????????????????????
     * android 4.4??????????????????????????????SD???????????????
     * ?????????????????????6.0??????????????????SD???????????????????????????????????????????????????????????? ??????????????????????????????
     * @param context ?????????
     * @param type ??????????????? ??????????????????????????????API?????????????????????
     * @return ??????????????? ????????????SD??????SD????????????????????????????????????????????????????????????SD???????????????
     */
    public File getCacheDirectory(Context context,String type) {
        File appCacheDir = getExternalCacheDirectory(context,type);
        if (appCacheDir == null){
            appCacheDir = getInternalCacheDirectory(context,type);
        }

        if (appCacheDir == null){
            Log.e("getCacheDirectory","getCacheDirectory fail ,the reason is mobile phone unknown exception !");
        }else {
            if (!appCacheDir.exists()&&!appCacheDir.mkdirs()){
                Log.e("getCacheDirectory","getCacheDirectory fail ,the reason is make directory fail !");
            }
        }
        return appCacheDir;
    }

    /**
     * ??????SD???????????????
     * @param context ?????????
     * @param type ??????????????? ????????????????????? /storage/emulated/0/Android/data/app_package_name/cache
     *             ???????????????????????????????????????Environment.DIRECTORY_PICTURES ????????????????????? .../data/app_package_name/files/Pictures
     * {@link Environment#DIRECTORY_MUSIC},
     * {@link Environment#DIRECTORY_PODCASTS},
     * {@link Environment#DIRECTORY_RINGTONES},
     * {@link Environment#DIRECTORY_ALARMS},
     * {@link Environment#DIRECTORY_NOTIFICATIONS},
     * {@link Environment#DIRECTORY_PICTURES}, or
     * {@link Environment#DIRECTORY_MOVIES}.or ????????????????????????
     * @return ????????????????????? ??? null??????SD??????SD??????????????????
     */
    public File getExternalCacheDirectory(Context context,String type) {
        File appCacheDir = null;
        if( Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (TextUtils.isEmpty(type)){
                appCacheDir = context.getExternalCacheDir();
            }else {
                appCacheDir = context.getExternalFilesDir(type);
            }

            if (appCacheDir == null){// ???????????????????????????????????????
                appCacheDir = new File(Environment.getExternalStorageDirectory(),"Android/data/"+context.getPackageName()+"/cache/"+type);
            }

            if (appCacheDir == null){
                Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is sdCard unknown exception !");
            }else {
                if (!appCacheDir.exists()&&!appCacheDir.mkdirs()){
                    Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is make directory fail !");
                }
            }
        }else {
            Log.e("getExternalDirectory","getExternalDirectory fail ,the reason is sdCard nonexistence or sdCard mount fail !");
        }
        return appCacheDir;
    }

    /**
     * ????????????????????????
     * @param type ?????????????????????????????????????????????????????????
     * @return ????????????????????? ??? null??????????????????????????????
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????? ??????????????????
     */
    public File getInternalCacheDirectory(Context context,String type) {
        File appCacheDir = null;
        if (TextUtils.isEmpty(type)){
            appCacheDir = context.getCacheDir();// /data/data/app_package_name/cache
        }else {
            appCacheDir = new File(context.getFilesDir(),type);// /data/data/app_package_name/files/type
        }

        if (!appCacheDir.exists()&&!appCacheDir.mkdirs()){
            Log.e("getInternalDirectory","getInternalDirectory fail ,the reason is make directory fail !");
        }
        return appCacheDir;
    }

    /**
     * ?????????????????????????????????
     * @param context
     * @return true:??????
     */
    public boolean hasNiticePremission(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        return manager.areNotificationsEnabled();
    }

    /**
     * ??????????????????
     * @param context ?????????
     */
    public void startAppSetting(Context context) {
        Intent localIntent = new Intent();
        //?????????????????????????????????????????????
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", context.getPackageName());
            localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            //4.4???????????????app????????????????????????????????????Action???????????????????????????????????????,
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }
        }
        context.startActivity(localIntent);
    }

    /**
     * ???????????????????????????
     * @param context
     * @param contentUri
     * @return ????????????
     */
    public String getPathFromURI(Context context,Uri contentUri){
        //???????????????
        if("file".equals(contentUri.getScheme())){
            return contentUri.getPath();
        }
        if(ContentResolver.SCHEME_CONTENT.equals(contentUri.getScheme())){
            File file;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4??????
                String path = getPath(context, contentUri);
                file = new File("file://" + path);
            } else {//4.4????????????????????????
                String realPathFromURI = getRealPathFromURI(context,contentUri);
                file = new File("file://" + realPathFromURI);//????????????
            }
            if(null!=file){
                return file.getAbsolutePath();
            }
            return null;
        }
        return null;//getPath(context, contentUri)
    }

    /**
     * ???????????????????????????
     * @param context
     * @param contentUri
     * @return ????????????
     */
    public String getRealPathFromURI(Context context,Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    public static String ACTION_OPEN_DOCUMENT = "android.intent.action.OPEN_DOCUMENT";
    public static int Build_VERSION_KITKAT = 19;

    public  String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= 19;
        // DocumentProvider
        if (isKitKat && isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static final String PATH_DOCUMENT = "document";

    private boolean isDocumentUri(Context context, Uri uri) {
        final List<String> paths = uri.getPathSegments();
        if (paths.size() < 2) {
            return false;
        }
        if (!PATH_DOCUMENT.equals(paths.get(0))) {
            return false;
        }

        return true;
    }

    private String getDocumentId(Uri documentUri) {
        final List<String> paths = documentUri.getPathSegments();
        if (paths.size() < 2) {
            throw new IllegalArgumentException("Not a document: " + documentUri);
        }
        if (!PATH_DOCUMENT.equals(paths.get(0))) {
            throw new IllegalArgumentException("Not a document: " + documentUri);
        }
        return paths.get(1);
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     *            [url=home.php?mod=space&uid=7300]@return[/url] The value of
     *            the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * ?????????????????????????????????
     * @param count
     * @param continueSmall ?????????????????????????????????
     * @return ??????????????????????????????
     */
    public String formatNumToWan(long count, boolean continueSmall) {
        if (continueSmall && count <= 10000) return String.valueOf(count);
        double n = (double) count / 10000;
        return changeDouble(n) + "???";
    }

    public double changeDouble(Double dou) {
        try {
            NumberFormat nf = new DecimalFormat("0.0 ");
            dou = Double.parseDouble(nf.format(dou));
            return dou;
        }catch (RuntimeException e){

        }
        return dou;
    }

    /**
     * ??????????????????????????????
     * @param millis
     * @return ?????????????????????
     */
    public static String formatDateFromMillis(long millis){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return formatter.format(millis);
    }

    public String getTimeNow(Long time) {
        Calendar cal = Calendar.getInstance();
        long timel = cal.getTimeInMillis() - time;
        if (timel / 1000 < 60) {
            return "1????????????";
        } else if (timel / 1000 / 60 < 60) {
            return timel / 1000 / 60 + "?????????";
        } else if (timel / 1000 / 60 / 60 < 24) {
            return timel / 1000 / 60 / 60 + "?????????";
        } else {
            return getTimeForString(time);
        }
    }
    public String getTimeForString(long time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(time);
    }

    /**
     * ????????????????????????????????????
     * @param url
     * @return ????????????
     */
    public String formatImageUrl(String url) {
        if(TextUtils.isEmpty(url)){
            return url;
        }
        return url.substring(0,url.indexOf("?"));
    }

    /**
     * ??????????????????
     * @param context ?????????
     * @return ????????????
     */
    public int getScreenDensity(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.densityDpi;
    }

    /**
     * Get AppCompatActivity from context
     * @param context context
     * @return AppCompatActivity if it's not null
     */
    public AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    public boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ????????????Context
     * @return APP??????????????????
     */
    public Context getApplicationContext() {
        try {
            Class<?> ActivityThread = Class.forName("android.app.ActivityThread");

            Method method = ActivityThread.getMethod("currentActivityThread");
            Object currentActivityThread = method.invoke(ActivityThread);//??????currentActivityThread ??????

            Method method2 = currentActivityThread.getClass().getMethod("getApplication");
            return (Context)method2.invoke(currentActivityThread);//?????? Context??????

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}