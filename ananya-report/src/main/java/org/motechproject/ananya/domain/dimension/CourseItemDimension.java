package org.motechproject.ananya.domain.dimension;

import org.motechproject.ananya.domain.CourseItemType;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "course_item_dimension")
@NamedQuery(name = CourseItemDimension.FIND_BY_NAME, query = "select cid from CourseItemDimension cid where cid.name=:name")
public class CourseItemDimension {

    public static final String FIND_BY_NAME = "find.by.name";

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name")
    private String name;

    @Column(name="type")
    private String type;

    public CourseItemDimension() {
    }

    public CourseItemDimension(String name, CourseItemType type) {
        this.name = name;
        this.type = String.valueOf(type);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CourseItemType getType() {
        return CourseItemType.valueOf(type);
    }
}
