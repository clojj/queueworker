package queueworker.ejb;

import queueworker.common.QueueObject;
import queueworker.common.TopicEvent;
import queueworker.common.Topics;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class Worker {
    
    private static final int INITIAL_MINUTES = 1;
    
    private Logger LOGGER = Logger.getLogger("Worker");
    
    @Resource(lookup = "java:jboss/ee/concurrency/executor/queueworkerExecutorService")
    private ManagedExecutorService managedExecutorService;
    
    private DelayQueue<QueueObject> topicDelayQueue = new DelayQueue<>();
    
    @PostConstruct
    private void init() {
        
        // send initial TopicEvent
        boolean initialOffer = topicDelayQueue.offer(new QueueObject(INITIAL_MINUTES * 60 * 1000, new TopicEvent(Topics.TOPIC_A, new AtomicLong(0))));
        if (initialOffer) {
            LOGGER.log(Level.INFO, "Triggering initial event in " + INITIAL_MINUTES + " minutes");
        }
    
        managedExecutorService.execute(() -> {
            try {
                while (true) {
                    QueueObject queueObject = topicDelayQueue.take();
                    LOGGER.info("PROCESSING " + queueObject.toString());
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Interrupted: " + e.getMessage());
            }
        });
        LOGGER.info("Worker STARTED");
    }
    
    public boolean addEvent() {
        return topicDelayQueue.offer(new QueueObject(8000, new TopicEvent(Topics.TOPIC_A, new AtomicLong(0))));
    }

    public long addEventDebounced() {
        QueueObject queueObject = topicDelayQueue.peek();
        if (queueObject != null) {
            return queueObject.inc();
        } else {
            queueObject = new QueueObject(8000, new TopicEvent(Topics.TOPIC_A, new AtomicLong(0)));
            if (topicDelayQueue.offer(queueObject)) {
                LOGGER.info("Start new event " + queueObject.toString());
                return 0L;
            }
        }
        return -1;
    }
}
