package com.miroshnychenko.kotlinweb.controller.error

import com.miroshnychenko.kotlinweb.exception.DepotException
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.HandlerMethod
import java.util.*
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class ControllerExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleArgumentNotValidException(ex: MethodArgumentNotValidException): List<ResponseEntity<String>> {
        val errors = ex.bindingResult.fieldErrors.stream().map { fieldError: FieldError -> Objects.requireNonNull(fieldError.code) + " " + Objects.requireNonNull(fieldError.defaultMessage) + " " + fieldError.field }.collect(Collectors.toList())
        return errors.stream().map { error: String -> ResponseEntity.internalServerError().body(error) }
                .collect(Collectors.toList())
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun requestBodyMissing(request: HttpServletRequest): Map<String?, Any> {
        val method = request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler") as HandlerMethod
        val requestBody = Arrays.stream(method.methodParameters)
                .map { m: MethodParameter -> m.parameterType.simpleName + " " + m.parameterName }
                .collect(Collectors.joining(","))
        return Arrays.stream(arrayOf(arrayOf<Any>("status", 400),
                arrayOf<Any>("message", "Required request body is missing: $requestBody")
        )).collect(Collectors.toMap({ o: Array<Any> -> o[0] as String }, { o: Array<Any> -> o[1] }))
    }

    @ExceptionHandler(DepotException::class)
    fun handleStoreException(ex: DepotException): ResponseEntity<String?> {
        val status: String = ex.status
        return ResponseEntity<String?>(ex.message, HttpStatus.resolve(status.toInt())!!)
    }
}