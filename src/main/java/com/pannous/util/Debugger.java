package com.pannous.util;

import com.skype.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Copyright 2013 Pannous GmbH
 * <p/>
 * User: info@pannous.com / me
 * Date: 3/10/13
 * Time: 10:05 PM
 */
public class Debugger {

    public static void error(Throwable arg1) {
        try {
            String fullMess = "";
            String message = arg1.getMessage();
            if (message != null)
                fullMess = message;

            if (arg1.getCause() != null && arg1.getCause().getMessage() != null)
                fullMess += " " + arg1.getCause().getMessage();
            StringWriter sw = new StringWriter();
            arg1.printStackTrace(new PrintWriter(sw));
            fullMess = fullMess + " \r\n" + sw.toString();
            if (arg1.getCause() != null) {
                arg1.getCause().printStackTrace(new PrintWriter(sw));
                fullMess = fullMess + " \r\n" + sw.toString();
            }
            Log.e("Pannous", fullMess);
            System.err.println(fullMess);
        } catch (Exception e1) {
            Log.e("Pannous", "debug failed " + e1);
            arg1.printStackTrace();
        }
    }

    public static void info(String s) {
        Log.d("Pannous", s);
        System.out.println(s);
    }
}
