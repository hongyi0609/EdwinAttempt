package com.edwin.attempt.proxy;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by hongy_000 on 2017/11/6.
 *
 * @author hongy_000
 */

public class ProxyHandler implements InvocationHandler {
    private static final String TAG = ProxyHandler.class.getSimpleName();
    /**
     * 被代理对象
     */
    private Object target;

    public ProxyHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if ("TVShow".equals(methodName) || "movieShow".equals(methodName)) {
            if (args[0] instanceof Integer && (int) args[0] < 2000000) {
                Log.i(TAG, "多少？$money 你打发叫花子呢？！");
                return null;
            }
        }
        return method.invoke(target,args);
    }

    /**
     * 获取代理对象
     * @return
     */
    public Object getProxy() {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                this
        );
    }
}
