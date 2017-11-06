package com.edwin.attempt.proxy

import android.util.Log

/**
 * Created by hongy_000 on 2017/11/6.
 * @author hongy_000
 *
 * @link StarProxy 代理某个明星
 */
class StarProxy(star: Star?) : IMovieStar {
    private var mTarget:Star? = star
    companion object {
        private val TAG: String = StarProxy::class.java.simpleName
    }

    override fun movieShow(money: Int) {
        if (money < 2000000) {
            Log.i(TAG, "多少？$money 你打发叫花子呢？！")
            return
        }
        mTarget?.movieShow(money)
    }

    override fun TVShow(money: Int) {
        if (money < 300000) {
            Log.i(TAG, "多少？$money 你这剧不准备上热播吧？！")
            return
        }
        mTarget?.TVShow(money)
    }
}