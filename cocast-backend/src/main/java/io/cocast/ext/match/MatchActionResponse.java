package io.cocast.ext.match;

/**
 * Response for match action API
 */
public class MatchActionResponse {

    private String message;
    private Integer status;
    private boolean match;

    public MatchActionResponse() {
        match = false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }
}
