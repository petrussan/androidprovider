package petra.ac.id.androidprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class MyDataProvider extends ContentProvider {
    static final String PROVIDER_NAME = "petra.ac.id.androidprovider.MyDataProvider";
    static final String URL = "content://"+PROVIDER_NAME+"/mydata";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "_id";
    static final String NAME = "name";
    static final String GRADE = "grade";

    private static HashMap<String, String> STUDENTS_DATA;

    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME,"students",1);
        uriMatcher.addURI(PROVIDER_NAME,"students/#",2);
    }


    // SQLIte
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "MyDB";
    static final String TABLE_NAME = "students";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_TABLE =
            " CREATE TABLE "+TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    " name TEXT NOT NULL, "+
                    " grade TEXT NOT NULL); ";

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
            onCreate(db);
        }
    }


    @Override
    public boolean onCreate() {
        Context c = getContext();
        DBHelper dbHelper = new DBHelper(c);
        db = dbHelper.getWritableDatabase();
        return (db == null)?false:true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings,
                        @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        //strings --> projection
        //s --> selection
        //strings1 --> selectionArgs
        //s1 --> sortOrder
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        switch(uriMatcher.match(uri)) {
            case 1:
                qb.setProjectionMap(STUDENTS_DATA);
                break;
            case 2:
                qb.appendWhere(_ID + "=" +uri.getPathSegments().get(1));
                break;
            default:
        }
        if (s1==null || s1=="") {
            s1 = NAME;
        }

        Cursor c = qb.query(db, strings, s,strings1,null,null,s1);
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(uriMatcher.match(uri)) {
            case 1:
                return "vnd.android.cursor.dir/vnd.example.mydata";
            case 2:
                return "vnd.android.cursor.item/vnd.example.mydata";
            default:
                throw new IllegalArgumentException("unsupported");
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long rowID = db.insert(TABLE_NAME,"", contentValues);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI,rowID);
            getContext().getContentResolver().notifyChange(_uri,null);
            return _uri;
        }
        throw new SQLException("Gagal menambah data");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
