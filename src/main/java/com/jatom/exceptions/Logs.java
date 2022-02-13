package com.jatom.exceptions;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.message.MessageFactory;

public class Logs extends Logger {
    protected Logs(LoggerContext context, String name, MessageFactory messageFactory) {
        super(context, name, messageFactory);
    }
}
