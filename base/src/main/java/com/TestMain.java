package com;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jogger on 2019/12/27
 * 描述：
 */
public class TestMain {
    public static void main(String[] args) {
        ListNode listNode = new ListNode(1);
        ListNode listNode1 = new ListNode(0);
        ListNode listNode2 = new ListNode(1);
//        ListNode listNode3 = new ListNode(1);
//        ListNode listNode4 = new ListNode(0);
//        ListNode listNode5 = new ListNode(1);
        listNode.next = listNode1;
        listNode1.next = listNode2;
//        listNode2.next = listNode3;
//        listNode3.next = listNode4;
//        listNode4.next = listNode5;
        System.out.println("------num:" + getDecimalValue(listNode));
    }

    public static int getDecimalValue(ListNode head) {
        List<Integer> list = new ArrayList<>();
        int count = 1;
        ListNode listNode = head;
        list.add(head.val);
        while (listNode.next != null) {
            count++;
            listNode = listNode.next;
            list.add(listNode.val);
        }
        int num = 0;
        for (int i = 0; i < count; i++) {
            if (list.get(i) != 0)
                num += Math.pow(2, count - i - 1);
        }
        return num;
    }

    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }
}
