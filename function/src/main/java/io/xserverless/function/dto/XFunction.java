package io.xserverless.function.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Java methods
 */
@Getter
@EqualsAndHashCode
@ToString
public class XFunction implements XObject {
    /**
     * owner class. <br>
     * eg: java/lang/Object
     */
    private final String owner;
    /**
     * Java method name. <br>
     * eg: desc <br>
     * eg: &lt;init&gt;
     */
    private final String name;
    /**
     * Java method descriptor. <br>
     * eg: ([Ljava/lang/String;)V = void desc(String[] arg)
     */
    private final String descriptor;

    XFunction(String owner, String name, String descriptor) {
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
    }
}
