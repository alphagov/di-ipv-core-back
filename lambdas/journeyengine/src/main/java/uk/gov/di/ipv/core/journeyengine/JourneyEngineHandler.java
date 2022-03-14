package uk.gov.di.ipv.core.journeyengine;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.lambda.powertools.tracing.Tracing;
import uk.gov.di.ipv.core.journeyengine.domain.JourneyEngineResult;
import uk.gov.di.ipv.core.journeyengine.domain.JourneyResponse;
import uk.gov.di.ipv.core.journeyengine.domain.PageResponse;
import uk.gov.di.ipv.core.journeyengine.exceptions.JourneyEngineException;
import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;
import uk.gov.di.ipv.core.library.domain.ErrorResponse;
import uk.gov.di.ipv.core.library.domain.UserStates;
import uk.gov.di.ipv.core.library.helpers.ApiGatewayResponseGenerator;
import uk.gov.di.ipv.core.library.helpers.RequestHelper;
import uk.gov.di.ipv.core.library.persistence.item.IpvSessionItem;
import uk.gov.di.ipv.core.library.service.ConfigurationService;
import uk.gov.di.ipv.core.library.service.IpvSessionService;

import java.util.List;

import static uk.gov.di.ipv.core.library.domain.UserStates.CRI_ACTIVITY_HISTORY;
import static uk.gov.di.ipv.core.library.domain.UserStates.CRI_ADDRESS;
import static uk.gov.di.ipv.core.library.domain.UserStates.CRI_FRAUD;
import static uk.gov.di.ipv.core.library.domain.UserStates.CRI_KBV;
import static uk.gov.di.ipv.core.library.domain.UserStates.CRI_UK_PASSPORT;
import static uk.gov.di.ipv.core.library.domain.UserStates.DEBUG_PAGE;
import static uk.gov.di.ipv.core.library.domain.UserStates.TRANSITION_PAGE_1;
import static uk.gov.di.ipv.core.library.domain.UserStates.TRANSITION_PAGE_2;

public class JourneyEngineHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JourneyEngineHandler.class.getName());

    private static final String IPV_SESSION_ID_HEADER_KEY = "ipv-session-id";
    private static final String JOURNEY_STEP_PARAM = "journeyStep";
    private static final String UK_PASSPORT_CRI_ID = "ukPassport";
    private static final String ADDRESS_CRI_ID = "address";
    private static final String KBV_CRI_ID = "kbv";
    private static final String FRAUD_CRI_ID = "fraud";
    private static final String ACTIVITY_HISTORY_CRI_ID = "activityHistory";

    private static final List<String> VALID_JOURNEY_STEPS = List.of("next");

    private final IpvSessionService ipvSessionService;
    private final ConfigurationService configurationService;

    public JourneyEngineHandler(
            IpvSessionService ipvSessionService, ConfigurationService configurationService) {
        this.ipvSessionService = ipvSessionService;
        this.configurationService = configurationService;
    }

    @ExcludeFromGeneratedCoverageReport
    public JourneyEngineHandler() {
        this.configurationService = new ConfigurationService();
        this.ipvSessionService = new IpvSessionService(configurationService);
    }

    @Override
    @Tracing
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent input, Context context) {
        String journeyStep = input.getPathParameters().get(JOURNEY_STEP_PARAM);

        var ipvSessionId =
                RequestHelper.getHeaderByKey(input.getHeaders(), IPV_SESSION_ID_HEADER_KEY);

        if (ipvSessionId == null || ipvSessionId.isEmpty()) {
            LOGGER.warn("User credentials could not be retrieved. No session ID received.");
            return ApiGatewayResponseGenerator.proxyJsonResponse(
                    HttpStatus.SC_BAD_REQUEST, ErrorResponse.MISSING_IPV_SESSION_ID);
        }

        IpvSessionItem ipvSessionItem = ipvSessionService.getIpvSession(ipvSessionId);

        if (ipvSessionItem == null) {
            LOGGER.warn("Failed to find ipv-session");
            return ApiGatewayResponseGenerator.proxyJsonResponse(
                    HttpStatus.SC_BAD_REQUEST, ErrorResponse.INVALID_SESSION_ID);
        }
        try {
            JourneyEngineResult journeyEngineResult =
                    executeJourneyEvent(journeyStep, ipvSessionItem);

            if (journeyEngineResult.getJourneyResponse() != null) {
                return ApiGatewayResponseGenerator.proxyJsonResponse(
                        HttpStatus.SC_OK, journeyEngineResult.getJourneyResponse());
            } else {
                return ApiGatewayResponseGenerator.proxyJsonResponse(
                        HttpStatus.SC_OK, journeyEngineResult.getPageResponse());
            }
        } catch (JourneyEngineException e) {
            return ApiGatewayResponseGenerator.proxyJsonResponse(
                    HttpStatus.SC_INTERNAL_SERVER_ERROR, ErrorResponse.FAILED_JOURNEY_ENGINE_STEP);
        }
    }

    private JourneyEngineResult executeJourneyEvent(
            String journeyStep, IpvSessionItem ipvSessionItem) throws JourneyEngineException {
        String criStartUri = configurationService.getIpvJourneyCriStartUri();
        String journeyEndUri = configurationService.getIpvJourneySessionEnd();

        String currentUserState = ipvSessionItem.getUserState();

        try {
            UserStates currentUserStateValue = UserStates.valueOf(currentUserState);

            JourneyEngineResult.Builder builder = new JourneyEngineResult.Builder();

            if (!VALID_JOURNEY_STEPS.contains(journeyStep)) {
                LOGGER.warn("Unknown journey step: {}", journeyStep);
                throw new JourneyEngineException(
                        "Invalid journey step provided, failed to execute journey engine step.");
            }

            switch (currentUserStateValue) {
                case INITIAL_IPV_JOURNEY:
                    updateUserState(TRANSITION_PAGE_1, ipvSessionItem);
                    builder.setPageResponse(new PageResponse(TRANSITION_PAGE_1.value));
                    break;
                case TRANSITION_PAGE_1:
                    updateUserState(CRI_UK_PASSPORT, ipvSessionItem);
                    builder.setJourneyResponse(
                            new JourneyResponse(criStartUri + UK_PASSPORT_CRI_ID));
                    break;
                case CRI_UK_PASSPORT:
                    updateUserState(CRI_ADDRESS, ipvSessionItem);
                    builder.setJourneyResponse(new JourneyResponse(criStartUri + ADDRESS_CRI_ID));
                    break;
                case CRI_ADDRESS:
                    updateUserState(CRI_KBV, ipvSessionItem);
                    builder.setJourneyResponse(new JourneyResponse(criStartUri + KBV_CRI_ID));
                    break;
                case CRI_KBV:
                    updateUserState(TRANSITION_PAGE_2, ipvSessionItem);
                    builder.setPageResponse(new PageResponse(TRANSITION_PAGE_2.value));
                    break;
                case TRANSITION_PAGE_2:
                    updateUserState(CRI_FRAUD, ipvSessionItem);
                    builder.setJourneyResponse(new JourneyResponse(criStartUri + FRAUD_CRI_ID));
                    break;
                case CRI_FRAUD:
                    updateUserState(CRI_ACTIVITY_HISTORY, ipvSessionItem);
                    builder.setJourneyResponse(
                            new JourneyResponse(criStartUri + ACTIVITY_HISTORY_CRI_ID));
                    break;
                case CRI_ACTIVITY_HISTORY:
                    builder.setJourneyResponse(new JourneyResponse(journeyEndUri));
                    break;
                case DEBUG_PAGE:
                    builder.setPageResponse(new PageResponse(DEBUG_PAGE.value));
                    break;
            }

            return builder.build();
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Unknown user state: {}", currentUserState);
            throw new JourneyEngineException(
                    "Unknown user state, failed to execute journey engine step.");
        }
    }

    private void updateUserState(UserStates updatedStateValue, IpvSessionItem previosSessionItem) {
        IpvSessionItem updatedIpvSessionItem = new IpvSessionItem();
        updatedIpvSessionItem.setIpvSessionId(previosSessionItem.getIpvSessionId());
        updatedIpvSessionItem.setCreationDateTime(previosSessionItem.getCreationDateTime());
        updatedIpvSessionItem.setUserState(updatedStateValue.toString());

        ipvSessionService.updateIpvSession(updatedIpvSessionItem);
    }
}
