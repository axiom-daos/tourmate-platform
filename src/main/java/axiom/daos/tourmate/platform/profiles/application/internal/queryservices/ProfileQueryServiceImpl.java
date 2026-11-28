package axiom.daos.tourmate.platform.profiles.application.internal.queryservices;

import axiom.daos.tourmate.platform.profiles.application.queryservices.ProfileQueryService;
import axiom.daos.tourmate.platform.profiles.domain.model.aggregates.Profile;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetAllProfilesQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByEmailQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import axiom.daos.tourmate.platform.profiles.domain.model.queries.GetProfileByUserIdQuery;
import axiom.daos.tourmate.platform.profiles.domain.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link ProfileQueryService}.
 */
@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public List<Profile> handle(GetAllProfilesQuery query) {
        return profileRepository.findAll();
    }

    @Override
    public Optional<Profile> handle(GetProfileByIdQuery query) {
        return profileRepository.findById(query.profileId());
    }

    @Override
    public Optional<Profile> handle(GetProfileByEmailQuery query) {
        return profileRepository.findByEmail(query.email());
    }

    @Override
    public Optional<Profile> handle(GetProfileByUserIdQuery query) {
        return profileRepository.findByUserId(query.userId());
    }
}
