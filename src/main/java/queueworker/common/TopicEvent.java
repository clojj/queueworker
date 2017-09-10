package queueworker.common;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TopicEvent implements Delayed {
    
    private Logger LOGGER = Logger.getLogger("TopicEvent");
    
    private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
    
    private Long start;
    private final long interval;
    
    private AtomicLong counter = new AtomicLong(0);
    
    
    public TopicEvent(long interval) {
        this.interval = interval;
    }
    
    public long inc() {
        long count = counter.incrementAndGet();
        LOGGER.log(Level.INFO, "inced " + this.toString());
        return count;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(startTime() - System.currentTimeMillis(), TIME_UNIT);
    }
    
    @Override
    public int compareTo(Delayed delayed) {
        TopicEvent otherTopicEvent = (TopicEvent) delayed;
        return startTime().compareTo(otherTopicEvent.startTime());
    }
    
    private Long startTime() {
        if (start == null) {
            start = System.currentTimeMillis() + interval;
        }
        return start;
    }
    
    @Override
    public String toString() {
        return "TopicEvent{" +
                "start=" + start +
                ", interval=" + interval +
                ", counter=" + counter +
                '}';
    }
}

