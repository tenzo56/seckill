package com.tenzo.seckill.mq;

import com.tenzo.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class MQConsumer {

    private final String queueName = "seckill";

    @Autowired
    private UserService userService;
    public void receive() {
        MQueue queue = QueueManager.getQueue(queueName);
        while (true) {
            Integer i = queue.get();
            userService.purchase(i);
        }
    }
}
