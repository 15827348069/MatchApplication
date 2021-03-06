package com.zbmf.newmatch.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.BlogDetailActivity;
import com.zbmf.newmatch.activity.BoxDetailWebActivity;
import com.zbmf.newmatch.activity.Chat1Activity;
import com.zbmf.newmatch.activity.GroupDetailActivity;
import com.zbmf.newmatch.activity.MyQuestionActivity;
import com.zbmf.newmatch.activity.StockMessageActivity;
import com.zbmf.newmatch.activity.TopTicActivity;
import com.zbmf.newmatch.bean.BlogBean;
import com.zbmf.newmatch.bean.BoxBean;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.bean.LiveMessage;
import com.zbmf.newmatch.bean.PushNoticeBean;
import com.zbmf.newmatch.bean.Screen;
import com.zbmf.newmatch.bean.TopticBean;
import com.zbmf.newmatch.common.IntentKey;


/**
 * Created by xuhao on 2017/2/27.
 */

public class NotificationUtil {
    /**
     * 博文通知栏显示
     * @param context
     * @param message·
     */
    public static void showNoticeLayout(Context context,BlogBean message){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Intent intent = new Intent(context,BlogDetailActivity.class);
        intent.putExtra("blogBean",message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        @SuppressLint("WrongConstant")
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent,Notification.FLAG_AUTO_CANCEL);
        mBuilder.setContentTitle(context.getString(R.string.app_name))//设置通知栏标题
                .setContentText(message.getTitle()) //设置通知栏显示内容
                .setContentIntent(pendingIntent) //设置通知栏点击意图
                //  .setNumber(number) //设置通知集合的数量
                .setTicker(message.getTitle()) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_HIGH) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON

        mNotificationManager.notify((Integer.valueOf(message.getBlog_id().replace("-",""))),mBuilder.build());
    }
    /**
     * 宝盒通知栏显示
     * @param context
     * @param message·
     */
    public static void showBoxNoticeLayout(Context context,BoxBean message){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Intent intent = new Intent(context,BoxDetailWebActivity.class);
        intent.putExtra("BoxBean",message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentTitle(context.getString(R.string.app_name))//设置通知栏标题
                .setContentText(message.getDescription()) //设置通知栏显示内容
                .setContentIntent(pendingIntent) //设置通知栏点击意图
                //  .setNumber(number) //设置通知集合的数量
                .setTicker(message.getDescription()) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_HIGH) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFA ULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        if(message.getBox_id()!=null&&!message.getBox_id().equals("")){
            int notify_id=Integer.valueOf(message.getBox_id().replace("-",""));
            mNotificationManager.notify(notify_id,mBuilder.build());
        }
    }
    /**
     * 广告通知栏显示
     * @param context
     * @param message
     */
    public static void showTopicNoticeLayout(Context context,TopticBean message){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Intent intent=null;
        if(message.getLink()!=null){
            intent = WebNoticitionUitl.ShowActivity(context,message.getLink());
        }else{
            intent = new Intent(context,TopTicActivity.class);
            intent.putExtra("TopticBean",message);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentTitle(context.getString(R.string.app_name))//设置通知栏标题
                .setContentText(message.getContent()) //设置通知栏显示内容
                .setContentIntent(pendingIntent) //设置通知栏点击意图
                //  .setNumber(number) //设置通知集合的数量
                .setTicker(message.getContent()) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_HIGH) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFA ULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
            String time=String.valueOf(System.currentTimeMillis());
            int size=time.length();
            time=time.substring(size-6,size);
            mNotificationManager.notify(Integer.valueOf(time),mBuilder.build());
        }
    public static void showGroupNoticeLayout(Context context,TopticBean message){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Intent intent=new Intent(context,GroupDetailActivity.class);;
        Group group=new Group();
        group.setId(message.getId());
        intent.putExtra("GROUP",group);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentTitle(context.getString(R.string.app_name))//设置通知栏标题
                .setContentText(message.getContent()) //设置通知栏显示内容
                .setContentIntent(pendingIntent) //设置通知栏点击意图
                //  .setNumber(number) //设置通知集合的数量
                .setTicker(message.getSubject()) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_HIGH) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFA ULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        mNotificationManager.notify(Integer.valueOf(message.getId()),mBuilder.build());
    }
    public static void showAnsterNoticeLayout(Context context,LiveMessage message){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Intent intent=new Intent(context,MyQuestionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentTitle(context.getString(R.string.app_name))//设置通知栏标题
                .setContentText(message.getAnswer_name()+"回答了您的问题:"+message.getQuestion_content()) //设置通知栏显示内容
                .setContentIntent(pendingIntent) //设置通知栏点击意图
                //  .setNumber(number) //设置通知集合的数量
                .setTicker(message.getAnswer_name()+"回答了您的问题!") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_HIGH) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFA ULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        mNotificationManager.notify(Integer.valueOf(message.getAnswer_id()),mBuilder.build());
    }

    /**
     * 铁粉消息通知栏提示
     * @param group
     */
    public static void showGroupNoticeLayout(Context context,Group group) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Intent intent=new Intent(context,Chat1Activity.class);
        intent.putExtra(IntentKey.GROUP,group);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentTitle(group.getSubject())//设置通知栏标题
                .setContentText(group.getContent()) //设置通知栏显示内容
                .setContentIntent(pendingIntent) //设置通知栏点击意图
                .setTicker(group.getContent()) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_HIGH) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        mNotificationManager.notify(Integer.valueOf(group.getId()),mBuilder.build());
    }
    public static void showScreenNotification(Context context,Screen screen) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Intent intent=new Intent(context,StockMessageActivity.class);
        intent.putExtra(IntentKey.SCREEN,screen);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentTitle(screen.getName())//设置通知栏标题
                .setContentText(screen.getDescripition()) //设置通知栏显示内容
                .setContentIntent(pendingIntent) //设置通知栏点击意图
                .setTicker(screen.getDescripition()) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_HIGH) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        String time=String.valueOf(System.currentTimeMillis());
        int size=time.length();
        time=time.substring(size-6,size);
        mNotificationManager.notify(Integer.valueOf(time),mBuilder.build());
    }
    public static void showNotification(Context context,PushNoticeBean noticeBean) {
        if(noticeBean!=null){
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0, getIntent(context,noticeBean.getType()),PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentTitle(noticeBean.getTitle())//设置通知栏标题
                    .setContentText(noticeBean.getContent()) //设置通知栏显示内容
                    .setContentIntent(pendingIntent) //设置通知栏点击意图
                    .setTicker(noticeBean.getContent()) //通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(Notification.PRIORITY_HIGH) //设置该通知优先级
                    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
            mNotificationManager.notify(noticeBean.getId(),mBuilder.build());
        }
    }
    private static Intent getIntent(Context context,String type){
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
//        switch (type){
//            case Constans.PUSH_STOCK_ASK:
//                intent.setClass(context, DongAskActivity.class);
//                bundle.putInt(IntentKey.SELECT,1);
//                intent.putExtras(bundle);
//                break;
//            case Constans.PUSH_STOCK_NOTICE:
//                intent.setClass(context, DongAskActivity.class);
//                bundle.putInt(IntentKey.SELECT,2);
//                intent.putExtras(bundle);
//                break;
//        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      return intent;
    }
}
