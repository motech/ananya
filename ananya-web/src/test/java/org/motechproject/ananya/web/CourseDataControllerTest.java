package org.motechproject.ananya.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ananya.repository.AllNodes;
import org.motechproject.ananya.service.CertificateCourseService;
import org.motechproject.ananya.service.JobAidService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class CourseDataControllerTest {

    @Mock
    AllNodes allNodes;

    @Mock
    private CourseDataController controller;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new CourseDataController(allNodes);
    }


    @Test
    public void shouldServeJobAidCourseDataWithoutLevels() throws Exception {
        String testNodeWithoutChildren = "TestNodeWithoutChildren";
        String expectedNodeWithoutLevels = "var courseData = " + testNodeWithoutChildren + ";";
        when(allNodes.nodeWithoutChildrenAsJson("JobAidCourse")).thenReturn(testNodeWithoutChildren);

        String actualNodeWithoutLevels = controller.serveJobAidCourseDataWithoutLevels(new MockHttpServletResponse());

        assertEquals(expectedNodeWithoutLevels, actualNodeWithoutLevels);
    }

    @Test
    public void shouldServeCertificateCourseWithoutLevels() throws Exception {
        String testNodeWithoutChildren = "TestNodeWithoutChildren";
        String expectedNodeWithoutLevels = "var courseData = " + testNodeWithoutChildren + ";";
        when(allNodes.nodeWithoutChildrenAsJson("CertificationCourse")).thenReturn(testNodeWithoutChildren);

        String actualNodeWithoutLevels = controller.serveCertificationCourseData(new MockHttpServletResponse());

        assertEquals(expectedNodeWithoutLevels, actualNodeWithoutLevels);
    }

    @Test
    public void shouldServeJobAidLevelData() throws Exception {
        String testLevelData = "TestNodeWithoutChildren";
        String levelNumber = "1";
        String expectedLevelData = "courseData.children[0] = " + testLevelData;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("levelNumber", levelNumber);
        when(allNodes.nodeAsJson("level " + levelNumber)).thenReturn(testLevelData);

        String actualLevelData = controller.serveJobAidLevelData(request, new MockHttpServletResponse());

        assertEquals(expectedLevelData, actualLevelData);
    }

    @Test
    public void shouldServeCertificateCourseData() throws Exception {
        String chapterData = "TestNodeWithoutChildren";
        String chapterNumber = "1";
        String expectedChapterData = "courseData.children[0] = " + chapterData;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("chapterNumber", chapterNumber);
        when(allNodes.nodeAsJson("Chapter " + chapterNumber)).thenReturn(chapterData);

        String actualChapterData = controller.serveCertificationCourseChapter(request, new MockHttpServletResponse());

        assertEquals(expectedChapterData, actualChapterData);
    }


}
