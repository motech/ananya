package org.motechproject.bbcwt.domain.tree;

import org.motechproject.bbcwt.domain.BaseCouchEntity;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.*;

@TypeDiscriminator("doc.documentType == 'Node'")
public class Node extends BaseCouchEntity {
    @JsonProperty
    private String name;
    @JsonProperty
    private List<Node> children;
    @JsonProperty
    private Map<String, Object> data;

    public Node() {
        this(null);
    }

    public Node(String name) {
        this.name = name;
        this.children = new ArrayList<Node>(10);
        this.data = new HashMap<String, Object>(20);
    }

    public Node addChild(Node child){
        children.add(child);
        return this;
    }

    public Node put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, Object> data() {
        return Collections.unmodifiableMap(this.data);
    }

    public List<Node> children() {
        return Collections.unmodifiableList(this.children);
    }
}