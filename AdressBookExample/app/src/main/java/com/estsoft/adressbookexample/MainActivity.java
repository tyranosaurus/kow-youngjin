package com.estsoft.adressbookexample;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    // 어떤 액티비티에서 권한을 요청하는지 구별해주는 requestCode 값
    // requestCode 값은 startActivityForResult(인텐트 값 ,requestCode 값) 에서도 쓰인다
    // 숫자는 내가 지정할 수 있다.
    final int MainActivity_RequestCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 요청하고자 하는 권한들
        String[] permissions = new String[] {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE
        };

        // 안드로이드 버전이 마시멜로 이상인지 체크 : 마시멜로 미만에서는 런타임 요청 없이 바로 쿼리를 실행한다.
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {

            ArrayList<String> reqPermissionList = new ArrayList<>();

            // 안드로이드 버전이 마시멜로 이상인 경우
            for ( String permission : permissions ) {
                Log.e("-------onCreate", permission);
                // 권한 허용여부 체크 : 허용 0 반환, 거부 -1 반환
                int result = PermissionChecker.checkSelfPermission(MainActivity.this, permission);

                if ( result == PermissionChecker.PERMISSION_GRANTED ) {
                    // 권한이 허용된 경우
                    Log.e("-------onCreate", permission + "획득");

                } else {
                    // 권한이 거부된 경우
                    reqPermissionList.add(permission);
                }
            }
            getPermissions(reqPermissionList);
        } else {
            // 안드로이드 버전이 마시멜로 미만인 경우
            Log.e("-------onCreate", "마쉬멜로 미만 버전(권한 요청 X)");
        }

        // 주소록 가져오는 버튼
        Button bringAddressBook = (Button)findViewById(R.id.bringAddressBook);
        bringAddressBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetUserContactsList();
            }
        });

        // 자기번호 가져오기
        Button bringMyPhoneNumber = (Button)findViewById(R.id.bringMyPhoneNumber);
        bringMyPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetUserPhoneNumber();
            }
        });
    }

    // 권한 획득 성공 또는 실패 결과에 따른 처리 해주는 메소드
   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MainActivity_RequestCode: {
                // If request is cancelled, the result arrays are empty.
                for ( int i = 0; i < permissions.length; i++ ) {
                    if ( grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        Log.e("onRequestPermissionsRes", permissions[i] + "획득");
                        Log.e("onRequestPermissionsRes", grantResults[i]+"");
                    } else {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                }

                // 이 권한이 필요한 이유를 설명하고 다시 권한을 재요청 한다.
                // ActivityCompat.shouldShowRequestPermissionRationale(this, 요청하고자 하는 권한) 값은 디폴트가 false 로 되어있다.
                // 사용자가 권한 요청에 거부를 하면 false -> true 로 값이 바뀐다.
                if ( ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED ||
                        ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_PHONE_STATE)) {

                    // 다이어로그 같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다
                    // AlertDialog.Builder import : app 과 android.support.v7.app 두 가지가 있는데, app은 예전 디자인 이고 v7은 최신 material 디자인 이다.
                    // new AlertDialog.Builder(this) : 여기서 MainActivity.this 는 Context이다. -> Context context = this 이렇게 해도 된다.
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setTitle("권한 요청")
                            .setMessage("주소록 동기화를 위해서 권한에 대한 허용이 필요합니다. " +
                                    "\"다시 묻지 않기\"를 체크하시면 이후 \"설정->애플리케이션 관리\"에서 " +
                                    "권한 설정을 할 수 있습니다.")
                            .setCancelable(false) // back 키 터치했을때 다이얼로그 cancel 여부 설정, 디폴트는 true
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    // 다이얼로그 보여주기
                    alertDialogBuilder.show();
                }

                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void getPermissions(ArrayList<String> reqPermissionList) {
        Log.e("-------getPermission함수", reqPermissionList+"");

        String[] permissions = new String[reqPermissionList.size()];
        for ( int i = 0; i < reqPermissionList.size(); i++ ) {
            permissions[i] = reqPermissionList.get(i);
        }

        if ( reqPermissionList.size() > 0 ) {
            ActivityCompat.requestPermissions(MainActivity.this, permissions, MainActivity_RequestCode);
        } else {
            // 예외처리 : 요청할 권한이 없을 경우
        }
    }

    public void GetUserContactsList() {
        // 필요한 권한 : Manifest.permission.READ_CONTACTS;
        // 권한 승인 안받은 경우 예외처리
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED ||
                ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {

            Log.e("GetUserContactsList", ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)+"");
            // 권한 없음 & 권한 재요청 안내
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("[주소록 엑세스] 권한 요청")
                    .setMessage("주소록 동기화를 위해 [주소록 엑세스] 권한이 필요합니다." +
                            "\"설정->애플리케이션 관리->애플리케이션 관리자 -> 해당 앱 -> 권한\"에서 설정을 바꿀 수 있습니다.")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialogBuilder.show();

            return;
        }

        /**
         *  ContactsContract : 연락처(주소록)와 관련된 정보를 저장하는 DB를 정의하고 있다.
         *  ContactsContract.Contacts : DB의 연락처 테이블에 대한 클래스. 동일한 사람에 대한 연락처 정보들을 종합한 기록들을 가지고 있다.
         *  CommonDataKinds : ContactsContract.Data table 에 저장된 일반 데이터 타입에 대한 정의를 담고 있는 컨테이너
         * */

        // 연락처의 [ID, 이름] 을 저장하는 String 배열 변수
        String[] arrProjection = {
            ContactsContract.Contacts._ID, // ID 열에 해당 하는 정보. 저장된 각 사용자는 고유의 ID를 가진다.
            ContactsContract.Contacts.DISPLAY_NAME // 연락처에 저장된 이름 정보.
        };

        // 연락처의 [전화번호] 를 저장하는 String 배열 변수
        String[] arrPhoneProjection = {
            ContactsContract.CommonDataKinds.Phone.NUMBER // 연락처에 저장된 전화번호 정보
        };

        // 연락처의 [이메일] 을 저장하는 String 배열 변수
        String[] arrEmailProjection = {
            ContactsContract.CommonDataKinds.Email.DATA // 연락처에 저장된 이메일 정보
        };

        /**
         * ※ Cursor 란?
         *
         * 안드로이드에서는 DB에서 가져온 데이터를 쉽게 처리하기 위해서 Cursor 라는 인터페이스를 제공한다.
         * Cursor는 기본적으로 DB에서 값을 가져와서 마치 실제 Table의 한 행(Row), 한 행(Row) 을 참조하는 것 처럼 사용 할 수 있게 해 준다.
         *
         * */

        /**
         * ※ this.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder) 란?
         *
         * 1. uri : content://scheme 방식의 원하는 데이터를 가져오기 위한 uri
         * 2. projection : 가져올 컬럼의 이름들 배열, null이면 모든 컬럼
         * 3. selection : where 절에 해당하는 내용 ("where" 는 생략)
         * 4. selectionArgs : 위의 selection에서 ?로 표시한 곳에 들어갈 데이터 배열
         *    ex) ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
         * 5. sortOrder : order by 절의 내용이 들어간다 ("order by"는 생략)
         *
         * */

        // get user list
        // 주소록에 기록된 연락처 정보 중 ID 와 저장된 이름을 가져오는 Cursor
        // 이후 ID, 저장된 이름에 이어서 email 같은 부가적인 정보를 뒤에 붙인다.
        Cursor clsCursor = MainActivity.this.getContentResolver().query (
                ContactsContract.Contacts.CONTENT_URI,
                arrProjection, // 연락처의 [ID, 이름] 컬럼의 정보를 가져온다.
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1" , // HAS_PHONE_NUMBER : 하나 이상의 전화번호가 있으면 1, 그 외에는 0
                null,
                null
        );
        // Cursor.moveToNext() : Cursor를 다음 행(Row)으로 이동 시킨다. 다음행이 있으면 true, 없으면 false
        // 더이상 출력할 연락처 정보가 없으면 false를 반환하면 while문을 빠져나간다.
        while( clsCursor.moveToNext() )
        {
            // DB에 정의된 필드의 타입에 의해 int로 설정한 필드는 getInt, String으로 설정한 필드는 getString 으로 가져와야 한다.
            // getInt(int index), getString(int index) ... : 쿼리에 따라 요청된 각 컬럼 순서대로 0, 1, 2.. 이렇게 index가 부여되고 index에 해당하는 컬럼의 값이 int나 String 등의 타입으로 가져와진다.
            String strContactId = clsCursor.getString( 0 );

            Log.d("Unity", "연락처 사용자 ID : " + clsCursor.getString( 0 ));
            Log.d("Unity", "연락처 사용자 이름 : " + clsCursor.getString( 1 ));

            // phone number에 접근하는 Cursor
            Cursor clsPhoneCursor = MainActivity.this.getContentResolver().query (
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrPhoneProjection, // 연락처의 [전화번호] 컬럼의 정보를 가져온다.
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + strContactId, // where 절 : 연락처의 ID와 일치하는 전화번호를 가져온다.
                    null,
                    null
            );

            // 연락처에서 하나의 ID에 핸드폰, 집전화 등 전화번호가 하나 이상 있는 경우 while문으로 모두 출력한다.
            // 더이상 출력할 연락처 번호가 없으면 false를 반환하면 while문을 빠져나간다.
            while( clsPhoneCursor.moveToNext() )
            {
                Log.d("Unity", "연락처 사용자 번호 : " + clsPhoneCursor.getString( 0 ));
            }
            // 전화번호 정보에 접근하는 Cursor 닫는다.
            clsPhoneCursor.close();


            // email에 접근하는 Cursor
            Cursor clsEmailCursor = MainActivity.this.getContentResolver().query (
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    arrEmailProjection, // 연락처의 [이메일] 컬럼의 정보를 가져온다.
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + strContactId,
                    null,
                    null
            );

            // 연락처에서 하나의 ID에 이메일 정보가 하나 이상 있는 경우 while문으로 모두 출력한다.
            // 더이상 출력할 연락처 번호가 없으면 false를 반환하면 while문을 빠져나간다.
            while( clsEmailCursor.moveToNext() )
            {
                Log.d("Unity", "연락처 사용자 email : " + clsEmailCursor.getString( 0 ));
            }
            // 이메일 정보에 접근하는 Cursor 닫는다.
            clsEmailCursor.close();


            // note(메모)
            String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] noteWhereParams = new String[]{
                    strContactId,
                    ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE // MIMETYPE 중 Note(즉, 메모)에 해당하는 내용을 불러오라는 뜻
            };

            // note(메모)에 접근하는 Cursor
            Cursor clsNoteCursor = MainActivity.this.getContentResolver().query (
                    ContactsContract.Data.CONTENT_URI,
                    null, // null이면 모든 컬럼 조회
                    noteWhere,
                    noteWhereParams, // noteWhere의 첫번째 ?에 noteWhereParams[0]이 들어가고, 두번째 ?d에 noteWhereParams[1]이 들어간다.
                    null
            );

            // 연락처에서 사용자 메모가 하나 이상 있는 경우 while문으로 모두 출력한다.
            // 더이상 출력할 연락처 번호가 없으면 false를 반환하면 while문을 빠져나간다.
            while( clsNoteCursor.moveToNext() )
            {
                // clsNoteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE) : 컬럼을 지정하고, 컬럼의 index 번호를 얻는다.
                Log.d("Unity", "연락처 사용자 메모 : " + clsNoteCursor.getString(clsNoteCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE)) );
            }
            // note(메모) 정보에 접근하는 Cursor 닫는다.
            clsNoteCursor.close();


            // address(주소지)
            String addressWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] addressWhereParams = new String[]{
                    strContactId,
                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE  // MIMETYPE 중  StructuredPostal(즉, 우편주소)에 해당하는 내용을 불러오라는 뜻
            };

            Cursor clsAddressCursor = MainActivity.this.getContentResolver().query (
                    ContactsContract.Data.CONTENT_URI,
                    null, // null이면 모든 컬럼 조회
                    addressWhere,
                    addressWhereParams, // addressWhere 첫번째 ?에 addressWhereParams[0]이 들어가고, 두번째 ?d에 addressWhereParams[1]이 들어간다.
                    null
            );

            // 연락처에서 사용자 주소지가 하나 이상 있는 경우 while문으로 모두 출력한다.
            // 더이상 출력할 연락처 번호가 없으면 false를 반환하면 while문을 빠져나간다.
            while( clsAddressCursor.moveToNext() )
            {
                // clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.[StructuredPostal.컬럼 이름]) : 컬럼을 지정하고, 컬럼의 index 번호를 얻는다.
                Log.d("Unity", "연락처 사용자 주소 poBox : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX)) );
                Log.d("Unity", "연락처 사용자 주소 street : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET)) );
                Log.d("Unity", "연락처 사용자 주소 city : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY)) );
                Log.d("Unity", "연락처 사용자 주소 region : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION)) );
                Log.d("Unity", "연락처 사용자 주소 postCode : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE)) );
                Log.d("Unity", "연락처 사용자 주소 country : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY)) );
                Log.d("Unity", "연락처 사용자 주소 type : " + clsAddressCursor.getString(clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE)) );
            }
            // address(주소지) 정보에 접근하는 Cursor 닫는다.
            clsAddressCursor.close();


            // Organization(회사)
            String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] orgWhereParams = new String[]{
                    strContactId,
                    ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE // MIMETYPE 중  Organization(즉, 회사)에 해당하는 내용을 불러오라는 뜻
            };

            Cursor clsOrgCursor = MainActivity.this.getContentResolver().query (
                    ContactsContract.Data.CONTENT_URI,
                    null, // null이면 모든 컬럼 조회
                    orgWhere,
                    orgWhereParams, // orgWhere 첫번째 ?에 orgWhereParams[0]이 들어가고, 두번째 ?d에 orgWhereParams[1]이 들어간다.
                    null
            );

            while( clsOrgCursor.moveToNext() )
            {
                // clsAddressCursor.getColumnIndex(ContactsContract.CommonDataKinds.[Organization.컬럼 이름]) : 컬럼을 지정하고, 컬럼의 index 번호를 얻는다.
                Log.d("Unity", "연락처 사용자 회사 : " + clsOrgCursor.getString(clsOrgCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA)) );
                Log.d("Unity", "연락처 사용자 직급 : " + clsOrgCursor.getString(clsOrgCursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE)) );
            }
            // Organization(회사) 정보에 접근하는 Cursor 닫는다.
            clsOrgCursor.close();

        }

        // ID, 이름 정보에 접근하는 Cursor 닫는다.
        clsCursor.close( );
    }


    public void GetUserPhoneNumber() {
        // 내 번호를 저장할 변수
        String phoneNum = "아무값도 안들어옴";

        // 필요한 권한 : Manifest.permission.READ_PHONE_STATE;
        // 권한 승인 안받은 경우 예외처리
        if ( ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED ||
                ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_PHONE_STATE) ) {
            Log.e("GetUserPhoneNumber", ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_PHONE_STATE)+"");
            // 권한 없음 & 권한 재요청 안내
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("[전화 걸기 및 관리] 권한 요청")
                    .setMessage("주소록 동기화를 위해 [전화 걸기 및 관리] 권한이 필요합니다." +
                            "\"설정->애플리케이션 관리->애플리케이션 관리자 -> 해당 앱 -> 권한\"에서 설정을 바꿀 수 있습니다.")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialogBuilder.show();

            return;
        }
        // 내 핸드폰 번호를 가져와서 phoneNum에 저장한다.
        TelephonyManager telManager = (TelephonyManager) MainActivity.this.getSystemService(this.TELEPHONY_SERVICE);
        phoneNum = telManager.getLine1Number();

        // 내 핸드폰 번호를 출력한다.
        TextView myPhoneNumber = (TextView)findViewById(R.id.myPhoneNumber);
        myPhoneNumber.setText(phoneNum);

        // 내 핸드폰 번호를 DB에 넣는다.
        final String phoneNumber = phoneNum;
        // 네트워크 exception이 발생해 Thread로 해결
        new Thread() {
            public void run() {
                // OkHttpClient 를 사용하기 위해서는 Manifest에 INTERNET 권한을 추가하고,
                // build.gradle 에 okhttp 컴파일 코드를 추가해야 한다.
                // 참고 : OkHttpClient을 이용한 통신은 3G환경에서는 java.net.SocketTimeoutException 에러가 발생
                //        Wifi 환경에서는 잘됨.
                OkHttpClient client = new OkHttpClient();
                RequestBody body = new FormBody.Builder()
                        .add("phoneNumber", phoneNumber)
                        .build();

                //request
                Request request = new Request.Builder()
                        // input MAC address of Ethernet
                        .url("http://192.168.22.73/addressbookphp/insertPhoneNumber.php")
                        .post(body)
                        .build();
                try {
                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}