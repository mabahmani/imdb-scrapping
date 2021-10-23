package ir.mab.imdbscrapping.model;

public class ApiResponse <T>{
    T data;
    String error;
    Boolean success;

    public ApiResponse(T data, String error, Boolean success) {
        this.data = data;
        this.error = error;
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
