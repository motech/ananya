package org.motechproject.ananya.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.ananya.domain.Node;
import org.motechproject.ananya.util.gson.AttributeExclusionDeterminer;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.repository.AllStringContents;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AllNodes extends MotechBaseRepository<Node> {
    private AllStringContents allStringContents;
    private static Gson GSON;
    private ConcurrentHashMap<String, String> cachedTreeJsons = new ConcurrentHashMap();

    static {
        GsonBuilder gsonBuilder = new GsonBuilder().addSerializationExclusionStrategy(new AttributeExclusionDeterminer("id", "revision", "parentId", "contentIds"));
        GSON = gsonBuilder.create();
    }

    @Autowired
    public AllNodes(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector, AllStringContents allStringContents) {
        super(Node.class, dbCouchDbConnector);
        this.allStringContents = allStringContents;
    }

    @GenerateView
    public Node findByName(String treeName) {
        Node rootNode = findNode(treeName, "by_name");
        return assembleDescendantsAndContent(rootNode);
    }

    @GenerateView
    private List<Node> findByParentId(String parentId) {
        return queryView("by_parentId", parentId);
    }

    @Override
    public Node get(String id) {
        Node node = super.get(id);
        return assembleDescendantsAndContent(node);
    }

    private Node assembleDescendantsAndContent(Node node) {
        List<Node> children = findByParentId(node.getId());
        if (children.size() != 0) {
            for (Node childNode : children) {
                node.addChild(childNode);
                assembleDescendantsAndContent(childNode);
            }
        }
        addContentToNode(node);
        return node;
    }

    private void addContentToNode(Node node) {
        for(String contentId : node.contentIds()) {
            node.addContent(allStringContents.get(contentId));
        }
    }

    private Node findNode(String nodeName, String viewName) {
        List<Node> nodes = queryView(viewName, nodeName);
        if (nodes != null && !nodes.isEmpty()) {
            return nodes.get(0);
        }
        return null;
    }

    public String nodeAsJson(String treeName) throws IOException {
        String nodeAsJson = cachedTreeJsons.get(treeName);
        if(nodeAsJson == null){
             Node node = findByName(treeName);
             nodeAsJson = GSON.toJson(node);
             cachedTreeJsons.putIfAbsent(treeName, nodeAsJson);
        }
        return nodeAsJson;
    }

    public String nodeWithoutChildrenAsJson(String nodeName) throws  IOException {
        final String mapKey = nodeName + "_without_children";
        String nodeAsJson = cachedTreeJsons.get(mapKey);
        if(nodeAsJson == null){
             Node node = findNode(nodeName, "by_name");
             addContentToNode(node);
             nodeAsJson = GSON.toJson(node);
             cachedTreeJsons.putIfAbsent(mapKey, nodeAsJson);
        }
        return nodeAsJson;
    }

    public void addNodeWithDescendants(Node rootNode) {
        recursivelyAddNodeWithDescendants(rootNode);
    }

    private void recursivelyAddNodeWithDescendants(Node node) {
        for(StringContent stringContentToSave : node.contents()){
            allStringContents.add(stringContentToSave);
            node.addContentId(stringContentToSave.getId());
        }
        add(node);
        final String nodeId = node.getId();
        final List<Node> children = node.children();
        for(Node childNode : children){
            childNode.setParentId(nodeId);
            recursivelyAddNodeWithDescendants(childNode);
        }
    }
}