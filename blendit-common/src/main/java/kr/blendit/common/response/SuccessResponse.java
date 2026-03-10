package kr.blendit.common.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SuccessResponse {

    private String message;

    public SuccessResponse(String message) {
        this.message = message;
    }
}
