package io.xserverless.function;

import java.util.HashSet;
import java.util.Set;

public interface FunctionFilter {
    boolean allowedByAnnotationType(String annotation);

    Set<String> REQUEST_MAPPING_SET = new HashSet<String>() {
        {
            add("Lorg/springframework/web/bind/annotation/PostMapping;");
            add("Lorg/springframework/web/bind/annotation/GetMapping;");
            add("Lorg/springframework/web/bind/annotation/DeleteMapping;");
            add("Lorg/springframework/web/bind/annotation/PatchMapping;");
            add("Lorg/springframework/web/bind/annotation/PutMapping;");
            add("Lorg/springframework/web/bind/annotation/RequestMapping;");
        }
    };

    FunctionFilter REQUEST_MAPPING_FILTER = REQUEST_MAPPING_SET::contains;
}
