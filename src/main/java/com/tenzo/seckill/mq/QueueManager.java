package com.tenzo.seckill.mq;

import java.util.HashMap;
import java.util.Map;

public class QueueManager {
    private static Map<String , MQueue> queueManager = new HashMap<>();

    public static MQueue getQueue(String queueName) {
        synchronized (queueName) {
            MQueue queue = queueManager.get(queueName);
            if (queue == null) {
                queue = new MQueue();
                putQueue(queueName, queue);
                return queue;
            }
            return queue;
        }
    }

    public static void putQueue(String queueName, MQueue mq) {
        queueManager.put(queueName, mq);
    }
}
