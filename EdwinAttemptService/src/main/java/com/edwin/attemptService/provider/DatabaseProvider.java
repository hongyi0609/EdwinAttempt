package com.edwin.attemptService.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Edwin on 2017/12/23.
 *
 * @author Edwin
 */

public class DatabaseProvider extends ContentProvider {
    public final static int BOOK_DIR = 0;
    public final static int BOOK_ITEM = 1;
    public final static int CATEGORY_DIR = 2;
    public final static int CATEGORY_ITEM = 3;
    private  static UriMatcher mUriMatcher;
    public static final String AUTHORITY = "com.edwin.attempt.database.provider";

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, "book", BOOK_DIR);
        mUriMatcher.addURI(AUTHORITY, "book/#", BOOK_ITEM);
        mUriMatcher.addURI(AUTHORITY, "category", CATEGORY_DIR);
        mUriMatcher.addURI(AUTHORITY, "category/#", CATEGORY_ITEM);
    }

    private DatabaseSQLiteHelper mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseSQLiteHelper(getContext(), "BookStore.db", null, 2);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (mUriMatcher.match(uri)) {
            case BOOK_DIR:
                cursor = db.query("Book", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                db.query("Book", projection, "id = ?", new String[]{bookId}, null, null, sortOrder);
                break;
            case CATEGORY_DIR:
                cursor = db.query("Category", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                db.query("Category", projection, "id = ?", new String[]{categoryId}, null, null, sortOrder);
                break;
            default:break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        Uri uriTemp = null;
        switch (mUriMatcher.match(uri)) {
            case BOOK_DIR:
            case BOOK_ITEM:
                long newBookId = database.insert("Book", null, values);
                uriTemp = Uri.parse("content://" + AUTHORITY + "/book/" + newBookId);
                break;
            case CATEGORY_DIR:
            case CATEGORY_ITEM:
                long newCategoryId = database.insert("Category", null, values);
                uriTemp = Uri.parse("content://" + AUTHORITY + "/book/" + newCategoryId);
            default:break;
        }
        return uriTemp;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        int row = 0;
        switch (mUriMatcher.match(uri)) {
            case BOOK_DIR:
                row = database.delete("Book", selection, selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1);
                row = database.delete("Book", "id = ?", new String[]{bookId});
                break;
            case CATEGORY_DIR:
                row = database.delete("Category", selection, selectionArgs);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                row = database.delete("Category", "id = ?", new String[]{categoryId});
                break;
            default:break;
        }
        return row;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        int row = 0;
        switch (mUriMatcher.match(uri)) {
            case BOOK_DIR:
                row = database.update("Book", values, selection, selectionArgs);
                break;
            case BOOK_ITEM:
                String bookId = uri.getPathSegments().get(1); // Uri 权限部分之后以‘/’分割
                row = database.update("Book", values, "id = ?", new String[]{bookId});
                break;
            case CATEGORY_DIR:
                row = database.update("Category", values, selection, selectionArgs);
                break;
            case CATEGORY_ITEM:
                String categoryId = uri.getPathSegments().get(1);
                row = database.update("Category", values, "id = ?", new String[]{categoryId});
                break;
            default:break;

        }
        return row;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String type = null;
        switch (mUriMatcher.match(uri)) {
            case BOOK_DIR:
                type = "vnd.android.cursor.dir/vnd.com.edwin.attempt.database.provider.book";
                break;
            case BOOK_ITEM:
                type = "vnd.android.cursor.item/vnd.com.edwin.attempt.database.provider.book";
                break;
            case CATEGORY_DIR:
                type = "vnd.android.cursor.dir/vnd.com.edwin.attempt.database.provider.category";
                break;
            case CATEGORY_ITEM:
                type = "vnd.android.cursor.item/vnd.com.edwin.attempt.database.provider.category";
                break;
            default:break;
        }
        return type;
    }

}
