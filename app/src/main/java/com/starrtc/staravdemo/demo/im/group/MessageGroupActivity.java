package com.starrtc.staravdemo.demo.im.group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.demo.serverAPI.InterfaceUrls;
import com.starrtc.staravdemo.demo.ui.CircularCoverView;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.ColorUtils;
import com.starrtc.staravdemo.utils.DensityUtils;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHGroupManager;
import com.starrtc.starrtcsdk.apiInterface.IXHCallback;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

public class MessageGroupActivity extends Activity implements IEventListener {
    public static String TYPE = "TYPE";
    public static String GROUP_NAME = "GROUP_NAME";
    public static String GROUP_ID = "GROUP_ID";
    public static String CREATER_ID = "CREATER_ID";

    private XHGroupManager groupManager;

    private EditText vEditText;
    private ListView vMsgList;
    private View vSendBtn;

    private String mGroupId;
    private String mGroupName;
    private String mCreaterId;
    private List<XHIMMessage> mDatas;
    private MyChatroomListAdapter mAdapter ;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_group);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        groupManager = XHClient.getInstance().getGroupManager();
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.title_right_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_right_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageGroupActivity.this,MessageGroupSettingActivity.class);
                intent.putExtra("groupId",mGroupId);
                intent.putExtra("createrId",mCreaterId);
                startActivity(intent);
            }
        });
        addListener();


        type = getIntent().getStringExtra(TYPE);
        if(type.equals(GROUP_ID)){
            mGroupId = getIntent().getStringExtra(GROUP_ID);
            mGroupName = getIntent().getStringExtra(GROUP_NAME);
            mCreaterId = getIntent().getStringExtra(CREATER_ID);
        }else if(type.equals(GROUP_NAME)){
            mGroupName = getIntent().getStringExtra(GROUP_NAME);
            mCreaterId = MLOC.userId;
            groupManager.createGroup(mGroupName, new IXHCallback() {
                @Override
                public void success(Object data) {
                    mGroupId = (String) data;
                    InterfaceUrls.demoRequestGroupMembers(mGroupId);
                }

                @Override
                public void failed(final String errMsg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MLOC.showMsg(MessageGroupActivity.this,errMsg);
                        }
                    });
                    finish();
                }

            });
        }
        ((TextView)findViewById(R.id.title_text)).setText(mGroupName);
        vEditText = (EditText) findViewById(R.id.id_input);
        mDatas = new ArrayList<>();
        mAdapter = new MyChatroomListAdapter();

        vMsgList = (ListView) findViewById(R.id.msg_list);
        vMsgList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        vMsgList.setStackFromBottom(true);

        mAdapter = new MyChatroomListAdapter();
        vMsgList.setAdapter(mAdapter);

        vSendBtn = findViewById(R.id.send_btn);
        vSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = vEditText.getText().toString();
                if(!TextUtils.isEmpty(txt)){
                    sendMsg(txt);
                    vEditText.setText("");
                }
            }
        });
    }

    private void sendMsg(String msg){
        XHIMMessage imMessage = groupManager.sendMessage(mGroupId, new ArrayList<String>(), msg, new IXHCallback() {
            @Override
            public void success(Object data) {

            }
            @Override
            public void failed(String errMsg) {

            }
        });
        mDatas.add(imMessage);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume(){
        super.onResume();
        InterfaceUrls.demoRequestGroupMembers(mGroupId);
    }

    private void addListener(){
        AEvent.addListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_REV_MSG,this);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        addListener();
    }

    @Override
    public void onStop(){
        AEvent.removeListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_REV_MSG,this);
        super.onStop();
    }


    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        MLOC.d("IM_GROUP",aEventID+"||"+eventObj);
        switch (aEventID){
            case AEvent.AEVENT_GROUP_GOT_MEMBER_LIST:
                if(!success)finish();
                break;
            case AEvent.AEVENT_GROUP_REV_MSG:
                XHIMMessage revMsg = (XHIMMessage) eventObj;
                mDatas.add(revMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }

    public class MyChatroomListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyChatroomListAdapter(){
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            if(mDatas ==null) return 0;
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            if(mDatas ==null)
                return null;
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            if(mDatas ==null)
                return 0;
            return position;
        }

        @Override
        public int getViewTypeCount(){
            return 2;
        }

        @Override
        public int getItemViewType(int position){
            return mDatas.get(position).fromId.equals(MLOC.userId)?0:1;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            int currLayoutType = getItemViewType(position);
            if(currLayoutType == 0){ //自己的信息
                final ViewHolder itemSelfHolder;
                if(convertView == null){
                    itemSelfHolder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.item_chat_msg_list_right,null);
                    itemSelfHolder.vUserId = (TextView) convertView.findViewById(R.id.item_user_id);
                    itemSelfHolder.vMsg = (TextView) convertView.findViewById(R.id.item_msg);
                    itemSelfHolder.vHeadBg = convertView.findViewById(R.id.head_bg);
                    itemSelfHolder.vHeadCover = (CircularCoverView) convertView.findViewById(R.id.head_cover);
                    itemSelfHolder.vHeadImage = (ImageView) convertView.findViewById(R.id.head_img);
                    convertView.setTag(itemSelfHolder);
                }else{
                    itemSelfHolder = (ViewHolder)convertView.getTag();
                }
                itemSelfHolder.vUserId.setText(mDatas.get(position).fromId);
                itemSelfHolder.vMsg.setText(mDatas.get(position).contentData);
                itemSelfHolder.vHeadBg.setBackgroundColor(ColorUtils.getColor(MessageGroupActivity.this,mDatas.get(position).fromId));
                itemSelfHolder.vHeadCover.setCoverColor(Color.parseColor("#f6f6f6"));
                int cint = DensityUtils.dip2px(MessageGroupActivity.this,20);
                itemSelfHolder.vHeadCover.setRadians(cint, cint, cint, cint,0);
                itemSelfHolder.vHeadImage.setImageResource(R.drawable.starfox_50);
            }else if(currLayoutType == 1){//别人的信息
                final ViewHolder itemOtherHolder;
                if(convertView == null){
                    itemOtherHolder = new ViewHolder();
                    convertView = mInflater.inflate(R.layout.item_chat_msg_list_left,null);
                    itemOtherHolder.vUserId = (TextView) convertView.findViewById(R.id.item_user_id);
                    itemOtherHolder.vMsg = (TextView) convertView.findViewById(R.id.item_msg);
                    itemOtherHolder.vHeadBg = convertView.findViewById(R.id.head_bg);
                    itemOtherHolder.vHeadCover = (CircularCoverView) convertView.findViewById(R.id.head_cover);
                    itemOtherHolder.vHeadImage = (ImageView) convertView.findViewById(R.id.head_img);
                    itemOtherHolder.vHeadCover.setCoverColor(Color.parseColor("#f6f6f6"));
                    int cint = DensityUtils.dip2px(MessageGroupActivity.this,20);
                    itemOtherHolder.vHeadCover.setRadians(cint, cint, cint, cint,0);
                    itemOtherHolder.vHeadImage.setImageResource(R.drawable.starfox_50);
                    convertView.setTag(itemOtherHolder);
                }else{
                    itemOtherHolder = (ViewHolder)convertView.getTag();
                }
                itemOtherHolder.vUserId.setText(mDatas.get(position).fromId);
                itemOtherHolder.vMsg.setText(mDatas.get(position).contentData);
                itemOtherHolder.vHeadBg.setBackgroundColor(ColorUtils.getColor(MessageGroupActivity.this,mDatas.get(position).fromId));
            }
            return convertView;
        }
    }

    public class ViewHolder{
        public TextView vUserId;
        public TextView vMsg;
        public View vHeadBg;
        public CircularCoverView vHeadCover;
        public ImageView vHeadImage;
    }

}
