package queueworker.ejb;

import queueworker.common.TopicEvent;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import java.util.concurrent.DelayQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class Worker {
    
    private static final int INITIAL_MINUTES = 2;
    
    private Logger LOGGER = Logger.getLogger("Worker");
    
    @Resource(lookup = "java:jboss/ee/concurrency/executor/queueworkerExecutorService")
    private ManagedExecutorService managedExecutorService;
    
    private DelayQueue<TopicEvent> topicDelayQueue = new DelayQueue<>();
    
    @PostConstruct
    private void init() {
        
        // send initial TopicEvent
        boolean initialOffer = topicDelayQueue.offer(new TopicEvent(INITIAL_MINUTES * 60 * 1000));
        if (initialOffer) {
            LOGGER.log(Level.INFO, "Triggering initial event in " + INITIAL_MINUTES + " minutes");
        }
    
        managedExecutorService.execute(() -> {
            try {
                while (true) {
                    TopicEvent topicEvent = topicDelayQueue.take();
                    LOGGER.info("PROCESSING " + topicEvent.toString());
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Interrupted: " + e.getMessage());
            }
        });
        LOGGER.info("Worker STARTED");
    }
    
    public boolean addEvent() {
        return topicDelayQueue.offer(new TopicEvent(8000));
    }

    public long addEventDebounced() {
        TopicEvent topicEvent = topicDelayQueue.peek();
        if (topicEvent != null) {
            return topicEvent.inc();
        } else {
            topicEvent = new TopicEvent(8000);
            if (topicDelayQueue.offer(topicEvent)) {
                LOGGER.info("Start new event " + topicEvent.toString());
                return 0L;
            }
        }
        return -1;
    }
}
