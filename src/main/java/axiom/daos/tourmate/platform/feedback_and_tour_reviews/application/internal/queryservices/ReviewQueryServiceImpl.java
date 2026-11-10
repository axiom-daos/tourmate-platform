
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.internal.queryservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.queryservices.ReviewQueryService;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetAllReviewsQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetReviewByIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetReviewsByTourIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.ReviewRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Servicio encargado de consultar las reseñas.
 */
@Service
@Transactional(readOnly = true)
public class ReviewQueryServiceImpl implements ReviewQueryService {

    private final ReviewRepository reviewRepository;

    public ReviewQueryServiceImpl(
            ReviewRepository reviewRepository
    ) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Obtiene todas las reseñas registradas.
     */
    @Override
    public List<Review> handle(
            GetAllReviewsQuery query
    ) {

        Objects.requireNonNull(
                query,
                "La consulta de reseñas es obligatoria."
        );

        return reviewRepository.findAll();
    }

    /**
     * Busca una reseña por su identificador.
     */
    @Override
    public Optional<Review> handle(
            GetReviewByIdQuery query
    ) {

        Objects.requireNonNull(
                query,
                "La consulta de reseña es obligatoria."
        );

        return reviewRepository.findById(
                query.reviewId()
        );
    }

    /**
     * Obtiene las reseñas de un tour específico.
     */
    @Override
    public List<Review> handle(
            GetReviewsByTourIdQuery query
    ) {

        Objects.requireNonNull(
                query,
                "La consulta de reseñas por tour es obligatoria."
        );

        return reviewRepository.findByTourId(
                query.tourId()
        );
    }
}
