package org.motechproject.ananya.performance.framework;

import java.util.concurrent.Callable;

public class DataSetupTask implements Callable {

    private DataSetupMethod dataSetupMethod;

    public DataSetupTask(DataSetupMethod dataSetupMethod) {
        this.dataSetupMethod = dataSetupMethod;
    }

    @Override
    public Object call() throws Exception {
        dataSetupMethod.run();
        return dataSetupMethod.description() + " completed";
    }
}
