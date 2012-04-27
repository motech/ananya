package org.motechproject.ananya.domain.dimension;

import javax.persistence.*;

@Entity
@Table(name = "job_aid_content_dimension")
public class JobAidContentDimension {

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
}
