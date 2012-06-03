package org.motechproject.ananya.mapper;

import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.request.FrontLineWorkerRequest;
import org.motechproject.ananya.response.FrontLineWorkerResponse;

public class FrontLineWorkerMapper {
    public static FrontLineWorkerResponse mapFrom(FrontLineWorkerDimension frontLineWorkerDimension) {
        return new FrontLineWorkerResponse(frontLineWorkerDimension.getMsisdn().toString(),
                frontLineWorkerDimension.getName(),
                frontLineWorkerDimension.getStatus(),
                frontLineWorkerDimension.getDesignation(),
                frontLineWorkerDimension.getOperator(),
                frontLineWorkerDimension.getCircle());
    }

    public static FrontLineWorker mapFrom(FrontLineWorkerRequest frontLineWorkerRequest) {
        return new FrontLineWorker(frontLineWorkerRequest.getMsisdn(),
                frontLineWorkerRequest.getName(),
                Designation.getFor(frontLineWorkerRequest.getDesignation()),
                frontLineWorkerRequest.getOperator(),
                frontLineWorkerRequest.getCircle(),
                new Location(frontLineWorkerRequest.getLocation().getDistrict(), frontLineWorkerRequest.getLocation().getBlock(), frontLineWorkerRequest.getLocation().getPanchayat(), 0, 0, 0),
                RegistrationStatus.PARTIALLY_REGISTERED);
    }
}
