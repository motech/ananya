package org.motechproject.ananya.seed.domain;

import org.motechproject.ananya.domain.FrontLineWorker;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FrontLineWorkerList {

    private List<FrontLineWorker> frontLineWorkers;
    private Comparator<FrontLineWorker> comparator;

    public FrontLineWorkerList(List<FrontLineWorker> frontLineWorkers) {
        this.frontLineWorkers = frontLineWorkers;
        this.comparator = getComparator();
        Collections.sort(frontLineWorkers, comparator);
    }

    public FrontLineWorker findLongCodeDuplicate(String msisdn) {
        int i = Collections.binarySearch(frontLineWorkers, new FrontLineWorker("91" + msisdn, ""), comparator);
        return i > 0 ? frontLineWorkers.get(i) : null;
    }

    private Comparator<FrontLineWorker> getComparator() {
        return new Comparator<FrontLineWorker>() {
            @Override
            public int compare(FrontLineWorker frontLineWorker, FrontLineWorker frontLineWorker1) {
                return frontLineWorker.msisdn().compareTo(frontLineWorker1.msisdn());
            }
        };
    }
}
