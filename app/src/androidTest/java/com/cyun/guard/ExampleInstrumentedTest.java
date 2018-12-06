package com.cyun.guard;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.cyun.guard", appContext.getPackageName());
    }

    public static class User {
        public String name;
        public Date date;

        public User() {

        }
    }

    @Test
    public void gsonTest1() throws Exception {
        Gson gson = new GsonBuilder().create();
        User u = new User();
        u.name = "12321";
        u.date = new Date();
        System.out.println("默认时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(u.date));

        String json = "{\"name\":\"12345\",\"date\":\"2013-01-04 10:30:30\"}";
        System.out.println(gson.toJson(u));
        User uu = gson.fromJson(json, User.class);
        String udateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(uu.date);
        System.out.println(udateStr);

        assertEquals("323", udateStr);

    }
}
