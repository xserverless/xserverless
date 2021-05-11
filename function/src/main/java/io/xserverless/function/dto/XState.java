package io.xserverless.function.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Java field
 */
@Getter
@EqualsAndHashCode
@ToString
public class XState implements XObject {
    private final String name;
    private final String owner;
    private final String descriptor;

    XState(String owner, String name, String descriptor) {
        this.name = name;
        this.owner = owner;
        this.descriptor = descriptor;
    }
}
