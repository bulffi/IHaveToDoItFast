package com.example.ihavetodoitfast;

import android.os.Bundle;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;

class MySynthesizerListener implements SynthesizerListener {
    @Override
    public void onSpeakBegin() {
        //showTip(" 开始播放 ");
    }

    @Override
    public void onSpeakPaused() {
        //showTip(" 暂停播放 ");
    }

    @Override
    public void onSpeakResumed() {
        //showTip(" 继续播放 ");
    }

    @Override
    public void onBufferProgress(int percent, int beginPos, int endPos ,
                                 String info) {
        // 合成进度
    }

    @Override
    public void onSpeakProgress(int percent, int beginPos, int endPos) {
        // 播放进度
    }

    @Override
    public void onCompleted(SpeechError error) {
        if (error == null) {
            //showTip("播放完成 ");
        } else if (error != null ) {
            //showTip(error.getPlainDescription( true));
        }
    }

    @Override
    public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
        // 以下代码用于获取与云端的会话 id，当业务出错时将会话 id提供给技术支持人员，可用于查询会话日志，定位出错原因
        // 若使用本地能力，会话 id为null
        //if (SpeechEvent.EVENT_SESSION_ID == eventType) {
        //     String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
        //     Log.d(TAG, "session id =" + sid);
        //}
    }
}
