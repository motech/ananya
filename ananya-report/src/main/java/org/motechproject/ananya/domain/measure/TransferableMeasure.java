package org.motechproject.ananya.domain.measure;

import org.motechproject.ananya.domain.dimension.FrontLineWorkerDimension;
import org.motechproject.ananya.domain.dimension.FrontLineWorkerHistory;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class TransferableMeasure {

    @Column(name = "flw_id", updatable = false, insertable = false)
    private Integer flwId;

    @ManyToOne
    @JoinColumn(name = "flw_id", nullable = false)
    protected FrontLineWorkerDimension frontLineWorkerDimension;

    @Column(name = "flw_history_id")
    private Integer flwHistoryId;

    public Integer flwId() {
        return frontLineWorkerDimension.getId();
    }

    public void addFlwHistory(FrontLineWorkerHistory frontLineWorkerHistory) {
        this.flwHistoryId = frontLineWorkerHistory.id();
    }
}
