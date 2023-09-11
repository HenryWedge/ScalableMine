package io.spring.dataflow.sample.usagecostlogger;

import java.io.File;
import java.util.Collection;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.processmining.models.cnet.CNet;
import beamline.events.BEvent;
import beamline.exceptions.EventException;
import beamline.miners.hm.lossycounting.HeuristicsMinerLossyCounting;
import beamline.miners.hm.view.CNetSimplifiedModelView;
import plg.generator.ProgressAdapter;
import plg.generator.log.LogGenerator;
import plg.generator.log.SimulationConfiguration;
import plg.generator.process.ProcessGenerator;
import plg.generator.process.RandomizationConfiguration;
import plg.model.Process;

public class Test {

    //public static void main(String[] args) throws Exception {
    //    Process p = new Process("");
    //    ProcessGenerator.randomizeProcess(p, RandomizationConfiguration.BASIC_VALUES);
    //    LogGenerator logGenerator = new LogGenerator(p, new SimulationConfiguration(1000), new ProgressAdapter());
    //    final XLog xTraces = logGenerator.generateLog();
//
    //    HeuristicsMinerLossyCounting miner = new HeuristicsMinerLossyCounting(
    //        0.0001, // the maximal approximation error
    //        0.8, // the minimum dependency threshold
    //        10, // the positive observation threshold
    //        0.1 // the and threshold
    //    );
//
    //    xTraces
    //        .stream()
    //        .flatMap(Collection::stream)
    //        .forEach(event -> {
    //            try {
    //                miner.ingest(BEvent.create("process", "Bla", getActivityName(event)));
    //            } catch ( EventException e ) {
    //                throw new RuntimeException(e);
    //            }
    //        });
    //    final CNet cnet = miner
    //        .updateModel()
    //        .getCnet();
    //    new CNetSimplifiedModelView(cnet).exportToSvg(new File("output.svg"));
    //    System.out.println(cnet);
    //}

    static String getActivityName(XEvent event) {
        return event
            .getAttributes()
            .get("concept:name")
            .toString();
    }
}
