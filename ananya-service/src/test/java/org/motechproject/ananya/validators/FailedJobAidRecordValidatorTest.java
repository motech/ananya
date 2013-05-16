package org.motechproject.ananya.validators;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.ananya.contract.FailedRecordCSVRequest;
import org.motechproject.ananya.contract.FailedRecordCSVRequestBuilder;
import org.motechproject.ananya.domain.dimension.JobAidContentDetailsDimension;
import org.motechproject.ananya.domain.dimension.JobAidContentDimension;
import org.motechproject.ananya.domain.dimension.LanguageDimension;
import org.motechproject.ananya.repository.dimension.AllCourseItemDetailsDimensions;
import org.motechproject.ananya.repository.dimension.AllCourseItemDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDetailsDimensions;
import org.motechproject.ananya.repository.dimension.AllJobAidContentDimensions;
import org.motechproject.ananya.repository.dimension.AllLanguageDimension;

@RunWith(MockitoJUnitRunner.class)
public class FailedJobAidRecordValidatorTest {
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

    private FailedJobAidRecordValidator failedJobAidRecordValidator;

    @Before
    public void setUp() {
        failedJobAidRecordValidator = new FailedJobAidRecordValidator(allCourseItemDimensions, allJobAidContentDimensions, allCourseItemDetailsDimensions, allJobAidContentDetailsDimensions, allLanguageDimension);
    }
    @Test
    public void shouldInValidateIfFieldToPostIsInInvalidFormat() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid fields to post: callId:9886000002-1346784033040;operator: ;callerId:232323");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:9886000002-1346784033040;operator: ;callerId:232323")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfInvalidFieldToPostIsPresent() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid field to post: Opeartor");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:9886000002-1346784033040;Opeartor:airtel")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfOperatorFieldToPostIsAbsent() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Missing mandatory field to post: operator");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:9886000002-1346784033040;callDuration:123;promptList:[]")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfCallIdFieldToPostIsAbsent() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Missing mandatory field to post: callId");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("operator:airtel;callDuration:123;promptList:[]")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfCallDurationFieldToPostIsAbsent() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Missing mandatory field to post: callDuration");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:9886000002-1346784033040;operator:airtel;promptList:[]")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfPromptListFieldToPostIsAbsent() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Missing mandatory field to post: promptList");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:9886000002-1346784033040;operator:airtel;callDuration:123")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfCallIdIsInvalid() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid call id: 90002-1346784033040");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:90002-1346784033040;operator:airtel;callDuration:123;promptList:[]")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfCallerIdIsInvalid() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid caller id: 123");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:1234567890-1346784033040;operator:airtel;callDuration:123;promptList:[]")
                .withMsisdn("123")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfCalledNumberIsInvalid() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid called number: 123a");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:1234567890-1346784033040;operator:airtel;callDuration:123;promptList:[]")
                .withCalledNumber("123a")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldFailForInvalidPrompt() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid JobAid prompt list something");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:1234567890-1346784033040;operator:airtel;callDuration:123;promptList:something")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldFailForInvalidAudioTrackerList() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid JobAid prompt list something");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[]")
                .withFieldsToPost("callId:1234567890-1346784033040;operator:airtel;callDuration:123;promptList:something")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateAudioTrackLogsForInvalidContentId() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid audio tracker content id: invalid_contentId");
        
        when(allJobAidContentDimensions.findByContentId("invalid_contentId")).thenReturn(null);
        when(allCourseItemDetailsDimensions.getFor("invalid_contentId", null)).thenReturn(null);
        when(allLanguageDimension.getFor("language")).thenReturn(new LanguageDimension("language", "lang", "badhai ho..."));
        
        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[{\"token\":4,\"data\":{\"duration\":55277,\"time\":1350379442356,\"contentId\":\"invalid_contentId\"},\"type\":\"audioTracker\"}]")
                .withCalledNumber("1234567")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfAudioTrackLogsDurationIsGreaterThanTheActualDimensionDuration() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Audio tracker duration greater than actual job aid content duration: 55277, actual: 1234, content id: 1234567890987654321");
        
        when(allJobAidContentDimensions.findByContentId("1234567890987654321")).thenReturn(new JobAidContentDimension(null,null,null,null));
        when(allLanguageDimension.getFor("language")).thenReturn(new LanguageDimension("language", "lang", "badhai ho..."));
        when(allJobAidContentDetailsDimensions.getFor("1234567890987654321", null)).thenReturn(new JobAidContentDetailsDimension(null, null, null, 1234));
       
        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[{\"token\":4,\"data\":{\"duration\":55277,\"time\":1350379442356,\"contentId\":\"1234567890987654321\"},\"type\":\"audioTracker\"}]")
                .withCalledNumber("1234567")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldInValidateIfAudioTrackLogsWithInvalidRequestTime() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Invalid audio tracker request time: 2sa");
        
        when(allJobAidContentDimensions.findByContentId("1234567890987654321")).thenReturn(new JobAidContentDimension(null, null, null, null));
        when(allLanguageDimension.getFor("language")).thenReturn(new LanguageDimension("language", "lang", "badhai ho..."));
        when(allJobAidContentDetailsDimensions.getFor("1234567890987654321", null)).thenReturn(new JobAidContentDetailsDimension(null, null, null, 55277));

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[{\"token\":4,\"data\":{\"duration\":55277,\"time\":2sa,\"contentId\":\"1234567890987654321\"},\"type\":\"audioTracker\"}]")
                .withCalledNumber("1234567")
                .build();
        failedJobAidRecordValidator.validate(request);
    }

    @Test
    public void shouldInvalidateCallDurationLogIfCallTypeIsNull() {
        expectedException.expect(FailedRecordValidationException.class);
        expectedException.expectMessage("Call duration call event is null");

        FailedRecordCSVRequest request = new FailedRecordCSVRequestBuilder()
                .withJobAidDefaults()
                .withDataToPost("[{\"token\":1,\"data\":{\"time\":1350379378663},\"type\":\"callDuration\"}]")
                .withCalledNumber("1234567")
                .build();
        failedJobAidRecordValidator.validate(request);
    }
}
