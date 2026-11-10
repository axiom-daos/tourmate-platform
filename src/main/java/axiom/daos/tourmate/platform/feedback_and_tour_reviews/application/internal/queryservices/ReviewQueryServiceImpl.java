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
 * Implementación del servicio de consultas de reseñas.
 *
 * Se encarga de coordinar las operaciones de lectura
 * mediante el repositorio de dominio.
 *
 * Siguiendo CQRS, este servicio no modifica
 * la información de las reseñas.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Service
@Transactional(readOnly = true)
public class ReviewQueryServiceImpl implements ReviewQueryService {

    // Repositorio encargado de consultar las reseñas.
    private final ReviewRepository reviewRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * Spring proporcionará la implementación
     * correspondiente del repositorio.
     *
     * @param reviewRepository Repositorio de reseñas.
     */
    public ReviewQueryServiceImpl(
            ReviewRepository reviewRepository
    ) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Obtiene todas las reseñas registradas.
     *
     * @param query Consulta de todas las reseñas.
     * @return Lista de reseñas almacenadas.
     */
    @Override
    public List<Review> handle(GetAllReviewsQuery query) {

        // Comprobamos que la consulta no sea nula.
        Objects.requireNonNull(
                query,
                "La consulta de reseñas es obligatoria."
        );

        // Solicitamos todas las reseñas al repositorio.
        return reviewRepository.findAll();
    }

    /**
     * Busca una reseña mediante su identificador.
     *
     * @param query Contiene el ID de la reseña.
     * @return La reseña encontrada o un Optional vacío.
     */
    @Override
    public Optional<Review> handle(GetReviewByIdQuery query) {

        // Validamos que la consulta exista.
        Objects.requireNonNull(
                query,
                "La consulta de reseña es obligatoria."
        );

        // Buscamos la reseña utilizando su ID.
        return reviewRepository.findById(
                query.reviewId()
        );
    }

    /**
     * Obtiene todas las reseñas asociadas
     * a un determinado tour turístico.
     *
     * @param query Contiene el ID del tour.
     * @return Lista de reseñas del tour.
     */
    @Override
    public List<Review> handle(GetReviewsByTourIdQuery query) {

        // Comprobamos que la consulta sea válida.
        Objects.requireNonNull(
                query,
                "La consulta de reseñas por tour es obligatoria."
        );

        // Consultamos las reseñas del tour indicado.
        return reviewRepository.findByTourId(
                query.tourId()
        );
    }
}
