package site.milanchen.chat.vo;

import lombok.Data;

/**
 * @author milan
 * @description
 */
@Data
public class ResultResponse<T> {

    public static final Integer CODE_SUCCESS = 0;
    public static final String MESSAGE_SUCCESS = "success";
    public static final Integer CODE_FAILURE = -1;
    public static final String MESSAGE_FAILURE = "failure";

    private int code;
    private String message;
    private T data;

    public boolean isSuccess() {
        return this.code == CODE_SUCCESS;
    }

    public static ResultResponse success() {
        ResultResponse resultResponse = new ResultResponse();
        resultResponse.setCode(CODE_SUCCESS);
        resultResponse.setMessage(MESSAGE_SUCCESS);
        return resultResponse;
    }

    public static <T> ResultResponse<T> success(T data) {
        ResultResponse resultResponse = success();
        resultResponse.setData(data);
        return resultResponse;
    }

    public static ResultResponse failure() {
        ResultResponse resultResponse = success();
        resultResponse.setCode(CODE_FAILURE);
        resultResponse.setMessage(MESSAGE_FAILURE);
        return resultResponse;
    }

    public static ResultResponse failure(String message) {
        ResultResponse resultResponse = success();
        resultResponse.setCode(CODE_FAILURE);
        resultResponse.setMessage(message);
        return resultResponse;
    }

    public static ResultResponse failure(int code, String message) {
        ResultResponse resultResponse = success();
        resultResponse.setCode(code);
        resultResponse.setMessage(message);
        return resultResponse;
    }

}
