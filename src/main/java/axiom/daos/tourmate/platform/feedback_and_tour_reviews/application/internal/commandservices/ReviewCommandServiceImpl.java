
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.internal.commandservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices.ReviewCommandService;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteReviewCommand;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.ReviewRepository;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.repositories.CommentRepository;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Locale;

/**
 * Implementación del servicio de comandos de reseñas.
 *
 * Gestiona la creación, actualización y eliminación
 * de reseñas turísticas.
 *
 * Reglas principales:
 * - Un usuario solo puede reseñar un tour una vez.
 * - No se permite cambiar el usuario ni el tour.
 * - Solo se puede modificar la calificación.
 * - Los comentarios se eliminan junto con la reseña.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
@Service
public class ReviewCommandServiceImpl
        implements ReviewCommandService {

    // Repositorio encargado de gestionar las reseñas.
    private final ReviewRepository reviewRepository;

    // Repositorio encargado de gestionar los comentarios.
    private final CommentRepository commentRepository;

    // Nombre de la restricción de PostgreSQL
    // que impide reseñas duplicadas.
    private static final String UNIQUE_REVIEW_CONSTRAINT =
            "uk_reviews_user_tour";

    /**
     * Constructor con inyección de dependencias.
     */
    public ReviewCommandServiceImpl(
            ReviewRepository reviewRepository,
            CommentRepository commentRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Procesa la creación de una nueva reseña.
     *
     * Comprueba duplicados antes de guardar
     * y también controla los conflictos detectados
     * por la restricción UNIQUE de PostgreSQL.
     *
     * @param command Datos de creación.
     * @return Reseña creada o error de aplicación.
     */
    @Override
    public Result<Review, ApplicationError> handle(
            CreateReviewCommand command
    ) {

        try {

            // Comprobamos si el usuario ya calificó el tour.
            boolean alreadyReviewed =
                    reviewRepository.existsByUserIdAndTourId(
                            command.userId(),
                            command.tourId()
                    );

            // Rechazamos las reseñas duplicadas.
            if (alreadyReviewed) {
                return Result.failure(
                        ApplicationError.conflict(
                                "Review",
                                "El usuario ya registró una reseña para este tour."
                        )
                );
            }

            // Creamos el agregado del dominio.
            Review review = new Review(
                    command.userId(),
                    command.tourId(),
                    command.rating()
            );

            // Guardamos la reseña mediante el repositorio.
            // El adaptador utiliza saveAndFlush() para
            // detectar restricciones durante la operación.
            Review savedReview =
                    reviewRepository.save(review);

            // Devolvemos la reseña almacenada.
            return Result.success(savedReview);

        } catch (IllegalArgumentException exception) {

            // Convertimos las validaciones del dominio
            // en errores de aplicación.
            return Result.failure(
                    ApplicationError.validationError(
                            "Review",
                            exception.getMessage()
                    )
            );

        } catch (DataIntegrityViolationException exception) {

            // Comprobamos si PostgreSQL rechazó
            // una reseña por usuario y tour duplicados.
            if (isDuplicateReviewConstraint(exception)) {

                return Result.failure(
                        ApplicationError.conflict(
                                "Review",
                                "El usuario ya registró una reseña para este tour."
                        )
                );
            }

            // No todas las violaciones de integridad
            // son duplicados. No las confundimos con 409.
            return Result.failure(
                    ApplicationError.unexpected(
                            "create-review",
                            "Error de integridad al guardar la reseña."
                    )
            );

        } catch (RuntimeException exception) {

            // Gestionamos otros fallos inesperados.
            return Result.failure(
                    ApplicationError.unexpected(
                            "create-review",
                            exception.toString()
                    )
            );
        }
    }

    /**
     * Comprueba si un error de persistencia
     * corresponde a la restricción que impide
     * registrar reseñas duplicadas.
     *
     * Revisa también las causas internas porque
     * Spring puede envolver el error de PostgreSQL.
     *
     * @param exception Error recibido.
     * @return true cuando identifica la restricción.
     */
    private boolean isDuplicateReviewConstraint(
            Throwable exception
    ) {

        Throwable current = exception;

        // Recorremos la cadena de excepciones.
        while (current != null) {

            String message = current.getMessage();

            // Buscamos el nombre exacto de la restricción.
            if (message != null
                    && message.toLowerCase(Locale.ROOT)
                    .contains(UNIQUE_REVIEW_CONSTRAINT)) {

                return true;
            }

            // Revisamos la causa del error.
            current = current.getCause();
        }

        return false;
    }

    /**
     * Procesa la actualización de una reseña.
     *
     * Conserva el usuario y el tour originales.
     * Solamente permite modificar la calificación.
     *
     * @param command Datos de actualización.
     * @return Reseña actualizada o error.
     */
    @Override
    @Transactional
    public Result<Review, ApplicationError> handle(
            UpdateReviewCommand command
    ) {

        try {

            // Buscamos la reseña solicitada.
            var reviewOptional =
                    reviewRepository.findById(
                            command.reviewId()
                    );

            // Verificamos que exista.
            if (reviewOptional.isEmpty()) {

                return Result.failure(
                        ApplicationError.notFound(
                                "Review",
                                command.reviewId().toString()
                        )
                );
            }

            // Recuperamos el agregado original.
            Review review = reviewOptional.get();

            // Verificamos que no se intente
            // modificar el propietario de la reseña.
            boolean sameUser =
                    review.getUserId().equals(
                            command.userId()
                    );

            // Verificamos que el tour sea el original.
            boolean sameTour =
                    review.getTourId().equals(
                            command.tourId()
                    );

            // No permitimos cambiar estas relaciones.
            if (!sameUser || !sameTour) {

                return Result.failure(
                        ApplicationError.businessRuleViolation(
                                "Review association",
                                "No se puede cambiar el usuario "
                                        + "ni el tour de una reseña existente."
                        )
                );
            }

            // Actualizamos únicamente la calificación.
            // El Value Object Rating valida el valor.
            review.updateRating(
                    command.rating()
            );

            // Guardamos los cambios.
            Review updatedReview =
                    reviewRepository.save(review);

            return Result.success(updatedReview);

        } catch (IllegalArgumentException exception) {

            return Result.failure(
                    ApplicationError.validationError(
                            "Review",
                            exception.getMessage()
                    )
            );

        } catch (RuntimeException exception) {

            // Revertimos la transacción si la
            // actualización falla inesperadamente.
            TransactionAspectSupport
                    .currentTransactionStatus()
                    .setRollbackOnly();

            return Result.failure(
                    ApplicationError.unexpected(
                            "update-review",
                            exception.toString()
                    )
            );
        }
    }

    /**
     * Procesa la eliminación de una reseña.
     *
     * Primero elimina los comentarios relacionados
     * y después la reseña, dentro de una transacción.
     *
     * @param command Identificador de la reseña.
     * @return true si se eliminó o error.
     */
    @Override
    @Transactional
    public Result<Boolean, ApplicationError> handle(
            DeleteReviewCommand command
    ) {

        try {

            // Verificamos que la reseña exista.
            boolean exists =
                    reviewRepository.existsById(
                            command.reviewId()
                    );

            if (!exists) {

                return Result.failure(
                        ApplicationError.notFound(
                                "Review",
                                command.reviewId().toString()
                        )
                );
            }

            // Eliminamos los comentarios asociados.
            commentRepository.deleteByReviewId(
                    command.reviewId()
            );

            // Eliminamos la reseña.
            reviewRepository.deleteById(
                    command.reviewId()
            );

            return Result.success(true);

        } catch (RuntimeException exception) {

            // Revertimos todos los cambios
            // cuando ocurre un fallo.
            TransactionAspectSupport
                    .currentTransactionStatus()
                    .setRollbackOnly();

            return Result.failure(
                    ApplicationError.unexpected(
                            "delete-review",
                            exception.toString()
                    )
            );
        }
    }
}
