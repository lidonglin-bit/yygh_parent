package com.donglin.yygh.common.handler;

import com.donglin.yygh.common.exception.YyghException;
import com.donglin.yygh.common.result.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

//凡是由@ControllerAdvice 标记的类都表示全局异常类
@RestControllerAdvice  //@ControllerAdvice+@RequestBody=@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)  //粒度有点大
    public R handleException(Exception ex){
        ex.printStackTrace();
        return R.error().message(ex.getMessage());
    }

    @ExceptionHandler(value = SQLException.class)
    public R handleException(SQLException ex){
        ex.printStackTrace();
        return R.error().message("Sql异常");
    }

    @ExceptionHandler(value = ArithmeticException.class)
    public R handleException(ArithmeticException ex){
        ex.printStackTrace();
        return R.error().message("数学异常");
    }

    @ExceptionHandler(value = YyghException.class)
    public R handleException(YyghException ex){
        ex.printStackTrace();
        return R.error().message(ex.getMessage()).code(ex.getCode());
    }
}
