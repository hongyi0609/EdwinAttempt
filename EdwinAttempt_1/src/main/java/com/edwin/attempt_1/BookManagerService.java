package com.edwin.attempt_1;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.edwin.attempt.aidl.Book;
import com.edwin.attempt.aidl.IBookManager;
import com.edwin.attempt.aidl.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.os.Binder.getCallingUid;

/**
 * Created by hongy_000 on 2017/10/25.
 *
 * @author hongy_000
 */

public class BookManagerService extends Service {
    private final static String TAG = BookManagerService.class.getSimpleName();

    /**
     * 支持并发读写,线程安全
     * */
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    /**
     * @serial {RemoteCallBackList}
     * 用于跨进程通信，客户端可以通过该类型访问服务端数据
     * */
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();

    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    private static final String ACCESS_BOOK_SERVICE = "com.edwin.attempt_1.permission.ACCESS_BOOK_SERVICE";

    private Binder mBinder = new IBookManager.Stub(){
        @Override
        public List<Book> getBookList() throws RemoteException {
            // 测试thread运行问题，服务端默认运行在binder线程池里
            SystemClock.sleep(5000);
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            // 通过onTransact()方法检查权限，既验证了权限又验证了包名。如果验证失败直接返回false，
            // 服务端不会终止执行AIDL中的方法，达到保护服务端的效果
            /*int check = checkCallingOrSelfPermission(ACCESS_BOOK_SERVICE);
            Log.e(TAG, "check = " + check);
            if (check == PackageManager.PERMISSION_DENIED) {
                return false;
            }
            String packageName;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            Log.e(TAG, "packages = " + packages);
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
                if (packageName != null && !packageName.startsWith("com.edwin")) {
                    return false;
                }
            }*/
            return super.onTransact(code, data, reply, flags);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android aidl first"));
        mBookList.add(new Book(2, "Android aidl second"));
        new Thread(new ServiceWorker()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (!hasPermission()) {
            return null;
        }
        return mBinder;
    }

    /**
     *  check Permission
     *  @return whether has permission to access
     *  如果客户端是静态注册权限，可以选择在IBookManager.Stub#onTransact()方法中鉴权
     *  */
    private boolean hasPermission() {
        int check = checkCallingOrSelfPermission("com.edwin.attempt_1.permission.ACCESS_BOOK_SERVICE");
        return check == PackageManager.PERMISSION_DENIED;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "mIsServiceDestroyed = " + mIsServiceDestroyed);
            while (!mIsServiceDestroyed.get()) {
                try {
                    //添加一本新书/5s
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "new book# " + bookId);
                try {
                    newBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void newBookArrived(Book newBook) throws RemoteException{
        mBookList.add(newBook);
        int size = mListenerList.beginBroadcast();
        for (int i=0; i<size; i++) {
            final IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            Log.d(TAG, "newBookArrived, notify listener: " + listener);
            if (null != listener) {
                listener.onNewBookArrived(newBook);
            }
        }
        mListenerList.finishBroadcast();
    }
}
