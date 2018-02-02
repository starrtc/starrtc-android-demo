package com.starrtc.staravdemo.demo.videomeeting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.serverAPI.InterfaceUrls;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.staravdemo.utils.StarListUtil;

public class VideoMeetingListActivity extends Activity implements IEventListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ListView vList;
    private MyListAdapter myListAdapter;
    private ArrayList<MeetingInfo> mDatas;
    private LayoutInflater mInflater;
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_meeting_list);
        findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VideoMeetingListActivity.this,VideoMeetingCreateActivity.class));
            }
        });
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        //设置刷新时动画的颜色，可以设置4个
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        refreshLayout.setOnRefreshListener(this);



        mDatas = new ArrayList<>();
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myListAdapter = new MyListAdapter();
        vList = (ListView) findViewById(R.id.meeting_list);
        vList.setAdapter(myListAdapter);
        vList.setOnItemClickListener(this);
        vList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i) {
                    case SCROLL_STATE_IDLE:
                        if(StarListUtil.isListViewReachTopEdge(absListView)){
                            refreshLayout.setEnabled(true);
                        }else{
                            refreshLayout.setEnabled(false);
                        }
                        break;
                }
            }
            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });
        InterfaceUrls.demoRequestMeetingList();
    }

    @Override
    public void onStart(){
        super.onStart();
        AEvent.addListener(AEvent.AEVENT_MEETING_GOT_LIST,this);
    }
    @Override
    public void onStop(){
        AEvent.removeListener(AEvent.AEVENT_MEETING_GOT_LIST,this);
        super.onStop();
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        switch (aEventID){
            case AEvent.AEVENT_MEETING_GOT_LIST:
                refreshLayout.setRefreshing(false);
                mDatas.clear();
                if(success){
                    ArrayList<MeetingInfo> res = (ArrayList<MeetingInfo>) eventObj;
                    mDatas.addAll(res);
                    myListAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MeetingInfo clickMeetingInfo = mDatas.get(position);
        Intent intent = new Intent(VideoMeetingListActivity.this, VideoMeetingActivity.class);
        intent.putExtra(VideoMeetingActivity.MEETING_ID,clickMeetingInfo.meetingId);
        intent.putExtra(VideoMeetingActivity.CHANNEL_ID,clickMeetingInfo.channelId);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        InterfaceUrls.demoRequestMeetingList();
    }


    class MyListAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewIconImg;
            if(convertView == null){
                viewIconImg = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_meeting_list,null);
                viewIconImg.vMeetingId = (TextView)convertView.findViewById(R.id.item_meeting_id);
                viewIconImg.vCreaterId = (TextView)convertView.findViewById(R.id.item_creater_id);
                convertView.setTag(viewIconImg);
            }else{
                viewIconImg = (ViewHolder)convertView.getTag();
            }
            viewIconImg.vMeetingId.setText(mDatas.get(position).meetingId);
            viewIconImg.vCreaterId.setText(mDatas.get(position).createrId);
            return convertView;
        }

        class  ViewHolder{
            private TextView vMeetingId;
            private TextView vCreaterId;
        }
    }


}
