package org.motechproject.bbcwt.repository.tree;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.domain.tree.Node;
import org.motechproject.bbcwt.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.motechproject.bbcwt.matcher.NodeDeepMatcher.isSameAsNodeRepresentedBy;

public class NodeRepositoryTest extends SpringIntegrationTest {
    @Autowired
    private NodeRepository nodeRepository;
    private Node root;
    private final static String COURSE_NAME = "Job Aid Course";

    @Before
    public void setup() {
        root = new Node(COURSE_NAME);
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

    @Test
    public void shouldReturnTreeByName() {
        nodeRepository.add(root);

        Node treeFromDB = nodeRepository.findByName(COURSE_NAME);

        assertThat(treeFromDB, isSameAsNodeRepresentedBy(root));
    }
}