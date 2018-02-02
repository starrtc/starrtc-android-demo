package com.starrtc.staravdemo.demo.im.group;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.starrtc.staravdemo.R;
import com.starrtc.staravdemo.demo.MLOC;
import com.starrtc.staravdemo.demo.serverAPI.InterfaceUrls;
import com.starrtc.staravdemo.utils.AEvent;
import com.starrtc.staravdemo.utils.IEventListener;
import com.starrtc.starrtcsdk.StarManager;
import com.starrtc.starrtcsdk.im.message.StarIMMessage;
import com.starrtc.starrtcsdk.im.message.StarIMMessageBuilder;

public class MessageGroupActivity extends Activity implements IEventListener {
    public static String TYPE = "TYPE";
    public static String GROUP_NAME = "GROUP_NAME";
    public static String GROUP_ID = "GROUP_ID";
    public static String CREATER_ID = "CREATER_ID";

    private EditText vEditText;
    private TextView vGroupName;
    private ListView vMsgList;
    private View vSendBtn;

    private String mGroupId;
    private String mGroupName;
    private String mCreaterId;
    private List<StarIMMessage> mDatas;
    private MyChatroomListAdapter mAdapter ;
    private Timer onLineTimer;
    private TimerTask onLineTimerTask;

    private List<Map<String,String>> mMembersDatas;
    private SimpleAdapter simplead;

    private String type;
    private boolean joinOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_group);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        vGroupName = (TextView) findViewById(R.id.group_name);

        type = getIntent().getStringExtra(TYPE);
        if(type.equals(GROUP_ID)){
            mGroupId = getIntent().getStringExtra(GROUP_ID);
            mGroupName = getIntent().getStringExtra(GROUP_NAME);
            mCreaterId = getIntent().getStringExtra(CREATER_ID);
            vGroupName.setText(mGroupName);
            InterfaceUrls.demoRequestGroupMembers(mGroupId);
        }else if(type.equals(GROUP_NAME)){
            mGroupName = getIntent().getStringExtra(GROUP_NAME);
            mCreaterId = MLOC.userId;
            StarManager.getInstance().createGroup(mGroupName);
            vGroupName.setText(mGroupName);
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
                if(!clickUserId.equals(MLOC.userId)){
                    showManagerDialog(clickUserId);
                }
            }
        });

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

        mMembersDatas = new ArrayList<>();
        simplead = new SimpleAdapter(this, mMembersDatas,
                R.layout.item_group_member,new String[]{"userId"},new int[]{R.id.item_id});
        ((ListView)findViewById(R.id.user_list)).setAdapter(simplead);




        if(mCreaterId.equals(MLOC.userId)){
            findViewById(R.id.add_user_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddUserDialog();
                }
            });
            findViewById(R.id.delet_group_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteGroup();
                }
            });
        }else{
            findViewById(R.id.add_user_btn).setVisibility(View.GONE);
            findViewById(R.id.delet_group_btn).setVisibility(View.GONE);
        }

        findViewById(R.id.ignore_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(findViewById(R.id.ignore_btn).isSelected()){
                    findViewById(R.id.ignore_btn).setSelected(false);
                    StarManager.getInstance().groupPushUnIgnore(mGroupId);
                }else{
                    findViewById(R.id.ignore_btn).setSelected(true);
                    StarManager.getInstance().groupPushIgnore(mGroupId);
                }
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InterfaceUrls.demoRequestGroupMembers(mGroupId);
                if(findViewById(R.id.users_view).getVisibility()==View.VISIBLE){
                    findViewById(R.id.users_view).setVisibility(View.GONE);
                }else{
                    findViewById(R.id.users_view).setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void addUserToGroup(String addUserId){
        StarManager.getInstance().addUserToGroup(mGroupId, addUserId);
    }

    private void deleteUserFormGroup(String delUserId){
        StarManager.getInstance().deleteUserFromGroup(mGroupId, delUserId);
    }

    private void deleteGroup(){
        StarManager.getInstance().deleteGroup(mGroupId);
    }

    private void sendMsg(String msg){
        StarIMMessage imMessage = StarManager.getInstance().sendGroupMessage(mGroupId,"",msg);
        mDatas.add(imMessage);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onStart(){
        super.onStart();
        AEvent.addListener(AEvent.AEVENT_GROUP_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_CREATE_SUCCESS,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_CREATE_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_SET_PUSH_MODE_SUCCESS,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_SET_PUSH_MODE_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_ADD_USER_SUCCESS,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_ADD_USER_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_DELETE_USER_SUCCESS,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_DELETE_USER_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_DELETE_SUCCESS,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_DELETE_FAILED,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_SEND_MESSAGE_SUCCESS,this);
        AEvent.addListener(AEvent.AEVENT_GROUP_SEND_MESSAGE_FAILED,this);
    }

    @Override
    public void onStop(){
        AEvent.removeListener(AEvent.AEVENT_GROUP_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_GOT_MEMBER_LIST,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_CREATE_SUCCESS,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_CREATE_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_SET_PUSH_MODE_SUCCESS,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_SET_PUSH_MODE_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_ADD_USER_SUCCESS,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_ADD_USER_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_DELETE_USER_SUCCESS,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_DELETE_USER_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_DELETE_SUCCESS,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_DELETE_FAILED,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_SEND_MESSAGE_SUCCESS,this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_SEND_MESSAGE_FAILED,this);
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
                    StarManager.getInstance().queryRoomOnlineNumber(mGroupId);
                }
            }
        };
        onLineTimer.schedule(onLineTimerTask,1000,10000);
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        MLOC.d("IM_GROUP",aEventID+"||"+eventObj);
        String[] datas;
        switch (aEventID){
            case AEvent.AEVENT_GROUP_REV_MSG:
                StarIMMessage revMsg = (StarIMMessage) eventObj;
                mDatas.add(revMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case AEvent.AEVENT_GROUP_SEND_MESSAGE_SUCCESS:
                break;
            case AEvent.AEVENT_GROUP_SEND_MESSAGE_FAILED:
                break;
            case AEvent.AEVENT_GROUP_GOT_MEMBER_LIST:
                mMembersDatas.clear();
                if(success){
                    List<String> rec = (List) eventObj;
                    for(int i=0;i<rec.size();i++){
                        Map<String,String> user = new HashMap<>();
                        user.put("userId",rec.get(i));
                        mMembersDatas.add(user);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        simplead.notifyDataSetChanged();
                    }
                });
                break;
            case AEvent.AEVENT_GROUP_CREATE_SUCCESS:
                mGroupId = (String) eventObj;
                InterfaceUrls.demoRequestGroupMembers(mGroupId);
                break;
            case AEvent.AEVENT_GROUP_CREATE_FAILED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(MessageGroupActivity.this,(String) eventObj);
                    }
                });
                finish();
                break;
            case AEvent.AEVENT_GROUP_DELETE_SUCCESS:
                MLOC.d("IM_GROUP","deleteGroup:"+success+eventObj);
                finish();
                break;
            case AEvent.AEVENT_GROUP_DELETE_FAILED:
                MLOC.d("IM_GROUP","deleteGroup:"+success+eventObj);
                break;
            case AEvent.AEVENT_GROUP_SET_PUSH_MODE_SUCCESS:
                MLOC.d("IM_GROUP","setPushMode:"+success+eventObj);
                break;
            case AEvent.AEVENT_GROUP_SET_PUSH_MODE_FAILED:
                MLOC.d("IM_GROUP","setPushMode:"+success+eventObj);
                break;
            case AEvent.AEVENT_GROUP_ADD_USER_SUCCESS:
                MLOC.d("IM_GROUP","addUserToGroup:"+success+eventObj);
                InterfaceUrls.demoRequestGroupMembers(mGroupId);
                break;
            case AEvent.AEVENT_GROUP_ADD_USER_FAILED:
                MLOC.d("IM_GROUP","addUserToGroup:"+success+eventObj);
                break;
            case AEvent.AEVENT_GROUP_DELETE_USER_SUCCESS:
                MLOC.d("IM_GROUP","delUserToGroup:"+success+eventObj);
                InterfaceUrls.demoRequestGroupMembers(mGroupId);
                break;
            case AEvent.AEVENT_GROUP_DELETE_USER_FAILED:
                MLOC.d("IM_GROUP","delUserToGroup:"+success+eventObj);
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


    private void showAddUserDialog(){
        final Dialog dialog = new Dialog(this,R.style.dialog_popup);
        dialog.setContentView(R.layout.dialog_group_add_user);
        Window win = dialog.getWindow();
        win.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        win.setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        dialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addUser = ((EditText)dialog.findViewById(R.id.add_user_id)).getText().toString();
                if(TextUtils.isEmpty(addUser)){
                    MLOC.showMsg(MessageGroupActivity.this,"用户ID不能为空");
                }else{
                    addUserToGroup(addUser);
                    dialog.dismiss();
                }
            }
        });
        dialog.findViewById(R.id.del_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addUser = ((EditText)dialog.findViewById(R.id.add_user_id)).getText().toString();
                if(TextUtils.isEmpty(addUser)){
                    MLOC.showMsg(MessageGroupActivity.this,"用户ID不能为空");
                }else{
                    deleteUserFormGroup(addUser);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void showManagerDialog(final String userId) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final String[] Items={"踢出群","取消"};
        builder.setItems(Items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    deleteUserFormGroup(userId);
                }
            }
        });
        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

}
