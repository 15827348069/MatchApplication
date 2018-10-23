package com.zbmf.newmatch.util;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.bean.LoginUser;
import com.zbmf.newmatch.bean.TagBean;
import com.zbmf.newmatch.bean.User;
import com.zbmf.newmatch.callback.ResultCallback;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.common.SharedKey;
import com.zbmf.worklibrary.util.Logx;
import com.zbmf.worklibrary.util.SharedpreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhao
 * on 2017/11/24.
 * 存储用户信息
 */

public class MatchSharedUtil {
    private static final String defaultValue="";

    //获取单例对象
    private static MatchSharedUtil matchSharedUtil;
    public static MatchSharedUtil instance(){
        if (matchSharedUtil==null){
            synchronized (MatchSharedUtil.class){
                if (matchSharedUtil==null){
                    matchSharedUtil=new MatchSharedUtil();
                }
            }
        }
        return matchSharedUtil;
    }

    public static void saveUser(LoginUser loginUser){
        SharedpreferencesUtil.getInstance().putString(SharedKey.AUTH_TOKEN,loginUser.getAuth_token());
        saveUser(loginUser.getUser());
    }
    public static void saveUser(User user){
        SharedpreferencesUtil.getInstance().putString(SharedKey.USER_ID,String.valueOf(user.getUser_id()));
        SharedpreferencesUtil.getInstance().putString(SharedKey.USER_NAME,user.getUsername());
        SharedpreferencesUtil.getInstance().putString(SharedKey.NICK_NAME,user.getNickname());
        SharedpreferencesUtil.getInstance().putString(SharedKey.TRUE_NAME,user.getTruename());
        SharedpreferencesUtil.getInstance().putString(SharedKey.AVATAR,user.getAvatar());
        SharedpreferencesUtil.getInstance().putString(SharedKey.ROLE,user.getRole());
        SharedpreferencesUtil.getInstance().putString(SharedKey.PHONE,user.getPhone());
        SharedpreferencesUtil.getInstance().putString(SharedKey.MPAY,String.valueOf(user.getMpay()));
        SharedpreferencesUtil.getInstance().putString(SharedKey.IDCARD,user.getIdcard());
    }
    public static void clearUser(){
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.AUTH_TOKEN);
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.USER_ID);
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.USER_NAME);
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.NICK_NAME);
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.TRUE_NAME);
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.AVATAR);
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.ROLE);
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.PHONE);
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.MPAY);
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.IDCARD);
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.POINT);
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.PAYS);
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.COUPON);
    }
    public static void clearAuthToken(){
        SharedpreferencesUtil.getInstance().removeKey(SharedKey.AUTH_TOKEN);
    }
    public static void putAuthToken(String authToken){
        SharedpreferencesUtil.getInstance().putString(SharedKey.AUTH_TOKEN,authToken);
    }
    public static String AuthToken(){
        String string = SharedpreferencesUtil.getInstance().getString(SharedKey.AUTH_TOKEN, defaultValue);
        return  string;
    }
    //设置用户ID
    public static void setUserId(String userId){
        SharedpreferencesUtil.getInstance().putString(SharedKey.USER_ID, userId);
    }
    //获取当前用户的ID
    public static String UserId(){
        return  SharedpreferencesUtil.getInstance().getString(SharedKey.USER_ID,defaultValue);
    }
    public static String UserName(){
        return  SharedpreferencesUtil.getInstance().getString(SharedKey.USER_NAME,defaultValue);
    }
    public static String UserPhone(){
        return  SharedpreferencesUtil.getInstance().getString(SharedKey.PHONE,defaultValue);
    }
    public static void setUserPhone(String userPhone){
        SharedpreferencesUtil.getInstance().putString(SharedKey.PHONE,userPhone);
    }
    //设置用户头像
    public static void setUserAvatar(String useravatar){
        SharedpreferencesUtil.getInstance().putString(SharedKey.AVATAR, useravatar);
    }
    //获取用户头像
    public static String UserAvatar(){
        return  SharedpreferencesUtil.getInstance().getString(SharedKey.AVATAR,defaultValue);
    }
    public static String UserMpay(){
        return  SharedpreferencesUtil.getInstance().getString(SharedKey.MPAY,defaultValue);
    }
    //设置当前用户的昵称
    public static void setNickName(String userName){
        SharedpreferencesUtil.getInstance().putString(USER_NICK_NAME, userName);
    }
    //获取当前用户的昵称
    public static String NickName(){
        return  SharedpreferencesUtil.getInstance().getString(SharedKey.NICK_NAME,defaultValue);
    }
    //设置当前用户的身份证号
    public static void setUserIDCard(String userIDCard){
        SharedpreferencesUtil.getInstance().putString(SharedKey.IDCARD,userIDCard);
    }
    //获取当前用户的身份证号
    public static String UserIDCard(){
        return SharedpreferencesUtil.getInstance().getString(SharedKey.IDCARD,defaultValue);
    }
    //设置当前用户的真是姓名
    public static void setTrueName(String trueName){
        SharedpreferencesUtil.getInstance().putString(TRUE_NAME, trueName);
    }
    //获取当前用户的真是姓名
    public static String UserTrueName(){
        return SharedpreferencesUtil.getInstance().getString(SharedKey.TRUE_NAME,defaultValue);
    }
    public static void setUserTrueName(String trueName){
        SharedpreferencesUtil.getInstance().putString(SharedKey.TRUE_NAME,trueName);
    }

    /*************************设置圈子相关************************/
    //设置直播室图片宽度
    public static void setLiveImg(String liveImg){
        SharedpreferencesUtil.getInstance().putString(LIVE_IMG,liveImg);
    }
    //获取直播室的图片
    public static String getLiveImg(){
        return  SharedpreferencesUtil.getInstance().getString(LIVE_IMG, Constans.LIVE_IMG_350);
    }
    //设置优惠券
    public static void setCoupon(String coupon) {
        SharedpreferencesUtil.getInstance().putString(SharedKey.COUPON, coupon);
    }
    //获取优惠券
    public static String getCoupon() {
        return SharedpreferencesUtil.getInstance().getString(SharedKey.COUPON,"0");
    }
    //存储是否显示当前铁粉
    public static void setIsShowFans(int isShowFans){
        SharedpreferencesUtil.getInstance().putInt(IntentKey.SHOW_FANS,isShowFans);
    }
    //获取当前是否铁粉
    public static boolean getIsShowFans(){
        return SharedpreferencesUtil.getInstance().getInt(IntentKey.SHOW_FANS,1)==1;// 0非紧急 显示,1紧急 隐藏
    }
    // 设置当前用户的推送PUSH_CILENT_ID
    public static void setPushCilentId(String authToken) {
        SharedpreferencesUtil.getInstance().putString(PUSH_CILENT_ID, authToken);
    }
    //获取当前用户的推送ID
    public static String getPUSH_CILENT_ID() {
        return SharedpreferencesUtil.getInstance().getString(PUSH_CILENT_ID,"");
    }
    public static void setStockModeDesc(boolean isFirst){
        SharedpreferencesUtil.getInstance().putBoolean(STOCK_MODE_DESC,isFirst);
    }
    public static boolean getStockModeDesc(){
        return  SharedpreferencesUtil.getInstance().getBoolean(STOCK_MODE_DESC,false);
    }
    //设置当前用户是VIP
    public static void setIsVip(int vip){
        SharedpreferencesUtil.getInstance().putInt(SharedKey.VIP,vip);
    }
    public static int getIsVip(){
        return SharedpreferencesUtil.getInstance().getInt(SharedKey.VIP,-1);
    }
    //设置当前用户是超级VIP
    public static void setSuperVip(int superVip){
        SharedpreferencesUtil.getInstance().putInt(SUPER_VIP,superVip);
    }
    //设置当前用户的VIP的结束时间
    public static void setVipAtEnd(String vipAtEnd){
        SharedpreferencesUtil.getInstance().putString(SharedKey.VIP_AT_END,vipAtEnd);
    }
    public static String getVipAtEnd(){
       return SharedpreferencesUtil.getInstance().getString(SharedKey.VIP_AT_END,defaultValue);
    }
    //设置博文字体大小
    public static void setBlogTextSize(int textsize) {
        SharedpreferencesUtil.getInstance().putInt(BLOG_TEXT_SIZE, textsize);
    }
    //获取用户设置博文字体大小
    public static int getBlogTextSize(){
        return SharedpreferencesUtil.getInstance().getInt(BLOG_TEXT_SIZE,2);
    }
    /**
     * 设置消息是否提示声音
     * @param group_id
     * @param vedio
     */
    public static void setNewMessageVedio(String group_id,boolean vedio){
        SharedpreferencesUtil.getInstance().putBoolean(MESSAGE_VEDIO+group_id,vedio);
    }
    /**
     * 获取圈子是否提示声音
     * @param group_id
     * @return
     */
    public boolean getNewMessageVedio(String group_id){
        return SharedpreferencesUtil.getInstance().getBoolean(MESSAGE_VEDIO+group_id,false);
    }
    //设置直播字体大小
    public static void setTextSize(int textSize) {
        SharedpreferencesUtil.getInstance().putInt(TEXT_SIZE, textSize);
    }
    //用户设置字体大小
    public static int getTextSize(){
        return SharedpreferencesUtil.getInstance().getInt(TEXT_SIZE, R.dimen.tv_size_15);
    }
    /**
     * 设置消息是否提示声音
     * @param group_id
     * @param vedio
     */
    public static void setNewChatMessageVedio(String group_id,boolean vedio){
        SharedpreferencesUtil.getInstance().putBoolean(CHAT_MESSAGE_VEDIO+group_id,vedio);
    }
    /**
     * 获取圈子是否提示声音
     * @param group_id
     * @return
     */
    public static boolean getNewChatMessageVedio(String group_id){
        return SharedpreferencesUtil.getInstance().getBoolean(CHAT_MESSAGE_VEDIO+group_id,false);
    }
    /**
     * 设置可用魔方宝
     * @param pays
     */
    public static void setPays(String pays){
        SharedpreferencesUtil.getInstance().putString(SharedKey.PAYS+UserId(),pays);
    }
    /**
     * 获取可用魔方宝
     * @return
     */
    public static String getPays(){
        return SharedpreferencesUtil.getInstance().getString((SharedKey.PAYS+UserId()),"0.00");
    }
    /**
     * 设置可用积分
     * @param pays
     */
    public static void setPoint(long pays){
        SharedpreferencesUtil.getInstance().putLong(SharedKey.POINT+UserId(),pays);
    }
    /**
     * 获取可用积分
     * @return
     */
    public static long getPoint(){
        return SharedpreferencesUtil.getInstance().getLong(SharedKey.POINT+UserId(),0);
    }
    //设置client_id
    public static void setClientId(String clientId) {
        SharedpreferencesUtil.getInstance().putString(CLIENT_ID, clientId);
    }
    //获取client_id
    public static String getClientId(){
        return SharedpreferencesUtil.getInstance().getString(CLIENT_ID,null);
    }
    //设置全局消息
    public static void setMessageAll(boolean vedio){
        SharedpreferencesUtil.getInstance().putBoolean(MESSAGE_All,vedio);
    }
    //全局消息获取
    public static boolean getMessageAll(){
        return SharedpreferencesUtil.getInstance().getBoolean(MESSAGE_All,false);
    }
    //设置当前的聊天
    public static void setCurrentChat(String currentChat) {
        SharedpreferencesUtil.getInstance().putString(CURRENT_CHAT, currentChat);
    }
    //获取当前的聊天
    public static String getCurrentChat() {
        return SharedpreferencesUtil.getInstance().getString(CURRENT_CHAT,"");
    }
    //更新直播
    public static void setUpdataLive(long currentChat,String groupId) {
        SharedpreferencesUtil.getInstance().putLong(UPDATA_LIVE+UserId()+groupId, currentChat);
    }
    //获取更新的直播数据
    public static long getUpdataLive(String groupId) {
        return SharedpreferencesUtil.getInstance().getLong(UPDATA_LIVE+UserId()+groupId,0);
    }
    //清空搜索记录
    public static void clearSearchHistory(){
        SharedpreferencesUtil.getInstance().removeKey(SEARCH_HISTORY);
    }
    //获取搜索的历史记录
    public void getSearchHistory(ResultCallback callback){
        SharedpreferencesUtil instance = SharedpreferencesUtil.getInstance();
        String result = instance.getString(SEARCH_HISTORY,"");
        List<TagBean.ChildrenTag> tags=new ArrayList<>();
        try {
            JSONArray array ;
            if(!result.equals("")){
                array = new JSONArray(result);
            }else{
                array=new JSONArray();
            }
            for(int i=array.length()-1;i>=0;i--){
                JSONObject object = array.getJSONObject(i);
                tags.add(new TagBean.ChildrenTag(object.optString("name"),
                        object.optString("id"),object.optInt("hot")));
            }
            if(tags.size()>0){
                callback.onSuccess(tags);
            }else{
                Logx.e("搜索历史为空");
                callback.onFail("搜索历史为空");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Logx.e("取出异常"+e.getMessage());
            instance.putString(SEARCH_HISTORY,"");
            callback.onFail(e.getMessage());
        }
    }
    //设置搜索的历史记录
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setSearchHistory(TagBean.ChildrenTag tag){
        String result = SharedpreferencesUtil.getInstance().getString(SEARCH_HISTORY,"");
        if(!result.contains(tag.getName())){
            try {
                JSONObject object=new JSONObject();
                object.put("name",tag.getName());
                object.put("id",tag.getId());
                object.put("hot",tag.getIs_hot());
                JSONArray jsonArray=null;
                if(!result.equals("")){
                    jsonArray =new JSONArray(result);
                    if(jsonArray.length()==5){
                        jsonArray.remove(0);
                    }
                }else{
                    jsonArray=new JSONArray();
                }
                jsonArray.put(object);
                SharedpreferencesUtil.getInstance().putString(SEARCH_HISTORY,jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private static final String TAG="UserDefaultsManager";
    private static SettingDefaultsManager defaultInstance=null;
    private static SharedPreferences sharedPreferences=null;
    SharedPreferences.Editor editor=null;

    public static final String CLIENT_ID="client_id";
    // 用户authtoken
    public static final String USER_AUTHTOKEN = "User_AuthToken";
    //用户名
    public static final String USER_NICK_NAME="User_Nick_name";
    //真实名字
    public static final String TRUE_NAME="true_name";
    //idcard
    public static final String IDCARD="idcard";
    //用户头像
    public static final String USER_AVATAR="User_Avatar";
    //用户ID
    public static final String USER_ID="User_Id";
    //用户手机
    public static final String USER_PHONE="user_phone";
    //用户设置字体大小
    public static final String TEXT_SIZE="Text_Size";
    //用户设置字体大小
    public static final String  BLOG_TEXT_SIZE="Blog_Text_Size";
    //设置用户声音是否提示
    public static final String MESSAGE_VEDIO="message_vedio_";
    //设置用户群聊声音是否提示
    public static final String CHAT_MESSAGE_VEDIO="chat_message_vedio_";
    //设置用户可用魔方宝
//    public static final String PAYS="pays";
    //设置用户可用积分
//    public static final String POINT="point";
    //设置用户可用优惠券
//    public static final String COUPON="coupon";
    //是否显示铁粉
    public static final String SHOW_FANS="show_fans";

    public static final String LIVE_IMG ="live_img";
    //vip
//    public static final String VIP="vip";
    //superVip
    public static final String SUPER_VIP="super_vip";
    //vip到期时间
//    public static final String VIP_AT_END="vip_at_end";
    //设置用户声音是否提示
    public static final String MESSAGE_All="message_all";

    public static  final String PUSH_CILENT_ID="push_client_id";
    public static  final String CURRENT_CHAT="current_chat";
    public static final String SEARCH_HISTORY="search_history;";
    public static final String UPDATA_LIVE="updata_live";

    public static final String STOCK_MODE_DESC="stock_mode_desc";
//    public void clearUserInfo(){
//        editor.remove(USER_AUTHTOKEN);
//        editor.remove(USER_ID);
//        editor.remove(USER_PHONE);
//        editor.remove(USER_AVATAR);
//        editor.remove(USER_NICK_NAME);
//        editor.remove(TRUE_NAME);
//        editor.remove(IDCARD);
//        editor.remove(POINT);
//        editor.remove(PAYS);
//        editor.remove(COUPON);
//        editor.commit();
//    }



//    public static SettingDefaultsManager getInstance()
//    {
//        if (defaultInstance == null)
//        {
//            defaultInstance = new SettingDefaultsManager();
//        }
//        return defaultInstance;
//    }
//    //存储为Group_UserInformation的文件中
//    public void setSharedPreferences(Context context)
//    {
//        sharedPreferences = context.getSharedPreferences(context.getPackageName()+"Group_SettingDefaultsManager", Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//    }




}
