package org.motechproject.ananya.repository.dimension;

import org.motechproject.ananya.domain.dimension.OperatorDimension;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class AllOperatorDimensions {
    @Autowired
    private DataAccessTemplate template;

    public AllOperatorDimensions() {
    }

    public void add(OperatorDimension operatorDimension) {
        template.save(operatorDimension);
    }
}
