
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.internal.queryservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.queryservices.CommentQueryService;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.entities.Comment;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetAllCommentsQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetCommentByIdQuery;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.queries.GetCommentsByReviewIdQuery;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.CommentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementación del servicio de consultas de comentarios.
 *
 * Se encarga de recuperar la información
 * almacenada mediante el repositorio de dominio.
 *
 * Siguiendo CQRS, este servicio solamente
 * realiza operaciones de lectura.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Service
@Transactional(readOnly = true)
public class CommentQueryServiceImpl implements CommentQueryService {

    // Repositorio encargado de consultar los comentarios.
    private final CommentRepository commentRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * Spring proporcionará la implementación
     * del repositorio de comentarios.
     *
     * @param commentRepository Repositorio de dominio.
     */
    public CommentQueryServiceImpl(
            CommentRepository commentRepository
    ) {
        this.commentRepository = commentRepository;
    }

    /**
     * Obtiene todos los comentarios registrados.
     *
     * @param query Consulta sin parámetros.
     * @return Lista de comentarios.
     */
    @Override
    public List<Comment> handle(
            GetAllCommentsQuery query
    ) {

        // Comprobamos que la consulta no sea nula.
        Objects.requireNonNull(
                query,
                "La consulta de comentarios es obligatoria."
        );

        // Solicitamos todos los comentarios al repositorio.
        return commentRepository.findAll();
    }

    /**
     * Busca un comentario mediante su identificador.
     *
     * @param query Contiene el ID del comentario.
     * @return Comentario encontrado o un Optional vacío.
     */
    @Override
    public Optional<Comment> handle(
            GetCommentByIdQuery query
    ) {

        // Validamos que la consulta exista.
        Objects.requireNonNull(
                query,
                "La consulta del comentario es obligatoria."
        );

        // Buscamos el comentario por su identificador.
        return commentRepository.findById(
                query.commentId()
        );
    }

    /**
     * Obtiene los comentarios asociados
     * a una reseña turística específica.
     *
     * @param query Contiene el ID de la reseña.
     * @return Lista de comentarios relacionados.
     */
    @Override
    public List<Comment> handle(
            GetCommentsByReviewIdQuery query
    ) {

        // Comprobamos que la consulta sea válida.
        Objects.requireNonNull(
                query,
                "La consulta de comentarios por reseña es obligatoria."
        );

        // Obtenemos los comentarios de la reseña.
        return commentRepository.findByReviewId(
                query.reviewId()
        );
    }
}
