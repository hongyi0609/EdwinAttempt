package com.edwin.attempt;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.edwin.attempt.aidl.Book;
import com.edwin.attempt.aidl.IBookManager;
import com.edwin.attempt.aidl.IOnNewBookArrivedListener;

import java.util.List;

/**
 * Created by hongy_000 on 2017/10/25.
 *
 * @author hongy_000
 */

public class BookManagerActivity extends AppCompatActivity {
    private static final String TAG = BookManagerActivity.class.getSimpleName();
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    private IBookManager mRemoteBookManager;

    private static final String ACCESS_BOOK_SERVICE = "com.edwin.attempt_1.permission.ACCESS_BOOK_SERVICE";
    private static final int ACCESS_BOOK_SERVICE_REQUEST_CODE = 1;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.d(TAG, "received new book : " + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };


    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "binder died.");
            Log.d(TAG, "binderDied(), " + ",threadName: " + Thread.currentThread().getName());
            if (mRemoteBookManager == null) {
                return;
            }
            mRemoteBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mRemoteBookManager = null;
            bindServiceAndRecipient();
        }
    };

    private void bindServiceAndRecipient() {
//        Intent intent = new Intent(BookManagerActivity.this, BookManagerService.class);
        Intent intent = new Intent();
        intent.setAction("com.edwin.attempt");
        intent.setPackage("com.edwin.attempt_1");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final IBookManager bookManager = IBookManager.Stub.asInterface(service);
                mRemoteBookManager = bookManager;
                try {
                    //为保证程序的健壮性, 使用Binder类的linkToDeath()方法对服务端进程意外停止时进行处理.
                    //服务端进程停止时, IBinder.DeathRecipient mDeathRecipient对象的binderDied()方法会被回调.
                    //也可选择在onServiceDisconnected()时处理服务端进程意外停止的情况， 区别是binderDied()方法在线程池中执行，而onServiceDisconnected()在UI线程执行, 选择一种处理方式即可.
                    mRemoteBookManager.asBinder().linkToDeath(mDeathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<Book> list = bookManager.getBookList();
                            Log.i(TAG, "query book list, list type: " + list.getClass().getCanonicalName());
                            Book newBook = new Book(3, "沧浪之水");
                            bookManager.addBook(newBook);
                            Log.i(TAG, "add Book " + newBook.toString());
                            List<Book> newList = bookManager.getBookList();
                            Log.i(TAG, "query Book list:" + newList.toString());
                            bookManager.registerListener(mOnNewBookArrivedListener);
                        } catch (RemoteException re) {
                            re.printStackTrace();
                        }
                    }
                }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteBookManager = null;
            Log.e(TAG, "onServiceDisconnected(),binder died." + ",threadName: " + Thread.currentThread().getName());
        }
    };

    /**
     * 运行在客户端的Binder线程池，服务端（BookManagerService）在主线程调用该接口会阻塞，记得开辟新的线程
     * 这里通过handler切换到主线程，更新UI
     * */
    private IOnNewBookArrivedListener mOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager);
        if (ContextCompat.checkSelfPermission(this, ACCESS_BOOK_SERVICE) != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.access_book_permission))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(
                                    BookManagerActivity.this,
                                    new String[]{ACCESS_BOOK_SERVICE},
                                    ACCESS_BOOK_SERVICE_REQUEST_CODE);
                        }
                    }).setCancelable(false).show();
        } else {
  //        Intent intent = new Intent(this, BookManagerService.class);
  //        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            bindServiceAndRecipient();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_BOOK_SERVICE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bindServiceAndRecipient();
            } else {
                new AlertDialog.Builder(this).setMessage(getString(R.string.access_book_no_permission))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(
                                        BookManagerActivity.this,
                                        new String[]{ACCESS_BOOK_SERVICE},
                                        ACCESS_BOOK_SERVICE_REQUEST_CODE);
                            }
                        }).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            Log.i(TAG, "unregister listener: " + mOnNewBookArrivedListener);
            try {
                mRemoteBookManager.unregisterListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
