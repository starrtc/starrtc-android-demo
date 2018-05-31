package com.starrtc.staravdemo.demo.im.c2c;

import android.app.Activity;
import android.content.Context;
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
import com.starrtc.staravdemo.demo.ui.CircularCoverView;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.ColorUtils;
import com.starrtc.staravdemo.utils.DensityUtils;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.starrtcsdk.core.StarRtcCore;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;
import com.starrtc.starrtcsdk.core.utils.StarLog;

public class C2CActivity extends Activity implements IEventListener {

    private EditText vEditText;
    private TextView vTargetId;
    private ListView vMsgList;
    private View vSendBtn;

    private String mTargetId;
    private List<XHIMMessage> mDatas;
    private MyChatroomListAdapter mAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c2c);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addListener();
        vEditText = (EditText) findViewById(R.id.id_input);
        mDatas = new ArrayList<>();

        mTargetId = getIntent().getStringExtra("targetId");
        ((TextView)findViewById(R.id.title_text)).setText(mTargetId);
        mAdapter = new MyChatroomListAdapter();
        vMsgList = (ListView) findViewById(R.id.msg_list);
        vMsgList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
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
        XHIMMessage message = StarRtcCore.getInstance().sendMessage(mTargetId,msg);
        ColorUtils.getColor(this,message.fromId);
        mDatas.add(message);
        mAdapter.notifyDataSetChanged();
    }



    private void addListener(){
        AEvent.addListener(AEvent.AEVENT_C2C_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_C2C_SEND_MESSAGE_SUCCESS,this);
        AEvent.addListener(AEvent.AEVENT_C2C_SEND_MESSAGE_FAILED,this);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        addListener();
    }

    @Override
    public void onStop(){
        AEvent.removeListener(AEvent.AEVENT_C2C_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_C2C_SEND_MESSAGE_SUCCESS,this);
        AEvent.removeListener(AEvent.AEVENT_C2C_SEND_MESSAGE_FAILED,this);
        super.onStop();
    }



    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        StarLog.d("IM_C2C",aEventID+"||"+eventObj);
        switch (aEventID){
            case AEvent.AEVENT_C2C_REV_MSG:
                XHIMMessage revMsg = (XHIMMessage) eventObj;
                if(revMsg.fromId.equals(mTargetId)){
                    ColorUtils.getColor(C2CActivity.this,revMsg.fromId);
                    mDatas.add(revMsg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
                break;
            case AEvent.AEVENT_C2C_SEND_MESSAGE_SUCCESS:
                StarLog.d("IM_C2C","消息序号："+eventObj);
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
                itemSelfHolder.vHeadBg.setBackgroundColor(ColorUtils.getColor(C2CActivity.this,mDatas.get(position).fromId));
                itemSelfHolder.vHeadCover.setCoverColor(Color.parseColor("#f6f6f6"));
                int cint = DensityUtils.dip2px(C2CActivity.this,20);
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
                    int cint = DensityUtils.dip2px(C2CActivity.this,20);
                    itemOtherHolder.vHeadCover.setRadians(cint, cint, cint, cint,0);
                    itemOtherHolder.vHeadImage.setImageResource(R.drawable.starfox_50);
                    convertView.setTag(itemOtherHolder);
                }else{
                    itemOtherHolder = (ViewHolder)convertView.getTag();
                }
                itemOtherHolder.vUserId.setText(mDatas.get(position).fromId);
                itemOtherHolder.vMsg.setText(mDatas.get(position).contentData);
                itemOtherHolder.vHeadBg.setBackgroundColor(ColorUtils.getColor(C2CActivity.this,mDatas.get(position).fromId));
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
