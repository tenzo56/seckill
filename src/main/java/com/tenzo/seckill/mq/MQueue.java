package com.tenzo.seckill.mq;

import java.util.LinkedList;

public class MQueue {
    private LinkedList<Integer> queue = new LinkedList<>();

    public void put(Integer i) {
        synchronized (queue) {
            if (queue.isEmpty()) {
                queue.notifyAll();
            }
            queue.push(i);
        }
    }

    public Integer get() {
        synchronized (queue) {
            if (queue.isEmpty()) {
                try {
                    queue.wait();
                    //等待被唤醒后获取事务消息
                    Integer i = queue.pollFirst();
                    return i;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Integer i = queue.pollFirst();
                return i;
            }
        }
        return null;
    }
}
