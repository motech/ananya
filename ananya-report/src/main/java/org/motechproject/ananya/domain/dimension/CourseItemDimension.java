package org.motechproject.ananya.domain.dimension;

import org.motechproject.ananya.domain.CourseItemType;

import javax.persistence.*;

@Entity

@Table(name = "course_item_dimension")
@NamedQueries({
        @NamedQuery(name = CourseItemDimension.FIND_BY_NAME_AND_TYPE, query = "select cid from CourseItemDimension cid where cid.name=:name and cid.type=:type"),
        @NamedQuery(name = CourseItemDimension.FIND_BY_CONTENT_ID, query = "select cid from CourseItemDimension cid where cid.contentId=:contentId")
})

public class CourseItemDimension {

    public static final String FIND_BY_NAME_AND_TYPE = "find.by.name.and.type";
    public static final String FIND_BY_CONTENT_ID = "find.by.certificate.course.content.id";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "content_id")
    private String contentId;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private CourseItemDimension parent;

    @Column(name = "type")
    private String type;

    public CourseItemDimension() {
    }

    public CourseItemDimension(String name, String contentId, CourseItemType type, CourseItemDimension parent) {
        this.name = name;
        this.contentId = contentId;
        this.type = String.valueOf(type);
        this.parent = parent;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContentId() {
        return contentId;
    }

    public CourseItemType getType() {
        return CourseItemType.valueOf(type);
    }

    public CourseItemDimension getParent() {
        return parent;
    }

    public void setParentDimension(CourseItemDimension parentDimension) {
        parent = parentDimension;
    }
}
