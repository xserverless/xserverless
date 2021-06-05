package io.xserverless.function;

import io.xserverless.function.dto.XObject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class XEntry {
    private XObject method;
    private byte[] file;
    private String urlPattern;
}
