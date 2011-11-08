package org.motechproject.bbcwt.repository.tree;

import org.ektorp.CouchDbConnector;
import org.motechproject.bbcwt.domain.tree.Node;
import org.motechproject.bbcwt.repository.AbstractCouchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class NodeRepository extends AbstractCouchRepository<Node> {

    @Autowired
    public NodeRepository(@Qualifier("bbcwtDbConnector") CouchDbConnector dbCouchDbConnector) {
        super(Node.class, dbCouchDbConnector);
    }

}