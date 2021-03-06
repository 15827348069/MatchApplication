package com.zbmf.newmatch.fragment.chat;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.BindPhoneActivity;
import com.zbmf.newmatch.activity.Chat1Activity;
import com.zbmf.newmatch.adapter.MessageAdapter;
import com.zbmf.newmatch.api.ChatHandler;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.ChatCatalog;
import com.zbmf.newmatch.bean.ChatMessage;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.common.RequestCode;
import com.zbmf.newmatch.db.Database;
import com.zbmf.newmatch.dialog.EditTextDialog;
import com.zbmf.newmatch.fragment.BaseFragment;
import com.zbmf.newmatch.util.ChatMessageComparator;
import com.zbmf.newmatch.util.DateUtil;
import com.zbmf.newmatch.util.EditTextUtil;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MD5Util;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MessageType;
import com.zbmf.newmatch.util.SettingDefaultsManager;
import com.zbmf.newmatch.view.TextDialog;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

import static android.view.View.VISIBLE;
import static com.zbmf.newmatch.util.ShowActivity.showActivityForResult;


public class FansFragment extends BaseFragment implements View.OnClickListener/*, AdapterView.OnItemClickListener */ {
    private static final String ARG_PARAM1 = "group";
    @BindView(R.id.tv_return)
    TextView tv_return;
    @BindView(R.id.ed_msg)
    TextView ed_msg;
    @BindView(R.id.send_layout)
    LinearLayout send_layout;
    @BindView(R.id.iv_emoji)
    ImageView iv_emoji;
    @BindView(R.id.ll_send)
    LinearLayout ll_send;
    @BindView(R.id.listview)
    PullToRefreshListView psr;
    @BindView(R.id.tv_unread)
    TextView tv_unread;
    @BindView(R.id.tv_reply)
    TextView tv_reply;
    @BindView(R.id.iv_close)
    ImageView iv_close;
    @BindView(R.id.ll_tip)
    LinearLayout ll_tip;

    private ListView listview;
    private List<ChatMessage> messages = new ArrayList<>();

    private MessageAdapter messageAdapter;
    private ChatMessage mMessage = null;
    private ClipboardManager mClipboardManager;
    private UpdateReceiver receiver;

    private boolean is_bottom;
    private Database db;
    private String groupId;
    private Chat1Activity chat_activity;
    private int role;

    public FansFragment() {
    }

    public void livenotifyDataSetChanged() {
        if (messageAdapter != null) {
            messageAdapter.notifyDataSetChanged();
            if (is_bottom) {
                listview.smoothScrollToPosition(messageAdapter.getCount());
            }
        }
    }

    public static FansFragment newInstance(Group group) {
        FansFragment fragment = new FansFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Group group = (Group) getArguments().getSerializable(ARG_PARAM1);
            groupId = group.getId();
            role = group.getRoles();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void initView() {
        listview = psr.getRefreshableView();

        iv_close.setOnClickListener(this);
        tv_unread.setOnClickListener(this);
        tv_reply.setOnClickListener(this);
        send_layout.setOnClickListener(this);
        iv_emoji.setOnClickListener(this);

        messageAdapter = new MessageAdapter(getActivity(), messages, true);
        messageAdapter.setListener((v, pos) -> {
            mMessage = messages.get(pos);
            switch (v.getId()) {
                case R.id.tv_item1:
                    if (mClipboardManager == null)
                        mClipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    mClipboardManager.setText(mMessage.getContent());
                    break;
                case R.id.tv_item2:
                    partStr = "回复" + mMessage.getNickname() + "：" + mMessage.getContent() + "\n";
                    SpannableString rebackStr = EditTextUtil.getContent(getActivity(), tv_return, partStr);
                    tv_return.setVisibility(View.VISIBLE);
                    tv_return.setText(rebackStr);
                    ed_msg.requestFocus();
//                        imm.showSoftInput(ed_msg,InputMethodManager.SHOW_IMPLICIT);
                    break;
                case R.id.tv_item3:
                    complaint(mMessage.getMsg_id());
                    break;

                case R.id.iv_error:
                    sendMessage(mMessage);
                    break;
            }
        });

        psr.setOnRefreshListener(refreshView -> {
            if (messages.size() > 0)
                lastTime = messages.get(0).getTime();
            getRoomMsg();
        });

        listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);//设置列表始终可以滑动
        listview.setAdapter(messageAdapter);
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    View firstVisibleItemView = listview.getChildAt(0);
                    if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                        int no_read = totalItemCount - firstVisibleItem - visibleItemCount;
                        if (no_read > 0) {
                            is_bottom = false;
                        } else {
                            is_bottom = true;
                        }
                    }
                } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    View lastVisibleItemView = listview.getChildAt(listview.getChildCount() - 1);
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == listview.getHeight()) {
                        is_bottom = true;
                        new_live_message = 0;
                        tv_unread.setVisibility(View.INVISIBLE);
                        chat_activity.setLiveNumbeGone(1);
                    }
                } else {
                    is_bottom = false;
                    //向上滑动隐藏按钮
                    if (new_live_message != 0 && firstVisibleItem + visibleItemCount > new_live_message) {
                        int no_read = totalItemCount - firstVisibleItem - visibleItemCount;
                        new_live_message = firstVisibleItem + visibleItemCount;
                        if (no_read == 0) {
                            is_bottom = true;
                            tv_unread.setVisibility(View.GONE);
                            return;
                        }
                        tv_unread.setText(String.valueOf(no_read));
                        tv_unread.setVisibility(View.VISIBLE);
                        chat_activity.setlive_number_text(1, no_read);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
    }

    @Override
    protected void initData() {
        chat_activity = (Chat1Activity) getActivity();
        db = new Database(getActivity());
        getRoomMsg();
        fragmentregisterReceiver();
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void fragmentregisterReceiver() {
        receiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constans.FANS_CHAT_MSG);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (db != null) {
            db.updateUnReadNum(groupId, 0);
        }
    }

    private int new_live_message = 10;
    private String lastTime = DateUtil.getTimes() + "";

    private void getRoomMsg() {
        WebBase.getFansMsg(groupId, lastTime, new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                psr.onRefreshComplete();
                ChatMessage chatMessage = JSONParse.getRoomMsg(obj);
                if (chatMessage != null && chatMessage.getMessages() != null) {
                    List<ChatMessage> tempMessages = chatMessage.getMessages();
                    Collections.sort(tempMessages, new ChatMessageComparator());
                    for (int i = 0; i < tempMessages.size(); i++) {
                        ChatMessage message = tempMessages.get(i);
                        db.addChat(groupId, message);
                    }
                    listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
                    messages.addAll(0, tempMessages);
                    messageAdapter.notifyDataSetChanged();
                    listview.setSelection(tempMessages.size());
                }
            }

            @Override
            public void onFailure(String err_msg) {
                psr.onRefreshComplete();
                listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
            }
        });
    }

    @NonNull
    private ChatCatalog addChatCatalog(ChatMessage message) {
        ChatCatalog catalog = new ChatCatalog();
        catalog.setGroup_id(message.getTo() + "");
        catalog.setUnreadnum(0);
        catalog.setType(0);
        catalog.setMsg_id(message.getMsg_id());
        catalog.setTime(Long.parseLong(message.getTime()));
        return catalog;
    }

    private String content = "";
    private String partStr = "";

    private EditTextDialog editdialog1;

    private EditTextDialog Editdialog1() {
        return EditTextDialog
                .createDialog(getActivity(), R.style.myDialogTheme)
                .setRightButton(getString(R.string.send))
                .setEditHint("")
                .setEmailVisibility(VISIBLE)
                .setDiss(content -> ed_msg.setText(content))
                .setSendClick((content_message, type) -> {
                    ChatMessage message = new ChatMessage();
                    String time = new Date().getTime() + "";
                    message.setTime(time);
                    message.setClient_msg_id(MD5Util.getMD5String(time));
                    message.setContent(partStr + content_message);
                    message.setFrom(MatchSharedUtil.UserId());
                    message.setChat_type(MessageType.CHAT_GROUP);
                    message.setType(MessageType.TXT);
                    message.setState(MessageType.UPLOADING);
                    message.setUrl("");
                    message.setTo(Integer.parseInt(groupId));
                    message.setMsg_type(MessageType.CHAT);
                    message.setNickname(MatchSharedUtil.NickName());
                    message.setAvatar(MatchSharedUtil.UserAvatar());
                    message.setRole(role);
                    db.addChat(groupId, message);
                    messages.add(message);
                    listview.setSelection(listview.getBottom());
                    sendMessage(message);
                    partStr = "";
                    ed_msg.setText("");
                    tv_return.setVisibility(View.GONE);
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_emoji:
                if (TextUtils.isEmpty(MatchSharedUtil.UserPhone())) {
                    TextDialog.createDialog(getActivity())
                            .setTitle("")
                            .setMessage("为响应国家政策，请于操作前绑定手机信息！")
                            .setLeftButton("下次再说")
                            .setRightButton("前往绑定")
                            .setRightClick(() -> {
                                Bundle bundle = new Bundle();
                                bundle.putInt(IntentKey.FLAG, 1);
                                showActivityForResult(getActivity(), bundle, BindPhoneActivity.class, RequestCode.BINDED);
                            })
                            .show();
                } else {
                    if (editdialog1 == null) {
                        editdialog1 = Editdialog1();
                    }
                    editdialog1.showEmail();
                }
                break;
            case R.id.send_layout:
                if (TextUtils.isEmpty(MatchSharedUtil.UserPhone())) {
                    TextDialog.createDialog(getActivity())
                            .setTitle("")
                            .setMessage("为响应国家政策，请于操作前绑定手机信息！")
                            .setLeftButton("下次再说")
                            .setRightButton("前往绑定")
                            .setRightClick(() -> {
                                Bundle bundle = new Bundle();
                                bundle.putInt(IntentKey.FLAG, 1);
                                showActivityForResult(getActivity(), bundle, BindPhoneActivity.class, RequestCode.BINDED);
                            })
                            .show();
                } else {
                    if (editdialog1 == null) {
                        editdialog1 = Editdialog1();
                    }
                    editdialog1.show();
                }
                break;
            case R.id.tv_unread:
                listview.setSelection(listview.getBottom());
                break;
            case R.id.tv_reply:

                break;
            case R.id.iv_close:
                ll_tip.setVisibility(View.GONE);
                break;
        }
    }

    private void sendMessage(ChatMessage message) {
        WebBase.sendToFans(groupId, message.getChat_type(), message.getContent(),
                message.getUrl(), message.getClient_msg_id(), new ChatHandler(getActivity(), message.getClient_msg_id()) {
                    @Override
                    public void onSuccess(JSONObject obj) {
                        JSONObject object = obj.optJSONObject("msg");
                        ChatMessage message = JSONParse.getChatMsgObj(object);
                        updateUi(message, MessageType.UPLOAD_SUCCESS);
                        db.addChatCatalog(addChatCatalog(message));
                    }

                    @Override
                    public void onFailure(String err_msg, String receptId) {
                        ChatMessage message = new ChatMessage();
                        message.setClient_msg_id(receptId);
                        updateUi(message, MessageType.UPLOAD_FAIL);
                        Toast.makeText(getActivity(), err_msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 举报
     *
     * @param msg_id
     */
    private void complaint(String msg_id) {
        WebBase.complaint(msg_id, MessageType.ROOM_FANS + "", new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                Toast.makeText(getContext(), "成功举报", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String err_msg) {
                Toast.makeText(getContext(), err_msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 更是adapter
     *
     * @param message
     */
    private void updateUi(ChatMessage message, int state) {
        db.updateChat(groupId, message);
        for (ChatMessage m : messages) {
            if (message.getClient_msg_id().equalsIgnoreCase(m.getClient_msg_id())) {
                m.setState(state);
                m.setMsg_id(message.getMsg_id());
                messageAdapter.notifyDataSetChanged();
                listview.setSelection(listview.getBottom());
                break;
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (content.length() == 0 && keyCode == event.KEYCODE_DEL) {
            partStr = "";
            tv_return.setVisibility(View.GONE);
        }
        if (keyCode == event.KEYCODE_BACK) {
            if (editdialog1 != null && editdialog1.isShowing()) {
                editdialog1.dismiss();
            } else {
                getActivity().finish();
            }
        }
        return true;
    }

    public class DelMsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String msg_id = intent.getStringExtra("msg_id");
            for (ChatMessage message : messages) {
                if (msg_id.equals(message.getMsg_id())) {
                    messages.remove(message);
                    break;
                }
            }
            messageAdapter.notifyDataSetChanged();
        }
    }


    public class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取service传过来的信息
            ChatMessage message = (ChatMessage) intent.getSerializableExtra("new_chat_msg");
            if (message != null) {
                if (MessageType.ACTION_deleteMsg.equalsIgnoreCase(message.getAction())) {
                    String msg_id = message.getMsg_id();
                    for (ChatMessage m : messages) {
                        if (msg_id.equals(m.getMsg_id())) {
                            messages.remove(m);
                            break;
                        }
                    }
                    messageAdapter.notifyDataSetChanged();
                } else if (MessageType.ACTION_ban.equalsIgnoreCase(message.getAction())) {
                    ll_tip.setVisibility(View.VISIBLE);
                    tv_reply.setText("您已被禁言");
                } else if (MessageType.ACTION_unBan.equalsIgnoreCase(message.getAction())) {
                    ll_tip.setVisibility(View.VISIBLE);
                    tv_reply.setText("您已被解除禁言");
                } else {
                    messages.add(message);
                    messageAdapter.notifyDataSetChanged();
                    if (is_bottom) {
                        listview.smoothScrollToPosition(listview.getBottom());
                    } else {
                        if (new_live_message == 0) {
                            new_live_message = messageAdapter.getCount() - 1;
                        }
                        int number = messageAdapter.getCount() - new_live_message;
                        tv_unread.setText(String.valueOf(number));
                        if (tv_unread.getVisibility() == View.INVISIBLE) {
                            tv_unread.setVisibility(View.VISIBLE);
                            chat_activity.setlive_number_text(1, number);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }

//        if(mDelMsgReceiver != null){
//            getActivity().unregisterReceiver(mDelMsgReceiver);
//        }
    }
}
