package de.cau.se;

import de.cau.se.datastructure.TaggedRelation;
import de.cau.se.model.EventRelationLogger;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.model.ModelUpdateService;
import de.cau.se.model.PrecisionChecker;
import de.cau.se.processmodel.ProcessModel;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AggregationSink extends AbstractConsumer<TaggedRelation> {

    private final Set<TaggedRelation> relationBuffer;

    private final ModelUpdateService modelUpdateService;

    private final PrecisionChecker precisionChecker;

    private final Integer refreshRate;

    private int n = 0;

    private final ProcessModel originalProcessModel;
    private final MinedProcessModel processModel;

    public AggregationSink(final Consumer<String, TaggedRelation> consumer,
                           final int refreshRate,
                           final ModelUpdateService modelUpdateService,
                           final EventRelationLogger eventRelationLogger,
                           final PrecisionChecker precisionChecker,
                           final ProcessModel originalProcessModel,
                           final MinedProcessModel minedProcessModel) {
        super(consumer);
        this.relationBuffer = new HashSet<>();
        this.refreshRate = refreshRate;
        this.modelUpdateService = modelUpdateService;
        this.eventRelationLogger = eventRelationLogger;
        this.precisionChecker = precisionChecker;
        this.processModel = minedProcessModel;
        this.originalProcessModel = originalProcessModel;
    }

    private final EventRelationLogger eventRelationLogger;

    @Override
    public void receive(final TaggedRelation relation) {
        n++;
        relationBuffer.add(relation);

        if (n % refreshRate == 0) {
            relationBuffer.forEach(
                    taggedRelation -> {
                        if (TaggedRelation.Tag.RELEVANT == taggedRelation.getTag()) {
                            modelUpdateService.update(processModel, taggedRelation.getDirectlyFollows());
                        } else {
                            modelUpdateService.remove(processModel, taggedRelation.getDirectlyFollows());
                        }
                    }
            );
            relationBuffer.clear();

            eventRelationLogger.logRelations(processModel);
            precisionChecker.calculatePrecision(originalProcessModel, processModel);
        }
    }
}
