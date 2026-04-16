package work.soho.ai.biz.exception;

import lombok.Getter;

@Getter
public class AiOpenApiGuardException extends RuntimeException {
    private final String clientMessage;

    private final String errorCode;

    private final String rejectReason;

    private final boolean riskHit;

    private final boolean banHit;

    private final int httpStatus;

    public AiOpenApiGuardException(String message,
                                   String clientMessage,
                                   String errorCode,
                                   String rejectReason,
                                   boolean riskHit,
                                   boolean banHit,
                                   int httpStatus) {
        super(message);
        this.clientMessage = clientMessage;
        this.errorCode = errorCode;
        this.rejectReason = rejectReason;
        this.riskHit = riskHit;
        this.banHit = banHit;
        this.httpStatus = httpStatus;
    }
}
