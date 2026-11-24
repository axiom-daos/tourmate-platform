package axiom.daos.tourmate.platform.tour_management.application.internal.outboundservices.acl;

import axiom.daos.tourmate.platform.iam.interfaces.acl.IamContextFacade;
import axiom.daos.tourmate.platform.tour_management.domain.model.valueobjects.UserId;
import org.springframework.stereotype.Service;

@Service
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public boolean userIsRegistered(UserId id) {
        return this.iamContextFacade.existsUserById(id.value());
    }
}
