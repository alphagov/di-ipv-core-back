package uk.gov.di.ipv.core.library.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorResponse {
    MISSING_QUERY_PARAMETERS(1000, "Missing query parameters for auth request"),
    MISSING_AUTHORIZATION_CODE(1003, "Missing authorization code"),
    FAILED_TO_EXCHANGE_AUTHORIZATION_CODE(
            1004, "Failed to exchange the authorization code for an access token"),
    MISSING_CREDENTIAL_ISSUER_ID(1007, "Missing credential issuer id"),
    INVALID_CREDENTIAL_ISSUER_ID(1008, "Invalid credential issuer id"),
    INVALID_TOKEN_REQUEST(1009, "Invalid token request"),
    MISSING_IPV_SESSION_ID(1010, "Missing ipv session id header"),
    FAILED_TO_GET_CREDENTIAL_FROM_ISSUER(1011, "Failed to get credential from issuer"),
    FAILED_TO_SAVE_CREDENTIAL(1012, "Failed to save credential"),
    FAILED_TO_PARSE_OAUTH_QUERY_STRING_PARAMETERS(
            1013, "Failed to parse oauth2-specific query string parameters"),
    FAILED_TO_PARSE_CREDENTIAL_ISSUER_CONFIG(
            1014, "Failed to parse credential issuers config to credential issuers object"),
    FAILED_TO_GET_SHARED_ATTRIBUTES(1015, "Failed to get Shared Attributes"),
    FAILED_TO_SIGN_SHARED_ATTRIBUTES(1016, "Failed to sign Shared Attributes"),
    INVALID_REDIRECT_URL(1017, "Provided redirect URL is not in those configured for client"),
    INVALID_REQUEST_PARAM(1018, "Invalid request param"),
    FAILED_TO_PARSE_JWK(1019, "Failed to parse JWK"),
    FAILED_TO_VALIDATE_VERIFIABLE_CREDENTIAL(1020, "Failed to validate verifiable credential"),
    INVALID_SESSION_ID(
            1021,
            "Invalid ipv-session-id has been provided, could not record of that requested session"),
    FAILED_JOURNEY_ENGINE_STEP(1022, "Failed to execute journey engine step"),
    MISSING_JOURNEY_STEP_URL_PATH_PARAM(1023, "Missing journeyStep url path parameter in request"),
    FAILED_TO_PARSE_ISSUED_CREDENTIALS(1024, "Failed to parse issued credentials"),
    CREDENTIAL_SUBJECT_MISSING(1025, "Credential subject missing from verified credential"),
    INVALID_SESSION_REQUEST(1026, "Failed to parse the session start request");

    @JsonProperty("code")
    private final int code;

    @JsonProperty("message")
    private final String message;

    ErrorResponse(
            @JsonProperty(required = true, value = "code") int code,
            @JsonProperty(required = true, value = "message") String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
