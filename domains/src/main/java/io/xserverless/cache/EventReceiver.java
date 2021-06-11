package io.xserverless.cache;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.xserverless.event.Event;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

public abstract class EventReceiver implements StreamListener<String, ObjectRecord<String, Event>> {
    public EventReceiver(StringRedisTemplate redisTemplate, StreamMessageListenerContainer<String, ObjectRecord<String, Event>> container) {
        try {
            redisTemplate.opsForStream().createGroup(Const.EVENT_STREAM, Const.EVENT_STREAM);
        } catch (RedisSystemException ignore) {
            // ignore
        }
        try {
            container.receiveAutoAck(
                    Consumer.from(Const.EVENT_STREAM, InetAddress.getLocalHost().getHostName()),
                    StreamOffset.create(Const.EVENT_STREAM, ReadOffset.lastConsumed()),
                    this);
            container.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    protected abstract void onMessage(Event event);

    @Override
    public void onMessage(ObjectRecord<String, Event> message) {
        try {
            onMessage(message.getValue());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
