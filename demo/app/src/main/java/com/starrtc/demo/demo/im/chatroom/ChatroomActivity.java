package com.starrtc.demo.demo.im.chatroom;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.listener.XHChatroomManagerListener;
import com.starrtc.demo.serverAPI.InterfaceUrls;
import com.starrtc.demo.ui.CircularCoverView;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.ColorUtils;
import com.starrtc.demo.utils.DensityUtils;
import com.starrtc.starrtcsdk.api.XHChatroomManager;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatroomActivity extends BaseActivity implements AdapterView.OnItemLongClickListener {
    public static String TYPE = "TYPE";
    public static String CHATROOM_NAME = "CHATROOM_NAME";
    public static String CHATROOM_TYPE = "CHATROOM_TYPE";
    public static String CHATROOM_ID = "CHATROOM_ID";
    public static String CREATER_ID = "CREATER_ID";

    private XHChatroomManager chatroomManager;

    private EditText vEditText;
    private ListView vMsgList;
    private View vSendBtn;

    private String mRoomId;
    private String mRoomName;
    private XHConstants.XHChatroomType createType;
    private String mCreaterId;
    private String mPrivateMsgTargetId;
    private int onLineUserNumber;
    private List<XHIMMessage> mDatas;
    private MyChatroomListAdapter mAdapter ;


    private String type;
    private boolean joinOk = false;
    private HashMap<String,Integer> colors = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_chatroom);

        chatroomManager = XHClient.getInstance().getChatroomManager();
        chatroomManager.addListener(new XHChatroomManagerListener());

        findViewById(R.id.title_left_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.title_left_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addListener();
        type = getIntent().getStringExtra(TYPE);
        if(type.equals(CHATROOM_ID)){
            mRoomId = getIntent().getStringExtra(CHATROOM_ID);
            mRoomName = getIntent().getStringExtra(CHATROOM_NAME);
            mCreaterId = getIntent().getStringExtra(CREATER_ID);
            joinChatroom();
        }else if(type.equals(CHATROOM_NAME)){
            mRoomName = getIntent().getStringExtra(CHATROOM_NAME);
            createType = (XHConstants.XHChatroomType) getIntent().getSerializableExtra(CHATROOM_TYPE);
            mCreaterId = MLOC.userId;
            createChatroom();
        }

        if(mCreaterId.equals(MLOC.userId)){
            findViewById(R.id.title_right_btn).setVisibility(View.VISIBLE);
            ((ImageView)findViewById(R.id.title_right_icon)).setImageResource(R.drawable.icon_main_setting);
            findViewById(R.id.title_right_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(ChatroomActivity.this);
                    builder.setItems(new String[]{"删除本聊天室"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            chatroomManager.deleteChatroom(mRoomId, new IXHResultCallback() {
                                @Override
                                public void success(Object data) {
                                    MLOC.d("IM_CHATROOM","聊天室删除成功! "+data);
                                    if(MLOC.AEventCenterEnable){
                                        InterfaceUrls.demoDeleteFromList(MLOC.userId,MLOC.LIST_TYPE_CHATROOM,mRoomId);
                                    }else{
                                        chatroomManager.deleteFromList(MLOC.userId, MLOC.LIST_TYPE_CHATROOM, mRoomId, null);
                                    }
                                    ChatroomActivity.this.finish();
                                }

                                @Override
                                public void failed(String errMsg) {
                                    MLOC.d("IM_CHATROOM","聊天室删除失败！"+errMsg);
                                }
                            });
                        }
                    });
                    builder.setCancelable(true);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            });
        }

        ((TextView)findViewById(R.id.title_text)).setText(mRoomName);
        vEditText = (EditText) findViewById(R.id.id_input);
        mDatas = new ArrayList<>();
        mAdapter = new MyChatroomListAdapter();

        vMsgList = (ListView) findViewById(R.id.msg_list);
        vMsgList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        vMsgList.setOnItemLongClickListener(this);
//        vMsgList.setStackFromBottom(true);
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
    }

    private void createChatroom(){
        chatroomManager.createChatroom(mRoomName,createType, new IXHResultCallback() {
            @Override
            public void success(final Object data) {
                mRoomId = data.toString();
                try {
                    JSONObject info = new JSONObject();
                    info.put("id",mRoomId);
                    info.put("creator",MLOC.userId);
                    info.put("name",mRoomName);
                    String infostr = info.toString();
                    infostr = URLEncoder.encode(infostr,"utf-8");
                    if(MLOC.AEventCenterEnable){
                        InterfaceUrls.demoSaveToList(MLOC.userId,MLOC.LIST_TYPE_CHATROOM,mRoomId,infostr);
                    }else{
                        chatroomManager.saveToList(MLOC.userId,MLOC.LIST_TYPE_CHATROOM,mRoomId,infostr, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                joinChatroom();
            }

            @Override
            public void failed(String errMsg) {
                final String err = errMsg;
                MLOC.showMsg(ChatroomActivity.this,err.toString());
                finish();
            }
        });
    }
    private void joinChatroom(){
        chatroomManager.joinChatroom(mRoomId, new IXHResultCallback() {
            @Override
            public void success(final Object data) {
                mRoomId = data.toString();
                joinOk = true;
            }

            @Override
            public void failed(String errMsg) {
                final String err = errMsg;
                MLOC.showMsg(ChatroomActivity.this,err.toString());
                finish();
            }
        });
    }

    private void sendChatMsg(String msg){
        if(TextUtils.isEmpty(mPrivateMsgTargetId)){
            XHIMMessage imMessage = chatroomManager.sendMessage(msg,null);
            if(colors.get(imMessage.fromId)==null){
                colors.put(imMessage.fromId,  ColorUtils.randomColor(200,200,200));
            }
            mDatas.add(imMessage);
        }else{
            XHIMMessage imMessage = chatroomManager.sendPrivateMessage(msg,mPrivateMsgTargetId,null);
            if(colors.get(imMessage.fromId)==null){
                colors.put(imMessage.fromId, ColorUtils.randomColor(200,200,200));
            }
            mDatas.add(imMessage);
        }
        mAdapter.notifyDataSetChanged();
        mPrivateMsgTargetId = "";
    }

    public void addListener(){

        AEvent.addListener(AEvent.AEVENT_CHATROOM_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_REV_PRIVATE_MSG,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_GET_ONLINE_NUMBER,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_ERROR,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_SELF_KICKED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_SELF_BANNED,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_STOP_OK,this);
        AEvent.addListener(AEvent.AEVENT_CHATROOM_DELETE_OK,this);
    }

    @Override
    public void onStop(){
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_REV_PRIVATE_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_GET_ONLINE_NUMBER,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_ERROR,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_SELF_KICKED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_SELF_BANNED,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_STOP_OK,this);
        AEvent.removeListener(AEvent.AEVENT_CHATROOM_DELETE_OK,this);

        super.onStop();
    }


    @Override
    public void onRestart(){
        super.onRestart();
        addListener();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        chatroomManager.exitChatroom(mRoomId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
            }
            @Override
            public void failed(String errMsg) {
            }
        });
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        MLOC.d("IM_CHATROOM",aEventID+"||"+eventObj);
        switch (aEventID){
            case AEvent.AEVENT_CHATROOM_REV_MSG:
                XHIMMessage revMsg = (XHIMMessage) eventObj;
                if(colors.get(revMsg.fromId)==null){
                    colors.put(revMsg.fromId, ColorUtils.randomColor(200,200,200));
                }
                mDatas.add(revMsg);
                mAdapter.notifyDataSetChanged();
                break;
            case AEvent.AEVENT_CHATROOM_REV_PRIVATE_MSG:
                XHIMMessage revMsgPrivate = (XHIMMessage) eventObj;
                if(colors.get(revMsgPrivate.fromId)==null){
                    colors.put(revMsgPrivate.fromId, ColorUtils.randomColor(200,200,200));
                }
                mDatas.add(revMsgPrivate);
                mAdapter.notifyDataSetChanged();
                break;
            case AEvent.AEVENT_CHATROOM_GET_ONLINE_NUMBER:
                onLineUserNumber = (int) eventObj;
                ((TextView)findViewById(R.id.title_text)).setText(mRoomName+"("+onLineUserNumber+"人在线)");
                break;
            case AEvent.AEVENT_CHATROOM_ERROR:
                final String err2 = eventObj.toString();
                MLOC.showMsg(ChatroomActivity.this,err2.toString());
                finish();
                break;
            case AEvent.AEVENT_CHATROOM_SELF_KICKED:
                MLOC.showMsg(ChatroomActivity.this,"你已被踢出聊天室");
                ChatroomActivity.this.finish();
                break;
            case AEvent.AEVENT_CHATROOM_SELF_BANNED:
                final String banTime = eventObj.toString();
                MLOC.showMsg(ChatroomActivity.this,"你已被禁言,"+banTime+"秒后自动解除");
                break;
            case AEvent.AEVENT_CHATROOM_STOP_OK:
                ChatroomActivity.this.finish();
                break;
            case AEvent.AEVENT_CHATROOM_DELETE_OK:
                ChatroomActivity.this.finish();
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(mDatas.get(position).contentData);
        Toast.makeText(this,"消息已复制",Toast.LENGTH_LONG).show();;
        return false;
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
                itemSelfHolder.vHeadBg.setBackgroundColor(ColorUtils.getColor(ChatroomActivity.this,mDatas.get(position).fromId));
                itemSelfHolder.vHeadCover.setCoverColor(Color.parseColor("#f6f6f6"));
                int cint = DensityUtils.dip2px(ChatroomActivity.this,20);
                itemSelfHolder.vHeadCover.setRadians(cint, cint, cint, cint,0);
                itemSelfHolder.vHeadImage.setImageResource(MLOC.getHeadImage(ChatroomActivity.this,mDatas.get(position).fromId));
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
                    convertView.setTag(itemOtherHolder);
                }else{
                    itemOtherHolder = (ViewHolder)convertView.getTag();
                }
                itemOtherHolder.vUserId.setText(mDatas.get(position).fromId);
                itemOtherHolder.vMsg.setText(mDatas.get(position).contentData);
                itemOtherHolder.vHeadBg.setBackgroundColor(ColorUtils.getColor(ChatroomActivity.this,mDatas.get(position).fromId));
                itemOtherHolder.vHeadCover.setCoverColor(Color.parseColor("#f6f6f6"));
                int cint = DensityUtils.dip2px(ChatroomActivity.this,20);
                itemOtherHolder.vHeadCover.setRadians(cint, cint, cint, cint,0);
                itemOtherHolder.vHeadImage.setImageResource(MLOC.getHeadImage(ChatroomActivity.this,mDatas.get(position).fromId));
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


    private void showManagerDialog(final String userId) {
        if(!userId.equals(MLOC.userId)){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            if(mCreaterId.equals(MLOC.userId)){
                final String[] Items={"踢出房间","禁止发言","私信"};
                builder.setItems(Items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            chatroomManager.kickMember(mRoomId, userId, new IXHResultCallback() {
                                @Override
                                public void success(Object data) {
                                    MLOC.showMsg(ChatroomActivity.this,"踢人成功");
                                }

                                @Override
                                public void failed(final String errMsg) {
                                    MLOC.showMsg(ChatroomActivity.this,"踢人失败:"+errMsg);
                                    finish();
                                }
                            });
                        }else if(i==1){
                            chatroomManager.muteMember(mRoomId, userId,60, new IXHResultCallback() {
                                @Override
                                public void success(Object data) {
                                    MLOC.showMsg(ChatroomActivity.this,"禁言成功");
                                }

                                @Override
                                public void failed(final String errMsg) {
                                    MLOC.showMsg(ChatroomActivity.this,"禁言失败:"+errMsg);
                                }
                            });
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
