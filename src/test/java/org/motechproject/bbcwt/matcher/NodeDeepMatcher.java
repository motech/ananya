package org.motechproject.bbcwt.matcher;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.motechproject.bbcwt.domain.tree.Node;

import java.util.List;

public class NodeDeepMatcher extends BaseMatcher<Node> {
    private Node actualNode;
    private String description;

    public NodeDeepMatcher(Node actualNode) {
        this.actualNode = actualNode;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.description);
    }

    @Override
    public boolean matches(Object otherNode) {
        if (otherNode instanceof Node) {
            Node nodeToCompare = (Node) otherNode;
            return compareRecursively(actualNode, nodeToCompare);
        }
        return false;
    }

    private boolean compareRecursively(Node actualNode, Node expectedNode) {
        if (!StringUtils.equals(actualNode.getName(), expectedNode.getName())) {
            this.description = String.format("Node getName did not match, actual: %s, expected: %s", actualNode.getName(), expectedNode.getName());
            return false;
        }

        if (!actualNode.data().equals(expectedNode.data())) {
            this.description = String.format("Node %s does not contain same data in actual and expected. \nActual: %s\nExpected: %s", actualNode.getName(), actualNode.data(), expectedNode.data());
            return false;
        }

        List<Node> childrenInActualNode = actualNode.children();
        List<Node> childrenInNodeToCompare = expectedNode.children();

        int actualNoOfChildren = childrenInActualNode.size(), expectedNumberOfChildren = childrenInNodeToCompare.size();

        if (actualNoOfChildren != expectedNumberOfChildren) {
            this.description = String.format("Number of children in node %s does not match. Actual: %d, Expected: %d", actualNode.getName(), actualNoOfChildren, expectedNumberOfChildren);
            return false;
        }

        for (int i = 0; i < actualNoOfChildren; i++) {
            boolean lastNodesComparedAreEqual =
                    compareRecursively(childrenInActualNode.get(i), childrenInNodeToCompare.get(i)
                    );
            if (!lastNodesComparedAreEqual) {
                return false;
            }
        }
        return true;
    }

    public static Matcher<Node> isSameAsNodeRepresentedBy(final Node actualNode) {
        return new NodeDeepMatcher(actualNode);
    }

}
