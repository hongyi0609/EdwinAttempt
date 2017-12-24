package com.edwin.attempt.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.edwin.attempt.R
import com.edwin.attempt.proxy.IMovieStar
import com.edwin.attempt.proxy.ProxyHandler
import com.edwin.attempt.proxy.Star
import com.edwin.attempt.proxy.StarProxy

/**
 * Created by hongy_000 on 2017/11/6.
 * @author hongy_000
 */
 class ProxyActivity : AppCompatActivity() {
    companion object {
        private val TAG = ProxyActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proxy)
        val huangbo: Star? = Star("黄渤")
        val starAgent: StarProxy? = StarProxy(huangbo)

        starAgent?.movieShow(10000000)
        starAgent?.TVShow(5000)
    }

    fun dynamicProxy(v: View) {
        val sunhonglei: Star? = Star("孙红雷")
        val proxyHandler: ProxyHandler? = ProxyHandler(sunhonglei)
        val agent: IMovieStar? = proxyHandler?.proxy as IMovieStar
        agent?.movieShow(30000000)
        agent?.movieShow(300)
    }
}