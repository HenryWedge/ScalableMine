import de.cau.se.AbstractProducer;
import de.cau.se.FilterProcessor;
import de.cau.se.FilterProcessorLossyCounting;
import de.cau.se.KafkaConsumer;
import de.cau.se.datastructure.DirectlyFollowsRelation;
import de.cau.se.datastructure.Event;
import de.cau.se.datastructure.Result;
import de.cau.se.map.directlyfollows.DirectlyFollowsRelationCountMap;
import de.cau.se.map.trace.TraceIdMap;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.openMocks;

public class FilterProcessorLossyCountingTest {

    private FilterProcessorLossyCounting testee;

    @Mock
    private AbstractProducer<Result> sender;
    @Mock
    private KafkaConsumer<Event> consumer;

    @Captor
    private ArgumentCaptor<ProducerRecord<String, Result>> producerRecordCaptor;

    @BeforeEach
    void prepare() {
        openMocks(this);
        testee = new FilterProcessorLossyCounting(sender, consumer, 3);
    }

    @Test
    void testProcessingStep() {
        testee.receive(new Event(1, "A"));
        testee.receive(new Event(1, "B"));
        testee.receive(new Event(1, "C"));

        Mockito.verify(sender, Mockito.atLeastOnce()).send(producerRecordCaptor.capture());
        ProducerRecord<String, Result> sendRecord = producerRecordCaptor.getValue();
        assertEquals(1, sendRecord.value().getCount());
        assertEquals(new DirectlyFollowsRelation("B", "C"), sendRecord.value().getDirectlyFollows());
    }
}
