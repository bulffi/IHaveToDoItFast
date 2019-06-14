package com.example.ihavetodoitfast;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

public class PhoneActivity extends AppCompatActivity {
    EditText editText;
    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码
    private ListView mlist_view;
    private ArrayAdapter<String> mAdapter;
    private List<String> mContactList = new ArrayList<>();
    private Map<String,String> mPinyinList = new HashMap<>();
    public static final int REQ_CODE_CONTACT = 1;

    /**
     * 判断是否有某项权限
     * @param string_permission 权限
     * @param request_code 请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission,int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }
    /**
     * 检查权限后的回调
     * @param requestCode 请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PERMISSION: //拨打电话
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this,"请允许拨号权限后再试",Toast.LENGTH_SHORT).show();
                } else {//成功
                    call("tel:"+"10086");
                }
                break;
        }
    }

    /**
     * 拨打电话（直接拨打）
     * @param telPhone 电话
     */
    public void call(String telPhone) {
        if (checkReadPermission(Manifest.permission.CALL_PHONE, REQUEST_CALL_PERMISSION)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telPhone));
            startActivity(intent);
        }
    }

    private void speakText(String text) {
        //1. 创建 SpeechSynthesizer 对象 , 第二个参数： 本地合成时传 InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer( this, null);
        mTts.setParameter(SpeechConstant. VOICE_NAME, "xiaoyu" ); // 设置发音人
        mTts.setParameter(SpeechConstant. SPEED, "50" );// 设置语速
        mTts.setParameter(SpeechConstant. VOLUME, "80" );// 设置音量，范围 0~100
        mTts.setParameter(SpeechConstant. ENGINE_TYPE, SpeechConstant. TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在 “./sdcard/iflytek.pcm”
        //保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
        //仅支持保存为 pcm 和 wav 格式， 如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant. TTS_AUDIO_PATH, "./sdcard/iflytek.pcm" );
        //3.开始合成
        mTts.startSpeaking( text, new MySynthesizerListener()) ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        Button btn = this.findViewById(R.id.bt1);
        editText = findViewById(R.id.edit);
        ImageButton btn_contact=findViewById(R.id.btn_contact);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5cf2b0f1");

        initView();
        initAdapter();
        checkContactPermission();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call("tel:"+ editText.getText());
            }
        });

        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecognizerDialog dialog = new RecognizerDialog(v.getContext(),null);
                // set language
                dialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                dialog.setParameter(SpeechConstant.ACCENT, "mandarin");
                // receive pure text
                dialog.setParameter(SpeechConstant.RESULT_TYPE, "plain");
                // filter out punctuation
                dialog.setParameter(SpeechConstant.ASR_PTT,"0");
                // set interval
                dialog.setParameter(SpeechConstant.VAD_BOS, "4000");
                dialog.setParameter(SpeechConstant.VAD_EOS, "1000");

                dialog.setListener(new RecognizerDialogListener() {
                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean b) {
                        String text = recognizerResult.getResultString();

                        String pinyinResult=PinyinHelper.convertToPinyinString(text,"",PinyinFormat.WITHOUT_TONE);
                        if(pinyinResult!=""){
                            String phone = mPinyinList.get(pinyinResult);
                            if(phone!=null){
                                editText.setText(phone);
                                speakText("您选择的联系人是"+text);
                            }
                            else if(pinyinResult.contains("liyimin")||pinyinResult.contains("jiangxiaohan")||pinyinResult.contains("zhangzijian")){
                                speakText("恭喜发现一位帅哥");
                            }
                            else if(pinyinResult.contains("luohao")){
                                speakText("恭喜发现一位傻逼");
                            }
                            else {
                                speakText("抱歉，没有听清联系人");
                            }
                        }
                    }
                    @Override
                    public void onError(SpeechError speechError) {
                    }
                });
                dialog.show();
                // remove the fucking stupid link
                TextView txt = (TextView)dialog.getWindow().getDecorView().findViewWithTag("textlink");
                txt.setText("");
                // prompt
                Toast.makeText(v.getContext(), "请说出联系人名", Toast.LENGTH_SHORT).show();

            }
        });
    }
    /**
     * 检查申请联系人权限
     */
    private void checkContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            //未获取到读取联系人权限

            //向系统申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQ_CODE_CONTACT);
        } else {
            query();
        }
    }

    private void query() {
        ContentResolver contentResolver = this.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        Log.d("SmallLetters", ContactsContract.CommonDataKinds.Phone.CONTENT_URI.toString());
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                String pinyinResult=PinyinHelper.convertToPinyinString(name,"",PinyinFormat.WITHOUT_TONE);
                mPinyinList.put(pinyinResult,number);

                mContactList.add(name + " " + number);
            }
            //更新数据
            mAdapter.notifyDataSetChanged();
            cursor.close();

        }
    }


    private void initAdapter() {
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mContactList);

        mlist_view.setAdapter(mAdapter);
    }

    private void initView() {
        //获取list控件
        mlist_view = findViewById(R.id.list_view);
    }
}
