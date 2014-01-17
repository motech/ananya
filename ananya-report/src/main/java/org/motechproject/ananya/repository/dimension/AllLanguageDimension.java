package org.motechproject.ananya.repository.dimension;

import org.motechproject.ananya.domain.dimension.LanguageDimension;
import org.motechproject.ananya.repository.DataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllLanguageDimension {

    @Autowired
    private DataAccessTemplate template;

    public AllLanguageDimension() {
    }

    public LanguageDimension getFor(String name) {
        return (LanguageDimension) template.getUniqueResult(LanguageDimension.FIND_BY_LANGUAGE_NAME, new String[]{"name"}, new Object[]{name.trim()});
    }

    public LanguageDimension add(LanguageDimension languageDimension) {
        template.save(languageDimension);
        return languageDimension;
    }

    public void update(LanguageDimension languageDimension) {
        template.update(languageDimension);
    }
    
    public LanguageDimension addOrUpdate(LanguageDimension languageDimension) {
        template.saveOrUpdate(languageDimension);
        return languageDimension;
    }
}
