package io.xserverless.cache;

import io.xserverless.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void sendEvent(Event event) {
        ObjectRecord<String, Event> record = StreamRecords.newRecord()
                .ofObject(event)
                .withStreamKey(Const.EVENT_STREAM);
        redisTemplate.opsForStream().add(record);
    }
}
