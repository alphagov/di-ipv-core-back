package uk.gov.di.ipv.core.session;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.powertools.tracing.Tracing;
import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;
import uk.gov.di.ipv.core.library.auditing.AuditEventTypes;
import uk.gov.di.ipv.core.library.dto.ClientSessionDetailsDto;
import uk.gov.di.ipv.core.library.exceptions.JarValidationException;
import uk.gov.di.ipv.core.library.exceptions.RecoverableJarValidationException;
import uk.gov.di.ipv.core.library.exceptions.SqsException;
import uk.gov.di.ipv.core.library.helpers.ApiGatewayResponseGenerator;
import uk.gov.di.ipv.core.library.helpers.LogHelper;
import uk.gov.di.ipv.core.library.service.AuditService;
import uk.gov.di.ipv.core.library.service.ConfigurationService;
import uk.gov.di.ipv.core.library.service.IpvSessionService;
import uk.gov.di.ipv.core.library.service.KmsRsaDecrypter;
import uk.gov.di.ipv.core.library.validation.JarValidator;

import java.text.ParseException;
import java.util.Map;

import static uk.gov.di.ipv.core.library.config.ConfigurationVariable.JAR_KMS_ENCRYPTION_KEY_ID;

public class IpvSessionStartHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String IPV_SESSION_ID_KEY = "ipvSessionId";
    private static final String CLIENT_ID_PARAM_KEY = "clientId";
    private static final String REQUEST_PARAM_KEY = "request";
    private static final String IS_DEBUG_JOURNEY_PARAM_KEY = "isDebugJourney";

    private final ConfigurationService configurationService;
    private final IpvSessionService ipvSessionService;
    private final KmsRsaDecrypter kmsRsaDecrypter;
    private final JarValidator jarValidator;
    private final AuditService auditService;

    @ExcludeFromGeneratedCoverageReport
    public IpvSessionStartHandler() {
        this.configurationService = new ConfigurationService();
        this.ipvSessionService = new IpvSessionService(configurationService);
        this.kmsRsaDecrypter =
                new KmsRsaDecrypter(
                        configurationService.getSsmParameter(JAR_KMS_ENCRYPTION_KEY_ID));
        this.jarValidator = new JarValidator(kmsRsaDecrypter, configurationService);
        this.auditService =
                new AuditService(AuditService.getDefaultSqsClient(), configurationService);
    }

    public IpvSessionStartHandler(
            IpvSessionService ipvSessionService,
            ConfigurationService configurationService,
            KmsRsaDecrypter kmsRsaDecrypter,
            JarValidator jarValidator,
            AuditService auditService) {
        this.ipvSessionService = ipvSessionService;
        this.configurationService = configurationService;
        this.kmsRsaDecrypter = kmsRsaDecrypter;
        this.jarValidator = jarValidator;
        this.auditService = auditService;
    }

    @Override
    @Tracing
    @Logging(clearState = true)
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent input, Context context) {
        LogHelper.attachComponentIdToLogs();
        try {
            Map<String, String> sessionParams =
                    objectMapper.readValue(input.getBody(), new TypeReference<>() {});
            if (!validateSessionParams(sessionParams)) {
                LOGGER.error("Validation of the client session params failed");
                return ApiGatewayResponseGenerator.proxyEmptyResponse(HttpStatus.SC_BAD_REQUEST);
            }

            SignedJWT signedJWT =
                    jarValidator.decryptJWE(JWEObject.parse(sessionParams.get(REQUEST_PARAM_KEY)));
            JWTClaimsSet claimsSet =
                    jarValidator.validateRequestJwt(
                            signedJWT, sessionParams.get(CLIENT_ID_PARAM_KEY));

            ClientSessionDetailsDto clientSessionDetailsDto =
                    generateClientSessionDetails(
                            claimsSet,
                            sessionParams.get(CLIENT_ID_PARAM_KEY),
                            Boolean.parseBoolean(sessionParams.get(IS_DEBUG_JOURNEY_PARAM_KEY)));

            String ipvSessionId =
                    ipvSessionService.generateIpvSession(clientSessionDetailsDto, null);

            auditService.sendAuditEvent(AuditEventTypes.IPV_JOURNEY_START);

            Map<String, String> response = Map.of(IPV_SESSION_ID_KEY, ipvSessionId);

            return ApiGatewayResponseGenerator.proxyJsonResponse(HttpStatus.SC_OK, response);
        } catch (RecoverableJarValidationException e) {
            LOGGER.error(
                    "Recoverable Jar validation failed because: {}",
                    e.getErrorObject().getDescription());

            ClientSessionDetailsDto clientSessionDetailsDto =
                    generateErrorClientSessionDetails(
                            e.getRedirectUri(), e.getClientId(), e.getState());

            String ipvSessionId =
                    ipvSessionService.generateIpvSession(
                            clientSessionDetailsDto, e.getErrorObject());

            Map<String, String> response = Map.of(IPV_SESSION_ID_KEY, ipvSessionId);

            return ApiGatewayResponseGenerator.proxyJsonResponse(HttpStatus.SC_OK, response);
        } catch (ParseException e) {
            LOGGER.error("Failed to parse the decrypted JWE because: {}", e.getMessage());
            return ApiGatewayResponseGenerator.proxyEmptyResponse(HttpStatus.SC_BAD_REQUEST);
        } catch (JarValidationException e) {
            LOGGER.error("Jar validation failed because: {}", e.getErrorObject().getDescription());
            return ApiGatewayResponseGenerator.proxyEmptyResponse(HttpStatus.SC_BAD_REQUEST);
        } catch (SqsException e) {
            LOGGER.error("Failed to send audit event to SQS queue because: {}", e.getMessage());
            return ApiGatewayResponseGenerator.proxyJsonResponse(
                    HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (JsonProcessingException | IllegalArgumentException e) {
            LOGGER.error("Failed to parse request body into map because: {}", e.getMessage());
            return ApiGatewayResponseGenerator.proxyEmptyResponse(HttpStatus.SC_BAD_REQUEST);
        }
    }

    @Tracing
    private boolean validateSessionParams(Map<String, String> sessionParams) {
        boolean valid = true;

        if (StringUtils.isBlank(sessionParams.get(CLIENT_ID_PARAM_KEY))) {
            LOGGER.warn("Missing client_id query parameter");
            valid = false;
        }
        LogHelper.attachClientIdToLogs(sessionParams.get(CLIENT_ID_PARAM_KEY));

        if (StringUtils.isBlank(sessionParams.get(REQUEST_PARAM_KEY))) {
            LOGGER.warn("Missing request query parameter");
            valid = false;
        }

        return valid;
    }

    @Tracing
    private ClientSessionDetailsDto generateClientSessionDetails(
            JWTClaimsSet claimsSet, String clientId, boolean isDebugJourney) throws ParseException {
        return new ClientSessionDetailsDto(
                claimsSet.getStringClaim("response_type"),
                clientId,
                claimsSet.getStringClaim("redirect_uri"),
                claimsSet.getStringClaim("state"),
                claimsSet.getSubject(),
                isDebugJourney);
    }

    @Tracing
    private ClientSessionDetailsDto generateErrorClientSessionDetails(
            String redirectUri, String clientId, String state) {
        return new ClientSessionDetailsDto(null, clientId, redirectUri, state, null, false);
    }
}
