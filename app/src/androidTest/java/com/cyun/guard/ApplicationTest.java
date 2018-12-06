package com.cyun.guard;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * desc:
 * author:  Administrator
 * date:    2018/2/9  10:57
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest extends TestCase {

    public static class User {
        public String name;
        public Date date;

        public User() {

        }
    }

    @Test
    public void gsonTest1() throws Exception {
        Gson gson = new GsonBuilder().create();
        ExampleInstrumentedTest.User u = new ExampleInstrumentedTest.User();
        u.name = "12321";
        u.date = new Date();
        System.out.println("默认时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(u.date));

        String json = "{\"name\":\"12345\",\"date\":\"2013-01-04 10:30:30\"}";
        System.out.println(gson.toJson(u));
        ExampleInstrumentedTest.User uu = gson.fromJson(json, ExampleInstrumentedTest.User.class);
        String udateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(uu.date);
        System.out.println(udateStr);

        assertEquals("323", udateStr);

    }
}
