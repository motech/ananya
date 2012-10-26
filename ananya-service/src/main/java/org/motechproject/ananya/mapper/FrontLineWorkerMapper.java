package org.motechproject.ananya.mapper;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
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
}
