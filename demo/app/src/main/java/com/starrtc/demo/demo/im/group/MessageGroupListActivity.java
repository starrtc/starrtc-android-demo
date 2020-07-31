package com.starrtc.demo.demo.im.group;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import java.util.List;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.database.CoreDB;
import com.starrtc.demo.database.HistoryBean;
import com.starrtc.demo.serverAPI.InterfaceUrls;
import com.starrtc.demo.ui.CircularCoverView;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.ColorUtils;
import com.starrtc.demo.utils.DensityUtils;
import com.starrtc.demo.utils.StarListUtil;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;

import org.json.JSONArray;
import org.json.JSONException;

public class MessageGroupListActivity extends BaseActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ListView vList;
    private MyListAdapter myListAdapter;
    private ArrayList<HistoryBean> mDatas;
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
        MLOC.hasNewGroupMsg = false;
        AEvent.addListener(AEvent.AEVENT_GROUP_GOT_LIST,this);
        queryGroupList();
    }

    private void queryGroupList(){
        if(MLOC.AEventCenterEnable){
            InterfaceUrls.demoQueryImGroupList(MLOC.userId);
        }else{
            XHClient.getInstance().getGroupManager().queryGroupList(new IXHResultCallback() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void success(final Object data) {
                    MLOC.d("IM_GROUP", "applyGetGroupList success:" + data);
                    try {
                        JSONArray datas = (JSONArray) data;
                        ArrayList<MessageGroupInfo> res = new ArrayList<MessageGroupInfo>();
                        for (int i = 0; i < datas.length(); i++) {
                            MessageGroupInfo groupInfo = new MessageGroupInfo();
                            groupInfo.createrId = datas.getJSONObject(i).getString("creator");
                            groupInfo.groupId = datas.getJSONObject(i).getString("groupId");
                            groupInfo.groupName = datas.getJSONObject(i).getString("groupName");
                            res.add(groupInfo);
                        }
                        AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_LIST, true, res);
                        return;
                    } catch (JSONException e) {
                        AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_LIST, false, "数据解析失败");
                        e.printStackTrace();
                    }
                }
                @Override
                public void failed(String errMsg) {
                    AEvent.notifyListener(AEvent.AEVENT_GROUP_GOT_LIST,false,errMsg);
                    MLOC.d("IM_GROUP","applyGetGroupList failed:"+errMsg);
                }
            });
        }
    }

    @Override
    public void onRestart(){
        super.onRestart();
    }

    @Override
    public void onPause(){
        AEvent.removeListener(AEvent.AEVENT_GROUP_GOT_LIST,this);
        super.onPause();
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        switch (aEventID) {
            case AEvent.AEVENT_GROUP_GOT_LIST:
                mDatas.clear();
                List<HistoryBean> historyList = MLOC.getHistoryList(CoreDB.HISTORY_TYPE_GROUP);
                if (success) {
                    ArrayList<MessageGroupInfo> res = (ArrayList<MessageGroupInfo>) eventObj;
                    //删除已经不再的群
                    for (int i = historyList.size() - 1; i >= 0; i--) {
                        HistoryBean historyBean = historyList.get(i);
                        Boolean needRemove = true;
                        for (int j = 0; j < res.size(); j++) {
                            if (historyBean.getConversationId().equals(res.get(j).groupId)) {
                                historyBean.setGroupName(res.get(j).groupName);
                                historyBean.setGroupCreaterId(res.get(j).createrId);
                                MLOC.updateHistory(historyBean);
                                needRemove = false;
                                break;
                            }
                        }
                        if (needRemove) {
                            MLOC.removeHistory(historyList.remove(i));
                        }
                    }

                    //添加新加的群
                    for (int i = 0; i < res.size(); i++) {
                        MessageGroupInfo groupInfo = res.get(i);
                        boolean needAdd = true;
                        for (int j = 0; j < historyList.size(); j++) {
                            if (groupInfo.groupId.equals(historyList.get(j).getConversationId())) {
                                needAdd = false;
                                break;
                            }
                        }
                        if (needAdd) {
                            HistoryBean historyBean = new HistoryBean();
                            historyBean.setType(CoreDB.HISTORY_TYPE_GROUP);
                            historyBean.setNewMsgCount(0);
                            historyBean.setConversationId(res.get(i).groupId);
                            historyBean.setGroupName(res.get(i).groupName);
                            historyBean.setGroupCreaterId(res.get(i).createrId);
                            historyBean.setLastMsg("");
                            historyBean.setLastTime("");
                            MLOC.addHistory(historyBean, true);
                            historyList.add(historyBean);
                        }
                    }
                    mDatas.addAll(historyList);
                    refreshLayout.setRefreshing(false);
                    myListAdapter.notifyDataSetChanged();
                    return;
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HistoryBean clickInfo = mDatas.get(position);

        MLOC.addHistory(clickInfo,true);

        Intent intent = new Intent(MessageGroupListActivity.this, MessageGroupActivity.class);
        intent.putExtra(MessageGroupActivity.TYPE,MessageGroupActivity.GROUP_ID);
        intent.putExtra(MessageGroupActivity.GROUP_ID,clickInfo.getConversationId());
        intent.putExtra(MessageGroupActivity.GROUP_NAME,clickInfo.getGroupName());
        intent.putExtra(MessageGroupActivity.CREATER_ID,clickInfo.getGroupCreaterId());
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        queryGroupList();
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
                convertView = mInflater.inflate(R.layout.item_group_list,null);
                viewIconImg.vRoomName = (TextView)convertView.findViewById(R.id.item_id);
                viewIconImg.vCreaterId = (TextView)convertView.findViewById(R.id.item_creater_id);
                viewIconImg.vTime = (TextView) convertView.findViewById(R.id.item_time);
                viewIconImg.vCount = (TextView) convertView.findViewById(R.id.item_count);
                viewIconImg.vHeadBg =  convertView.findViewById(R.id.head_bg);
                viewIconImg.vHeadImage = (ImageView) convertView.findViewById(R.id.head_img);
                viewIconImg.vHeadCover = (CircularCoverView) convertView.findViewById(R.id.head_cover);
                convertView.setTag(viewIconImg);
            }else{
                viewIconImg = (MyListAdapter.ViewHolder)convertView.getTag();
            }
            viewIconImg.vRoomName.setText(mDatas.get(position).getGroupName());
            viewIconImg.vCreaterId.setText(mDatas.get(position).getGroupCreaterId());
            viewIconImg.vTime.setText(mDatas.get(position).getLastTime());
            viewIconImg.vCount.setText(""+mDatas.get(position).getNewMsgCount());
            viewIconImg.vHeadBg.setBackgroundColor(ColorUtils.getColor(MessageGroupListActivity.this,mDatas.get(position).getConversationId()));
            viewIconImg.vHeadCover.setCoverColor(Color.parseColor("#FFFFFF"));
            int cint = DensityUtils.dip2px(MessageGroupListActivity.this,28);
            viewIconImg.vHeadCover.setRadians(cint, cint, cint, cint,0);
            viewIconImg.vHeadImage.setImageResource(R.drawable.icon_im_group_item);

            if(mDatas.get(position).getNewMsgCount()==0){
                viewIconImg.vCount.setVisibility(View.INVISIBLE);
            }else{
                viewIconImg.vCount.setText(""+mDatas.get(position).getNewMsgCount());
                viewIconImg.vCount.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        class  ViewHolder{
            private TextView vRoomName;
            public TextView vTime;
            public TextView vCount;
            private TextView vCreaterId;
            public View vHeadBg;
            public CircularCoverView vHeadCover;
            public ImageView vHeadImage;
        }
    }


}
