package org.motechproject.ananya.domain;

import org.motechproject.ananya.domain.grid.AcademyCallGrid;
import org.motechproject.ananya.domain.grid.CallDetailGrid;
import org.motechproject.ananya.domain.grid.DataGrid;
import org.motechproject.ananya.domain.grid.KunjiCallGrid;
import org.motechproject.ananya.support.admin.AdminInquiryService;
import org.motechproject.ananya.support.admin.domain.CallContent;
import org.motechproject.ananya.support.admin.domain.CallDetail;
import org.motechproject.ananya.support.admin.domain.CallerDetail;

import java.util.List;
import java.util.Map;

public enum DataMapper {
    AcademyCalls(AdminInquiryService.ACADEMY_CALLS) {
        @Override
        protected DataGrid dataFor(Map<String, Object> data) {
            return new AcademyCallGrid((List<CallContent>) data.get(this.key));
        }
    },
    KunjiCalls(AdminInquiryService.KUNJI_CALLS) {
        @Override
        protected DataGrid dataFor(Map<String, Object> data) {
            return new KunjiCallGrid((List<CallContent>) data.get(this.key));
        }
    },
    CallDetails(AdminInquiryService.CALL_DETAILS) {
        @Override
        protected DataGrid dataFor(Map<String, Object> data) {
            return new CallDetailGrid((List<CallDetail>) data.get(this.key));
        }
    },
    CallerDataJs(AdminInquiryService.CALLER_DATA_JS) {
        @Override
        protected String dataFor(Map<String, Object> data) {
            return (String) data.get(this.key);
        }
    },
    CallerDetail(AdminInquiryService.CALLER_DETAIL) {
        @Override
        protected CallerDetail dataFor(Map<String, Object> data) {
            return (CallerDetail) data.get(this.key);
        }
    },
    CouchError(AdminInquiryService.COUCHDB_ERROR) {
        @Override
        protected String dataFor(Map<String, Object> data) {
            return (String) data.get(this.key);
        }
    },
    PostgresError(AdminInquiryService.POSTGRES_ERROR) {
        @Override
        protected String dataFor(Map<String, Object> data) {
            return (String) data.get(this.key);
        }
    };

    protected String key;

    DataMapper(String key) {
        this.key = key;
    }

    public static Object prepareDataFor(String key, Map<String, Object> data) {
        for (DataMapper mapper : DataMapper.values()) {
            if (mapper.key.equals(key))
                return mapper.dataFor(data);
        }
        return null;
    }

    protected abstract Object dataFor(Map<String, Object> data);
}
