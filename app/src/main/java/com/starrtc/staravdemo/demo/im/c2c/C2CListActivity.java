package com.starrtc.staravdemo.demo.im.c2c;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.demo.ui.CircularCoverView;
import com.starrtc.staravdemo.utils.ColorUtils;
import com.starrtc.staravdemo.utils.DensityUtils;
import com.starrtc.staravdemo.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class C2CListActivity extends Activity {
    private String mTargetId;
    private List<Map<String, String>> mHistoryList;
    private ListView vHistoryList;
    private MyListAdapter listAdapter;

    private C2CActivity.MyChatroomListAdapter mAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c2c_list);
        //StatusBarUtils.with(this).setColor(Color.parseColor("#FF6C00")).init();
        ((TextView)findViewById(R.id.title_text)).setText("一对一会话列表");
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(C2CListActivity.this,C2CCreateActivity.class));
            }
        });

        mHistoryList = new ArrayList<>();
        listAdapter = new MyListAdapter();
        vHistoryList = (ListView) findViewById(R.id.history_list);
        vHistoryList.setAdapter(listAdapter);
        vHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTargetId = (String) mHistoryList.get(position).get("id");
                MLOC.saveC2CUserId(C2CListActivity.this,mTargetId);
                Intent intent = new Intent(C2CListActivity.this,C2CActivity.class);
                intent.putExtra("targetId",mTargetId);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        mHistoryList.clear();
        String history = MLOC.loadSharedData(this,"c2cHistory");
        if(history.length()>0){
            String[] arr = history.split(",,");
            for (int i = 0;i<arr.length;i++){
                Map<String,String> map = new HashMap<String , String >();
                map.put("id",arr[i]);
                mHistoryList.add(map);
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    public class MyListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyListAdapter(){
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if(mHistoryList!=null)
                return mHistoryList.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if(mHistoryList ==null)
                return null;
            return mHistoryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            if(mHistoryList ==null)
                return 0;
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder itemSelfHolder;
            if(convertView == null){
                itemSelfHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_history,null);
                itemSelfHolder.vUserId = (TextView) convertView.findViewById(R.id.item_id);
                itemSelfHolder.vHeadBg =  convertView.findViewById(R.id.head_bg);
                itemSelfHolder.vHeadImage = (ImageView) convertView.findViewById(R.id.head_img);
                itemSelfHolder.vHeadCover = (CircularCoverView) convertView.findViewById(R.id.head_cover);
                convertView.setTag(itemSelfHolder);
            }else{
                itemSelfHolder = (ViewHolder)convertView.getTag();
            }
            String userId = mHistoryList.get(position).get("id");
            itemSelfHolder.vUserId.setText(userId);
            itemSelfHolder.vHeadBg.setBackgroundColor(ColorUtils.getColor(C2CListActivity.this,userId));
            itemSelfHolder.vHeadCover.setCoverColor(Color.parseColor("#FFFFFF"));
            int cint = DensityUtils.dip2px(C2CListActivity.this,28);
            itemSelfHolder.vHeadCover.setRadians(cint, cint, cint, cint,0);
            itemSelfHolder.vHeadImage.setImageResource(R.drawable.icon_im_c2c);
            return convertView;
        }
    }

    public class ViewHolder{
        public TextView vUserId;
        public View vHeadBg;
        public CircularCoverView vHeadCover;
        public ImageView vHeadImage;
    }

}
