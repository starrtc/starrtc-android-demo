package com.starrtc.staravdemo.demo.videolive;

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

public class VideoLiveListActivity extends Activity implements IEventListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ListView vList;
    private MyListAdapter myListAdapter;
    private ArrayList<LiveInfo> mDatas;
    private LayoutInflater mInflater;
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_live_list);
        findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VideoLiveListActivity.this,VideoLiveCreateActivity.class));
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
        vList = (ListView) findViewById(R.id.live_list);
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
        InterfaceUrls.demoRequestLiveList();
    }

    @Override
    public void onResume(){
        super.onResume();
        AEvent.addListener(AEvent.AEVENT_LIVE_GOT_LIST,this);
    }
    @Override
    public void onPause(){
        AEvent.removeListener(AEvent.AEVENT_LIVE_GOT_LIST,this);
        super.onPause();
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        switch (aEventID){
            case AEvent.AEVENT_LIVE_GOT_LIST:
                refreshLayout.setRefreshing(false);
                mDatas.clear();
                if(success){
                    ArrayList<LiveInfo> res = (ArrayList<LiveInfo>) eventObj;
                    mDatas.addAll(res);
                    myListAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LiveInfo clickMeetingInfo = mDatas.get(position);
        Intent intent = new Intent(VideoLiveListActivity.this, VideoLiveActivity.class);
        intent.putExtra(VideoLiveActivity.CREATER_ID,clickMeetingInfo.createrId);
        intent.putExtra(VideoLiveActivity.LIVE_ID,clickMeetingInfo.liveId);
        intent.putExtra(VideoLiveActivity.CHANNEL_ID,clickMeetingInfo.channelId);
        intent.putExtra(VideoLiveActivity.CHATROOM_ID,clickMeetingInfo.chatroomId);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        InterfaceUrls.demoRequestLiveList();
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
                convertView = mInflater.inflate(R.layout.item_live_list,null);
                viewIconImg.vLiveId = (TextView)convertView.findViewById(R.id.item_live_id);
                viewIconImg.vCreaterId = (TextView)convertView.findViewById(R.id.item_creater_id);
                convertView.setTag(viewIconImg);
            }else{
                viewIconImg = (ViewHolder)convertView.getTag();
            }
            viewIconImg.vLiveId.setText(mDatas.get(position).liveId);
            viewIconImg.vCreaterId.setText(mDatas.get(position).createrId);
            return convertView;
        }

        class  ViewHolder{
            private TextView vLiveId;
            private TextView vCreaterId;
        }
    }


}
