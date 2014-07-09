package com.patrickmckinnon.homeaudio;

/**
 * Created by prm on 1/21/14.
 */
public interface LoggerInterface {
    public boolean debugEnabled();
    public void e(String message);
    public void e(String message, Throwable cause);
    public void w(String message);
    public void w(String message, Throwable cause);
    public void i(String message);
    public void i(String message, Throwable cause);
    public void d(String message);
    public void d(String message, Throwable cause);
    public void v(String message);
    public void v(String message, Throwable cause);
}
