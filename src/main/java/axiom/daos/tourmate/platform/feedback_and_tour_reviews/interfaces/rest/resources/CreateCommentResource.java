
package axiom.daos.tourmate.platform.feedback_and_tour_reviews.interfaces.rest.resources;

/**
 * Recurso REST utilizado para solicitar
 * la creación de un comentario turístico.
 *
 * Representa los datos que el frontend Angular
 * envía al backend mediante una petición POST.
 *
 * Bounded Context: Feedback and Tour Reviews.
 *
 * @param reviewId Identificador de la reseña
 *                 a la que pertenece el comentario.
 * @param content Texto escrito por el usuario.
 */
public record CreateCommentResource(
        Long reviewId,
        String content
) {

    /**
     * Este recurso solamente transporta
     * los datos recibidos desde la API REST.
     *
     * Las validaciones del contenido y del
     * identificador serán realizadas por
     * la lógica del dominio y la aplicación.
     */
}
