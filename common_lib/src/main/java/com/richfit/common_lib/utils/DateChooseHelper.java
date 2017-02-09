
package com.richfit.common_lib.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by monday on 2016/1/20.
 */
public class DateChooseHelper {


    public static void chooseDateForEditText(final Context context, final EditText view,
                                             final String datePattern) {

        DatePickDialog dialog = new DatePickDialog(context);
        //设置上下年分限制
        dialog.setYearLimt(5);
        //设置标题
        dialog.setTitle("选择时间");
        //设置类型
        dialog.setType(DateType.TYPE_ALL);
        //设置消息体的显示格式，日期格式
        dialog.setMessageFormat("yyyy-MM-dd HH:mm");
        //设置选择回调
        dialog.setOnChangeLisener(null);
        //设置点击确定按钮回调
        dialog.setOnSureLisener(new OnSureLisener() {
            @Override
            public void onSure(Date date) {
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                calendar.setTime(date);// //为Calendar对象设置时间为当前日期
                final int year = calendar.get(Calendar.YEAR); // 获取Calendar对象中的年
                final int month = calendar.get(Calendar.MONTH);// 获取Calendar对象中的月
                final int day = calendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);
                final int second = calendar.get(Calendar.SECOND);
                updateDate(datePattern,year,month,day,view);
            }
        });
        dialog.show();

//        Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
//        Date mydate = new Date(); // 获取当前日期Date对象
//        mycalendar.setTime(mydate);// //为Calendar对象设置时间为当前日期
//        final int year = mycalendar.get(Calendar.YEAR); // 获取Calendar对象中的年
//        final int month = mycalendar.get(Calendar.MONTH);// 获取Calendar对象中的月
//        final int day = mycalendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
//        final int hour = mycalendar.get(Calendar.HOUR_OF_DAY);
//        final int minute = mycalendar.get(Calendar.MINUTE);
//        final int second = mycalendar.get(Calendar.SECOND);
//        DatePickerDialog dpd = new DatePickerDialog(context,
//                new DatePickerDialog.OnDateSetListener() {
//                    /**
//                     * params：com.richfit.dgshbarcodesystem.view：该事件关联的组件 params：myyear：当前选择的年
//                     * params：monthOfYear：当前选择的月
//                     * params：dayOfMonth：当前选择的日
//                     */
//                    int year;
//                    int month;
//                    int day;
//
//                    @Override
//                    public void onDateSet(DatePicker view, int myyear,
//                                          int monthOfYear, int dayOfMonth) {
//                        // 修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
//                        year = myyear;
//                        month = monthOfYear;
//                        day = dayOfMonth;
//                        // 更新日期
//                        updateDate();
//                    }
//
//                    // 当DatePickerDialog关闭时，更新日期显示
//                    private void updateDate() {
//                        // 在TextView上显示日期
//                        // System.out.println("当前日期："+year+"-"+(month+1)+"-"+day);
//                        month = month + 1;
//                        String strmonth;
//                        String strday;
//                        if (month < 10) {
//                            strmonth = "0" + month;
//                        } else {
//                            strmonth = "" + month;
//                        }
//                        if (day < 10) {
//                            strday = "0" + day;
//                        } else {
//                            strday = "" + day;
//                        }
//
//                        view.setText(getTextByDatePattern(datePattern,
//                                year,strmonth,strday));
//                    }
//                }, year, month, day);
//        dpd.show();
    }

    private static void updateDate(String datePattern,int year, int month, int day,final EditText view) {
        // 在TextView上显示日期
        month = month + 1;
        String strmonth;
        String strday;
        if (month < 10) {
            strmonth = "0" + month;
        } else {
            strmonth = "" + month;
        }
        if (day < 10) {
            strday = "0" + day;
        } else {
            strday = "" + day;
        }
        view.setText(getTextByDatePattern(datePattern, year, strmonth, strday));
    }


    private static String getTextByDatePattern(@NonNull String datePattern,
                                               int year, String month, String day) {
        String text;
        switch (datePattern) {
            case Global.GLOBAL_DATE_PATTERN_TYPE1:
                text = year + "" + month + "" + day;
                break;
            case Global.GLOBAL_DATE_PATTERN_TYPE2:
                text = year + " " + month + " " + day;
                break;
            case Global.GLOBAL_DATE_PATTERN_TYPE3:
                text = year + "_" + month + "_" + day;
                break;
            case Global.GLOBAL_DATE_PATTERN_TYPE4:
                text = year + "/" + month + "/" + day;
                break;
            default:
                text = year + "" + month + "" + day;
                break;
        }
        return text;
    }
}
