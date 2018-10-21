package com.zbmf.newmatch.common;

import com.zbmf.newmatch.R;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by xuhao
 * on 2017/11/28.
 */

public class Constans {

    public static final int MFWW=0x7;//魔方薇薇
    public static final int MFTT=0x8;//头条
    public static final String ZBMFTT_ID="1791321";
    public static final int BLOG_FRAGMENT=0X5;//博文
    public static final int ZBMFTT=0X6;//原创

    public static final String MFWW_ID="7878";
    public static final String MM_dd_HH_mm="MM-dd HH:mm";
    /**
     * 收藏的博文
     */
    public static final String BLOG_COLLECT_TYPE="100";
    public static final String UP_DATA_MESSAGE="com.zbmf.StockGroup.Updata_Message";
    public static final String UPDATE_VIDEO_LIST="com.zbmf.StockGroup.videoList";
    public static final String CC_KEY="iVGLTXNTOciHKaXElrbdwOBSLjHNmP4T";
    public static final int CC_TIME=3*60*1000;
    public static final String CC_USERID="B9641D861A8BF45E";
    public static final int PER_PAGE = 20;
    public static final int PERPAGE=20;
    public static final String MATCH_ID="267";
    public static final int CITY=0x1;//城市
    public static final int SCHOOL=0x2;//高校
    public static final int BUSINESS=0x3;//券商
    public static final int NEW_MATCH=0x4;//最新比赛
    public static final int All_MATCH =0x5;    //全部比赛

    public static final int DAY_RANK=0x6;//日榜
    public static final int WEEK_RANK=0x7;//周榜
    public static final int MOUNTH_RANK=0x8;//月榜
    public static final int ALL_RANK=0x9;//总榜

    public static final int LASTLY_DEALS=0x10;//最新交易
    public static final int RANK_LIST=0x11;//排行榜
    public static final int HOLDER_SUPERIOR=0x12;//操盘高手

    public static final int NEW_DEAL_FLAG=0x13;//最新交易标记
    public static final int DAY_RANK_FLAG=0x14;//日榜标记
    public static final int WEEK_RANK_FLAG=0x15;//周榜标记
    public static final int MONTH_RANK_FLAG=0x16;//月榜标记
    public static final int ALL_RANK_FLAG=0x17;//月榜标记
    public static final int TRADER_RANK_FLAG=0x18;//操盘高手标记

    /**
     * 一天
     */
    public static final int DAYS=1;
    /**
     * 一个月
     */
    public static final int MONTH_DAYS=31;

    public static final int BUY_FLAG=0x19;
    public static final int SELL_FLAG=0x20;
    /*----------------微信分享标识---------------*/
    public static final int WX_SHARE_WEB=0X50;
    public static final int WX_SHARE_TEXT=0X51;
    public static final int WX_SHARE_IMG=0X52;
    public static final int WX_SHARE_MUSIC=0X53;
    public static final int WX_SHARE_VIDEO=0X54;
    /*------------------订阅标识--------------------*/
    public static final int TRADER_FLAG1=0x61;
    public static final int TRADER_FLAG2=0x62;
    public static final int TRADER_FLAG3=0x63;

    /*--------------屏幕分辨率-------------*/
    public static final int WIDTH_PIX_960=540;
    public static final int WIDTH_PIX_1280=720;
    public static final int WIDTH_PIX_1920=1080;
    public static final int WIDTH_PIX_2560=1440;
    public static final int WIDTH_PIX_854=600;
    public static final int WIDTH_PIX_800=480;

    /*----------延时跳转----------*/
    public static final int DELAY_TIME=30;

    //清除popWindow最小的时间间隔
    public static final int CLEAR_TIME_MIN=18;
    /*------------是否显示K线图------------*/
    public static final int NOT_SHOW_K_LINE_CHART=0;
    public static final int IS_SHOW_K_LINE_CHART=1;

    /*------------------category-------------------*/
    public static final String HOME_TOP_BANNER="20";
    public static final String LUNCH_ADS="21";
    public static final String HOME_SPONSOR_ADS="22";

    /*--------------------接口数据获取成功或失败的标数------------------*/
    public static final int GAIN_DATA_SUCCESS=0x11;
    public static final int GAIN_DATA_FAIL=0x12;

    /*-------------------存储popWindow弹窗的信息标记---------------*/
    public static final String USER_ID="user_id";
    public static final String POP_ID="pop_id";//全站的弹窗Id
    public static final String TIME_STAMP="time";//全站的弹窗时间
    public static final String INIT_TIME_STAMP="init_time";//个赛的弹窗时间

    public static final String CLIENT_ID="abxd1234";//默认的个推ID

    public static final String MATCH_AD_TYPE="app://match";//表示比赛类型的广告
    public static final String REDIRECT_URL = "http://www.7878.com";
    public static final String SHARE_URL = "http://m1.zbmf.com/match/2052/invite/?user_id=1788263";
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    public static final String SINA_AT = "【资本魔方炒股大赛】这有属于我们自己的模拟比赛，快来参加吧！@资本魔方7878";

    public static final String LIVE_IMG_500="-500_auto";

    public static final String LIVE_IMG_350="-350_auto";

    public static final String LIVE_IMG_200="-200_auto";
    /*---------------微信----------------*/

    public static final String WEI_APK_KEY="wx1569265af11bd90c";//webchat
    public static final String TencentSDKAppKey = "100886835";//tencent qq
    public static final String WBSDKAppKey = "2967409088";//sina appkey

//    public static final String WEI_APK_KEY = "wxf076e40b3d21cda2";
//    public static final String TencentSDKAppKey = "205998";//tencent qq
//    public static final String  WBSDKAppKey = "679452946";//sina appkey
    public static final String NEED_LOGIN = "用户登录失败或已过期";//tencent qq
    public static final String ACCOUNT_EXIT="2103";

    /*--------- CODE --------报名----------------*/
    public static final int APPLY_REQUEST_CODE=0x50;
    public static final int APPLY_RESPONSE_CODE=0x51;

     /*----------------添加股票评论--------------*/
     public static final int ADD_COMMENT_REQUEST=0x60;
    public static final int ADD_COMMENT_RESPONSE=0x61;
    public static final String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE","android.permission.CAMERA",
            "android.permission.ACCESS_WIFI_STATE"};

    /*-------------------- Err ------------------*/
    public static final String INVALID_ERR_MSG="Login failed / Invalid auth token";
    public static final String INVALID_LOGIN_MSG="User not logged in / Insufficient permissions";
    public static final String INVALID_ERR_CODE ="1004";
    public static final String INVALID_LOGIN_CODE ="1005";

    /**************************圈子相关的标记***********************/
    public static final String LIVE="live";
    public static final String ROOM="room";
    public static final String USER="user";

    public static final int PEOPLE_ARROW=0x1;
    public static final int NOW_LIVE=0x2;
    public static final int EXCLUSIVE=0x3;
    public static final int PEOPLE_RECOMED=0x4;

    public static final String YYYYMM="yyyyMM";

    /**
     * 1、投票：进入圈子选择送礼投票，按1积分=1票，1魔方宝=25票统计票数。所有资本魔方平台圈主和用户可参加
     2、奖项：圈子当月送礼投票排行第1名获得“最喜爱圈主”冠军称号，该圈子第1名贡献用户获得100魔方宝奖励。
     3、全平台贡献最高用户有机会获得“找股票”智能选股产品体验资格1个月。
     */
    public static final String VOTING_DESC= "<font color=\"#ea3535\">【投票】</font><font  color=\"#333333\"> 进入圈子选择送礼投票，按1积分=1票，1魔方宝=25票统计票数。所有资本魔方平台圈主和用户可参加。</font><br />"
            +"<font color=\"#ea3535\">【奖项】</font><font  color=\"#333333\"> 圈子当月送礼投票排行第1名获得“最喜爱圈主”冠军称号，该圈子第1名贡献用户获得100魔方宝奖励。</font><br />"
            +"全平台贡献最高用户有机会获得“找股票”智能选股产品体验资格1个月";

    /**
     * 铁粉专区
     */
    public static final int FANS_MESSAGE=100;
    public static final String yy_MM_dd_HH_mm="yyyy-MM-dd HH:mm";
    public static final String YYYY年MM月dd日="yyyy年MM月dd日";

    public static final int STUDY=0x5;

    public static final String UNREADNUM="com.zbmf.StockGroup.unread_num";
    public static final String FANS_CHAT_MSG="com.zbmf.StockGroup.fans_chat_msg";
    public static final String NEW_LIVE_MSG="com.zbmf.StockGroup.new_live_msg";
    public static final String NEW_LIVE_MSG_READ="com.zbmf.StockGroup.new_live_msg_read";
    public static final String UP_DATA_COUPONS="com.zbmf.StockGroup.Updata_Coupons";
    public static final int HISTORY_DAYS=-20;//当前日期前20天

    private static HashMap<String, Integer> mEmojiIcon;
    public static HashMap<String, Integer> getEmojiIconMaps() {
        if (mEmojiIcon == null) {
            mEmojiIcon = new LinkedHashMap<String, Integer>();
            mEmojiIcon.put("[微笑]", R.drawable.face_001);
            mEmojiIcon.put("[撇嘴]", R.drawable.face_002);
            mEmojiIcon.put("[色]", R.drawable.face_003);
            mEmojiIcon.put("[发呆]", R.drawable.face_004);
            mEmojiIcon.put("[得意]", R.drawable.face_005);
            mEmojiIcon.put("[流泪]", R.drawable.face_006);
            mEmojiIcon.put("[害羞]", R.drawable.face_007);
            mEmojiIcon.put("[闭嘴]", R.drawable.face_008);
            mEmojiIcon.put("[睡]", R.drawable.face_009);
            mEmojiIcon.put("[大哭]", R.drawable.face_010);
            mEmojiIcon.put("[尴尬]", R.drawable.face_011);
            mEmojiIcon.put("[发怒]", R.drawable.face_012);
            mEmojiIcon.put("[调皮]", R.drawable.face_013);
            mEmojiIcon.put("[呲牙]", R.drawable.face_014);
            mEmojiIcon.put("[惊讶]", R.drawable.face_015);
            mEmojiIcon.put("[难过]", R.drawable.face_016);
            mEmojiIcon.put("[酷]", R.drawable.face_017);
            mEmojiIcon.put("[冷汗]", R.drawable.face_018);
            mEmojiIcon.put("[抓狂]", R.drawable.face_019);
            mEmojiIcon.put("[吐]", R.drawable.face_020);
            mEmojiIcon.put("[偷笑]", R.drawable.face_021);
            mEmojiIcon.put("[可爱]", R.drawable.face_022);
            mEmojiIcon.put("[白眼]", R.drawable.face_023);
            mEmojiIcon.put("[傲慢]", R.drawable.face_024);
            mEmojiIcon.put("[饥饿]", R.drawable.face_025);
            mEmojiIcon.put("[困]", R.drawable.face_026);
            mEmojiIcon.put("[惊恐]", R.drawable.face_027);
            mEmojiIcon.put("[流汗]", R.drawable.face_028);
            mEmojiIcon.put("[憨笑]", R.drawable.face_029);
            mEmojiIcon.put("[大兵]", R.drawable.face_030);
            mEmojiIcon.put("[奋斗]", R.drawable.face_031);
            mEmojiIcon.put("[咒骂]", R.drawable.face_032);
            mEmojiIcon.put("[疑问]", R.drawable.face_033);
            mEmojiIcon.put("[嘘]", R.drawable.face_034);
            mEmojiIcon.put("[晕]", R.drawable.face_035);
            mEmojiIcon.put("[折磨]", R.drawable.face_036);
            mEmojiIcon.put("[衰]", R.drawable.face_037);
            mEmojiIcon.put("[骷髅]", R.drawable.face_038);
            mEmojiIcon.put("[敲打]", R.drawable.face_039);
            mEmojiIcon.put("[再见]", R.drawable.face_040);
            mEmojiIcon.put("[擦汗]", R.drawable.face_041);
            mEmojiIcon.put("[抠鼻]", R.drawable.face_042);
            mEmojiIcon.put("[鼓掌]", R.drawable.face_043);
            mEmojiIcon.put("[糗大了]", R.drawable.face_044);
            mEmojiIcon.put("[坏笑]", R.drawable.face_045);
            mEmojiIcon.put("[左哼哼]", R.drawable.face_046);
            mEmojiIcon.put("[右哼哼]", R.drawable.face_047);
            mEmojiIcon.put("[哈欠]", R.drawable.face_048);
            mEmojiIcon.put("[鄙视]", R.drawable.face_049);
            mEmojiIcon.put("[委屈]", R.drawable.face_050);
            mEmojiIcon.put("[快哭了]", R.drawable.face_051);
            mEmojiIcon.put("[阴险]", R.drawable.face_052);
            mEmojiIcon.put("[亲亲]", R.drawable.face_053);
            mEmojiIcon.put("[吓]", R.drawable.face_054);
            mEmojiIcon.put("[可怜]", R.drawable.face_055);
            mEmojiIcon.put("[菜刀]", R.drawable.face_056);
            mEmojiIcon.put("[西瓜]", R.drawable.face_057);
            mEmojiIcon.put("[啤酒]", R.drawable.face_058);
            mEmojiIcon.put("[篮球]", R.drawable.face_059);
            mEmojiIcon.put("[乒乓]", R.drawable.face_060);
            mEmojiIcon.put("[咖啡]", R.drawable.face_061);
            mEmojiIcon.put("[饭]", R.drawable.face_062);
            mEmojiIcon.put("[猪头]", R.drawable.face_063);
            mEmojiIcon.put("[玫瑰]", R.drawable.face_064);
            mEmojiIcon.put("[凋谢]", R.drawable.face_065);
            mEmojiIcon.put("[示爱]", R.drawable.face_066);
            mEmojiIcon.put("[爱心]", R.drawable.face_067);
            mEmojiIcon.put("[心碎]", R.drawable.face_068);
            mEmojiIcon.put("[蛋糕]", R.drawable.face_069);
            mEmojiIcon.put("[闪电]", R.drawable.face_070);
            mEmojiIcon.put("[炸弹]", R.drawable.face_071);
            mEmojiIcon.put("[刀]", R.drawable.face_072);
            mEmojiIcon.put("[足球]", R.drawable.face_073);
            mEmojiIcon.put("[瓢虫]", R.drawable.face_074);
            mEmojiIcon.put("[便便]", R.drawable.face_075);
            mEmojiIcon.put("[月亮]", R.drawable.face_076);
            mEmojiIcon.put("[太阳]", R.drawable.face_077);
            mEmojiIcon.put("[礼物]", R.drawable.face_078);
            mEmojiIcon.put("[拥抱]", R.drawable.face_079);
            mEmojiIcon.put("[强]", R.drawable.face_080);
            mEmojiIcon.put("[弱]", R.drawable.face_081);
            mEmojiIcon.put("[握手]", R.drawable.face_082);
            mEmojiIcon.put("[胜利]", R.drawable.face_083);
            mEmojiIcon.put("[抱拳]", R.drawable.face_084);
            mEmojiIcon.put("[勾引]", R.drawable.face_085);
            mEmojiIcon.put("[拳头]", R.drawable.face_086);
            mEmojiIcon.put("[差劲]", R.drawable.face_087);
            mEmojiIcon.put("[爱你]", R.drawable.face_088);
            mEmojiIcon.put("[NO]", R.drawable.face_089);
            mEmojiIcon.put("[OK]", R.drawable.face_090);
            mEmojiIcon.put("[爱情]", R.drawable.face_091);
            mEmojiIcon.put("[飞吻]", R.drawable.face_092);
            mEmojiIcon.put("[跳跳]", R.drawable.face_093);
            mEmojiIcon.put("[发抖]", R.drawable.face_094);
            mEmojiIcon.put("[怄火]", R.drawable.face_095);
            mEmojiIcon.put("[转圈]", R.drawable.face_096);
            mEmojiIcon.put("[磕头]", R.drawable.face_097);
            mEmojiIcon.put("[回头]", R.drawable.face_098);
            mEmojiIcon.put("[跳绳]", R.drawable.face_099);
            mEmojiIcon.put("[挥手]", R.drawable.face_100);
            mEmojiIcon.put("[激动]", R.drawable.face_101);
            mEmojiIcon.put("[街舞]", R.drawable.face_102);
            mEmojiIcon.put("[献吻]", R.drawable.face_103);
            mEmojiIcon.put("[左太极]", R.drawable.face_104);
            mEmojiIcon.put("[右太极]", R.drawable.face_105);
            mEmojiIcon.put("[双喜]", R.drawable.face_106);
            mEmojiIcon.put("[鞭炮]", R.drawable.face_107);
            mEmojiIcon.put("[灯笼]", R.drawable.face_108);
            mEmojiIcon.put("[发财]", R.drawable.face_109);
            mEmojiIcon.put("[K歌]", R.drawable.face_110);
            mEmojiIcon.put("[购物]", R.drawable.face_111);
            mEmojiIcon.put("[邮件]", R.drawable.face_112);
            mEmojiIcon.put("[帅]", R.drawable.face_113);
            mEmojiIcon.put("[喝彩]", R.drawable.face_114);
            mEmojiIcon.put("[祈祷]", R.drawable.face_115);
            mEmojiIcon.put("[爆筋]", R.drawable.face_116);
            mEmojiIcon.put("[棒棒糖]", R.drawable.face_117);
            mEmojiIcon.put("[喝奶]", R.drawable.face_118);
            mEmojiIcon.put("[下面]", R.drawable.face_119);
            mEmojiIcon.put("[香蕉]", R.drawable.face_120);
            mEmojiIcon.put("[飞机]", R.drawable.face_121);
            mEmojiIcon.put("[开车]", R.drawable.face_122);
            mEmojiIcon.put("[高铁左车头]", R.drawable.face_123);
            mEmojiIcon.put("[车厢]", R.drawable.face_124);
            mEmojiIcon.put("[高铁右车头]", R.drawable.face_125);
            mEmojiIcon.put("[多云]", R.drawable.face_126);
            mEmojiIcon.put("[下雨]", R.drawable.face_127);
            mEmojiIcon.put("[钞票]", R.drawable.face_128);
            mEmojiIcon.put("[熊猫]", R.drawable.face_129);
            mEmojiIcon.put("[灯泡]", R.drawable.face_130);
            mEmojiIcon.put("[风车]", R.drawable.face_131);
            mEmojiIcon.put("[闹钟]", R.drawable.face_132);
            mEmojiIcon.put("[打伞]", R.drawable.face_133);
            mEmojiIcon.put("[彩球]", R.drawable.face_134);
            mEmojiIcon.put("[钻戒]", R.drawable.face_135);
            mEmojiIcon.put("[沙发]", R.drawable.face_136);
            mEmojiIcon.put("[纸巾]", R.drawable.face_137);
            mEmojiIcon.put("[药]", R.drawable.face_138);
            mEmojiIcon.put("[手枪]", R.drawable.face_139);
            mEmojiIcon.put("[青蛙]", R.drawable.face_140);
            mEmojiIcon.put("[招财猫]", R.drawable.face_141);
        }
        return mEmojiIcon;
    }

}
