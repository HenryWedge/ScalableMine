package de.cau.se.model;

import de.cau.se.processmodel.ProcessModel;

import java.util.Set;

public class PrecisionChecker {

    public Double calculatePrecision(final ProcessModel expectedprocessModel, final ProcessModel minedProcessModel) {


        long causalMatches = countMatches(minedProcessModel.getCausalEvents(), expectedprocessModel.getCausalEvents());
        long parallelMatches = countMatches(minedProcessModel.getParallelGateways(), expectedprocessModel.getParallelGateways());
        long xorMatches = countMatches(minedProcessModel.getChoiceGateways(), expectedprocessModel.getChoiceGateways());

        final double precision = (double) (causalMatches + parallelMatches + xorMatches) /
                (expectedprocessModel.getCausalEvents().size()
                + expectedprocessModel.getParallelGateways().size()
                + expectedprocessModel.getChoiceGateways().size());
        System.out.printf("Precision: %.2f \n", precision);
        return precision;
    }

    private <T> long countMatches(final Set<T> actual, final Set<T> expected) {
        return actual.stream().filter(expected::contains).count();
    }
}
