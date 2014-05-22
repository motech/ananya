package org.motechproject.ananya.matcher;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.motechproject.ananya.domain.Node;

import java.util.List;

public class NodeDeepMatcher extends BaseMatcher<Node> {
    private Node expectedNode;
    private String description;

    public NodeDeepMatcher(Node expectedNode) {
        this.expectedNode = expectedNode;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.description);
    }

    @Override
    public boolean matches(Object otherNode) {
        if (otherNode instanceof Node) {
            Node nodeUnderTest = (Node) otherNode;
            return compareRecursively(expectedNode, nodeUnderTest);
        }
        return false;
    }

    private boolean compareRecursively(Node expectedNode, Node nodeUnderTest) {
        if (!StringUtils.equals(expectedNode.getName(), nodeUnderTest.getName())) {
            this.description = String.format("Node getName did not match, expected: %s, actual: %s", expectedNode.getName(), nodeUnderTest.getName());
            return false;
        }

        if (!expectedNode.data().keySet().equals(nodeUnderTest.data().keySet())) {
            this.description = String.format("Node %s does not contain same data in actual and expected. \nExpected: %s\nActual: %s", expectedNode.getName(), expectedNode.data(), nodeUnderTest.data());
            return false;
        }

        List<Node> childrenInExpectedNode = expectedNode.children();
        List<Node> childrenInActualNode = nodeUnderTest.children();

        int expectedNumberOfChildren = childrenInExpectedNode.size(), actualNumberOfChildren = childrenInActualNode.size();

        if (expectedNumberOfChildren != actualNumberOfChildren) {
            this.description = String.format("Number of children in node %s does not match. Expected: %d, Actual: %d", expectedNode.getName(), expectedNumberOfChildren, actualNumberOfChildren);
            return false;
        }

        for (int i = 0; i < expectedNumberOfChildren; i++) {
            boolean lastNodesComparedAreEqual =
                    compareRecursively(childrenInExpectedNode.get(i), childrenInActualNode.get(i));
            if (!lastNodesComparedAreEqual) {
                return false;
            }
        }
        return true;
    }

    public static Matcher<Node> isSameAsNodeRepresentedBy(final Node expectedNode) {
        return new NodeDeepMatcher(expectedNode);
    }

}


