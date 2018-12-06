package com.thdz.fast.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.thdz.fast.app.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;


/**
 * 数据工具类
 */
public class DataUtils {

    private static String TAG = "DataUtils";

    /**
     * 根据登录地址生成服务器接口地址
     */
    public static void CreateHostApiUrl(String ip) {
        String hostUrl = "http://" + ip + "/api/";
        MyApplication.getInstance().setHostUrl(hostUrl);
    }

    /**
     * 动态获取登录地址
     */
    public static String getApiUrl(String module) {
        return MyApplication.getInstance().getHostUrl() + module;
    }

    /**
     * 根据告警类型获取告警类型名称
     */
    public static String getAlarmTypeNameById(String type) {
        if (TextUtils.isEmpty(type)) {
            return "";
        } else if (type.equals("-1")) {
            return "告警";
        } else if (type.equals("0")) {
            return "低压告警";
        } else if (type.equals("1")) {
            return "位移告警";
        } else if (type.equals("2")) {
            return "震动告警";
        } else if (type.equals("7")) {
            return "失联告警";
        } else if (type.equals("8")) {
            return "失联恢复";
        }
        return "";

    }


    /**
     * 根据告警类型获取告警类型名称
     */
    public static String getAlarmTypeIdByName(String name) {
        if (TextUtils.isEmpty(name) || name.equals("全部")) {
            return "-1";
        } else if (name.equals("低压告警")) {
            return "0";
        } else if (name.equals("位移告警")) {
            return "1";
        } else if (name.equals("震动告警")) {
            return "2";
        } else if (name.equals("失联告警")) {
            return "7";
        } else if (name.equals("失联恢复")) {
            return "8";
        }
        return "";

    }


    /**
     * 返回今天的时间格式化值
     */
    public static String getFormatToday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = sdf.format(date);
        return str;
    }


    /**
     * 返回时间格式化值
     *
     * @param offset 距离今天的天的间隔
     */
    public static String getFormatTime(int offset) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, offset);
        long value = cal.getTimeInMillis();
        Date date = new Date();
        date.setTime(value);
        String str = sdf.format(date);
        return str;
    }

    /**
     * 截取字符串的后len个字符
     *
     * @param str
     * @param len
     * @return
     */
    public static String getlenStr(String str, int len) {
        if (TextUtils.isEmpty(str) || str.length() < len) {
            return "";
        } else {
            return str.substring(str.length() - len, str.length());
        }
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }


    public static String getPackageName(Context context) {
        String pkName = "";
        try {
            pkName = context.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkName;
    }


    /**
     * 获得系统当前时间，格式 yyyy-MM-dd HH:MM:ss
     */
    public static String getTimeStrByLong(long time) {
        Date now = new Date(time);

        java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatTime = format.format(now);
        return formatTime;
    }


    /**
     * 获得系统当前时间，格式 yyyy-MM-dd HH:MM:ss
     */
    public static String getCurrentTimeStr() {
        Date now = new Date();
        java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatTime = format.format(now);
        return formatTime;
    }


    /**
     * 是否是合理的密码 校验: 长度要大于4位，
     */
    public static boolean isPwdValid(String password) {
        return password.length() > 4;
    }

    /**
     * 检验是否是合法的IP地址
     */
    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            if (text.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检验是否是合法的IP地址
     */
    public static boolean portCheck(int value) {
        if (value < 0 || value > 65535) {
            return false;
        }
        return true;

    }


    /**
     * 获取手机ip地址
     */
    public static String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        // if (!inetAddress.isLoopbackAddress() && inetAddress
                        // instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }


    public static String getLocalIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress address : inetAddresses) {
                    if (!address.isLoopbackAddress()) {
                        String sAddr = address.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    /**
     * 去掉回车符，换行符
     */
    public static String delEnterFromStr(String str) {
        String value = str;
        value = value.replaceAll("(\r\n|\r|\n|\n\r)", "");
        return value;
    }


    /**
     * 把RAW里的文件复制到本地目录里
     */
    private void copyResourceFile(int rid, String targetFile) {
        InputStream fin = MyApplication.getInstance().getResources().openRawResource(rid);
        FileOutputStream fos = null;
        int length;
        try {
            fos = new FileOutputStream(targetFile);
            byte[] buffer = new byte[1024];
            while ((length = fin.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


//    /**
//     * 解析出完整的json结构
//     */
//    public static String getRespString(String response) {
//        if (response.contains("{") && response.contains("}")) {
//            return response;
//        }
//        return null;
//    }

    public static boolean isReturnOK4BT(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        String code = jsonObj.getString("code");
        if (code.equals("0")) {
            return true;
        }
        return false;

    }

    public static boolean isReturnOK(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        int code = jsonObj.getInt("code");
        if (code == 0) {
            return true;
        }
        return false;
    }

    public static String getReturnMsg(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        return jsonObj.getString("msg");
    }


    public static String getReturnList(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        return jsonObj.getString("pageData");
    }

    public static int getReturnRowCount(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        return jsonObj.getInt("rowCount");
    }

    public static int getReturnPageSize(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        return jsonObj.getInt("pageSize");
    }

    public static int getReturnPageIndex(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        return jsonObj.getInt("pageIndex");
    }

    /**
     * 从json结构中解析中完整DATA结构
     */
    public static String getReturnData(String value) throws JSONException {
        JSONObject jsonObj = new JSONObject(value);
        String msg = jsonObj.getString("data");
        return msg;
    }


    /**
     * 获取版本号
     */
    public static String getVersion(Context context) {
        String value = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            value = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    /**
     * 是否安装了百度地图app
     */
    private boolean isInstallBaidumap(Context context) {
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> pkgList = manager.getInstalledPackages(0);
        for (int i = 0; i < pkgList.size(); i++) {
            PackageInfo pI = pkgList.get(i);
            if (pI.packageName.equalsIgnoreCase("com.baidu.baidumap"))
                return true;
        }
        return false;

    }


    /**
     * 是否安装了某款app
     */
    public static boolean isPkgInstalled(Context context, String packagename) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    /**
     * 高德转百度（火星坐标gcj02ll–>百度坐标bd09ll）
     */
    public static double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }


    /**
     * 百度转高德（百度坐标bd09ll–>火星坐标gcj02ll）
     */
    public static double[] bdToGaoDe(double bd_lat, double bd_lon) {
        double[] gd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        gd_lat_lon[0] = z * Math.cos(theta);
        gd_lat_lon[1] = z * Math.sin(theta);
        return gd_lat_lon;
    }

    //////////////////Runtime
    public static void testRuntime() {
        Runtime tRuntime = Runtime.getRuntime();
        int count = tRuntime.availableProcessors(); // 当前系统cpu的数目
        long memory = tRuntime.freeMemory() / 1000; // 可用内容kb
        tRuntime.totalMemory();
    }


}
