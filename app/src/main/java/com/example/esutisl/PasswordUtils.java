package com.example.esutisl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;


import java.util.HashMap;
import java.util.Map;

public class PasswordUtils {
    //3次登录机会
    private static int LOGIN_CHANCES = 3;
    //还剩几次登录机会的标志，初始值就是LOGIN_CHANCES

    private static long WAIT_TIME = 3000L;
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

    /*
    /设置密码接口
    return Map<String,object>
     */
    public static Map<String, Object> set_Password(int level, String pNewPassword, String oldPassword, Context context) {
        Map<String, Object> map = new HashMap<>();
        //创建数据库实例
        Mysql mysql = new Mysql(context);
        // 调用查询方法 如果i retrun 0 ，表示没有数据 表示首次登录
        int i = mysql.selectQuery();
        if (i == 0) {
            //首次设置密码
            boolean is_setPassword = First_password(level, context, pNewPassword);
            if (is_setPassword) {
                map.put("FIST", FIIST);
                map.put(" FAILURE_number", 0L);
                map.put("TIMER", SETCOUNT);
                return map;
            }
        } else {
            // 第二次修改密码需要校验旧密码
            boolean b = Second__password(level, pNewPassword, oldPassword, context);
            if (b) {
                Mysql mysql1 = new Mysql(context);
                mysql1.UpdataCount(0, 3);
                map.put("FIST", SUCCESSFULLU);
                map.put(" FAILURE_number", 0L);
                map.put("TIMER", SETCOUNT);
                return map;
            } else {
                Mysql mysql1 = new Mysql(context);
                Bean select = mysql1.select();
                int count = select.getCount();
                count--;
                if (count >= 0) {
                    mysql1.UpdataSetCount(0, count);
                }
                map.put("FIST", LOGON_FAILED);
                map.put(" FAILURE_number", 0L);
                map.put("TIMER", select.getSetcount());
                return map;
            }
        }
        return map;
    }

    // 校验旧密码
    private static boolean Second__password(int level, String pNewPassword, String oldPassword, Context context) {
        Mysql mysql = new Mysql(context);
        Bean select = mysql.select();//查询
        //校验密码 解密旧密码
//        String s = AESUtils.decryptPassword(select.getPassword());
//        Log.i("liu", s);
        //查询数据库校验旧密码
        if (level == 0 && oldPassword.equals(select.getPassword())) {
            //加密新密码
//            String password = AESUtils.encryptPassword(pNewPassword);
//            Log.i("liuhong", password);
            //修改根据id修改密码
            mysql.Updata(0, pNewPassword);
            return true;
        } else {
            return false;
        }
    }

    // 首次设置密码
    private static boolean First_password(int level, Context context, String password) {
        Mysql mysql = new Mysql(context);
        Bean bean = new Bean();
        //加密

        if (level == 0) {
            bean.setPassword(password);
            bean.setCount(COUNT);
            mysql.insert(bean);
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
        Mysql mysql = new Mysql(context);
        int i = mysql.selectQuery();
        boolean b = First_Loage(level);//首次登陆
        if (i == 0 && b) {
            //首次登陆
            Mysql mysql1 = new Mysql(context);
            Bean bean = new Bean();
            bean.setCount(COUNT);
//            mysql1.insert(bean);
            map.put("FIST", FIIST);
            map.put(" FAILURE_number", 0);
            map.put("TIMER", COUNT);
            return map;
        } else {
            Mysql mysql1 = new Mysql(context);
            Bean bean = mysql1.select();
            int count = bean.getCount();
            if (count >= 0) {
                bean.setCount(count);
                mysql1.UpdataCount(0, count);
            }
            //第二次登陆
            boolean b1 = Second_landing(level, password, context);

                if (b1) {
                    mysql1.UpdataCount(0, 3);
                    Bean select = mysql1.select();
                    Log.i("liuhongliang", select.toString());
                    map.put("FIST", SUCCESSFULLU);
                    map.put(" FAILURE_number", 0);
                    map.put("TIMER", 3);
                    return map;
                }else {
                    map.put("FIST", LOGON_FAILED);
                    map.put(" FAILURE_number", WAIT_TIME);
                    map.put("TIMER", bean.getCount());
                    return map;
                }
        }
    }

    public static boolean Second_landing(int level, String password, Context context) {
        Mysql mysql = new Mysql(context);
        Bean select = mysql.select();
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
