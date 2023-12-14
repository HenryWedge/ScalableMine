package de.cau.se;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import de.cau.se.marker.Pattern;
import de.cau.se.model.MinedProcessModel;
import de.cau.se.processmodel.MediumProcessModel;
import de.cau.se.processmodel.ProcessModel;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/model")
public class PrecisionChecker {
    private AtomicDouble precision;

    private final ProcessModel originalProcessModel = new MediumProcessModel();

    public PrecisionChecker(MeterRegistry meterRegistry) {
        precision = meterRegistry.gauge("precision", new AtomicDouble(0.0d));
    }

    @PostMapping(path = "/update")
    public ResponseEntity<Object> update(@RequestBody final MinedProcessModel processModel) {
        Set<? super Pattern> minedModel = new HashSet<>();
        minedModel.addAll(processModel.getCausalEvents());
        minedModel.addAll(processModel.getChoiceGateways());
        minedModel.addAll(processModel.getParallelGateways());
        Set<? super Pattern> originalModel = new HashSet<>();
        originalModel.addAll(originalProcessModel.getCausalEvents());
        originalModel.addAll(originalProcessModel.getChoiceGateways());
        originalModel.addAll(originalProcessModel.getParallelGateways());
        precision.set(calculateModelToModelSimilarityWithJaccardScore(
                minedModel,
                originalModel));
        return ResponseEntity.ok().build();
    }

    public Double calculateModelToModelSimilarityWithJaccardScore(final Set<?> set1, Set<?> set2) {
        final Double intersectionSize = (double) Sets.intersection(set1, set2).size();
        final Double unionSize = (double) Sets.union(set1, set2).size();
        if (unionSize == 0d || unionSize.isInfinite() || unionSize.isNaN()) {
            return 0d;
        }
        return intersectionSize / unionSize;
    }


}
