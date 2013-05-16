package org.motechproject.ananya.validators;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.contract.FailedRecordCSVRequestBuilder;
import org.motechproject.ananya.domain.CourseItemType;
import org.motechproject.ananya.domain.dimension.CourseItemDetailsDimension;
import org.motechproject.ananya.domain.dimension.CourseItemDimension;
import org.motechproject.ananya.domain.dimension.LanguageDimension;
import org.motechproject.ananya.repository.dimension.AllCourseItemDetailsDimensions;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDetailsDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.ananya.repository.dimension.AllLanguageDimension;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FailedCertificateCourseRecordValidatorTest {


    private FailedCertificateCourseRecordValidator failedCertificateCourseRecordValidator;

    @Mock
    private AllCourseItemDimensions allCourseItemDimensions;

    @Mock
    private AllJobAidContentDimensions allJobAidContentDimensions;
    @Mock
    private AllJobAidContentDetailsDimensions allJobAidContentDetailsDimensions;
    @Mock
    private AllCourseItemDetailsDimensions allCourseItemDetailsDimensions;
    @Mock
    private AllLanguageDimension allLanguageDimension;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        failedCertificateCourseRecordValidator = new FailedCertificateCourseRecordValidator(allCourseItemDimensions, allJobAidContentDimensions, allCourseItemDetailsDimensions, allJobAidContentDetailsDimensions, allLanguageDimension);
    }

    @Test
    public void shouldValidateAValidCertificateCourseRecord() {
        when(allCourseItemDimensions.getFor("Chapter 1 Lesson 4", CourseItemType.LESSON)).thenReturn(new CourseItemDimension());
        when(allCourseItemDimensions.getFor("Chapter 1", CourseItemType.QUIZ)).thenReturn(new CourseItemDimension());
        when(allCourseItemDimensions.getFor("7a823ae22badc42018c6542c597cced8")).thenReturn(new CourseItemDimension(null, null, null, null));
        when(allCourseItemDimensions.getFor("7a823ae22badc42018c6542c597ccf1c")).thenReturn(new CourseItemDimension(null, null, null, null));
        when(allCourseItemDimensions.getFor("7a823ae22badc42018c6542c597c4d3b")).thenReturn(new CourseItemDimension(null, null, null, null));
        when(allCourseItemDimensions.getFor("7a823ae22badc42018c6542c597cdcdd")).thenReturn(new CourseItemDimension(null, null, null, null));
        when(allCourseItemDimensions.getFor("7a823ae22badc42018c6542c597cdea4")).thenReturn(new CourseItemDimension(null, null, null, null));
        when(allLanguageDimension.getFor("language")).thenReturn(new LanguageDimension("language", "lang", "badhai ho.."));
        when(allCourseItemDetailsDimensions.getFor("7a823ae22badc42018c6542c597cced8", null)).thenReturn(new CourseItemDetailsDimension(null, null, null, 6122));
        when(allCourseItemDetailsDimensions.getFor("7a823ae22badc42018c6542c597ccf1c", null)).thenReturn(new CourseItemDetailsDimension(null, null, null, 7435));
        when(allCourseItemDetailsDimensions.getFor("7a823ae22badc42018c6542c597c4d3b", null)).thenReturn(new CourseItemDetailsDimension(null, null, null, 4551));
        when(allCourseItemDetailsDimensions.getFor("7a823ae22badc42018c6542c597cdcdd", null)).thenReturn(new CourseItemDetailsDimension(null, null, null, 3087));
        when(allCourseItemDetailsDimensions.getFor("7a823ae22badc42018c6542c597cdea4", null)).thenReturn(new CourseItemDetailsDimension(null, null, null, 3083));

        failedCertificateCourseRecordValidator.validate(new FailedRecordCSVRequestBuilder().withCertificateCourseDefaults().build());
    }

    @Test
    public void shouldInValidateIfFieldToPostIsInInvalidFormat() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid fields to post: callId:9886000002-1346784033040;operator: ;callerId:232323");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:9886000002-1346784033040;operator: ;callerId:232323")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfInvalidFieldToPostIsPresent() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid field to post: Opeartor");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:9886000002-1346784033040;Opeartor:airtel")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfOperatorFieldToPostIsAbsent() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Missing mandatory field to post: operator");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:9886000002-1346784033040;")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfCallIdFieldToPostIsAbsent() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Missing mandatory field to post: callId");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("operator:airtel;")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldValidateCallId() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid call id: 123456789a-3343");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:123456789a-3343; operator:airtel")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldValidateCallerId() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid caller id: 765441234");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[]")
                .withMsisdn("765441234")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldValidateCalledNumber() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid called number: 23a");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[]")
                .withCalledNumber("23a")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateCCStateRequestListForChapterIndexGreaterThan9() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid course chapter index: 10");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":3,\"data\":{\"lessonOrQuestionIndex\":5,\"time\":1350379378937,\"interactionKey\":\"poseQuestion\",\"chapterIndex\":10,\"certificateCourseId\":\"\"},\"type\":\"ccState\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateCCStateRequestListForInvalidChapterIndexLessThan0() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid course chapter index: -1");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":3,\"data\":{\"lessonOrQuestionIndex\":5,\"time\":1350379378937,\"interactionKey\":\"poseQuestion\",\"chapterIndex\":-1,\"certificateCourseId\":\"\"},\"type\":\"ccState\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateLessonOrQuestionInLessThan0() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid lesson or question index: -1");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":3,\"data\":{\"lessonOrQuestionIndex\":-1,\"time\":1350379378937,\"interactionKey\":\"poseQuestion\",\"chapterIndex\":1,\"certificateCourseId\":\"\"},\"type\":\"ccState\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateLessonOrQuestionInGreaterThan7() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid lesson or question index: 8");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":3,\"data\":{\"lessonOrQuestionIndex\":8,\"time\":1350379378937,\"interactionKey\":\"poseQuestion\",\"chapterIndex\":1,\"certificateCourseId\":\"\"},\"type\":\"ccState\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateForInvalidTime() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid request time: 123a");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":3,\"data\":{\"lessonOrQuestionIndex\":7,\"time\":123a,\"interactionKey\":\"poseQuestion\",\"chapterIndex\":1,\"certificateCourseId\":\"\"},\"type\":\"ccState\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateForValidContentIdButInvalidContentType() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid request content type: quiz123");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":7,\"data\":{\"lessonOrQuestionIndex\":7,\"time\":1350720949953,\"interactionKey\":\"reportChapterScore\",\"contentType\":\"quiz123\",\"contentData\":4,\"courseItemState\":\"end\",\"chapterIndex\":4,\"certificateCourseId\":\"\",\"contentName\":\"Chapter 5\",\"contentId\":\"5fc654d8ec2bac6c906be72af674d4a6\"},\"type\":\"ccState\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateForValidContentIdButInvalidContentName() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid request content name: Chapter 523");
        when(allCourseItemDimensions.getFor("Chapter 523",CourseItemType.QUIZ)).thenReturn(null);

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":7,\"data\":{\"lessonOrQuestionIndex\":7,\"time\":1350720949953,\"interactionKey\":\"reportChapterScore\",\"contentType\":\"quiz\",\"contentData\":4,\"courseItemState\":\"end\",\"chapterIndex\":4,\"certificateCourseId\":\"\",\"contentName\":\"Chapter 523\",\"contentId\":\"5fc654d8ec2bac6c906be72af674d4a6\"},\"type\":\"ccState\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateForValidContentIdButNoContentType() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid request content type: null");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":7,\"data\":{\"lessonOrQuestionIndex\":7,\"time\":1350720949953,\"interactionKey\":\"reportChapterScore\",\"contentData\":4,\"courseItemState\":\"end\",\"chapterIndex\":4,\"certificateCourseId\":\"\",\"contentName\":\"Chapter 5\",\"contentId\":\"5fc654d8ec2bac6c906be72af674d4a6\"},\"type\":\"ccState\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateForNonNumericContentData() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid content data (score): 4a");
        when(allCourseItemDimensions.getFor("Chapter 5",CourseItemType.QUIZ)).thenReturn(new CourseItemDimension());

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":7,\"data\":{\"lessonOrQuestionIndex\":7,\"time\":1350720949953,\"interactionKey\":\"reportChapterScore\",\"contentType\":\"quiz\",\"contentData\":4a,\"courseItemState\":\"end\",\"chapterIndex\":4,\"certificateCourseId\":\"\",\"contentName\":\"Chapter 5\",\"contentId\":\"5fc654d8ec2bac6c906be72af674d4a6\"},\"type\":\"ccState\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateForValidContentIdButInvalidContentState() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid request item state: random");
        when(allCourseItemDimensions.getFor("Chapter 5",CourseItemType.QUIZ)).thenReturn(new CourseItemDimension());

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":7,\"data\":{\"lessonOrQuestionIndex\":7,\"time\":1350720949953,\"interactionKey\":\"reportChapterScore\",\"contentType\":\"quiz\",\"contentData\":4,\"courseItemState\":\"random\",\"chapterIndex\":4,\"certificateCourseId\":\"\",\"contentName\":\"Chapter 5\",\"contentId\":\"5fc654d8ec2bac6c906be72af674d4a6\"},\"type\":\"ccState\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateAudioTrackLogsForInvalidContentId() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid audio tracker content id: invalid_contentId");
        
        when(allCourseItemDimensions.getFor("invalid_contentId")).thenReturn(null);
        when(allCourseItemDetailsDimensions.getFor("invalid_contentId", null)).thenReturn(null);
        when(allLanguageDimension.getFor("language")).thenReturn(new LanguageDimension("language", "lang", "badhai ho..."));
        
        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":4,\"data\":{\"duration\":55277,\"time\":1350379442356,\"contentId\":\"invalid_contentId\"},\"type\":\"audioTracker\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfAudioTrackLogsDurationIsGreaterThanTheActualDimensionDuration() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Audio tracker duration greater than actual course item duration: 55277, actual: 1234");
       
        when(allCourseItemDimensions.getFor("1234567890987654321")).thenReturn(new CourseItemDimension(null,null,null,null));
        when(allLanguageDimension.getFor("language")).thenReturn(new LanguageDimension("language", "lang", "badhai ho..."));
        when(allCourseItemDetailsDimensions.getFor("1234567890987654321", null)).thenReturn(new CourseItemDetailsDimension(null, null, null, 1234));
       
        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":4,\"data\":{\"duration\":55277,\"time\":1350379442356,\"contentId\":\"1234567890987654321\"},\"type\":\"audioTracker\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfAudioTrackLogsWithInvalidRequestTime() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid audio tracker request time: 2sa");
        
        when(allCourseItemDimensions.getFor("1234567890987654321")).thenReturn(new CourseItemDimension(null,null,null,null));
        when(allLanguageDimension.getFor("language")).thenReturn(new LanguageDimension("language", "lang", "badhai ho..."));
        when(allCourseItemDetailsDimensions.getFor("1234567890987654321", null)).thenReturn(new CourseItemDetailsDimension(null, null, null, 55277));

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":4,\"data\":{\"duration\":55277,\"time\":2sa,\"contentId\":\"1234567890987654321\"},\"type\":\"audioTracker\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }

    @Test
    public void shouldInvalidateCallDurationLogIfCallTypeIsNull() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Call duration call event is null");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withCertificateCourseDefaults()
                .withDataToPost("[{\"token\":1,\"data\":{\"time\":1350379378663},\"type\":\"callDuration\"}]")
                .withCalledNumber("1234567")
                .build();
        failedCertificateCourseRecordValidator.validate(request);
    }
}
