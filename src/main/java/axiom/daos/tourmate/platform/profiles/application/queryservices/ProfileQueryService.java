package axiom.daos.tourmate.platform.profiles.application.queryservices;

import axiom.daos.tourmate.platform.profiles.domain.model.aggregates.Profile;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetAllProfilesQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByEmailQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for profile read queries.
 */
public interface ProfileQueryService {

    List<Profile> handle(GetAllProfilesQuery query);

    Optional<Profile> handle(GetProfileByIdQuery query);

    Optional<Profile> handle(GetProfileByEmailQuery query);

    Optional<Profile> handle(GetProfileByUserIdQuery query);
}
