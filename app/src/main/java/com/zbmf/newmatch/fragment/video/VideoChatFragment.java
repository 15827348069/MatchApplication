package com.zbmf.newmatch.fragment.video;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.MessageAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.ChatMessage;
import com.zbmf.newmatch.bean.Video;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.dialog.EditTextDialog;
import com.zbmf.newmatch.fragment.BaseFragment;
import com.zbmf.newmatch.util.ChatMessageComparator;
import com.zbmf.newmatch.util.DateUtil;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.worklibrary.presenter.BasePresenter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class VideoChatFragment extends BaseFragment implements View.OnClickListener {
    Timer timer = null;
    private Video video;
    private String video_id;
    private String lastTime="";
    private int after;
    private ListView psr;
    private MessageAdapter messageAdapter;
    private List<ChatMessage>messages;
    private EditTextDialog dialog;
    public static VideoChatFragment newInstance(Video video) {
        VideoChatFragment fragment = new VideoChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(IntentKey.VIDEO_KEY, video);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            video = (Video) getArguments().getSerializable(IntentKey.VIDEO_KEY);
            video_id = video.getVideoId();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_video_chat;
    }

    @Override
    protected void initView() {
        psr=(ListView)getView(R.id.fragment_chat_list);
        getView(R.id.blog_detail_pinglun).setOnClickListener(this);
        messages=new ArrayList<>();
        messageAdapter = new MessageAdapter(getActivity(), messages,false);
        psr.setAdapter(messageAdapter);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    private void getRoomMsg() {
        WebBase.getVideoMsg(video_id,lastTime,after, new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                ChatMessage chatMessage = JSONParse.getRoomMsg(obj);
                if (chatMessage != null && chatMessage.getMessages() != null&&chatMessage.getMessages().size()!=0) {
                    List<ChatMessage> tempMessages = chatMessage.getMessages();
                    Collections.sort(tempMessages, new ChatMessageComparator());
                    psr.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
                    messages.addAll(tempMessages);
                    messageAdapter.notifyDataSetChanged();
                    psr.setSelection(messageAdapter.getCount());
                }
            }
            @Override
            public void onFailure(String err_msg) {
            }
        });
    }

    private void sendMessage(String message) {
        WebBase.SendVideoMessage(video_id,message, new JSONHandler() {
                    @Override
                    public void onSuccess(JSONObject obj) {
                        ChatMessage chatMessage=JSONParse.getChatMsgObj(obj.optJSONObject("msg"));
                        messages.add(chatMessage);
                        messageAdapter.notifyDataSetChanged();
                        psr.setSelection(messageAdapter.getCount());
                        showToast("发送成功");
                        dialog.dismiss();
                    }
                    @Override
                    public void onFailure(String err_msg) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), err_msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer.cancel();
        timer=null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(timer==null){
            timer=new Timer();
        }
        if(messages!=null){
            messages.clear();
        }else{
            messages=new ArrayList<>();
        }
        TimerTask   task = new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    if(messages.size()>0){
                        after=1;
                        lastTime=messages.get(messages.size()-1).getTime();
                    }else{
                        after=0;
                        lastTime= DateUtil.getTimes() + "";
                    }
                    getRoomMsg();
                });
            }
        };
        timer.schedule(task,200,5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.blog_detail_pinglun:
                if(dialog==null){
                    dialog=EditTextDialog.createDialog(getContext(),R.style.dialog)
                            .setRightButton(getString(R.string.send_button))
                            .setEmailVisibility(View.VISIBLE)
                            .setEditHint(getString(R.string.blog_message))
                            .setSendClick((message, type) -> sendMessage(message));
                }
                dialog.show();
                break;
        }
    }
}
