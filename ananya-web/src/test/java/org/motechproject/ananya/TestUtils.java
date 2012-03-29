package org.motechproject.ananya;

import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;

public class TestUtils {
    
    public static FrontLineWorker getSampleFLW() {
        return new FrontLineWorker("9986574000","name", Designation.ANGANWADI, new Location(), RegistrationStatus.REGISTERED);
    }
}
