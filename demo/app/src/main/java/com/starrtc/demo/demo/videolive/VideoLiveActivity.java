package com.starrtc.demo.demo.videolive;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.starrtc.demo.R;
import com.starrtc.demo.demo.BaseActivity;
import com.starrtc.demo.demo.MLOC;
import com.starrtc.demo.listener.XHLiveManagerListener;
import com.starrtc.demo.serverAPI.InterfaceUrls;
import com.starrtc.demo.ui.CircularCoverView;
import com.starrtc.demo.demo.videomeeting.ViewPosition;
import com.starrtc.demo.utils.AEvent;
import com.starrtc.demo.utils.DensityUtils;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHCustomConfig;
import com.starrtc.starrtcsdk.api.XHLiveItem;
import com.starrtc.starrtcsdk.api.XHLiveManager;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.audio.StarRTCAudioManager;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;
import com.starrtc.starrtcsdk.core.player.StarPlayerScaleType;
import com.starrtc.starrtcsdk.core.player.StarWhitePanel;
import com.starrtc.starrtcsdk.core.player.StarPlayer;
import com.starrtc.starrtcsdk.core.pusher.XHCameraRecorder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VideoLiveActivity extends BaseActivity {

    public static String CREATER_ID         = "CREATER_ID";          //创建者ID
    public static String LIVE_TYPE          = "LIVE_TYPE";           //创建信息
    public static String LIVE_ID            = "LIVE_ID";            //直播ID
    public static String LIVE_NAME          = "LIVE_NAME";          //直播名称

    private TextView vRoomId;
    private ListView vMsgList;
    private View vSendBtn;
    private EditText vEditText;
    private View vMicBtn;
    private View vCameraBtn;
    private View vPanelBtn;
    private View vCleanBtn;

    private List<XHIMMessage> mDatas;
    private MyChatroomListAdapter mAdapter ;

    private RelativeLayout vPlayerView;
    private ArrayList<ViewPosition> mPlayerList;
    private int borderW = 0;
    private int borderH = 0;

    private String mPrivateMsgTargetId;
    private XHLiveManager liveManager;
    private Boolean isUploader = false;
    private String createrId;
    private String liveId;
    private String liveName;
    private XHConstants.XHLiveType liveType;

    private StarWhitePanel vPaintPlayer;

    private Boolean isPortrait = true;

    private StarRTCAudioManager starRTCAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_live);

        starRTCAudioManager = StarRTCAudioManager.create(this);
        starRTCAudioManager.start(new StarRTCAudioManager.AudioManagerEvents() {
            @Override
            public void onAudioDeviceChanged(StarRTCAudioManager.AudioDevice selectedAudioDevice, Set availableAudioDevices) {

            }
        });

        DisplayMetrics dm = getResources().getDisplayMetrics();
        if(dm.heightPixels>dm.widthPixels){
            isPortrait = true;
        }else{
            isPortrait = false;
        }

        createrId = getIntent().getStringExtra(CREATER_ID);
        liveName = getIntent().getStringExtra(LIVE_NAME);
        liveId = getIntent().getStringExtra(LIVE_ID);
        liveType = (XHConstants.XHLiveType) getIntent().getSerializableExtra(LIVE_TYPE);
        if(TextUtils.isEmpty(liveId)){
            if(createrId.equals(MLOC.userId)){
                if(TextUtils.isEmpty(liveName)||liveType==null){
                    MLOC.showMsg(this,"没有直播信息");
                    stopAndFinish();
                    return;
                }
            }else{
                if(TextUtils.isEmpty(liveName)||liveType==null){
                    MLOC.showMsg(this,"没有直播信息");
                    stopAndFinish();
                    return;
                }
            }
        }

        liveManager = XHClient.getInstance().getLiveManager(this);
        liveManager.setRtcMediaType(XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_VIDEO_AND_AUDIO);
        liveManager.setRecorder(new XHCameraRecorder());
        liveManager.addListener(new XHLiveManagerListener());


        addListener();
        vRoomId = (TextView) findViewById(R.id.live_id_text);
        vRoomId.setText("直播编号："+ liveName);

        vPaintPlayer = (StarWhitePanel) findViewById(R.id.painter);
        vPanelBtn = findViewById(R.id.panel_btn);
        vCleanBtn = findViewById(R.id.clean_btn);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        vEditText = (EditText) findViewById(R.id.id_input);
        vEditText.clearFocus();
        mDatas = new ArrayList<>();
        vMsgList = (ListView) findViewById(R.id.msg_list);

        vMsgList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        vMsgList.setStackFromBottom(true);

        mAdapter = new MyChatroomListAdapter();
        vMsgList.setAdapter(mAdapter);
        vMsgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickUserId = mDatas.get(position).fromId;
                String msgText = mDatas.get(position).contentData;
                showManagerDialog(clickUserId,msgText);
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
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(vEditText.getWindowToken(), 0);
            }
        });

        vCameraBtn = findViewById(R.id.switch_camera);
        vMicBtn = findViewById(R.id.mic_btn);
        if(createrId!=null&&createrId.equals(MLOC.userId)){
            vMicBtn.setVisibility(View.GONE);
            vCameraBtn.setVisibility(View.VISIBLE);
            vPanelBtn.setVisibility(View.VISIBLE);
//            vCarBtn.setVisibility(View.VISIBLE);
        }else{
            vMicBtn.setVisibility(View.VISIBLE);
            vCameraBtn.setVisibility(View.GONE);
            vPanelBtn.setVisibility(View.GONE);
//            vCarBtn.setVisibility(View.GONE);
        }
        vMicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isUploader){
                    new AlertDialog.Builder(VideoLiveActivity.this).setCancelable(true)
                            .setTitle("是否结束上麦?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    isUploader = false;
                                    liveManager.changeToAudience(new IXHResultCallback() {
                                        @Override
                                        public void success(Object data) {

                                        }

                                        @Override
                                        public void failed(String errMsg) {

                                        }
                                    });
                                    vMicBtn.setSelected(false);
                                    vCameraBtn.setVisibility(View.GONE);
                                    vPanelBtn.setVisibility(View.GONE);
//                                            vCarBtn.setVisibility(View.GONE);
                                }
                            }
                    ).show();
                }else{
                    new AlertDialog.Builder(VideoLiveActivity.this).setCancelable(true)
                            .setTitle("是否申请上麦?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    liveManager.applyToBroadcaster(createrId);
                                }
                            }
                    ).show();
                }
            }
        });

        findViewById(R.id.switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveManager.switchCamera();
            }
        });

        vPlayerView = (RelativeLayout) findViewById(R.id.view1);
        borderW = DensityUtils.screenWidth(this);
        borderH = borderW/3*4;
        mPlayerList = new ArrayList<>();


        vPanelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vPanelBtn.isSelected()){
                    vPanelBtn.setSelected(false);
                    vCleanBtn.setVisibility(View.INVISIBLE);
                    vPaintPlayer.pause();
                }else{
                    vPanelBtn.setSelected(true);
                    vCleanBtn.setVisibility(View.VISIBLE);
                    vPaintPlayer.publish(liveManager);
                }
            }
        });
        vCleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vPaintPlayer.clean();
            }
        });

        init();
    }

    private void init(){
        if(createrId.equals(MLOC.userId)){
            if(liveId==null){
                createNewLive();
            }else {
               starLive();
            }
        }else{
            joinLive();
        }
    }

    private void createNewLive(){
        //创建新直播
        isUploader = true;
        XHLiveItem liveItem = new XHLiveItem();
        liveItem.setLiveName(liveName);
        liveItem.setLiveType(liveType);
        liveManager.createLive(liveItem, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                liveId = (String) data;
                starLive();
//上报到直播列表
                try {
                    JSONObject info = new JSONObject();
                    info.put("id",liveId);
                    info.put("creator",MLOC.userId);
                    info.put("name",liveName);
                    String infostr = info.toString();
                    infostr = URLEncoder.encode(infostr,"utf-8");
                    if(MLOC.AEventCenterEnable){
                        InterfaceUrls.demoSaveToList(MLOC.userId,MLOC.LIST_TYPE_LIVE,liveId,infostr);
                    }else {
                        liveManager.saveToList(MLOC.userId, MLOC.LIST_TYPE_LIVE, liveId, infostr, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failed(final String errMsg) {
                MLOC.showMsg(VideoLiveActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }

    private void starLive(){
        //开始直播
        isUploader = true;
        liveManager.startLive(liveId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d("XHLiveManager","startLive success "+data);
            }
            @Override
            public void failed(final String errMsg) {
                MLOC.d("XHLiveManager","startLive failed "+errMsg);
                MLOC.showMsg(VideoLiveActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }

    private void joinLive(){
        //观众加入直播
        isUploader = false;
        liveManager.watchLive(liveId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                MLOC.d("XHLiveManager","watchLive success "+data);
            }
            @Override
            public void failed(final String errMsg) {
                MLOC.d("XHLiveManager","watchLive failed "+errMsg);
                MLOC.showMsg(VideoLiveActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }

    private void sendChatMsg(String msg){
        MLOC.d("XHLiveManager","sendChatMsg "+msg);
        if(TextUtils.isEmpty(mPrivateMsgTargetId)){
            XHIMMessage imMessage = liveManager.sendMessage(msg,null);
            mDatas.add(imMessage);
        }else{
            XHIMMessage imMessage = liveManager.sendPrivateMessage(msg,mPrivateMsgTargetId,null);
            mDatas.add(imMessage);
        }
        mAdapter.notifyDataSetChanged();
        mPrivateMsgTargetId = "";

    }


    public void addListener(){
        AEvent.addListener(AEvent.AEVENT_LIVE_ERROR,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_APPLY_LINK,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_APPLY_LINK_RESULT,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_INVITE_LINK,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_INVITE_LINK_RESULT,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_GET_ONLINE_NUMBER,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_SELF_KICKED,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_SELF_BANNED,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_REV_PRIVATE_MSG,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_SELF_COMMANDED_TO_STOP,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_REV_REALTIME_DATA,this);
    }

    @Override
    public void onResume(){
        super.onResume();
        MLOC.canPickupVoip = false;
    }
    @Override
    public void onPause(){
        super.onPause();
        MLOC.canPickupVoip = true;
    }

    @Override
    public void onRestart(){
        super.onRestart();
        addListener();
    }

    @Override
    public void onStop(){
        removeListener();
        super.onStop();
    }

    private void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_LIVE_ERROR,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_APPLY_LINK,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_APPLY_LINK_RESULT,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_INVITE_LINK,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_INVITE_LINK_RESULT,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_GET_ONLINE_NUMBER,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_SELF_KICKED,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_SELF_BANNED,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_REV_PRIVATE_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_SELF_COMMANDED_TO_STOP,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_REV_REALTIME_DATA,this);
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(VideoLiveActivity.this).setCancelable(true)
                .setTitle("是否要退出?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        stop();
                    }
                }
        ).show();
    }

    private void stop(){
        liveManager.leaveLive( new IXHResultCallback() {
            @Override
            public void success(Object data) {
                stopAndFinish();
            }

            @Override
            public void failed(final String errMsg) {
                MLOC.showMsg(VideoLiveActivity.this,errMsg);
                stopAndFinish();
            }
        });
    }

    private void addPlayer(String addUserID){
        ViewPosition newOne = new ViewPosition();
        newOne.setUserId(addUserID);
        StarPlayer player = new StarPlayer(this);
        newOne.setVideoPlayer(player);
        mPlayerList.add(newOne);
        vPlayerView.addView(player);
        CircularCoverView coverView = new CircularCoverView(this);
        coverView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        coverView.setCoverColor(Color.BLACK);
        coverView.setRadians(35, 35, 35, 35,10);
        player.addView(coverView);
        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayout(v);
            }
        });
        resetLayout();
        player.setZOrderMediaOverlay(true);
        player.setScalType(StarPlayerScaleType.DRAW_TYPE_CENTER);

        if(mPlayerList.size()==1){
            liveManager.attachPlayerView(addUserID,player,true);
        }else{
            liveManager.attachPlayerView(addUserID,player,false);
        }
    }

    private boolean isRuning = false;
    private void changeLayout(View v){
        if(isRuning) return;
        if(v == mPlayerList.get(0).getVideoPlayer())return;
        ViewPosition clickPlayer = null;
        int clickIndex = 0;
        for (int i=0;i<mPlayerList.size();i++){
            if(mPlayerList.get(i).getVideoPlayer()==v){
                clickIndex = i;
                clickPlayer = mPlayerList.remove(i);
                liveManager.changeToBig(clickPlayer.getUserId());
                break;
            }
        }
        final ViewPosition mainPlayer = mPlayerList.remove(0);
        liveManager.changeToSmall(mainPlayer.getUserId());
        mPlayerList.remove(clickPlayer);
        mPlayerList.add(0, clickPlayer);
        mPlayerList.add(clickIndex,mainPlayer);

        final ViewPosition finalClickPlayer = clickPlayer;
        startAnimation(finalClickPlayer.getVideoPlayer(),mainPlayer.getVideoPlayer());
    }

    private void startAnimation(final StarPlayer clickPlayer, final StarPlayer mainPlayer){
        final float clickStartW = clickPlayer.getWidth();
        final float clickStartH = clickPlayer.getHeight();
        final float clickEndW = mainPlayer.getWidth();
        final float clickEndH = mainPlayer.getHeight();
        final float mainStartW = mainPlayer.getWidth();
        final float mainStartH = mainPlayer.getHeight();
        final float mainEndW = clickPlayer.getWidth();
        final float mainEndH = clickPlayer.getHeight();

        final float clickStartX = clickPlayer.getX();
        final float clickStartY = clickPlayer.getY();
        final float clickEndX = mainPlayer.getX();
        final float clickEndY = mainPlayer.getY();
        final float mainStartX = mainPlayer.getX();
        final float mainStartY = mainPlayer.getY();
        final float mainEndX = clickPlayer.getX();
        final float mainEndY = clickPlayer.getY();

        if(XHCustomConfig.getInstance(this).getOpenGLESEnable()){
            clickPlayer.setX(clickEndX);
            clickPlayer.setY(clickEndY);
            clickPlayer.getLayoutParams().width = (int) clickEndW;
            clickPlayer.getLayoutParams().height = (int)clickEndH;
            clickPlayer.requestLayout();

            mainPlayer.setX(mainEndX);
            mainPlayer.setY(mainEndY);
            mainPlayer.getLayoutParams().width = (int) mainEndW;
            mainPlayer.getLayoutParams().height = (int) mainEndH;
            mainPlayer.requestLayout();
        }else{

            final ValueAnimator valTotal  = ValueAnimator.ofFloat(0f,1f);
            valTotal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    clickPlayer.setX(clickStartX + (Float) animation.getAnimatedValue()*(clickEndX - clickStartX));
                    clickPlayer.setY(clickStartY + (Float) animation.getAnimatedValue()*(clickEndY - clickStartY));
                    clickPlayer.getLayoutParams().width = (int) (clickStartW + (Float) animation.getAnimatedValue()*(clickEndW - clickStartW));
                    clickPlayer.getLayoutParams().height = (int) (clickStartH + (Float) animation.getAnimatedValue()*(clickEndH - clickStartH));
                    clickPlayer.requestLayout();

                    mainPlayer.setX(mainStartX + (Float) animation.getAnimatedValue()*(mainEndX - mainStartX));
                    mainPlayer.setY(mainStartY + (Float) animation.getAnimatedValue()*(mainEndY - mainStartY));
                    mainPlayer.getLayoutParams().width = (int) (mainStartW + (Float) animation.getAnimatedValue()*(mainEndW - mainStartW));
                    mainPlayer.getLayoutParams().height = (int) (mainStartH + (Float) animation.getAnimatedValue()*(mainEndH - mainStartH));
                    mainPlayer.requestLayout();
                }
            });

            valTotal.setDuration(300);
            valTotal.setInterpolator(new LinearInterpolator());
            valTotal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isRuning = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isRuning = false;
                    clickPlayer.setScalType(StarPlayerScaleType.DRAW_TYPE_CENTER);
                    mainPlayer.setScalType(StarPlayerScaleType.DRAW_TYPE_CENTER);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isRuning = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valTotal.start();
        }
    }


    private void deletePlayer(String removeUserId){
        if(mPlayerList!=null&&mPlayerList.size()>0){
            for(int i = 0;i<mPlayerList.size();i++) {
                ViewPosition temp = mPlayerList.get(i);
                if (temp.getUserId().equals(removeUserId)) {
                    ViewPosition remove = mPlayerList.remove(i);
                    vPlayerView.removeView(remove.getVideoPlayer());
                    resetLayout();
                    liveManager.changeToBig(mPlayerList.get(0).getUserId());
                    break;
                }
            }
        }
    }

    private void resetLayout(){
        if(isPortrait){
            switch (mPlayerList.size()){
                case 1:{
                    StarPlayer player = mPlayerList.get(0).getVideoPlayer();
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW,borderH);
                    player.setLayoutParams(lp);
                    player.setY(0);
                    player.setX(0);
                    break;
                }
                case 2:
                case 3:
                case 4:
                {
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(i==0){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3*2,borderH);
                            player.setLayoutParams(lp);
                            player.setY(0);
                            player.setX(0);
                        }else{
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/3);
                            player.setLayoutParams(lp);
                            player.setY((i-1)*borderH/3);
                            player.setX(borderW/3*2);
                        }
                    }
                    break;
                }
                case 5:
                case 6:
                case 7:{
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(i == 0){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW-borderW/3,borderH-borderH/4);
                            player.setLayoutParams(lp);
                        }else if(i>0&&i<3){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/4);
                            player.setLayoutParams(lp);
                            player.setX(borderW-borderW/3);
                            player.setY((i-1)*borderH/4);
                        }else{
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/4);
                            player.setLayoutParams(lp);
                            player.setX((i-3)*borderW/3);
                            player.setY(borderH-borderH/4);
                        }
                    }
                    break;
                }
            }
        }else{
            switch (mPlayerList.size()){
                case 1:{
                    StarPlayer player = mPlayerList.get(0).getVideoPlayer();
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW,borderH);
                    player.setLayoutParams(lp);
                    player.setY(0);
                    player.setX(0);
                    break;
                }
                case 2:
                case 3:
                case 4:
                {
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(i==0){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4*3,borderH);
                            player.setLayoutParams(lp);
                            player.setY(0);
                            player.setX(0);
                        }else{
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4,borderH/3);
                            player.setLayoutParams(lp);
                            player.setY((i-1)*borderH/3);
                            player.setX(borderW/4*3);
                            player.setScalType(StarPlayerScaleType.DRAW_TYPE_TOTAL_GRAPH);
                        }
                    }
                    break;
                }
                case 5:
                case 6:
                case 7:{
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(i==0){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4*2,borderH);
                            player.setLayoutParams(lp);
                            player.setY(0);
                            player.setX(0);
                        }else if(i>0&&i<3){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4,borderH/3);
                            player.setLayoutParams(lp);
                            player.setY((i-1)*borderH/3);
                            player.setX(borderW/4*2);
                        }else{
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4,borderH/3);
                            player.setLayoutParams(lp);
                            player.setY((i-3)*borderH/3);
                            player.setX(borderW/4*3);
                        }
                    }
                    break;
                }
            }
        }


    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        super.dispatchEvent(aEventID,success,eventObj);
        MLOC.d("XHLiveManager","dispatchEvent  "+aEventID + eventObj);
        switch (aEventID){
            case AEvent.AEVENT_LIVE_ADD_UPLOADER:
                try {
                    JSONObject data = (JSONObject) eventObj;
                    String addId = data.getString("actorID");
                    addPlayer(addId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_LIVE_REMOVE_UPLOADER:
                try {
                    JSONObject data = (JSONObject) eventObj;
                    String removeUserId = data.getString("actorID");
                    deletePlayer(removeUserId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_LIVE_APPLY_LINK:
                new AlertDialog.Builder(VideoLiveActivity.this).setCancelable(true)
                        .setTitle(eventObj+"申请上麦")
                        .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                liveManager.refuseApplyToBroadcaster((String) eventObj);
                            }
                        }).setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                liveManager.agreeApplyToBroadcaster((String) eventObj);
                            }
                        }
                ).show();
                break;
            case AEvent.AEVENT_LIVE_APPLY_LINK_RESULT:
                if(((XHConstants.XHLiveJoinResult)eventObj)== XHConstants.XHLiveJoinResult.XHLiveJoinResult_accept){
                    new AlertDialog.Builder(VideoLiveActivity.this).setCancelable(true)
                            .setTitle("房主同意连麦，是否现在开始上麦？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                }
                            }).setPositiveButton("开始", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    isUploader = true;
                                    liveManager.changeToBroadcaster(new IXHResultCallback() {
                                        @Override
                                        public void success(Object data) {

                                        }

                                        @Override
                                        public void failed(String errMsg) {

                                        }
                                    });
                                    vMicBtn.setSelected(true);
                                    vCameraBtn.setVisibility(View.VISIBLE);
                                    vPanelBtn.setVisibility(View.VISIBLE);
                                }
                            }
                    ).show();
                }
                break;
            case AEvent.AEVENT_LIVE_INVITE_LINK:
                new AlertDialog.Builder(VideoLiveActivity.this).setCancelable(true)
                        .setTitle(eventObj+"邀请您上麦")
                        .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                liveManager.refuseInviteToBroadcaster((String) eventObj);
                            }
                        }).setPositiveButton("同意", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                vMicBtn.setSelected(true);
                                vCameraBtn.setVisibility(View.VISIBLE);
                                isUploader = true;
                                liveManager.agreeInviteToBroadcaster((String) eventObj);
                            }
                        }
                ).show();
                break;
            case AEvent.AEVENT_LIVE_INVITE_LINK_RESULT:
                XHConstants.XHLiveJoinResult result = (XHConstants.XHLiveJoinResult) eventObj;
                switch (result){
                    case XHLiveJoinResult_accept:
                        sendChatMsg("欢迎新的小伙伴上麦！！！");
                        break;
                    case XHLiveJoinResult_refuse:
                        break;
                    case XHLiveJoinResult_outtime:
                        break;
                }
                break;
            case AEvent.AEVENT_LIVE_GET_ONLINE_NUMBER:
//                onLineUserNumber = (int) eventObj;
                break;
            case AEvent.AEVENT_LIVE_SELF_KICKED:
                MLOC.showMsg(VideoLiveActivity.this,"你已被踢出");
                stopAndFinish();
                break;
            case AEvent.AEVENT_LIVE_SELF_BANNED:
                final String banTime = eventObj.toString();
                MLOC.showMsg(VideoLiveActivity.this,"你已被禁言,"+banTime+"秒后自动解除");
                break;
            case AEvent.AEVENT_LIVE_REV_MSG:
                XHIMMessage revMsg = (XHIMMessage) eventObj;
                mDatas.add(revMsg);
                mAdapter.notifyDataSetChanged();
                break;
            case AEvent.AEVENT_LIVE_REV_PRIVATE_MSG:
                XHIMMessage revMsgPrivate = (XHIMMessage) eventObj;
                mDatas.add(revMsgPrivate);
                mAdapter.notifyDataSetChanged();
                break;
            case AEvent.AEVENT_LIVE_ERROR:
                String errStr = (String) eventObj;
                if(errStr.equals("30016")){
                    errStr = "直播关闭";
                }
                MLOC.showMsg(getApplicationContext(),errStr);
                stopAndFinish();
                break;
            case AEvent.AEVENT_LIVE_SELF_COMMANDED_TO_STOP:
                if(isUploader){
                    isUploader = false;
                    vMicBtn.setSelected(false);
                    vCameraBtn.setVisibility(View.GONE);
                    vPanelBtn.setVisibility(View.GONE);
//                            vCarBtn.setVisibility(View.GONE);
                    MLOC.showMsg(VideoLiveActivity.this,"您的表演被叫停");
                }
                break;
            case AEvent.AEVENT_LIVE_REV_REALTIME_DATA:
                if(success){
                    try {
                        JSONObject jsonObject = (JSONObject) eventObj;
                        byte[] tData = (byte[]) jsonObject.get("data");
                        String tUpid = jsonObject.getString("upId");
                        vPaintPlayer.setPaintData(tData,tUpid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
                convertView = mInflater.inflate(R.layout.item_live_msg_list,null);
                holder.vUserId = (TextView) convertView.findViewById(R.id.item_user_id);
                holder.vMsg = (TextView) convertView.findViewById(R.id.item_msg);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            String msgText = mDatas.get(position).contentData;
            holder.vMsg.setText(msgText);
            holder.vUserId.setText(mDatas.get(position).fromId);
            return convertView;
        }
    }

    public class ViewHolder{
        public TextView vUserId;
        public TextView vMsg;
    }


    private void showManagerDialog(final String userId,final String msgText) {
        if(!userId.equals(MLOC.userId)){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            if(createrId.equals(MLOC.userId)){
                Boolean ac = false;
                for(int i = 0 ;i<mPlayerList.size();i++){
                    if(userId.equals(mPlayerList.get(i).getUserId())){
                        ac = true;
                        break;
                    }
                }
                if(ac){
                    final String[] Items={"踢出房间","禁止发言","私信","下麦"};
                    builder.setItems(Items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                kickUser(userId);
                            }else if(i==1){
                                muteUser(userId,60);
                            }else if(i==2){
                                mPrivateMsgTargetId = userId;
                                vEditText.setText("[私"+userId+"]");
                            }else if(i==3){
                                liveManager.commandToAudience(userId);
                            }
                        }
                    });
                }else{
                    final String[] Items={"踢出房间","禁止发言","私信","邀请上麦"};
                    builder.setItems(Items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                kickUser(userId);
                            }else if(i==1){
                                muteUser(userId,60);
                            }else if(i==2){
                                mPrivateMsgTargetId = userId;
                                vEditText.setText("[私"+userId+"]");
                            }else if(i==3){
                                liveManager.inviteToBroadcaster(userId);
                            }
                        }
                    });
                }


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


    private void kickUser(String userId){
        liveManager.kickMember(userId, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                //踢人成功
            }

            @Override
            public void failed(String errMsg) {
                //踢人失败
            }
        });
    }
    private void muteUser(String userId,int times){
        liveManager.muteMember(userId, times, new IXHResultCallback() {
            @Override
            public void success(Object data) {
                //禁言成功
            }

            @Override
            public void failed(String errMsg) {
                //禁言失败
            }
        });
    }

    private void stopAndFinish(){
        if(starRTCAudioManager!=null){
            starRTCAudioManager.stop();
        }
        removeListener();
        finish();
    }
}
