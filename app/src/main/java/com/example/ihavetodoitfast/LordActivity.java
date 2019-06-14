package com.example.ihavetodoitfast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class LordActivity extends AppCompatActivity {

    private void switchNote(){
        Intent intent = NoteListActivity.newIntent(this);
        startActivity(intent);
    }

    private void switchCall(){
        Intent intent=new Intent(LordActivity.this, PhoneActivity.class);
        startActivity(intent);
    }

    private void switchWeather(){
        Intent intent = new Intent(this,WeatherActivity.class);
        startActivity(intent);
    }

    private void showTip(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void speakText(String text,boolean ifChinese) {
        //1. 创建 SpeechSynthesizer 对象 , 第二个参数： 本地合成时传 InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer( this, null);
        if(ifChinese)
        mTts.setParameter(SpeechConstant. VOICE_NAME, "xiaoyu" ); // 设置发音人
        else
            mTts.setParameter(SpeechConstant. VOICE_NAME, "henry" );
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
        setContentView(R.layout.activity_lord);
        ImageButton mCallButton = findViewById(R.id.option_button);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5cf2b0f1");

        mCallButton.setOnClickListener(new OnClickListener() {
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

                        if (text.contains("备忘录")){
                            switchNote();
                            speakText("打开备忘录功能",true);

                        }
                        else if(text.contains("notepad")){
                            switchNote();
                            speakText("opening notepad for you. take care",false);
                        }
                        else if (text.contains("打电话")){
                            switchCall();
                            speakText("跳转打电话界面",true);
                        }
                        else if(text.contains("phone")){
                            switchCall();
                            speakText("switching to the calling form for you. good luck",false);
                        }
                        else if(text.contains("看天气")){
                            switchWeather();
                            speakText("准备天气信息",true);
                        }
                        else if(text.contains("weather")){
                            switchWeather();
                            speakText("fetching the weather conditions for you. enjoy it",false);
                        }
                        else if(text.contains("你好")||text.contains("您好"))
                            speakText("您好",true);
                        else if(text.contains("hello"))
                            speakText("hello",false);
                        else if(text.contains("再见")){
                            speakText("再您妈的见呢",true);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    System.exit(0);
                                }
                            }, 1400);
                        }
                        else if(text.contains("goodbye")||text.contains("seeyou")){
                            speakText("goodbye",false);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    System.exit(0);
                                }
                            }, 1000);
                        }
                        else if(text.contains("滚蛋")){
                            speakText("滚您妈的蛋呢",true);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    System.exit(0);
                                }
                            }, 1400);
                        }
                        else if(text.contains("*")){
                            speakText("fuck yourself",false);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    System.exit(0);
                                }
                            }, 1200);
                        }
                        else if(text!="") {
                            int l = (int) (System.currentTimeMillis() % 4);
                            switch (l) {
                                case 0:
                                    speakText("I don't understand what you are talking about", false);
                                    break;
                                case 1:
                                    speakText("sorry, but I can't make sense",false);
                                case 2:
                                    speakText("您在说什么批话呢", true);
                                    break;
                                case 3:
                                    speakText("所以呢，您想表达个几把呢", true);
                                    break;
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
                Toast.makeText(v.getContext(), "请说出要进行的操作", Toast.LENGTH_SHORT).show();
            }
        });
    }
}