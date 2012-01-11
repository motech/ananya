package org.motechproject.bbcwt.repository.tree;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.domain.tree.Node;
import org.motechproject.bbcwt.repository.SpringIntegrationTest;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNull;
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

    @Test
    public void shouldSaveNodeWithAllChildren() throws Exception {
        long randomizeData = System.currentTimeMillis();

        final String COURSE = "JobAid" + randomizeData;

        final String LEVEL_1 = "level1" + randomizeData;
        final String LEVEL_2 = "level2" + randomizeData;
        final String LEVEL_1_CHAP_1 = "1chap1" + randomizeData;
        final String LEVEL_1_CHAP_2 = "1chap2" + randomizeData;
        final String LEVEL_2_CHAP_1 = "2chap1" + randomizeData;
        final String LEVEL_2_CHAP_2 = "2chap2" + randomizeData;

        final Node course = new Node(COURSE);
        final Node level1 = new Node(LEVEL_1);
        final Node level2 = new Node(LEVEL_2);
        final Node level1Chapter1 = new Node(LEVEL_1_CHAP_1);
        final Node level1Chapter2 = new Node(LEVEL_1_CHAP_2);
        final Node level2Chapter1 = new Node(LEVEL_2_CHAP_1);
        final Node level2Chapter2 = new Node(LEVEL_2_CHAP_2);

        course.addChild(level1);
        course.addChild(level2);
        level1.addChild(level1Chapter1);
        level1.addChild(level1Chapter2);
        level2.addChild(level2Chapter1);
        level2.addChild(level2Chapter2);

        allNodes.addNodeWithDescendants(course);

        assertThat(course.getId(), is(notNullValue()));
        assertThat(level1.getId(), is(notNullValue()));
        assertThat(level2.getId(), is(notNullValue()));
        assertThat(level1Chapter1.getId(), is(notNullValue()));
        assertThat(level1Chapter2.getId(), is(notNullValue()));
        assertThat(level2Chapter1.getId(), is(notNullValue()));
        assertThat(level2Chapter2.getId(), is(notNullValue()));

        Node courseFromDb = allNodes.findByName(COURSE);
        assertThat(courseFromDb, isSameAsNodeRepresentedBy(course));
        assertNull(courseFromDb.getParentId());

        Node level1FromDb = allNodes.findByName(LEVEL_1);
        assertThat(level1FromDb, isSameAsNodeRepresentedBy(level1));
        assertEquals(level1FromDb.getParentId(), courseFromDb.getId());

        Node level2Chapter2FromDb = allNodes.findByName(LEVEL_2_CHAP_2);
        assertThat(level2Chapter2FromDb, isSameAsNodeRepresentedBy(level2Chapter2));
        assertEquals(level2Chapter2FromDb.getParentId(),level2.getId());

    }
}