package org.motechproject.ananya.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ananya.domain.VerificationStatus;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.response.FrontLineWorkerResponse;

public class FrontLineWorkerMapper {
    public static FrontLineWorkerResponse mapFrom(FrontLineWorkerDimension frontLineWorkerDimension) {
        VerificationStatus verificationStatus = frontLineWorkerDimension.getVerificationStatus();
        return new FrontLineWorkerResponse(frontLineWorkerDimension.getMsisdn().toString(),
                frontLineWorkerDimension.getName(),
                frontLineWorkerDimension.getStatus(),
                frontLineWorkerDimension.getDesignation(),
                frontLineWorkerDimension.getOperator(),
                frontLineWorkerDimension.getCircle(),
                verificationStatus == null ? StringUtils.EMPTY : verificationStatus.name());
    }
}
