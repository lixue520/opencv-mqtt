package com.example.opencvdemo.mqtt;

import com.example.opencvdemo.entity.IotData;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @Author qin
 * @Date 2023/3/12 0:56
 */
/**
 * MQTT接收消息
 */
@Service
public class MqttCaseServiceImpl implements MessageHandler {

    /**
     * MessageHeaders:
     * public static final String PREFIX = "mqtt_";
     * public static final String QOS = "mqtt_qos";
     * public static final String ID = "mqtt_id";
     * public static final String RECEIVED_QOS = "mqtt_receivedQos";
     * public static final String DUPLICATE = "mqtt_duplicate";
     * public static final String RETAINED = "mqtt_retained";
     * public static final String RECEIVED_RETAINED = "mqtt_receivedRetained";
     * public static final String TOPIC = "mqtt_topic";
     * public static final String RECEIVED_TOPIC = "mqtt_receivedTopic";
     * public static final String MESSAGE_EXPIRY_INTERVAL = "mqtt_messageExpiryInterval";
     * public static final String TOPIC_ALIAS = "mqtt_topicAlias";
     * public static final String RESPONSE_TOPIC = "mqtt_responseTopic";
     * public static final String CORRELATION_DATA = "mqtt_correlationData";
     */

    @Autowired
    Gson gson;


    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
        String payload = (String) message.getPayload();
        System.out.println("headers:" + topic + " 接收的数据:" + payload);
        if (topic.contains("receive_iot_topic")) {
            System.out.println("硬件的信息的主题");
            IotData entity = gson.fromJson(payload, IotData.class);
            if (entity!=null){
                //不是心跳数据
                if (!entity.getTypes().equals("heartbeat")) {
                    //判断硬件来源
                    if (entity.getTypes().equals("esp32")) {
                        System.out.println("来着esp32的数据");
                        //写入数据库 ...
                    }
                }
            }else {
                System.out.println("序列化失败");
            }

        }
        if (topic.contains("receive_chat_topic")) {
            System.out.println("对话的主题");
            //...构建聊天室.....
            //...相互订阅发送消息就可以了....
            //...逻辑代码...
        }
    }
}



