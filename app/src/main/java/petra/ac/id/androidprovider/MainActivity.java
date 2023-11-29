package petra.ac.id.androidprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addStudent(View v) {
        ContentValues cv = new ContentValues();
        cv.put(MyDataProvider.NAME,"Agus");
        cv.put(MyDataProvider.GRADE,"B+");
        Uri uri = getContentResolver().insert(MyDataProvider.CONTENT_URI,cv);
        Toast.makeText(getApplicationContext(),
                uri.toString(),Toast.LENGTH_LONG).show();
    }

    public void getStudents(View v) {
        String URL = "content://petra.ac.id.androidprovider.MyDataProvider";
        Uri students = Uri.parse(URL);
        Cursor c = managedQuery(students, null,null,null,null);
        if (c.moveToFirst()) {
            do {
                Toast.makeText(getApplicationContext(),
                        "Nama: "+c.getString(c.getColumnIndex(MyDataProvider.NAME)),
                        Toast.LENGTH_LONG).show();
            } while (c.moveToNext());
        }
    }
}