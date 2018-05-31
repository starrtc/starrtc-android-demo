package com.starrtc.staravdemo.demo.im.group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.demo.serverAPI.InterfaceUrls;
import com.starrtc.staravdemo.demo.ui.CircularCoverView;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.ColorUtils;
import com.starrtc.staravdemo.utils.DensityUtils;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.staravdemo.utils.StarListUtil;

public class MessageGroupListActivity extends Activity implements IEventListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ListView vList;
    private MyListAdapter myListAdapter;
    private ArrayList<MessageGroupInfo> mDatas;
    private LayoutInflater mInflater;
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_group_list);

        ((TextView)findViewById(R.id.title_text)).setText("群组列表");
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AEvent.addListener(AEvent.AEVENT_GROUP_GOT_LIST,this);
        findViewById(R.id.create_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageGroupListActivity.this,MessageGroupCreateActivity.class));
            }
        });
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        //设置刷新时动画的颜色，可以设置4个
        refreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        refreshLayout.setOnRefreshListener(this);



        mDatas = new ArrayList<>();
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myListAdapter = new MyListAdapter();
        vList = (ListView) findViewById(R.id.list);
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

    }

    @Override
    public void onResume(){
        super.onResume();
        InterfaceUrls.demoRequestGroupList(MLOC.userId);
    }
    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onRestart(){
        super.onRestart();
        AEvent.addListener(AEvent.AEVENT_GROUP_GOT_LIST,this);
    }

    @Override
    public void onStop(){
        AEvent.removeListener(AEvent.AEVENT_GROUP_GOT_LIST,this);
        super.onStop();
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        switch (aEventID){
            case AEvent.AEVENT_GROUP_GOT_LIST:
                refreshLayout.setRefreshing(false);
                mDatas.clear();
                if(success){
                    ArrayList<MessageGroupInfo> res = (ArrayList<MessageGroupInfo>) eventObj;
                    mDatas.addAll(res);
                }
                myListAdapter.notifyDataSetChanged();
                break;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MessageGroupInfo clickInfo = mDatas.get(position);
        Intent intent = new Intent(MessageGroupListActivity.this, MessageGroupActivity.class);
        intent.putExtra(MessageGroupActivity.TYPE,MessageGroupActivity.GROUP_ID);
        intent.putExtra(MessageGroupActivity.GROUP_ID,clickInfo.groupId);
        intent.putExtra(MessageGroupActivity.GROUP_NAME,clickInfo.groupName);
        intent.putExtra(MessageGroupActivity.CREATER_ID,clickInfo.createrId);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        InterfaceUrls.demoRequestGroupList(MLOC.userId);
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
            final MyListAdapter.ViewHolder viewIconImg;
            if(convertView == null){
                viewIconImg = new MyListAdapter.ViewHolder();
                convertView = mInflater.inflate(R.layout.item_all_list,null);
                viewIconImg.vRoomName = (TextView)convertView.findViewById(R.id.item_id);
                viewIconImg.vCreaterId = (TextView)convertView.findViewById(R.id.item_creater_id);
                viewIconImg.vHeadBg =  convertView.findViewById(R.id.head_bg);
                viewIconImg.vHeadImage = (ImageView) convertView.findViewById(R.id.head_img);
                viewIconImg.vHeadCover = (CircularCoverView) convertView.findViewById(R.id.head_cover);
                convertView.setTag(viewIconImg);
            }else{
                viewIconImg = (MyListAdapter.ViewHolder)convertView.getTag();
            }
            viewIconImg.vRoomName.setText(mDatas.get(position).groupName);
            viewIconImg.vCreaterId.setText(mDatas.get(position).createrId);
            viewIconImg.vHeadBg.setBackgroundColor(ColorUtils.getColor(MessageGroupListActivity.this,mDatas.get(position).groupId));
            viewIconImg.vHeadCover.setCoverColor(Color.parseColor("#FFFFFF"));
            int cint = DensityUtils.dip2px(MessageGroupListActivity.this,28);
            viewIconImg.vHeadCover.setRadians(cint, cint, cint, cint,0);
            viewIconImg.vHeadImage.setImageResource(R.drawable.icon_im_group_item);
            return convertView;
        }

        class  ViewHolder{
            private TextView vRoomName;
            private TextView vCreaterId;
            public View vHeadBg;
            public CircularCoverView vHeadCover;
            public ImageView vHeadImage;
        }
    }


}
