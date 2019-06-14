package com.example.ihavetodoitfast;

import android.os.Bundle;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class WeatherActivity extends AppCompatActivity {
    //private WebView webView;
    private EditText keyText;
    private Button searchButton;
    private Map<String,String> map=new HashMap<String, String>();
    private ListView listView;
    private ArrayAdapter<String> mAdapter;
    private List<String> list = new ArrayList<>();
    String DayDegree;
    String DayWindSpeed;
    String RiseTime;
    String NightDegree;
    String NightWindSpeed;
    String SinkTime;

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
        setContentView(R.layout.activity_weather);

        map.put("北京","101010100");
        map.put("上海","101020100");
        map.put("杭州","101210101");
        map.put("广州","101280101");
        map.put("武汉","101200101");
        map.put("南京","101190101");
        map.put("深圳","101280601");
        map.put("苏州","101190401");
        map.put("厦门","101230201");
        map.put("合肥","101220101");
        map.put("长沙","101250101");
        map.put("成都","101270101");
        map.put("乌鲁木齐","101041000");
        map.put("拉萨","101140101");
        map.put("呼和浩特","101080101");
        map.put("南宁","101300101");
        map.put("南昌","101240101");
        map.put("重庆","101040100");
        map.put("贵阳","101260101");
        map.put("石家庄","101090101");
        map.put("沈阳","101070101");
        map.put("哈尔滨","101050101");
        map.put("福州","101230101");
        map.put("济南","101120101");
        map.put("昆明","101290101");
        map.put("兰州","101160101");
        map.put("太原","101100101");
        map.put("长春","101060101");
        map.put("郑州","101180101");
        map.put("台北","101340101");
        map.put("海口","101310101");
        map.put("西宁","101150101");
        map.put("西安","101110101");
        map.put("澳门","101330101");
        map.put("香港","101320101");
        map.put("天津","101030100");
        searchButton = findViewById(R.id.bt1);
        keyText = findViewById(R.id.edit);
        /*webView=findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());*/
        listView=findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(mAdapter);
        ImageButton btn_weather=findViewById(R.id.btn_weather);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5cf2b0f1");

        btn_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecognizerDialog dialog = new RecognizerDialog(v.getContext(), null);
                // set language
                dialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                dialog.setParameter(SpeechConstant.ACCENT, "mandarin");
                // receive pure text
                dialog.setParameter(SpeechConstant.RESULT_TYPE, "plain");
                // filter out punctuation
                dialog.setParameter(SpeechConstant.ASR_PTT, "0");
                // set interval
                dialog.setParameter(SpeechConstant.VAD_BOS, "4000");
                dialog.setParameter(SpeechConstant.VAD_EOS, "1000");

                dialog.setListener(new RecognizerDialogListener() {
                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean b) {
                        String text = recognizerResult.getResultString();

                        if (text != ""){
                            if(map.get(text)!=null){
                                keyText.setText(text);
                                speakText("您所选择的城市是"+text,true);
                            }
                            else
                                speakText("抱歉，没有听清城市名",true);
                        }
                    }

                    @Override
                    public void onError(SpeechError speechError) {
                    }
                });
                dialog.show();
                // remove the fucking stupid link
                TextView txt = (TextView) dialog.getWindow().getDecorView().findViewWithTag("textlink");
                txt.setText("");
                // prompt
                Toast.makeText(v.getContext(), "请说出城市名 ", Toast.LENGTH_SHORT).show();
            }

        });



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //webView.loadUrl("http://m.weather.com.cn/mweather/" + map.get(keyText.getText().toString()) + ".shtml");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String text="";

                            Document doc= Jsoup.connect("http://www.weather.com.cn/weather1d/" + map.get(keyText.getText().toString()) + ".shtml").get();
                            Elements elements=doc.getElementsByClass("tem");
                            Element e=elements.first();
                            DayDegree="白天温度："+e.text();
                            text+="白天温度，"+e.text()+"。";
                            e=e.nextElementSibling();
                            DayWindSpeed="白天风速："+e.text();
                            text+="白天风速，"+e.text()+"。";
                            e=e.nextElementSibling();
                            RiseTime=e.text();
                            text+=e.text()+"。";
                            e=elements.get(1);
                            NightDegree="夜晚温度："+e.text();
                            text+="夜晚温度，"+e.text()+"。";
                            e=e.nextElementSibling();
                            NightWindSpeed="夜晚风速："+e.text();
                            text+="夜晚风速，"+e.text()+"。";
                            e=e.nextElementSibling();
                            SinkTime=e.text();
                            text+=e.text()+"。";

                            speakText(text,true);

                            //修改ui
                            WeatherActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    list.clear();
                                    list.add(DayDegree);
                                    list.add(DayWindSpeed);
                                    list.add(RiseTime);
                                    list.add(NightDegree);
                                    list.add(NightWindSpeed);
                                    list.add(SinkTime);

                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (IOException e) {
                            Log.i("mytag", e.toString());
                        }
                    }
                }).start();
            }
        });

    }
}
