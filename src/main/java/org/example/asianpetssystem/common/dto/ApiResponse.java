package org.example.asianpetssystem.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一响应包装类
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    private T data;

    /**
     * 成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * 成功响应（带自定义消息）
     *
     * @param data    响应数据
     * @param message 自定义消息
     * @param <T>     数据类型
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * 错误响应
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * 错误响应（带数据）
     *
     * @param code    错误码
     * @param message 错误消息
     * @param data    错误数据
     * @param <T>     数据类型
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    /**
     * 通用错误响应（500）
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(500, message);
    }

    /**
     * 通用错误响应（带数据）
     *
     * @param message 错误消息
     * @param data    错误数据
     * @param <T>     数据类型
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return error(500, message, data);
    }

    /**
     * 业务错误响应
     *
     * @param code    业务错误码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> businessError(int code, String message) {
        return error(code, message);
    }

    /**
     * 参数验证错误响应
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return ApiResponse实例
     */
    public static <T> ApiResponse<T> validationError(String message) {
        return error(400, message);
    }
}
