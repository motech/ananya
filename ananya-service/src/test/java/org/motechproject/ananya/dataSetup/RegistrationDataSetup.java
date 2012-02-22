package org.motechproject.ananya.dataSetup;

import org.apache.commons.lang.RandomStringUtils;
import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ananya.domain.Designation;
import org.motechproject.ananya.domain.FrontLineWorker;
import org.motechproject.ananya.domain.Location;
import org.motechproject.ananya.domain.RegistrationStatus;
import org.motechproject.ananya.repository.AllFrontLineWorkers;
import org.motechproject.ananya.repository.AllLocations;
import org.motechproject.ananya.service.ReportDataMeasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext-service.xml")
public class RegistrationDataSetup {

    @Qualifier("ananyaDbConnector")
    @Autowired
    protected CouchDbConnector ananyaDbConnector;

    @Autowired
    private AllLocations allLocations;

    @Autowired
    private AllFrontLineWorkers allFrontLineWorkers;

    @Autowired
    @Qualifier("dataSetupProperties")
    private Properties dataSetupProperties;
    private int[] locationSplit;

    // The Chosen Ones : Amni, West Thatha, Dumari, Gudara, Nautan Khund
    private String[] predefinedLocationCodes = new String[]
            {"S01D002B001V001", "S01D002B001V007", "S01D003B001V010", "S01D003B001V012", "S01D003B001V022"};

    private List<Location> locations;
    private DateTime startDate;
    private DateTime endDate;
    private Integer numberOfFLW;

    private ArrayList<FrontLineWorker> frontLineWorkerList;

    @Autowired
    private ReportDataMeasure reportDataMeasure;

    private void populateLocationCodes() {
        locations = new ArrayList<Location>();
        for (String code : predefinedLocationCodes) {
            locations.add(allLocations.findByExternalId(code));
        }
    }

    @Before
    public void setUp() {
        populateLocationCodes();
        locationSplit = splitProbabilityString(dataSetupProperties.getProperty("location.split"));
        startDate = DateTime.parse(dataSetupProperties.getProperty("capture.start.date"));
        endDate = DateTime.parse(dataSetupProperties.getProperty("capture.end.date"));
        numberOfFLW = Integer.parseInt(dataSetupProperties.getProperty("number.of.flw"));
        frontLineWorkerList = new ArrayList<FrontLineWorker>();

        allFrontLineWorkers.removeAll();
    }

    @Test
    public void shouldSetUpDataForRegistration() {

        for (int flwNumber = 0; flwNumber < numberOfFLW; flwNumber++) {

            Location location = getLocation();
            FrontLineWorker frontLineWorker = new FrontLineWorker(
                    RandomStringUtils.randomNumeric(10),
                    getDesignation(),
                    location.getId(),"");
            frontLineWorker.setRegisteredDate(getRegisteredDate());
            frontLineWorker.status(RegistrationStatus.REGISTERED);
            frontLineWorkerList.add(frontLineWorker);

            if ((flwNumber + 1) % 10 == 0) pushWorkersIntoDB();
        }

        pushWorkersIntoDB();
    }

    private void pushWorkersIntoDB(){
        if (frontLineWorkerList == null || frontLineWorkerList.size() == 0) return;

        ananyaDbConnector.executeBulk(frontLineWorkerList);
        for(FrontLineWorker frontLineWorker : frontLineWorkerList){
            reportDataMeasure.createRegistrationMeasureWith(frontLineWorker.getMsisdn());
        }
        frontLineWorkerList.clear();
    }
    
    private DateTime getRegisteredDate() {
        return startDate.plusMillis(new Random().nextInt((int) endDate.minus(startDate.getMillis()).getMillis()));
    }

    private Location getLocation() {
        return locations.get(getProbabilityNumber(locationSplit));
    }

    private Designation getDesignation() {
        return Designation.ANM;
    }

    private int[] splitProbabilityString(String s) {
        String[] splits = s.split(",");
        int[] arr = new int[splits.length];
        int runningValue = 0;
        for (int i = 0; i < splits.length; i++) {
            arr[i] = Integer.parseInt(splits[i]) + runningValue;
            runningValue = arr[i];
        }
        return arr;
    }

    private int getProbabilityNumber(int[] probabilityArray) {
        Random random = new Random();
        int randomNumber = random.nextInt(10);

        for (int i = 0; i < probabilityArray.length; i++) {
            if (randomNumber <= probabilityArray[i])
                return i;
        }

        return 0;
    }
}

