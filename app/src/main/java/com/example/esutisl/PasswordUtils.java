package com.example.esutisl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PasswordUtils {
    /* return FIIST = 9        表示首次登录  或  首次设置密码
     * return SUCCESSFULLU = 3 表示登录成功  或   设置密码成功
     * return  LOGON_FAILED =4 表示登录失败  或   设置密码失败
     * COUNT 剩余次数
     */
    private static int FIIST = 9;
    private static int SUCCESSFULLU = 3;
    private static int LOGON_FAILED = 4;
    private static int COUNT = 3;
    private static int SETCOUNT = 3;
    //3次登录机会
    public static int LOGIN_CHANCES = 3;
    //还剩几次登录机会的标志，初始值就是LOGIN_CHANCES
    //多次认证失败时需要等待的时间
    public static int fist = LOGIN_CHANCES;
    //修改次数
    private static int modifyFist = LOGIN_CHANCES;
    private static float WAIT_TIME = 30000L;
    private static long TIME = 0L;

    /*
    /设置密码接口
    return Map<String,object>
     */
    public static Map<String, Object> setPassword(int level, String pNewPassword, String oldPassword, Context context) {
        Map<String, Object> map = new HashMap<>();
        //创建数据库实例
        // 调用查询方法 如果i retrun 0 ，表示没有数据 表示首次登录
        int i = MyApp.mysql.selectQuery();
        if (i == 0) {
            //首次设置密码
            boolean is_setPassword = First_password(level, context, pNewPassword);
            if (is_setPassword) {
                map.put("FIST", FIIST);
                map.put("FAILURE_number", 0L);
                map.put("TIMER", SETCOUNT);
                return map;
            }
        } else {
            //获取开机时间
            long l = SystemClock.elapsedRealtime();
            Log.i("liuhongliang", "Loagin: 开机时间" + l);
            @SuppressLint("WrongConstant")
            SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_APPEND);
            long modifyErrorTime = sp.getLong("modifyErrorTime", 0L);
            int modifyErrorNumber = sp.getInt("modifyErrorNumber", 0);
            long TIME=30000*modifyErrorNumber;
            Bean select = MyApp.mysql.select();
            int count = select.getCount();
            if(l-modifyErrorTime>TIME){
                // 第二次修改密码需要校验旧密码
                boolean b = SecondPassword(level, pNewPassword, oldPassword, context);
                if (b) {
                    MyApp.mysql.UpdataCount(0, 3);
                    map.put("FIST", SUCCESSFULLU);
                    map.put(" FAILURE_number", 0L);
                    map.put("TIMER", SETCOUNT);
                    return map;
                } else {
                    if (modifyFist == 1) {
                        //count值重置
                        modifyFist = LOGIN_CHANCES;
                        //Toast提醒
                        Log.i("liuhongliang", "Loagin: 三次修改失败 ");
                        //LOGIN_CHANCES次修改失败时，获取此时的Java虚拟机运行时刻并保存提交
                        long errorTime = SystemClock.elapsedRealtime();
                        Log.i("liuhongliang", "Loagin: 系统时间" + errorTime);
                        SharedPreferences sp1 = context.getSharedPreferences("data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp1.edit();
                        editor.putLong("modifyErrorTime", errorTime);
                        editor.putInt("modifyErrorNumber",sp1.getInt("modifyErrorNumber",0)+1);
                        editor.commit();
                        int modifyErrorNumber1 = sp.getInt("modifyErrorNumber", 0);
                        map.put("FIST", LOGON_FAILED);
                        map.put("FAILURE",select.getCount());
                        map.put("TIMER", TimeUtils.formatTime(30000L*modifyErrorNumber1));
                        return map;
                    } else {
                        modifyFist--;
                        if (count >= 0) {
                            count--;
                        }
                        MyApp.mysql.UpdataCount(0, count);
                    }
                    map.put("FIST", LOGON_FAILED);
                    map.put("FAILURE", select.getCount());
                    map.put("TIMER", 0);
                    return map;
                }
            }else {
                long time = l - modifyErrorTime;
                long a = TIME - time;
                map.put("FIST", LOGON_FAILED);
                map.put("FAILURE",select.getCount());
                map.put("TIMER", TimeUtils.formatTime(a));
                return map;
            }
        }
        return map;
    }

    // 校验旧密码
    private static boolean SecondPassword(int level, String pNewPassword, String oldPassword, Context context) {
        Bean select = MyApp.mysql.select();//查询
        //校验密码 解密旧密码
//        String s = AESUtils.decryptPassword(select.getPassword());
//        Log.i("liu", s);
        //查询数据库校验旧密码
        if (level == 0 && oldPassword.equals(select.getPassword())) {
            //加密新密码
//            String password = AESUtils.encryptPassword(pNewPassword);
//            Log.i("liuhong", password);
            //修改根据id修改密码
            MyApp.mysql.Updata(0, pNewPassword);
            return true;
        } else {
            return false;
        }
    }

    // 首次设置密码
    private static boolean First_password(int level, Context context, String password) {
        Bean bean = new Bean();
        //加密
        if (level == 0) {
            bean.setPassword(password);
            bean.setCount(COUNT);
            MyApp.mysql.insert(bean);
            return true;
        } else {
            return false;
        }
    }

    /*
        return :登录接口
     */
    public static Map<String, Object> Loagin(int level, String password, Context context) {
        Map<String, Object> map = new HashMap<>();
        //p判断是否第一次登陆
        int i = MyApp.mysql.selectQuery();
        boolean b = First_Loage(level);//首次登陆

        @SuppressLint("WrongConstant")
        SharedPreferences sp = context.getSharedPreferences("data", Context.MODE_APPEND);
        if (i == 0 && b) {
            //首次登陆
            Bean bean = new Bean();
            bean.setCount(COUNT);
            return getStringObjectMap(FIIST,bean, 0L);
        } else {
            Bean bean = MyApp.mysql.select();
            int count = bean.getCount();
            //点击登录获取当前时间
            long l = SystemClock.elapsedRealtime();
            Log.i("liuhongliang", "Loagin: 点击登录获取当前时间" + l);
            long errorTime = sp.getLong("errorTime", 0L);//三次登录失败后记录的时间

            return lockedOrNot(level, password, context, sp, bean, count, l, errorTime);

        }
    }

    private static Map<String, Object> lockedOrNot(int level, String password, Context context, SharedPreferences sp, Bean bean, int count, long l, long errorTime) {
        if (l - errorTime > TIME) {//判断当前是否是锁定状态
            //已过锁定时间//判断登录是否成功
            if (Second_landing(level, password, context)){
                //成功
                bean.setCount(COUNT);
                return getStringObjectMap(SUCCESSFULLU,bean, 0L);
            }else {
                //失败
                // fist = 表示登录第几次失败
                if (fist == 1) {
                    //第三次失败
                    thirdFailure(sp);
                    return getStringObjectMap(LOGON_FAILED,bean, TIME);
                } else {
                    fist--;
                    if (count >= 0) {
                        count--;
                    }
                    MyApp.mysql.UpdataCount(0, count);
                }
                Bean select = MyApp.mysql.select();
                return getStringObjectMap(LOGON_FAILED,select, 0L);
            }
        } else {//是锁定
            //当前点击登录获取时间 - 三次登录失败记录的时间 = 锁定时长
            long time = l - errorTime;
            //规定的锁定时间 - 锁定时长 = 剩余时间
            long a = TIME - time;
            return getStringObjectMap(LOGON_FAILED,bean, a);
        }
    }

    private static void thirdFailure(SharedPreferences sp) {
        //count值重置
        fist = LOGIN_CHANCES;
        //Toast提醒
        Log.i("liuhongliang", "Loagin: 三次登录失败 ");
        //第三次登录失败时，获取此时的时间并保存提交
        long errorTime1 = SystemClock.elapsedRealtime();
        Log.i("liuhongliang", "Loagin: 系统时间" + errorTime1);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("errorTime", errorTime1);
        int errorNumber = sp.getInt("errorNumber", 1);
        TIME = 3000L*errorNumber;
        errorNumber*=2;
        editor.putInt("errorNumber",errorNumber);
        editor.apply();
    }

    private static Map<String, Object> getStringObjectMap(int type,Bean bean, long errorTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("FIST", type);
        map.put("FAILURE", bean.getCount());
        map.put("TIMER", TimeUtils.formatTime(errorTime));
        return map;
    }

    public static boolean Second_landing(int level, String password, Context context) {
        Bean select = MyApp.mysql.select();
        if (select.getPassword().equals(password) && level == 0) {
            return true;
        } else {
            return false;
        }
    }

    //首次登陆
    public static boolean First_Loage(int level) {
        if (level == 0) {
            return true;
        } else {
            return false;
        }
    }

}
