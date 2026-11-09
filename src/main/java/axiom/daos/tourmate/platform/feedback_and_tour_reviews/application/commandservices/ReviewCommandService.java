
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.application.commandservices;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.aggregates.Review;

import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.CreateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.UpdateReviewCommand;
import axiom.daos.tourmate.platform.feedback_and_tour_reviews.domain.model.commands.DeleteReviewCommand;

import axiom.daos.tourmate.platform.shared.application.result.ApplicationError;
import axiom.daos.tourmate.platform.shared.application.result.Result;

/**
 * Contrato del servicio de comandos de reseñas.
 *
 * Define las operaciones que permiten crear,
 * actualizar y eliminar reseñas turísticas.
 *
 * Utilizamos el patrón CQRS para separar las
 * operaciones de escritura de las consultas.
 *
 * Bounded Context: Feedback and Tour Reviews.
 */
public interface ReviewCommandService {

    /**
     * Procesa la creación de una nueva reseña.
     *
     * La implementación deberá verificar que
     * el usuario no haya calificado previamente
     * el mismo tour.
     *
     * @param command Datos para crear la reseña.
     * @return Resultado con la reseña creada
     *         o un error de aplicación.
     */
    Result<Review, ApplicationError> handle(
            CreateReviewCommand command
    );

    /**
     * Procesa la actualización de una reseña.
     *
     * La implementación deberá comprobar que
     * la reseña exista, que no se produzcan
     * duplicados y que la operación esté autorizada.
     *
     * @param command Datos que se actualizarán.
     * @return Resultado con la reseña actualizada
     *         o un error de aplicación.
     */
    Result<Review, ApplicationError> handle(
            UpdateReviewCommand command
    );

    /**
     * Procesa la eliminación de una reseña.
     *
     * La implementación deberá verificar que
     * la reseña exista y coordinar la eliminación
     * de sus comentarios relacionados.
     *
     * @param command Identificador de la reseña.
     * @return Resultado con true si se eliminó,
     *         o un error de aplicación.
     */
    Result<Boolean, ApplicationError> handle(
            DeleteReviewCommand command
    );
}
