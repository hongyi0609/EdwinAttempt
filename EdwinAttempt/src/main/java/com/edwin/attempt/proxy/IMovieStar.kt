package com.edwin.attempt.proxy

/**
 * Created by hongy_000 on 2017/11/6.
 * @author hongy_000
 */
interface IMovieStar {
    /**
     *  演电影
     *  @param money ，电影片酬
     */
    fun movieShow(money: Int)

    /**
     *  演电视剧
     *  @param money 电视剧片酬
     */
    fun TVShow(money: Int)
}