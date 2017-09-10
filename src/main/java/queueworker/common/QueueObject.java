package queueworker.common;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueueObject implements Delayed {
    
    private Logger LOGGER = Logger.getLogger("TopicEvent");
    
    private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;
    
    private Long start;
    private final long interval;
    
    // Kotlin Data Class
    private TopicEvent topicEvent;
    
    public QueueObject(long interval, TopicEvent topicEvent) {
        this.interval = interval;
        this.topicEvent = topicEvent;
    }
    
    public long inc() {
        long count = topicEvent.getCount().incrementAndGet();
        LOGGER.log(Level.INFO, "inced " + this.toString());
        return count;
    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        return unit.convert(startTime() - System.currentTimeMillis(), TIME_UNIT);
    }
    
    @Override
    public int compareTo(@NotNull Delayed delayed) {
        QueueObject otherQueueObject = (QueueObject) delayed;
        return startTime().compareTo(otherQueueObject.startTime());
    }
    
    private Long startTime() {
        if (start == null) {
            start = System.currentTimeMillis() + interval;
        }
        return start;
    }
    
    @Override
    public String toString() {
        return "QueueObject{" +
                "start=" + start +
                ", interval=" + interval +
                ", topicEvent=" + topicEvent +
                '}';
    }
}

