package com.cloudera.fts.flume.interceptors;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import java.util.List;


public class TimestampInterceptor implements Interceptor {

    private String field;
    private DateTimeFormatter formatter;

    public TimestampInterceptor(String field, DateTimeFormatter formatter) {
        this.field = field;
        this.formatter = formatter;
    }

    @Override
    public void initialize() {
        // NOP
    }

    @Override
    public Event intercept(Event event) {
        processEvent(event);
        return null;
    }

    @Override
    public List<Event> intercept(List<Event> list) {
        for(Event event : list) {
            processEvent(event);
        }
        return list;
    }

    @Override
    public void close() {
        // NOP
    }

    private void processEvent(Event event) {
        String input = event.getHeaders().get(field);
        DateTime date = formatter.parseDateTime(input);
        event.getHeaders().put("timestamp", date.getMillis() + "");
    }


    public static class Builder implements Interceptor.Builder {

        private String field;
        private DateTimeFormatter formatter;

        @Override
        public void configure(Context context) {
            field = context.getString("field");
            String pattern = context.getString("pattern");
            DateTimeParser[] parsers = { DateTimeFormat.forPattern(pattern).getParser() };
            formatter = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();
        }

        @Override
        public Interceptor build() {
            return new TimestampInterceptor(field, formatter);
        }

    }
}
