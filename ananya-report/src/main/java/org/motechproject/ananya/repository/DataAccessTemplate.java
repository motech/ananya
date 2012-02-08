package org.motechproject.ananya.repository;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
@Scope(value = "prototype")
public class DataAccessTemplate extends HibernateTemplate {

    @Autowired
    public DataAccessTemplate(@Qualifier(value = "sessionFactory") SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Object getUniqueResult(String namedQueryName, String[] parameterNames, Object[] parameterValues) {
        return DataAccessUtils.uniqueResult(findByNamedQueryAndNamedParam(namedQueryName, parameterNames, parameterValues));
    }

    public Object getUniqueResult(String namedQueryName, String parameterName, Object parameterValue) {
        return DataAccessUtils.uniqueResult(findByNamedQueryAndNamedParam(namedQueryName, parameterName, parameterValue));
    }

    public Object getUniqueResult(String namedQueryName) {
        return DataAccessUtils.uniqueResult(findByNamedQuery(namedQueryName));
    }

    public List findPaginated(String query, Integer page, Integer resultsPerPage) {
        return executeFind(new PaginatedQuery(query, page, resultsPerPage));
    }

    public Object getUniqueResultByQuery(String query) {
        return DataAccessUtils.uniqueResult(find(query));
    }

    static class PaginatedQuery implements HibernateCallback {
        private Integer page;
        private Integer resultsPerPage;
        private String queryString;

        public PaginatedQuery(String query, Integer page, Integer resultsPerPage) {
            this.page = page;
            this.resultsPerPage = resultsPerPage;
            this.queryString = query;
        }

        public Object doInHibernate(Session session) throws HibernateException, SQLException {
            Query hibernateQuery = session.createQuery(queryString);
            hibernateQuery.setFirstResult(offset());
            hibernateQuery.setMaxResults(resultsPerPage);
            return hibernateQuery.list();
        }

        private Integer offset() {
            return (page - 1) * resultsPerPage;
        }
    }
}