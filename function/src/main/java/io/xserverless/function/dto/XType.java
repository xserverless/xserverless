package io.xserverless.function.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class XType implements XObject {
    private final String descriptor;

    XType(String descriptor) {
        this.descriptor = descriptor;
    }
}
