package cn.evlight.types.model;

import cn.evlight.types.common.Constants;
import cn.evlight.types.enums.ResponseCode;
import lombok.Builder;
import lombok.Data;

/**
 * @Description: 统一返回对象
 * @Author: evlight
 * @Date: 2024/5/29
 */
@Data
@Builder
public class Response<T> {

    private String code;
    private String info;
    private T data;

    public static <T> Response<T> success(T data){
        return Response.<T>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(data)
                .build();
    }

    public static <T> Response<T> success(){
        return Response.<T>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .build();
    }

    public static <T> Response<T> error(T data){
        return Response.<T>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .data(data)
                .build();
    }

    public static <T> Response<T> error(){
        return Response.<T>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .build();
    }

    public static <T> Response<T> error(String info){
        return Response.<T>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(info)
                .build();
    }

    public static <T> Response<T> error(Constants.Exception e){
        return Response.<T>builder()
                .code(e.getCode())
                .info(e.getInfo())
                .build();
    }

}
