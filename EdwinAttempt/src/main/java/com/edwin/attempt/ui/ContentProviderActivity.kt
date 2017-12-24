package com.edwin.attempt.ui

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import com.edwin.attempt.R

/**
 * Created by Edwin on 2017/12/24.
 * @author Edwin
 */
class ContentProviderActivity : AppCompatActivity(),View.OnClickListener {
    private var newId: String? = null
    private val TAG: String? = ContentProviderActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_provider_layout)
        findViewById<Button>(R.id.button_add_data).setOnClickListener(this)
        findViewById<Button>(R.id.button_query_from_book).setOnClickListener(this)
        findViewById<Button>(R.id.button_btn_update_data).setOnClickListener(this)
        findViewById<Button>(R.id.button_btn_delete_data).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_add_data -> {
                val uri: Uri? = Uri.parse("content://com.edwin.attempt.database.provider/book")
                val values: ContentValues? = ContentValues()
                values?.put("name", "A Clash of Kings")
                values?.put("author", "George Martin")
                values?.put("pages", 1040)
                values?.put("price", 22.83)

                val newUri: Uri? = contentResolver?.insert(uri, values)
                newId = newUri?.pathSegments?.get(1)
            }
            R.id.button_query_from_book -> {
                val uri: Uri? = Uri.parse("content://com.edwin.attempt.database.provider/book")
                val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        val name: String? = cursor.getString(cursor.getColumnIndex("name"))
                        val author: String? = cursor.getString(cursor.getColumnIndex("author"))
                        val pages: Int? = cursor.getInt(cursor.getColumnIndex("pages"))
                        val price: Double? = cursor.getDouble(cursor.getColumnIndex("price"))
                        Log.d(TAG, "book name is " + name)
                        Log.d(TAG, "book author is " + author)
                        Log.d(TAG, "book pages is " + pages)
                        Log.d(TAG, "book price is " + price)
                    }
                    cursor.close()
                }
            }
            R.id.button_btn_update_data -> {
                val uri: Uri? = Uri.parse("content://com.edwin.attempt.database.provider/book/" +
                        newId)
                val values: ContentValues? = ContentValues()
                values?.put("name", "A Storm of Swords")
                values?.put("pages", 1221)
                values?.put("price", 124.25)
                contentResolver?.update(uri, values, null, null)
            }
            R.id.button_btn_delete_data -> {
                val uri: Uri? = Uri.parse("content://com.edwin.attempt.database.provider/book/" +
                        newId)
                contentResolver.delete(uri,null,null)
            }
        }
    }
}
