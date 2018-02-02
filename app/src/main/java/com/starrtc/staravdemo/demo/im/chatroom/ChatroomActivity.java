package com.starrtc.staravdemo.demo.im.chatroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.starrtcsdk.StarManager;
import com.starrtc.starrtcsdk.im.message.StarIMMessage;
import com.starrtc.starrtcsdk.im.message.StarIMMessageBuilder;
import com.starrtc.starrtcsdk.utils.StarLog;

public class ChatroomActivity extends Activity implements IEventListener {
    public static String TYPE = "TYPE";
    public static String CHATROOM_NAME = "CHATROOM_NAME";
    public static String CHATROOM_ID = "CHATROOM_ID";
    public static String CREATER_ID = "CREATER_ID";

    private EditText vEditText;
    private TextView vRoomName;
    private TextView vOnlineNum;
    private TextView vMaxNum;
    private ListView vMsgList;
    private View vSendBtn;

    private String mRoomId;
    private String mRoomName;
    private String mCreaterId;
    private String mPrivateMsgTargetId;
    private int maxUserNumber = 100;
    private int maxMessageLength = 0;
    private int onLineUserNumber;
    private List<StarIMMessage> mDatas;
    private MyChatroomListAdapter mAdapter ;
    private Timer onLineTimer;
    private TimerTask onLineTimerTask;

    private String type;
    private boolean joinOk = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        vRoomName = (TextView) findViewById(R.id.chatroom_name);
        vOnlineNum = (TextView) findViewById(R.id.user_number);
        vMaxNum = (TextView) findViewById(R.id.user_max_number);

        type = getIntent().getStringExtra(TYPE);
        if(type.equals(CHATROOM_ID)){
            mRoomId = getIntent().getStringExtra(CHATROOM_ID);
            mRoomName = getIntent().getStringExtra(CHATROOM_NAME);
            mCreaterId = getIntent().getStringExtra(CREATER_ID);
            vRoomName.setText(mRoomName);
            StarManager.getInstance().joinChatroom(mRoomId);
        }else if(type.equals(CHATROOM_NAME)){
            mRoomName = getIntent().getStringExtra(CHATROOM_NAME);
            mCreaterId = MLOC.userId;
            StarManager.getInstance().createChatroom(mRoomName);
            vRoomName.setText(mRoomName);
        }

        if(mCreaterId.equals(MLOC.userId)){
            findViewById(R.id.delete_btn).setVisibility(View.VISIBLE);
            findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StarManager.getInstance().deleteChatRoom();
                }
            });
        }else{
            findViewById(R.id.delete_btn).setVisibility(View.INVISIBLE);
        }

        vEditText = (EditText) findViewById(R.id.id_input);
        mDatas = new ArrayList<>();
        mAdapter = new MyChatroomListAdapter();

        vMsgList = (ListView) findViewById(R.id.msg_list);

        vMsgList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        vMsgList.setStackFromBottom(true);

        mAdapter = new MyChatroomListAdapter();
        vMsgList.setAdapter(mAdapter);
        vMsgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickUserId = mDatas.get(position).fromId;
                showManagerDialog(clickUserId);
            }
        });

        vSendBtn = findViewById(R.id.send_btn);
        vSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = vEditText.getText().toString();
                if(!TextUtils.isEmpty(txt)){
                    sendChatMsg(txt);
                    vEditText.setText("");
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

    private void sendChatMsg(String msg){
        StarIMMessage imMessage = StarIMMessageBuilder.getGhatRoomMessage(MLOC.userId,mRoomId,msg);
        if(TextUtils.isEmpty(mPrivateMsgTargetId)){
            StarManager.getInstance().sendChatroomMessage(imMessage);
        }else{
            StarManager.getInstance().sendChatroomPrivateMessage(mPrivateMsgTargetId,imMessage);
        }
        mDatas.add(imMessage);
        mAdapter.notifyDataSetChanged();
        mPrivateMsgTargetId = "";
    }
    @Override
    public void onStart(){
        super.onStart();
        AEvent.addListener(AEvent.AEVENT_CHATROOM_CREATE_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_CREATE_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_JOIN_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_JOIN_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_REV_PRIVATE_MSG,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_GET_ONLINE_NUMBER,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_ERROR,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_SELF_KICKED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_SELF_BANNED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_KICK_OUT_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_KICK_OUT_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_BAN_USER_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_BAN_USER_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_STOP_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_DELETE_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_SEND_MSG_SUCCESS,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_SEND_MSG_FAILED,this);
    }

    @Override
    public void onStop(){
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_CREATE_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_CREATE_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_JOIN_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_JOIN_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_REV_PRIVATE_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_GET_ONLINE_NUMBER,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_ERROR,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_SELF_KICKED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_SELF_BANNED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_KICK_OUT_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_KICK_OUT_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_BAN_USER_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_BAN_USER_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_STOP_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_DELETE_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_SEND_MSG_SUCCESS,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_SEND_MSG_FAILED,this);
        StarManager.getInstance().exitChatroom();
        super.onStop();
    }

    @Override
    public void onPause(){
        if(onLineTimer!=null){
            onLineTimer.cancel();
            onLineTimerTask.cancel();
            onLineTimer = null;
            onLineTimerTask = null;
        }
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(onLineTimer!=null){
            onLineTimer.cancel();
            onLineTimerTask.cancel();
            onLineTimer = null;
            onLineTimerTask = null;
        }
        onLineTimer = new Timer();
        onLineTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(joinOk){
                    StarManager.getInstance().queryRoomOnlineNumber(mRoomId);
                }
            }
        };
        onLineTimer.schedule(onLineTimerTask,1000,10000);

    }


    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        StarLog.d("IM_CHATROOM",aEventID+"||"+eventObj);
        String[] datas;
        switch (aEventID){
            case AEvent.AEVENT_CHATROOM_CREATE_OK:
            case AEvent.AEVENT_CHATROOM_JOIN_OK:
                datas = eventObj.toString().split(":");
                mRoomId = datas[0];
                maxMessageLength = Integer.parseInt(datas[1]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vMaxNum.setText(""+maxUserNumber);
                    }
                });
                joinOk = true;
                break;
            case AEvent.AEVENT_CHATROOM_CREATE_FAILED:
            case AEvent.AEVENT_CHATROOM_JOIN_FAILED:
                final String err = eventObj.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(ChatroomActivity.this,err.toString());
                    }
                });
                finish();
                break;
            case AEvent.AEVENT_CHATROOM_REV_MSG:
                StarIMMessage revMsg = (StarIMMessage) eventObj;
                mDatas.add(revMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_REV_PRIVATE_MSG:
                StarIMMessage revMsgPrivate = (StarIMMessage) eventObj;
                mDatas.add(revMsgPrivate);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_GET_ONLINE_NUMBER:
                onLineUserNumber = (int) eventObj;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vOnlineNum.setText(""+onLineUserNumber);
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_ERROR:
                final String err2 = eventObj.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(err2.equals("CHATROOM_ERRID_ROOMID_ONLINE_OUTOFLIMIT")){
                            MLOC.showMsg(ChatroomActivity.this,"超出人数上限");
                        }else{
                            MLOC.showMsg(ChatroomActivity.this,err2.toString());
                        }

                    }
                });
                finish();
                break;
            case AEvent.AEVENT_CHATROOM_SELF_KICKED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(ChatroomActivity.this,"你已被踢出聊天室");
                        ChatroomActivity.this.finish();
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_SELF_BANNED:
                final String banTime = eventObj.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(ChatroomActivity.this,"你已被禁言,"+banTime+"秒后自动解除");
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_KICK_OUT_OK:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(ChatroomActivity.this,"踢人成功");
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_KICK_OUT_FAILED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(ChatroomActivity.this,"踢人失败");
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_BAN_USER_OK:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(ChatroomActivity.this,"禁言成功");
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_BAN_USER_FAILED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(ChatroomActivity.this,"禁言失败");
                    }
                });
                break;
            case AEvent.AEVENT_CHATROOM_STOP_OK:
                ChatroomActivity.this.finish();
                break;
            case AEvent.AEVENT_CHATROOM_DELETE_OK:
                ChatroomActivity.this.finish();
                break;
            case AEvent.AEVENT_CHATROOM_SEND_MSG_SUCCESS:
                break;
            case AEvent.AEVENT_CHATROOM_SEND_MSG_FAILED:
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


    private void showManagerDialog(final String userId) {
        if(!userId.equals(MLOC.userId)){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            if(mCreaterId.equals(MLOC.userId)){
                final String[] Items={"踢出房间","禁止发言","私信"};
                builder.setItems(Items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            StarManager.getInstance().kickOutUser(userId);
                        }else if(i==1){
                            StarManager.getInstance().banToSendMessage(userId,60);
                        }else if(i==2){
                            mPrivateMsgTargetId = userId;
                            vEditText.setText("[私"+userId+"]");
                        }
                    }
                });
            }else{
                final String[] Items={"私信"};
                builder.setItems(Items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            mPrivateMsgTargetId = userId;
                            vEditText.setText("[私"+userId+"]");
                        }
                    }
                });
            }


            builder.setCancelable(true);
            AlertDialog dialog=builder.create();
            dialog.show();
        }

    }

}
