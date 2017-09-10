package queueworker.common

import java.util.concurrent.atomic.AtomicLong

data class TopicEvent(val topic: String, val count: AtomicLong)