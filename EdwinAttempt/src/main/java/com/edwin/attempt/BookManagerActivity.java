package com.edwin.attempt;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.edwin.attempt.aidl.Book;
import com.edwin.attempt.aidl.BookManagerService;
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

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final IBookManager bookManager = IBookManager.Stub.asInterface(service);
                mRemoteBookManager = bookManager;
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
            Log.e(TAG, "binder died.");
        }
    };

    /**
     * 运行在客户端的Binder线程池，服务端（BookManagerService）在主线程调用该接口会阻塞，记得开辟新的线程
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
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
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
