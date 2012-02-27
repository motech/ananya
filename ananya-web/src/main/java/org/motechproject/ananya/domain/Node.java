package org.motechproject.ananya.domain;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.model.MotechBaseDataObject;

import java.util.*;

@TypeDiscriminator("doc.type == 'Node'")
public class Node extends MotechBaseDataObject {

    @JsonProperty
    private String name;
    @JsonProperty
    private Map<String, String> data;
    @JsonProperty
    private String parentId;
    @JsonProperty
    private List<String>  contentIds;

    private List<Node> children;
    private List<StringContent> contents;

    public Node() {
        this(null);
    }

    public Node(String name, Map<String, String> data, List<StringContent> contents, List<Node> children) {
        this.name = name;
        this.data = data;
        this.children = children;
        this.contents = contents;
        this.contentIds = new ArrayList<String>();
    }

    public Node(String name) {
        this.name = name;
        this.children = new ArrayList<Node>();
        this.data = new HashMap<String, String>();
        this.contents = new ArrayList<StringContent>();
        this.contentIds = new ArrayList<String>();
    }

    public Node addChild(Node child) {
        children.add(child);
        child.parentId = getId();
        return this;
    }

    public Node addContent(StringContent content) {
        contents.add(content);
        return this;
    }

    public List<StringContent> contents() {
        return Collections.unmodifiableList(contents);
    }

    public Node addContentId(String contentId) {
        contentIds.add(contentId);
        return this;
    }

    public List<String> contentIds() {
        return Collections.unmodifiableList(contentIds);
    }

    public Node put(String key, String value) {
        data.put(key, value);
        return this;
    }

    public String getName() {
        return this.name;
    }

    public String getParentId() {
        return parentId;
    }

    public Map<String, String> data() {
        return Collections.unmodifiableMap(this.data);
    }

    public void addData(String key, String value) {
        data.put(key, value);
    }

    public List<Node> children() {
        return Collections.unmodifiableList(this.children);
    }

    public Node setParentId(String nodeId) {
        this.parentId = nodeId;
        return this;
    }
}