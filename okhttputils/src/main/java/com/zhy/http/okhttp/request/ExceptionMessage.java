package com.zhy.http.okhttp.request;

/*
 * Created by apple on 2020-01-02
 */
public class ExceptionMessage {

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
