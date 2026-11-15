package demo.mable.global.exception;

import demo.mable.character.exception.CharacterRankMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(CharacterRankMismatchException.class)
    public ResponseEntity<CharacterRankMismatchException> handleCharacterRankMismatch() {
        return ResponseEntity.badRequest().body(new CharacterRankMismatchException());
    }

    @ExceptionHandler(Exception.class)
    public void exception(Exception e) {
        System.out.println("e.getMessage() = " + e.getMessage());
    }
}
