package demo.mable.global.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public void exception(Exception e) {
        System.out.println("e.getMessage() = " + e.getMessage());
    }
}
