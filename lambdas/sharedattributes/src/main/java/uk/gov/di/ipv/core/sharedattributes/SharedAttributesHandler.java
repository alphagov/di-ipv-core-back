package uk.gov.di.ipv.core.sharedattributes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.lambda.powertools.tracing.Tracing;
import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;
import uk.gov.di.ipv.core.library.domain.BirthDate;
import uk.gov.di.ipv.core.library.domain.ErrorResponse;
import uk.gov.di.ipv.core.library.domain.NameParts;
import uk.gov.di.ipv.core.library.domain.SharedAttributes;
import uk.gov.di.ipv.core.library.domain.SharedAttributesResponse;
import uk.gov.di.ipv.core.library.domain.SharedAttributesResponseNew;
import uk.gov.di.ipv.core.library.exceptions.HttpResponseExceptionWithErrorBody;
import uk.gov.di.ipv.core.library.helpers.ApiGatewayResponseGenerator;
import uk.gov.di.ipv.core.library.helpers.JwtHelper;
import uk.gov.di.ipv.core.library.helpers.KmsSigner;
import uk.gov.di.ipv.core.library.helpers.RequestHelper;
import uk.gov.di.ipv.core.library.service.ConfigurationService;
import uk.gov.di.ipv.core.library.service.UserIdentityService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static uk.gov.di.ipv.core.library.domain.SharedAttributesResponseNew.buildBirthDates;
import static uk.gov.di.ipv.core.library.domain.SharedAttributesResponseNew.buildNameParts;

public class SharedAttributesHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SharedAttributesHandler.class);

    public static final String IPV_SESSION_ID_HEADER_KEY = "ipv-session-id";
    public static final int BAD_REQUEST = 400;
    public static final int OK = 200;
    public static final String VC_HTTP_API_CLAIM = "vc_http_api";
    public static final String CLAIMS_CLAIM = "claims";
    private final UserIdentityService userIdentityService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final JWSSigner signer;

    public SharedAttributesHandler(UserIdentityService userIdentityService, JWSSigner signer) {
        this.userIdentityService = userIdentityService;
        this.signer = signer;
    }

    @ExcludeFromGeneratedCoverageReport
    public SharedAttributesHandler() {
        ConfigurationService configurationService = new ConfigurationService();
        this.userIdentityService = new UserIdentityService(configurationService);
        this.signer = new KmsSigner(configurationService.getSharedAttributesSigningKeyId());
    }

    @Override
    @Tracing
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent input, Context context) {
        try {
            String ipvSessionId = getIpvSessionId(input.getHeaders());
            SharedAttributesResponse sharedAttributesResponse = getSharedAttributes(ipvSessionId);

            SignedJWT signedJWT = signSharedAttributesResponse(sharedAttributesResponse);

            return ApiGatewayResponseGenerator.proxyJoseResponse(OK, signedJWT.serialize());
        } catch (HttpResponseExceptionWithErrorBody e) {
            return ApiGatewayResponseGenerator.proxyJsonResponse(
                    e.getResponseCode(), e.getErrorBody());
        }
    }

    @Tracing
    private SharedAttributesResponse getSharedAttributes(String ipvSessionId)
            throws HttpResponseExceptionWithErrorBody {
        Map<String, String> credentials =
                userIdentityService.getUserIssuedCredentials(ipvSessionId);

        List<SharedAttributes> sharedAttributes = new ArrayList<>();
        for (String credential : credentials.values()) {
            try {
                sharedAttributes.add(mapper.readValue(credential, SharedAttributes.class));
            } catch (JsonProcessingException e) {
                LOGGER.error("Failed to get Shared Attributes: {}", e.getMessage());
                throw new HttpResponseExceptionWithErrorBody(
                        500, ErrorResponse.FAILED_TO_GET_SHARED_ATTRIBUTES);
            }
        }
        return SharedAttributesResponse.from(sharedAttributes);
    }

    @Tracing
    private SignedJWT signSharedAttributesResponse(
            SharedAttributesResponse sharedAttributesResponse)
            throws HttpResponseExceptionWithErrorBody {
        try {
            //TOdo - new
            SharedAttributesResponseNew sharedAttributesResponseNew =convertResponse(sharedAttributesResponse);

            return JwtHelper.createSignedJwtFromObject(
                    Map.of(CLAIMS_CLAIM, Map.of(VC_HTTP_API_CLAIM, sharedAttributesResponseNew)),  //TOdo set to New
                    signer);
        } catch (JOSEException e) {
            LOGGER.error("Failed to sign Shared Attributes: {}", e.getMessage());
            throw new HttpResponseExceptionWithErrorBody(
                    500, ErrorResponse.FAILED_TO_SIGN_SHARED_ATTRIBUTES);
        }
    }

    private String getIpvSessionId(Map<String, String> headers)
            throws HttpResponseExceptionWithErrorBody {
        String ipvSessionId = RequestHelper.getHeaderByKey(headers, IPV_SESSION_ID_HEADER_KEY);
        if (ipvSessionId == null) {
            LOGGER.error("{} not present in header", IPV_SESSION_ID_HEADER_KEY);
            throw new HttpResponseExceptionWithErrorBody(
                    BAD_REQUEST, ErrorResponse.MISSING_IPV_SESSION_ID);
        }
        return ipvSessionId;
    }

    private SharedAttributesResponseNew convertResponse(SharedAttributesResponse sharedAttributesResponse) {
        Set<NameParts> name = buildNameParts(sharedAttributesResponse.getNames());
        Set<BirthDate> bday = buildBirthDates(sharedAttributesResponse.getDateOfBirths());
        Set<Map<String, String>> addresses = new HashSet<>();
        Map<String,String> s = new HashMap<>();
        s.put("postcode","S5 6un");
        addresses.add(s);
        Set<Map<String, String>> addressHistory = new HashSet<>();
        addressHistory.add(s);
        return new SharedAttributesResponseNew(name,bday,addresses,addressHistory);
    }



}
