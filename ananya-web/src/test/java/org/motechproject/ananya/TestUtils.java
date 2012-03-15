package org.motechproject.ananya;

import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;

public class TestUtils {
    
    public static FrontLineWorker getSampleFLW() {
        return new FrontLineWorker("9986574000", Designation.ANGANWADI, null, null);
    }
}
