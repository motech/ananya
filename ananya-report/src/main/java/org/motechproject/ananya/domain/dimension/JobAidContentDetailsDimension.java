package org.motechproject.ananya.domain.dimension;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity

@Table(name = "job_aid_content_details_dimension")
@NamedQueries({
        @NamedQuery(name = JobAidContentDetailsDimension.FIND_BY_CONTENT_ID_AND_LANGUAGE_ID, query = "select d from JobAidContentDetailsDimension d where d.contentId=:contentId and d.languageId=:languageId"),
})

public class JobAidContentDetailsDimension {
    public static final String FIND_BY_CONTENT_ID_AND_LANGUAGE_ID = "find.by.job.aid.content.id.and.language.id";
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "content_id")
    private String contentId;

    @Column(name = "language_id")
    private Integer languageId;
    
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "duration")
    private Integer duration;

    public JobAidContentDetailsDimension() {
    }

    public JobAidContentDetailsDimension(Integer languageId, String contentId, String fileName, Integer duration) {
        this.languageId = languageId;
        this.contentId = contentId;
        this.fileName = fileName;
        this.duration = duration;
    }

	public Integer getId() {
		return id;
	}

	public String getContentId() {
		return contentId;
	}

	public Integer getLanguageId() {
		return languageId;
	}

	public String getFileName() {
		return fileName;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

}
