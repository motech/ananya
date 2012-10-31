package org.motechproject.ananya.repository.measure;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.motechproject.ananya.domain.CallUsageDetails;
import org.motechproject.ananya.domain.measure.CallDurationMeasure;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
@Transactional
public class AllCallDurationMeasures {
    private DataAccessTemplate template;
    private Integer numberOfRecentCallDetails;
    private Long certificateCourseShortCode;
    private Long certificateCourseLongCode;

    public AllCallDurationMeasures() {
    }

    @Autowired
    public AllCallDurationMeasures(DataAccessTemplate template,
                                   @Value("#{ananyaProperties['course.shortcode']}") String certificateCourseShortCode,
                                   @Value("#{ananyaProperties['course.longcode']}") String certificateCourseLongCode,
                                   @Value("#{ananyaProperties['course.longcode']}") String numberOfRecentCallDetails) {
        this.template = template;
        this.numberOfRecentCallDetails = Integer.parseInt(numberOfRecentCallDetails);
        this.certificateCourseShortCode = Long.valueOf(certificateCourseShortCode);
        this.certificateCourseLongCode = Long.valueOf(certificateCourseLongCode);
    }

    public void add(CallDurationMeasure callDurationMeasure) {
        template.save(callDurationMeasure);
    }

    public List<CallDurationMeasure> findByCallId(String callId) {
        return (List<CallDurationMeasure>) template.findByNamedQueryAndNamedParam(CallDurationMeasure.FIND_BY_CALL_ID, new String[]{"callId"}, new Object[]{callId});
    }

    public List<CallDurationMeasure> findByCallerId(Long callerId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(CallDurationMeasure.class);
        criteria.createAlias("frontLineWorkerDimension", "flw");
        criteria.add(Restrictions.eq("flw.msisdn", callerId));

        return template.findByCriteria(criteria);
    }

    public void updateAll(List<CallDurationMeasure> callDurationMeasureList) {
        template.saveOrUpdateAll(callDurationMeasureList);
    }

    public List<CallDurationMeasure> getRecentCertificateCourseCallDetails(Long msisdn) {
        DetachedCriteria criteria = DetachedCriteria.forClass(CallDurationMeasure.class);
        criteria.createAlias("frontLineWorkerDimension", "flw");
        criteria.add(Restrictions.eq("flw.msisdn", msisdn));
        criteria.add(Restrictions.eq("type", "CALL").ignoreCase());
        criteria.add(Restrictions.or(Restrictions.eq("calledNumber", certificateCourseLongCode), Restrictions.eq("calledNumber", certificateCourseShortCode)));
        criteria.addOrder(Order.desc("timeDimension"));

        return template.findByCriteria(criteria, -1, numberOfRecentCallDetails);
    }

    public List<CallDurationMeasure> getRecentJobAidCallDetails(Long msisdn) {
        DetachedCriteria criteria = DetachedCriteria.forClass(CallDurationMeasure.class);
        criteria.createAlias("frontLineWorkerDimension", "flw");
        criteria.add(Restrictions.eq("flw.msisdn", msisdn));
        criteria.add(Restrictions.eq("type", "CALL").ignoreCase());
        criteria.add(Restrictions.ne("calledNumber", certificateCourseShortCode));
        criteria.add(Restrictions.ne("calledNumber", certificateCourseLongCode));
        criteria.addOrder(Order.desc("timeDimension"));

        return template.findByCriteria(criteria, -1, numberOfRecentCallDetails);
    }

    public List<CallUsageDetails> getCallUsageDetailsByMonthAndYear(final Long msisdn) {
        Iterator resultSet = template.executeFind(new HibernateCallback<Object>() {
            @Override
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery sqlQuery = session.createSQLQuery("SELECT sum(a.jausage) as ja_usage, \n" +
                        "       sum(a.ccusage) as cc_usage, \n" +
                        "       year, \n" +
                        "       month \n" +
                        "FROM   ((SELECT cdm.duration AS jaUsage, \n" +
                        "                NULL         AS ccUsage, \n" +
                        "                td.year      AS year, \n" +
                        "                td.month     AS month \n" +
                        "         FROM   report.call_duration_measure cdm, \n" +
                        "                report.time_dimension td, \n" +
                        "                report.front_line_worker_dimension flwd \n" +
                        "         WHERE  flwd.msisdn = " + msisdn + " \n" +
                        "                AND cdm.type = 'CALL' \n" +
                        "                AND cdm.called_number <> " + certificateCourseLongCode + " \n" +
                        "                AND cdm.called_number <> " + certificateCourseShortCode + " \n" +
                        "                AND cdm.time_id = td.id \n" +
                        "                AND cdm.flw_id = flwd.id) \n" +
                        "        UNION ALL \n" +
                        "        (SELECT NULL         AS jaUsage, \n" +
                        "                cdm.duration AS ccUsage, \n" +
                        "                td.year      AS year, \n" +
                        "                td.month     AS month \n" +
                        "         FROM   report.call_duration_measure cdm, \n" +
                        "                report.time_dimension td, \n" +
                        "                report.front_line_worker_dimension flwd \n" +
                        "         WHERE  flwd.msisdn = " + msisdn + " \n" +
                        "                AND cdm.type = 'CALL' \n" +
                        "                AND ( cdm.called_number = " + certificateCourseShortCode + " \n" +
                        "                       OR cdm.called_number = " + certificateCourseShortCode + ") \n" +
                        "                AND cdm.time_id = td.id \n" +
                        "                AND cdm.flw_id = flwd.id)) a \n" +
                        "GROUP  BY year, \n" +
                        "          month; " +
                        "");
                return sqlQuery.list();
            }
        }).iterator();

        return mapToCallUsageDetails(resultSet);
    }

    private List<CallUsageDetails> mapToCallUsageDetails(Iterator resultSet) {
        List<CallUsageDetails> callUsageDetailsList = new ArrayList<>();

        while (resultSet.hasNext()) {
            Object[] row = (Object[]) resultSet.next();
            Long jobAidDurationInSec = Long.valueOf(row[0].toString());
            Long certificateCourseDurationInSec = Long.valueOf(row[1].toString());
            Integer year = Integer.valueOf(row[2].toString());
            Integer month = Integer.valueOf(row[3].toString());
            callUsageDetailsList.add(new CallUsageDetails(jobAidDurationInSec, certificateCourseDurationInSec, year, month));
        }
        return callUsageDetailsList;
    }

    public List<CallDurationMeasure> findByLocationId(String locationId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(CallDurationMeasure.class);
        criteria.createAlias("locationDimension", "loc");
        criteria.add(Restrictions.eq("loc.locationId", locationId));

        return template.findByCriteria(criteria);
    }
}
