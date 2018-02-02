package com.starrtc.staravdemo.demo.listener;

import org.json.JSONObject;

import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.starrtcsdk.im.listener.IStarLiveListener;

/**
 * Created by zhangjt on 2017/8/21.
 */

public class DemoLiveListener implements IStarLiveListener {
    private String TEXTTAG= "DemoLiveListener";

    @Override
    public void initComplete(boolean success, String data) {
        MLOC.d(TEXTTAG,"initComplete:"+success+"|"+data.toString());
        AEvent.notifyListener(AEvent.AEVENT_LIVE_INIT_COMPLETE,success,data);
    }

    @Override
    public void onJoinOK(String channelId) {
        MLOC.d(TEXTTAG,"onJoinOK:"+channelId);
        AEvent.notifyListener(AEvent.AEVENT_LIVE_JOIN_OK,true,channelId);
    }

    @Override
    public void onAddUploader(JSONObject data) {
        MLOC.d(TEXTTAG,"onAddUploader:"+data.toString());
        AEvent.notifyListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,true,data);
    }

    @Override
    public void onRemoveUploader(JSONObject data) {
        MLOC.d(TEXTTAG,"onRemoveUploader:"+data.toString());
        AEvent.notifyListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER,true,data);
    }

    @Override
    public void onResizeAllVideo(byte[] config) {
        MLOC.d(TEXTTAG,"onResizeAllVideo:"+config);
        AEvent.notifyListener(AEvent.AEVENT_LIVE_RESIZE_ALL_VIDEO,true,config);
    }

    @Override
    public void onError(String err) {
        MLOC.d(TEXTTAG,"onError:"+err);
        AEvent.notifyListener(AEvent.AEVENT_LIVE_ERROR,true,err);
    }

    @Override
    public void onStopOK() {
        MLOC.d(TEXTTAG,"onStopOK");
        AEvent.notifyListener(AEvent.AEVENT_LIVE_STOP_OK,true,null);
    }
}
