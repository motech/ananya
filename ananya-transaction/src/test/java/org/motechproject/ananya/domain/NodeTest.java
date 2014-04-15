package org.motechproject.ananya.domain;

import org.junit.Test;
import org.motechproject.cmslite.api.model.StringContent;

import static junit.framework.Assert.assertEquals;

public class NodeTest {
    @Test
    public void shouldDeleteAllContents() {
        Node root = new Node("Certificate Course");
        root.addContent(new StringContent("hindi", "dummy", "foo"));
        root.addContentId("a343f");

        root.deleteAllContents();

        assertEquals(0, root.contents().size());
        assertEquals(0, root.contentIds().size());
    }
}
