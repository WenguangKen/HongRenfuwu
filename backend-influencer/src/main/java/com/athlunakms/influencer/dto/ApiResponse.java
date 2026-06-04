package com.athlunakms.influencer.dto;

/*
 * Exception performing whole class analysis ignored.
 */
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private boolean success;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse response = new ApiResponse();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        response.setSuccess(true);
        return response;
    }

    public static <T> ApiResponse<T> success() {
        return ApiResponse.success(null);
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse response = new ApiResponse();
        response.setCode(500);
        response.setMessage(message);
        response.setSuccess(false);
        return response;
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse response = new ApiResponse();
        response.setCode(code);
        response.setMessage(message);
        response.setSuccess(false);
        return response;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return (T)this.data;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ApiResponse)) {
            return false;
        }
        ApiResponse other = (ApiResponse)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.getCode() != other.getCode()) {
            return false;
        }
        if (this.isSuccess() != other.isSuccess()) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        Object this$data = this.getData();
        Object other$data = other.getData();
        return !(this$data == null ? other$data != null : !this$data.equals(other$data));
    }

    protected boolean canEqual(Object other) {
        return other instanceof ApiResponse;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getCode();
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result;
    }

    public String toString() {
        return "ApiResponse(code=" + this.getCode() + ", message=" + this.getMessage() + ", data=" + this.getData() + ", success=" + this.isSuccess() + ")";
    }

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }
}

