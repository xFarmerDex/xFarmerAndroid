package com.zhy.http.okhttp.callback;

import okhttp3.Call;
import okhttp3.Response;

/*
 * Created by apple on 2019/7/4
 */
public class DefaultCallback<T> extends Callback<T> {

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        return null;
    }

    @Override
    public void onError(Call call, Exception e, int id) {

    }

    @Override
    public void onResponse(T response, int id) {

    }
}
