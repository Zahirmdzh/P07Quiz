package sg.edu.rp.c347.p07quiz;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnRetrieve;
    TextView tvSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRetrieve = findViewById(R.id.btnRetrieve);
        tvSMS = findViewById(R.id.textViewSMS);

        int permissionCheck = PermissionChecker.checkSelfPermission
                (this, Manifest.permission.READ_SMS);

        if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_SMS}, 0);
            // stops the action from proceeding further as permission not
            //  granted yet
            return;
        }

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        // Create all messages URI
        Uri uri = Uri.parse("content://sms");

        // The columns we want
        //  date is when the message took place
        //  address is the number of the other party
        //  body is the message content
        //  type 1 is received, type 2 sent
        String[] reqCols = new String[]{"date", "address", "body", "type"};

        // Get Content Resolver object from which to
        //  query the content provider
        ContentResolver cr = getContentResolver();

        String filter ="address LIKE ? AND body LIKE ?";

        String[] filterArgs = {"%66%", "%RP"};


        // Fetch SMS Message from Built-in Content Provider
        Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
        String smsBody = "";

        if (cursor.moveToFirst()) {
            do {
                long dateInMillis = cursor.getLong(0);
                String address = cursor.getString(1);
                String body = cursor.getString(2);
                String type = cursor.getString(3);
                if (type.equalsIgnoreCase("1")) {
                    type = "Inbox:";
                } else {
                    type = "Sent:";
                }
                smsBody += type + address + "\n"
                        + "\n\"" + body + "\"\n\n";
            } while (cursor.moveToNext());
        }
        tvSMS.setText(smsBody);


            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnRetrieve.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(MainActivity.this, "Permission not granted",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
