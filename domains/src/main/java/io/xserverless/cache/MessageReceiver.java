package io.xserverless.cache;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;


public abstract class MessageReceiver implements MessageListener {

    public MessageReceiver(RedisMessageListenerContainer container, String topic) {
        container.addMessageListener(new MessageListenerAdapter(this), new ChannelTopic(topic));
    }

    private Logger log = LoggerFactory.getLogger(MessageReceiver.class);

    protected abstract void onMessage(String channel, String message);

    @Override
    public void onMessage(Message message, byte[] pattern) {

        byte[] channel = message.getChannel();
        byte[] body = message.getBody();
        try {
            String title = new String(channel, "UTF-8");
            String content = new String(body, "UTF-8");
            onMessage(title, content);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
