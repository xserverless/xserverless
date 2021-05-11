package io.xserverless.function.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

/**
 * Java methods
 */
@Data
@Builder
public class Function {
    /**
     * id = owner.name.descriptor
     */
    private String id;
    /**
     * owner class. <br>
     * eg: java/lang/Object
     */
    private String owner;
    /**
     * Java method name. <br>
     * eg: desc <br>
     * eg: &lt;init&gt;
     */
    private String name;
    /**
     * Java method descriptor. <br>
     * eg: ([Ljava/lang/String;)V = void desc(String[] arg)
     */
    private String descriptor;
    /**
     * id of dependent functions
     */
    private Set<String> relatedFunctions;
    /**
     * name of annotations type
     */
    private Set<String> relatedAnnotations;
    /**
     * id of dependent fields
     */
    private Set<State> relatedStates;
    /**
     * name of java types
     */
    private Set<String> relatedTypes;

    public String toString() {
        StringBuilder str = new StringBuilder("id:" + id +
                "\nowner:" + owner +
                "\nname:" + name +
                "\ndescriptor:" + descriptor);
        str.append("\nfunctions:\n");
        relatedFunctions.forEach(s -> str.append("\t").append(s).append("\n"));
        str.append("annotations:\n");
        relatedAnnotations.forEach(s -> str.append("\t").append(s).append("\n"));
        str.append("states:\n");
        relatedStates.forEach(s -> str.append("\t").append(s).append("\n"));
        str.append("types:\n");
        relatedTypes.forEach(s -> str.append("\t").append(s).append("\n"));

        return str.toString();
    }
}
