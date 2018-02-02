package com.starrtc.staravdemo.demo.im.c2c;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.starrtcsdk.StarManager;
import com.starrtc.starrtcsdk.im.message.StarIMMessage;
import com.starrtc.starrtcsdk.im.message.StarIMMessageBuilder;
import com.starrtc.starrtcsdk.utils.StarLog;

public class C2CActivity extends Activity implements IEventListener {


    private EditText vEditText;
    private TextView vTargetId;
    private ListView vMsgList;
    private View vSendBtn;

    private String mTargetId;

    private List<StarIMMessage> mDatas;
    private MyChatroomListAdapter mAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c2c);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        vTargetId = (TextView) findViewById(R.id.chatroom_name);
        ((TextView) findViewById(R.id.self_id)).setText(MLOC.userId);

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

        findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputId = ((EditText)findViewById(R.id.targetid_input)).getText().toString();
                if(TextUtils.isEmpty(inputId)){
                    MLOC.showMsg(C2CActivity.this,"id不能为空");
                }else{
                    mTargetId = inputId;
                    vTargetId.setText(MLOC.userId+">>"+mTargetId);
                    findViewById(R.id.ready_view).setVisibility(View.GONE);
                    findViewById(R.id.msg_view).setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void sendMsg(String msg){
        StarIMMessage message = StarManager.getInstance().sendMessage(mTargetId,msg);
        mDatas.add(message);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onStart(){
        super.onStart();
        AEvent.addListener(AEvent.AEVENT_C2C_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_C2C_SEND_MESSAGE_SUCCESS,this);
        AEvent.addListener(AEvent.AEVENT_C2C_SEND_MESSAGE_FAILED,this);
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
                StarIMMessage revMsg = (StarIMMessage) eventObj;
                if(revMsg.fromId.equals(mTargetId)){
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_chatroom_msg_list,null);
                holder.vUserName = (TextView) convertView.findViewById(R.id.item_user_name);
                holder.vUserId = (TextView) convertView.findViewById(R.id.item_user_id);
                holder.vMsg = (TextView) convertView.findViewById(R.id.item_msg);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }

            holder.vUserId.setText(mDatas.get(position).fromId);
            holder.vMsg.setText(mDatas.get(position).contentData);

            return convertView;
        }
    }

    public class ViewHolder{
        public TextView vUserName;
        public TextView vUserId;
        public TextView vMsg;
    }



}
