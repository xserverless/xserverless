package io.xserverless.function.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Command {
    default void log(String template, Object... args) {
        Logger logger = LoggerFactory.getLogger(getClass());
        if (logger.isDebugEnabled()) {
            logger.debug(template.replaceAll("([\\w\\s]+)[,)]", "$1=[{}]"), args);
        }
    }
}
