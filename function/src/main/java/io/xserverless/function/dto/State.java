package io.xserverless.function.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Java field
 */
@Data
@Builder
@EqualsAndHashCode
public class State {
    private String name;
    private String owner;
    private String descriptor;
}
