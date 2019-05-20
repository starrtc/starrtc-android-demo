package com.starrtc.demo.demo.thirdstream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
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
import org.json.JSONObject;

import java.util.ArrayList;

public class RtspTestListActivity extends BaseActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ListView vList;
    private MyListAdapter myListAdapter;
    private ArrayList<RtspInfo> mDatas;
    private LayoutInflater mInflater;
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtsp_test_list);
        ((TextView)findViewById(R.id.title_text)).setText("第三方流列表");
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
                startActivity(new Intent(RtspTestListActivity.this,RtspTestActivity.class));
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
        AEvent.addListener(AEvent.AEVENT_RTSP_GOT_LIST,this);
        AEvent.addListener(AEvent.AEVENT_RTSP_FORWARD,this);
        AEvent.addListener(AEvent.AEVENT_RTSP_STOP,this);
        AEvent.addListener(AEvent.AEVENT_RTSP_RESUME,this);
        AEvent.addListener(AEvent.AEVENT_RTSP_DELETE,this);
        if(MLOC.SERVER_TYPE.equals(MLOC.SERVER_TYPE_PUBLIC)){
            InterfaceUrls.demoRequestPushList();
        }else{
            queryAllList();
        }
    }
    @Override
    public void onPause(){
        AEvent.removeListener(AEvent.AEVENT_RTSP_GOT_LIST,this);
        AEvent.removeListener(AEvent.AEVENT_RTSP_FORWARD,this);
        AEvent.removeListener(AEvent.AEVENT_RTSP_STOP,this);
        AEvent.removeListener(AEvent.AEVENT_RTSP_RESUME,this);
        AEvent.removeListener(AEvent.AEVENT_RTSP_DELETE,this);
        super.onPause();
    }
    private void queryAllList(){
        XHClient.getInstance().getChatroomManager().queryList(MLOC.userId,MLOC.CHATROOM_LIST_TYPE_PUSH_ALL,new IXHResultCallback() {
            @Override
            public void success(final Object data) {
                refreshLayout.setRefreshing(false);
                mDatas.clear();
                try {
                    JSONArray array = (JSONArray) data;
                    for(int i = array.length()-1;i>=0;i--){
                        RtspInfo info = new RtspInfo();
                        JSONObject obj = array.getJSONObject(i);
                        info.creator = obj.getString("creator");
                        info.id = obj.getString("id");
                        info.name = obj.getString("name");
                        info.rtsp = obj.getString("rtsp");
                        if(obj.has("type")){
                            info.type = obj.getInt("type");
                        }else{
                            info.type = MLOC.CHATROOM_LIST_TYPE_CHATROOM;
                        }
                        mDatas.add(info);
                    }
                    myListAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(String errMsg) {
                MLOC.d("VideoMettingListActivity",errMsg);
                refreshLayout.setRefreshing(false);
                mDatas.clear();
                myListAdapter.notifyDataSetChanged();
            }
        });

    }
    @Override
    public void dispatchEvent(String aEventID, final boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        switch (aEventID){
            case AEvent.AEVENT_RTSP_GOT_LIST:
                refreshLayout.setRefreshing(false);
                mDatas.clear();
                if(success){
                    ArrayList<RtspInfo> res = (ArrayList<RtspInfo>) eventObj;
                    if(res==null) return;
                    for(int i = 0;i<res.size();i++){
                        if(res.get(i).isLiveOn.equals("1")){
                            mDatas.add(res.get(i));
                        }
                    }
                    for(int i = 0;i<res.size();i++){
                        if(res.get(i).isLiveOn.equals("0")){
                            mDatas.add(res.get(i));
                        }
                    }
                    myListAdapter.notifyDataSetChanged();
                }
                break;
            case AEvent.AEVENT_RTSP_FORWARD:
            case AEvent.AEVENT_RTSP_STOP:
            case AEvent.AEVENT_RTSP_RESUME:
            case AEvent.AEVENT_RTSP_DELETE:
                if(success){
                    MLOC.showMsg(RtspTestListActivity.this,"操作成功");
                }else{
                    MLOC.showMsg(RtspTestListActivity.this,"操作失败："+eventObj);
                }
                if(MLOC.SERVER_TYPE.equals(MLOC.SERVER_TYPE_PUBLIC)){
                    InterfaceUrls.demoRequestPushList();
                }else{
                    queryAllList();
                }

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final RtspInfo rtspInfo = mDatas.get(position);
        String[] arr = new String[]{"停止拉流","恢复拉流","删除记录"};

        AlertDialog.Builder builder=new AlertDialog.Builder(RtspTestListActivity.this);
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    //停止
                    if(MLOC.SERVER_TYPE.equals(MLOC.SERVER_TYPE_CUSTOM)){
                        InterfaceUrls.demoStopPushRtsp(MLOC.LIVE_PROXY_SERVER_URL,rtspInfo.id);
                    }else{
                        InterfaceUrls.demoStopPushRtsp(MLOC.LIVE_PROXY_SCHEDULE_URL,rtspInfo.id);
                    }

                }else if(i==1){
                    //恢复
                    if(!TextUtils.isEmpty(rtspInfo.rtsp)){
                        if(MLOC.SERVER_TYPE.equals(MLOC.SERVER_TYPE_CUSTOM)){
                            InterfaceUrls.demoResumePushRtsp(MLOC.LIVE_PROXY_SERVER_URL,rtspInfo.id,rtspInfo.rtsp);
                        }else{
                            InterfaceUrls.demoResumePushRtsp(MLOC.LIVE_PROXY_SCHEDULE_URL,rtspInfo.id,rtspInfo.rtsp);
                        }
                    }
                }else{
                    //删除
                    if(MLOC.SERVER_TYPE.equals(MLOC.SERVER_TYPE_CUSTOM)){
                        InterfaceUrls.demoDeleteRtsp(MLOC.LIVE_PROXY_SERVER_URL,rtspInfo.id);
                    }else{
                        InterfaceUrls.demoDeleteRtsp(MLOC.LIVE_PROXY_SCHEDULE_URL,rtspInfo.id);
                    }
                    if(MLOC.SERVER_TYPE.equals(MLOC.SERVER_TYPE_CUSTOM)){
                        XHClient.getInstance().getChatroomManager().deleteFromList(MLOC.userId,rtspInfo.type, rtspInfo.id.substring(16), new IXHResultCallback() {
                            @Override
                            public void success(Object data) {
                                queryAllList();
                            }

                            @Override
                            public void failed(String errMsg) {

                            }
                        });
                    }
                }
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    @Override
    public void onRefresh() {
        if(MLOC.SERVER_TYPE.equals(MLOC.SERVER_TYPE_PUBLIC)){
            InterfaceUrls.demoRequestPushList();
        }else{
            queryAllList();
        }
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
                convertView = mInflater.inflate(R.layout.item_all_list,null);
                viewIconImg.vRoomName = (TextView)convertView.findViewById(R.id.item_id);
                viewIconImg.vCreaterId = (TextView)convertView.findViewById(R.id.item_creater_id);
                viewIconImg.vLiveState = (TextView)convertView.findViewById(R.id.live_flag);
                viewIconImg.vHeadBg =  convertView.findViewById(R.id.head_bg);
                viewIconImg.vHeadImage = (ImageView) convertView.findViewById(R.id.head_img);
                viewIconImg.vHeadCover = (CircularCoverView) convertView.findViewById(R.id.head_cover);
                convertView.setTag(viewIconImg);
            }else{
                viewIconImg = (ViewHolder)convertView.getTag();
            }
            viewIconImg.vRoomName.setText(mDatas.get(position).name);
            viewIconImg.vCreaterId.setText(mDatas.get(position).creator);
            viewIconImg.vHeadBg.setBackgroundColor(ColorUtils.getColor(RtspTestListActivity.this,mDatas.get(position).name));
            viewIconImg.vHeadCover.setCoverColor(Color.parseColor("#FFFFFF"));
            if((!TextUtils.isEmpty(mDatas.get(position).isLiveOn))&&mDatas.get(position).isLiveOn.equals("1")){
                viewIconImg.vLiveState.setVisibility(View.VISIBLE);
            }else{
                viewIconImg.vLiveState.setVisibility(View.INVISIBLE);
            }
            int cint = DensityUtils.dip2px(RtspTestListActivity.this,28);
            viewIconImg.vHeadCover.setRadians(cint, cint, cint, cint,0);
            viewIconImg.vHeadImage.setImageResource(R.drawable.icon_hd_live_item);
            return convertView;
        }

        class  ViewHolder{
            private TextView vRoomName;
            private TextView vCreaterId;
            public View vHeadBg;
            public CircularCoverView vHeadCover;
            public ImageView vHeadImage;
            public TextView vLiveState;
        }
    }


}
