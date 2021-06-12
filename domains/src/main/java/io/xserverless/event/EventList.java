package io.xserverless.event;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class EventList {
    private List<Event> list = new ArrayList<>();
}
