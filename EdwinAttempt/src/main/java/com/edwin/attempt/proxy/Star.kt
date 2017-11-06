package com.edwin.attempt.proxy

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

/**
 * Created by hongy_000 on 2017/11/6.
 * @author hongy_000
 */
class Star(name: String?) : IMovieStar {
    private var mName :String? = name

    companion object {
        private val TAG = Star::class.java.simpleName
    }

    override fun movieShow(money: Int) {
        Log.i(TAG, "$mName 出演了片酬$money 的电影")
    }

    override fun TVShow(money: Int) {
        Log.i(TAG, "$mName 出演了片酬$money 的电视剧")
    }

}