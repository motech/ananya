package org.motechproject.bbcwt.repository.tree;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.domain.tree.Node;
import org.motechproject.bbcwt.repository.SpringIntegrationTest;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.motechproject.bbcwt.matcher.NodeDeepMatcher.isSameAsNodeRepresentedBy;

public class AllNodesTest extends SpringIntegrationTest {
    @Autowired
    private AllNodes allNodes;
    private Node root;
    private final static String COURSE_NAME = "Job Aid Course" + System.currentTimeMillis();

    @Before
    public void setup() {
        root = new Node(COURSE_NAME);
        root.put("message", "Welcome to the course.");
        allNodes.add(root);

        Node rN1 = new Node("Level 1");
        rN1.put("message", "Welcome to level1 in the course.");
        root.addChild(rN1);
        allNodes.add(rN1);

        Node rN2 = new Node("Level 2");
        rN2.put("message", "Welcome to level2 in the course.");
        root.addChild(rN2);
        allNodes.add(rN2);

        Node rN1N1 = new Node("Chapter 1");
        rN1N1.put("message", "Welcome to chapter1 in level1.");
        rN1.addChild(rN1N1);
        allNodes.add(rN1N1);

        Node rN1N2 = new Node("Chapter 2");
        rN1N2.put("message", "Welcome to chapter2 in level1.");
        rN1.addChild(rN1N2);
        allNodes.add(rN1N2);
    }

    @After
    public void tearDown() {
        allNodes.removeAll();
    }

    @Test
    public void shouldSaveTree() {
        assertThat(root.getId(), is(notNullValue()));
        Node rootNodeFromDB = allNodes.get(root.getId());
        assertThat(rootNodeFromDB, isSameAsNodeRepresentedBy(root));
    }

    @Test
    public void shouldReturnTreeByName() {
        Node treeFromDB = allNodes.findByName(COURSE_NAME);
        assertThat(treeFromDB, isSameAsNodeRepresentedBy(root));
    }

    @Test
    public void shouldReturnTreeAsJson() throws Exception {
        String treeAsJson = allNodes.nodeAsJson(COURSE_NAME);
        Node from = (Node) new MotechJsonReader().readFromString(treeAsJson, Node.class);
        assertThat(from, isSameAsNodeRepresentedBy(root));
    }
}