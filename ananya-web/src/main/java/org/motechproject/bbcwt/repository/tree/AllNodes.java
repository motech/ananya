package org.motechproject.bbcwt.repository.tree;

import com.google.gson.Gson;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.bbcwt.domain.tree.Node;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class AllNodes extends MotechBaseRepository<Node> {
    @Autowired
    public AllNodes(@Qualifier("ananyaDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(Node.class, dbCouchDbConnector);
    }

    @GenerateView
    public Node findByName(String treeName) {
        Node rootNode = findNode(treeName, "by_name");
        return addDescendants(rootNode);
    }

    @GenerateView
    private List<Node> findByParentId(String parentId) {
        return queryView("by_parentId", parentId);
    }

    @Override
    public Node get(String id) {
        Node node = super.get(id);
        return addDescendants(node);
    }

    private Node addDescendants(Node rootNode) {
        List<Node> children = findByParentId(rootNode.getId());
        if (children.size() != 0) {
            for (Node childNode : children) {
                rootNode.addChild(childNode);
                addDescendants(childNode);
            }
        }
        return rootNode;
    }

    private Node findNode(String nodeName, String viewName) {
        List<Node> nodes = queryView(viewName, nodeName);
        if (nodes != null && !nodes.isEmpty()) {
            return nodes.get(0);
        }
        return null;
    }

    public String nodeAsJson(String treeName) throws IOException {
        Node node = findByName(treeName);
        Gson gson = new Gson();
        return gson.toJson(node);
    }
}