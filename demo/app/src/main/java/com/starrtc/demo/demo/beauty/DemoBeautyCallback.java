package com.starrtc.demo.demo.beauty;

import com.starrtc.demo.demo.MLOC;
import com.starrtc.starrtcsdk.core.beauty.StarBeautyDataCallback;
import com.starrtc.starrtcsdk.core.beauty.StarVideoData;

public class DemoBeautyCallback extends StarBeautyDataCallback {
    @Override
    public void onFrame(StarVideoData videoData){
        MLOC.d("@@@@@@@@@@@@@@@@@@@@@@@@@@","美颜数据已经接到了，不做处理，直接再丢回去"+videoData.getDataLength());
        super.backfillData(videoData);
    }
}
