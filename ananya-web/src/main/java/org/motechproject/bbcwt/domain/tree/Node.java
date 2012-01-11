package org.motechproject.bbcwt.domain.tree;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.bbcwt.domain.BaseCouchEntity;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

import java.util.*;

@TypeDiscriminator("doc.type == 'Node'")
public class Node extends MotechBaseDataObject {
//    @JsonProperty
//    private String documentType = "Node";

    @JsonProperty
    private String name;
    private List<Node> children;
    @JsonProperty
    private Map<String, Object> data;
    @JsonProperty
    private String parentId;

    public Node() {
        this(null);
    }

    public Node(String name) {
        this.name = name;
        this.children = new ArrayList<Node>();
        this.data = new HashMap<String, Object>();
    }

    public Node addChild(Node child) {
        children.add(child);
        child.parentId = getId();
        return this;
    }

    public Node put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public String getParentId() {
        return parentId;
    }

    public Map<String, Object> data() {
        return Collections.unmodifiableMap(this.data);
    }

    public List<Node> children() {
        return Collections.unmodifiableList(this.children);
    }

    public Node setParentId(String nodeId) {
        this.parentId = nodeId;
        return this;
    }
}