package org.motechproject.bbcwt.repository.tree;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.domain.tree.Node;
import org.motechproject.bbcwt.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class NodeRepositoryTest extends SpringIntegrationTest {
    @Autowired
    private NodeRepository nodeRepository;
    private Node root;

    @Before
    public void setup() {
        root = new Node("Course");
        root.put("message", "Welcome to the course.");

        Node rN1 = new Node("Level 1");
        rN1.put("message", "Welcome to level1 in the course.");

        Node rN2 = new Node("Level 2");
        rN2.put("message", "Welcome to level2 in the course.");

        Node rN1N1 = new Node("Chapter 1");
        rN1N1.put("message", "Welcome to chapter1 in level1.");

        Node rN1N2 = new Node("Chapter 2");
        rN1N2.put("message", "Welcome to chapter2 in level1.");

        root.addChild(rN1);
        root.addChild(rN2);

        rN1.addChild(rN1N1);
        rN1.addChild(rN1N2);
    }

    @After
    public void tearDown() {
        nodeRepository.remove(root);
    }

    @Test
    public void shouldSaveTree() {
        nodeRepository.add(root);

        assertThat(root.getId(), is(notNullValue()));
        Node rootNodeFromDB = nodeRepository.get(root.getId());
        assertThat(rootNodeFromDB, isSameAsNodeRepresentedBy(root));
    }

    private Matcher<Node> isSameAsNodeRepresentedBy(final Node actualNode) {
        return new NodeDeepMatcher(actualNode);
    }

    public static class NodeDeepMatcher extends BaseMatcher<Node> {
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
            if(otherNode instanceof Node) {
                Node nodeToCompare = (Node) otherNode;
                return compareRecursively(actualNode, nodeToCompare);
            }
            return false;
        }

        private boolean compareRecursively(Node actualNode, Node expectedNode) {
            if(! StringUtils.equals(actualNode.name(), expectedNode.name()) ) {
                this.description = String.format("Node name did not match, actual: %s, expected: %s", actualNode.name(), expectedNode.name());
                return false;
            }

            if( ! actualNode.data().equals( expectedNode.data() ) ) {
                this.description = String.format("Node %s does not contain same data in actual and expected. \nActual: %s\nExpected: %s", actualNode.name(), actualNode.data(), expectedNode.data());
                return false;
            }

            List<Node> childrenInActualNode = actualNode.children();
            List<Node> childrenInNodeToCompare = expectedNode.children();

            int actualNoOfChildren = childrenInActualNode.size(), expectedNumberOfChildren = childrenInNodeToCompare.size();

            if(actualNoOfChildren != expectedNumberOfChildren) {
                this.description = String.format("Number of children in node %s does not match. Actual: %d, Expected: %d", actualNode.name(), actualNoOfChildren, expectedNumberOfChildren);
                return false;
            }

            for(int i=0; i<actualNoOfChildren; i++) {
                boolean lastNodesComparedAreEqual =
                        compareRecursively(childrenInActualNode.get(i), childrenInNodeToCompare.get(i)
                        );
                if( ! lastNodesComparedAreEqual ) {
                    return false;
                }
            }
            return true;
        }
    }
}