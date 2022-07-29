package com.redsource.distributed.system.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tyro.oss.logtesting.logback.LogbackCaptor;

import java.util.List;
import java.util.stream.Collectors;

public class LogHelper {

    public static List<ILoggingEvent> getlogs(final Level level, final LogbackCaptor logbackCaptor) {
        return logbackCaptor.getEvents().stream().filter(iLoggingEvent -> iLoggingEvent.getLevel().equals(level))
                .collect(Collectors.toList());
    }

}
