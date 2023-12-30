package de.cau.se.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import de.cau.se.marker.Pattern;
import de.cau.se.processmodel.ProcessModel;
import de.cau.se.processmodel.SmallProcessModel;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

/**
 * Calculates the similarity of two process models with the Jaccard coefficient.
 * It sends requests to the precision monitor server when it is enabled.
 */
public class PrecisionChecker {

    private final ObjectMapper objectMapper;

    final boolean usePrecisionMonitoring;

    final String precisionMonitoringUrl;

    public PrecisionChecker(boolean usePrecisionMonitoring, String precisionMonitoringUrl) {
        this.objectMapper = new ObjectMapper();
        this.usePrecisionMonitoring = usePrecisionMonitoring;
        this.precisionMonitoringUrl = precisionMonitoringUrl;
    }

    public void calculatePrecision(final ProcessModel expectedprocessModel, final ProcessModel minedProcessModel) {
        if (usePrecisionMonitoring) {
            sendPostRequestToPrecisionMonitor(minedProcessModel, precisionMonitoringUrl);
        } else {
            Set<? super Pattern> minedModel = new HashSet<>();
            minedModel.addAll(minedProcessModel.getCausalEvents());
            minedModel.addAll(minedProcessModel.getChoiceGateways());
            minedModel.addAll(minedProcessModel.getParallelGateways());
            Set<? super Pattern> originalModel = new HashSet<>();
            originalModel.addAll(expectedprocessModel.getCausalEvents());
            originalModel.addAll(expectedprocessModel.getChoiceGateways());
            originalModel.addAll(expectedprocessModel.getParallelGateways());
            Double precision = calculateModelToModelSimilarityWithJaccardScore(minedModel, originalModel);
            System.out.printf("Precision: %.2f \n", precision);
        }
    }

    public Double calculateModelToModelSimilarityWithJaccardScore(final Set<?> set1, Set<?> set2) {
        final Double intersectionSize = (double) Sets.intersection(set1, set2).size();
        final Double unionSize = (double) Sets.union(set1, set2).size();
        if (unionSize == 0d || unionSize.isInfinite() || unionSize.isNaN()) {
            return 0d;
        }
        return intersectionSize / unionSize;
    }

    private void sendPostRequestToPrecisionMonitor(ProcessModel minedProcessModel, String precisionMonitoringUrl) {
        HttpPost request = new HttpPost(precisionMonitoringUrl);
        try {
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(minedProcessModel)));
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(request)) {
                System.out.println(response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (UnsupportedEncodingException | JsonProcessingException  e) {
            // ignore: lost updates are acceptable
        }
    }
}
