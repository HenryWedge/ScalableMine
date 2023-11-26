package de.cau.se;

import com.google.common.util.concurrent.AtomicDouble;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PrecisionChecker {
    private AtomicDouble precision;

    public PrecisionChecker(MeterRegistry meterRegistry) {
        precision = meterRegistry.gauge("precison", new AtomicDouble(0.0d));
    }

    @Scheduled(fixedRateString = "1000", initialDelayString = "0")
    public void schedulingTask() {
        precision.set(1.0f);
    }

}
