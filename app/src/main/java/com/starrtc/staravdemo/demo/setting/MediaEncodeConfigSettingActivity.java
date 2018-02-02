package com.starrtc.staravdemo.demo.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.starrtcsdk.StarManager;
import com.starrtc.starrtcsdk.pusher.StarMediaEcodeConfigEnum;
import com.starrtc.starrtcsdk.utils.StarLog;

public class MediaEncodeConfigSettingActivity extends Activity {

    private ListView vList;
    private ArrayList<HashMap<String, Object>> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_encode_config_setting);
        mData = new ArrayList<>();

        for (StarMediaEcodeConfigEnum e : StarMediaEcodeConfigEnum.values()) {
            HashMap<String ,Object> map = new HashMap<String ,Object>();
            String name = "";
            switch (e){
                //标清
                case STAR_VIDEO_AND_AUDIO_CODEC_CONFIG_H264_AAC: 		//0
                    name = "视频 H264 | 音频 AAC";
                    break;
                case STAR_VIDEO_AND_AUDIO_CODEC_CONFIG_H264_OPUS: 	    //2
                    name = "视频 H264 | 音频 OPUS";
                    break;
                case STAR_VIDEO_AND_AUDIO_CODEC_CONFIG_H265_AAC: 		//3
                    name = "视频 H265 | 音频 AAC";
                    break;
                case STAR_VIDEO_AND_AUDIO_CODEC_CONFIG_H265_OPUS: 	    //4
                    name = "视频 H265 | 音频 OPUS";
                    break;
                case STAR_VIDEO_AND_AUDIO_CODEC_CONFIG_VP9_AAC: 		//5
                    name = "视频 VP9 | 音频 AAC";
                    break;
                case STAR_VIDEO_AND_AUDIO_CODEC_CONFIG_VP9_OPUS: 		//6
                    name = "视频 VP9 | 音频 OPUS";
                    break;
            }

            map.put("name",name);
            map.put("value",e);
            mData.add(map);
        }
        vList = (ListView) findViewById(R.id.setting_list);

        SimpleAdapter adapter = new SimpleAdapter(this, (List<? extends Map<String, ?>>) mData,
                R.layout.item_setting_list, new String[] { "name","value"},
                new int[] { R.id.setting_name,R.id.setting_value });
        vList.setAdapter(adapter);
        vList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                StarMediaEcodeConfigEnum selected = (StarMediaEcodeConfigEnum) mData.get(position).get("value");
                StarLog.d("Setting","Setting selected "+ StarManager.keepWatch_mediaEncodeConfig);
                if(StarManager.setMediaEncodeConfig(selected)){
                    StarManager.keepWatch_mediaEncodeConfig = (String) mData.get(position).get("name");
                    StarManager.mediaEncodeConfigEnum = selected;
                }else{
                    MLOC.showMsg("硬编 不支持"+mData.get(position).get("name"));
                }
                finish();
            }
        });
    }
}
