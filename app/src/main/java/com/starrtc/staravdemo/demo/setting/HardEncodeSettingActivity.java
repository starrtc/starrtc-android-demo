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
import com.starrtc.starrtcsdk.hard_codec.StarHardEncoderConfigEnum;
import com.starrtc.starrtcsdk.utils.StarLog;

public class HardEncodeSettingActivity extends Activity {

    private ListView vList;
    private ArrayList<HashMap<String, Object>> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hard_encode_setting);
        mData = new ArrayList<>();


        HashMap<String ,Object> map = new HashMap<String ,Object>();
        map.put("name","小图软编 | 大图软编");
        map.put("value",StarHardEncoderConfigEnum.STAR_HARD_ENCODER_CONFIG_ENUM_ALL_SF);
        mData.add(map);
        HashMap<String ,Object> map1 = new HashMap<String ,Object>();
        map1.put("name","小图硬编 | 大图软编");
        map1.put("value",StarHardEncoderConfigEnum.STAR_HARD_ENCODER_CONFIG_ENUM_SMALL_HW_BIG_SF);
        mData.add(map1);
        HashMap<String ,Object> map2 = new HashMap<String ,Object>();
        map2.put("name","小图软编 | 大图硬编");
        map2.put("value",StarHardEncoderConfigEnum.STAR_HARD_ENCODER_CONFIG_ENUM_SMALL_SF_BIG_HW);
        mData.add(map2);
        HashMap<String ,Object> map3 = new HashMap<String ,Object>();
        map3.put("name","小图硬编 | 大图硬编");
        map3.put("value",StarHardEncoderConfigEnum.STAR_HARD_ENCODER_CONFIG_ENUM_SMALL_HW_BIG_HW);
        mData.add(map3);

        vList = (ListView) findViewById(R.id.setting_list);

        SimpleAdapter adapter = new SimpleAdapter(this, (List<? extends Map<String, ?>>) mData,
                R.layout.item_setting_list, new String[] { "name","value"},
                new int[] { R.id.setting_name,R.id.setting_value });
        vList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        vList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if( StarManager.setHardEncodeConfig((StarHardEncoderConfigEnum) mData.get(position).get("value"))){
                    StarManager.keepWatch_hardEncoderSetting = (String) mData.get(position).get("name");
                    StarManager.hardEncoderConfigEnum = (StarHardEncoderConfigEnum) mData.get(position).get("value");
                    StarLog.d("Setting","Setting selected "+ StarManager.keepWatch_hardEncoderSetting);
                }else{
                    MLOC.showMsg("不支持"+mData.get(position).get("name"));
                }
                finish();
            }
        });
    }
}
