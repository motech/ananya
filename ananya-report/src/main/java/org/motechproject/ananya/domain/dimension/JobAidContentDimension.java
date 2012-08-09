package org.motechproject.ananya.domain.dimension;

import javax.persistence.*;


@Entity
@Table(name = "job_aid_content_dimension")
@NamedQuery(name = JobAidContentDimension.FIND_BY_CONTENT_ID, query = "select d from JobAidContentDimension d where d.contentId = :content_id")
public class JobAidContentDimension {

    public static final String FIND_BY_CONTENT_ID = "find.by.job.aid.content.id";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content_id")
    private String contentId;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private JobAidContentDimension parent;

    @Column(name = "name")
    private String name;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "type")
    private String type;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "short_code")
    private Long shortCode;

    public JobAidContentDimension() {
    }

    public JobAidContentDimension(String contentId, JobAidContentDimension parent,
                                  String name, String fileName, String type, Integer duration) {
        this.contentId = contentId;
        this.parent = parent;
        this.name = name;
        this.fileName = fileName;
        this.type = type;
        this.duration = duration;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getContentId() {
        return contentId;
    }

    public void setShortCode(Long shortCode) {
        this.shortCode = shortCode;
    }

    public String getName() {
        return name;
    }

    public JobAidContentDimension getParent() {
        return parent;
    }
}
