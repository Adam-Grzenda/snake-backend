package pl.put.snake.game.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pl.put.snake.game.logic.EntityNotFoundException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiExceptionResponse notFound(EntityNotFoundException e) {
        return new ApiExceptionResponse(e.getMessage());
    }

    private record ApiExceptionResponse(String message) {

    }
}
