package uk.gov.di.ipv.core.validatecricheck;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.lambda.powertools.logging.Logging;
import software.amazon.lambda.powertools.tracing.Tracing;
import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;
import uk.gov.di.ipv.core.library.domain.JourneyResponse;
import uk.gov.di.ipv.core.library.exceptions.HttpResponseExceptionWithErrorBody;
import uk.gov.di.ipv.core.library.helpers.ApiGatewayResponseGenerator;
import uk.gov.di.ipv.core.library.helpers.LogHelper;
import uk.gov.di.ipv.core.library.helpers.RequestHelper;
import uk.gov.di.ipv.core.library.service.ConfigurationService;
import uk.gov.di.ipv.core.library.service.UserIdentityService;
import uk.gov.di.ipv.core.validatecricheck.validation.CriCheckValidator;

import java.util.Map;

public class ValidateCriCheckHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String CRI_ID = "criId";
    public static final String IPV_SESSION_ID_HEADER_KEY = "ipv-session-id";
    public static final String JOURNEY_NEXT = "/journey/next";
    public static final String JOURNEY_FAIL = "/journey/fail";
    public static final String JOURNEY_ERROR = "/journey/error";

    private final CriCheckValidator criCheckValidator;
    private final UserIdentityService userIdentityService;

    public ValidateCriCheckHandler(
            CriCheckValidator criCheckValidator, UserIdentityService userIdentityService) {
        this.criCheckValidator = criCheckValidator;
        this.userIdentityService = userIdentityService;
    }

    @ExcludeFromGeneratedCoverageReport
    public ValidateCriCheckHandler() {
        this.criCheckValidator = new CriCheckValidator();
        this.userIdentityService = new UserIdentityService(new ConfigurationService());
    }

    @Override
    @Tracing
    @Logging(clearState = true)
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent input, Context context) {
        LogHelper.attachComponentIdToLogs();
        try {
            String ipvSessionId = RequestHelper.getIpvSessionId(input);
            String criId = getCriId(input.getPathParameters());
            LogHelper.attachCriIdToLogs(criId);

            JourneyResponse journeyResponse =
                    criCheckValidator.isSuccess(
                                    userIdentityService.getUserIssuedCredential(
                                            ipvSessionId, criId))
                            ? new JourneyResponse(JOURNEY_NEXT)
                            : new JourneyResponse(JOURNEY_FAIL);

            LOGGER.info("VALIDATION RESULT: {}", journeyResponse.getJourney());

            return ApiGatewayResponseGenerator.proxyJsonResponse(HttpStatus.SC_OK, journeyResponse);
        } catch (HttpResponseExceptionWithErrorBody e) {
            return ApiGatewayResponseGenerator.proxyEmptyResponse(e.getResponseCode());

        } catch (CriCheckValidationException e) {
            return ApiGatewayResponseGenerator.proxyJsonResponse(
                    HttpStatus.SC_OK, new JourneyResponse(JOURNEY_ERROR));
        }
    }

    @Tracing
    private String getCriId(Map<String, String> pathParameters)
            throws HttpResponseExceptionWithErrorBody {
        if (pathParameters == null || StringUtils.isBlank(pathParameters.get(CRI_ID))) {
            LOGGER.error("Credential issuer ID path parameter missing");
            throw new HttpResponseExceptionWithErrorBody(HttpStatus.SC_BAD_REQUEST);
        }
        return pathParameters.get(CRI_ID);
    }
}
