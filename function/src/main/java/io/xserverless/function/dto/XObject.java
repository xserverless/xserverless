package io.xserverless.function.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.objectweb.asm.Type;

@Getter
@EqualsAndHashCode
@ToString
public class XObject {
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

    private final String type;

    public XObject(String owner, String name, String descriptor) {
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;

        if (descriptor.contains("(")) {
            type = "function";
        }else{
            type = "state";
        }
    }

    public XObject(String descriptor) {
        this.owner = null;
        this.name = Type.getType(descriptor).getInternalName();
        this.descriptor = descriptor;
        type = "type";
    }

    public boolean isFunction() {
        return "function".equals(type);
    }
    public boolean isState() {
        return "state".equals(type);
    }
    public boolean isType() {
        return "type".equals(type);
    }
}
