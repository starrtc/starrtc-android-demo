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
import com.starrtc.starrtcsdk.StarManager;
import com.starrtc.starrtcsdk.pusher.StarCropTypeEnum;
import com.starrtc.starrtcsdk.utils.StarLog;

public class VideoSizeSettingActivity extends Activity {

    private ListView vList;
    private ArrayList<HashMap<String, Object>> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_size_setting);
        mData = new ArrayList<>();

        for (StarCropTypeEnum e : StarCropTypeEnum.values()) {
            HashMap<String ,Object> map = new HashMap<String ,Object>();
            String name = "";
            switch (e){
                //标清
                case STAR_VIDEO_CROP_CONFIG_368BW_640BH_SMALL_NONE: 		//0
                    name = "大图：368*640 | 小图：无";
                    break;
                case STAR_VIDEO_CROP_CONFIG_368BW_640BH_80SW_160SH: 	    //2
                    name = "大图：368*640 | 小图：80*160";
                    break;
                case STAR_VIDEO_CROP_CONFIG_368BW_640BH_112SW_160SH: 		//3
                    name = "大图：368*640 | 小图：112*160";
                    break;
                case STAR_VIDEO_CROP_CONFIG_368BW_640BH_160SW_160SH: 	    //4
                    name = "大图：368*640 | 小图：160*160";
                    break;
                case STAR_VIDEO_CROP_CONFIG_368BW_640BH_176SW_320SH: 		//5
                    name = "大图：368*640 | 小图：176*320";
                    break;
                case STAR_VIDEO_CROP_CONFIG_368BW_640BH_240SW_320SH: 		//6
                    name = "大图：368*640 | 小图：240*320";
                    break;
                case STAR_VIDEO_CROP_CONFIG_368BW_640BH_320SW_320SH: 		//7
                    name = "大图：368*640 | 小图：320*320";
                    break;
                //高清
                case STAR_VIDEO_CROP_CONFIG_720BW_1280BH_SMALL_NONE: 	    //1
                    name = "大图：720*1280 | 小图：无";
                    break;
                case STAR_VIDEO_CROP_CONFIG_720BW_1280BH_80SW_160SH: 		//8
                    name = "大图：720*1280 | 小图：80*160";
                    break;
                case STAR_VIDEO_CROP_CONFIG_720BW_1280BH_112SW_160SH: 	    //9
                    name = "大图：720*1280 | 小图：112*160";
                    break;
                case STAR_VIDEO_CROP_CONFIG_720BW_1280BH_160SW_160SH: 	    //10
                    name = "大图：720*1280 | 小图：160*160";
                    break;
                case STAR_VIDEO_CROP_CONFIG_720BW_1280BH_176SW_320SH: 	    //11
                    name = "大图：720*1280 | 小图：176*320";
                    break;
                case STAR_VIDEO_CROP_CONFIG_720BW_1280BH_240SW_320SH: 	    //12
                    name = "大图：720*1280 | 小图：240*320";
                    break;
                case STAR_VIDEO_CROP_CONFIG_720BW_1280BH_320SW_320SH: 	    //13
                    name = "大图：720*1280 | 小图：320*320";
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
                StarManager.keepWatch_videoSize = (String) mData.get(position).get("name");
                StarManager.cropTypeEnum = (StarCropTypeEnum) mData.get(position).get("value");
                StarLog.d("Setting","Setting selected "+ StarManager.cropTypeEnum.toString());
                StarManager.setVideoSizeConfig(StarManager.cropTypeEnum);
                finish();
            }
        });
    }
}
