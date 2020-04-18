package com.netease.nim.uikit.business.contact.core.model;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.uikit.business.helper.SystemMessageUnreadManager;
import com.netease.nim.uikit.business.reminder.ReminderId;
import com.netease.nim.uikit.business.reminder.ReminderItem;
import com.netease.nim.uikit.business.reminder.ReminderManager;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.contact.core.item.AbsContactItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

public class FunctionViewHolder  extends BaseContactViewHolder implements ReminderManager.UnreadNumChangedCallback{

    private static ArrayList<WeakReference<ReminderManager.UnreadNumChangedCallback>> sUnreadCallbackRefs = new ArrayList<>();
    public FunctionViewHolder(View view) {
        super(view);
    }

    @Override
   public void convert(AbsContactItem item) {
        ImageView image=getView(R.id.img_head);
        if (item == FuncViewHolder.FuncItem.VERIFY) {
            setText(R.id.tv_func_name,"验证提醒");
            image.setImageResource(R.drawable.icon_verify_remind);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            int unreadCount = SystemMessageUnreadManager.getInstance().getSysMsgUnreadCount();
            updateUnreadNum(unreadCount);
            ReminderManager.getInstance().registerUnreadNumChangedCallback(this);
            sUnreadCallbackRefs.add(new WeakReference<ReminderManager.UnreadNumChangedCallback>(this));
        } else if (item == FuncViewHolder.FuncItem.ROBOT) {
            setText(R.id.tv_func_name,"智能机器人");
            image.setImageResource(R.drawable.ic_robot);
        } else if (item == FuncViewHolder.FuncItem.NORMAL_TEAM) {
            setText(R.id.tv_func_name,"讨论组");
            image.setImageResource(R.drawable.ic_secretary);
        } else if (item == FuncViewHolder.FuncItem.ADVANCED_TEAM) {
            setText(R.id.tv_func_name,"高级群");
            image.setImageResource(R.drawable.ic_advanced_team);
        } else if (item == FuncViewHolder.FuncItem.BLACK_LIST) {
            setText(R.id.tv_func_name,"黑名单");
            image.setImageResource(R.drawable.ic_black_list);
        }
//        else if (item == FuncViewHolder.FuncItem.MY_COMPUTER) {
//            setText(R.id.tv_func_name,"我的电脑");
//            image.setImageResource(R.drawable.ic_my_computer);
//        }

        if (item != FuncViewHolder.FuncItem.VERIFY) {
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            getView(R.id.tab_new_msg_label).setVisibility(View.GONE);
        }
    }
    private void updateUnreadNum(int unreadCount) {
        // 2.*版本viewholder复用问题
        if (unreadCount > 0 && ((TextView)getView(R.id.tv_func_name)).getText().toString().equals("验证提醒")) {
            getView(R.id.tab_new_msg_label).setVisibility(View.VISIBLE);
            setText(R.id.tab_new_msg_label,"" + unreadCount);
        } else {
            getView(R.id.tab_new_msg_label).setVisibility(View.GONE);
        }
    }
    public static void unRegisterUnreadNumChangedCallback() {
        Iterator<WeakReference<ReminderManager.UnreadNumChangedCallback>> iter = sUnreadCallbackRefs.iterator();
        while (iter.hasNext()) {
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(iter.next().get());
            iter.remove();
        }
    }
    @Override
    public void onUnreadNumChanged(ReminderItem item) {
        if (item.getId() != ReminderId.CONTACT) {
            return;
        }
        updateUnreadNum(item.getUnread());
    }
}
