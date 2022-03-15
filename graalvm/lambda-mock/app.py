import json
import flask
from flask import Flask, make_response, request

app = Flask(__name__)

# https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format
input_json_string = '{"resource":"/my/path","path":"/my/path","httpMethod":"GET","headers":{"header1":"value1","header2":"value2"},"multiValueHeaders":{"header1":["value1"],"header2":["value1","value2"]},"queryStringParameters":{"parameter1":"value1","parameter2":"value"},"multiValueQueryStringParameters":{"parameter1":["value1","value2"],"parameter2":["value"]},"requestContext":{"accountId":"123456789012","apiId":"id","authorizer":{"claims":null,"scopes":null},"domainName":"id.execute-api.us-east-1.amazonaws.com","domainPrefix":"id","extendedRequestId":"request-id","httpMethod":"GET","identity":{"accessKey":null,"accountId":null,"caller":null,"cognitoAuthenticationProvider":null,"cognitoAuthenticationType":null,"cognitoIdentityId":null,"cognitoIdentityPoolId":null,"principalOrgId":null,"sourceIp":"IP","user":null,"userAgent":"user-agent","userArn":null,"clientCert":{"clientCertPem":"CERT_CONTENT","subjectDN":"www.example.com","issuerDN":"Exampleissuer","serialNumber":"a1:a1:a1:a1:a1:a1:a1:a1:a1:a1:a1:a1:a1:a1:a1:a1","validity":{"notBefore":"May2812:30:022019GMT","notAfter":"Aug509:36:042021GMT"}}},"path":"/my/path","protocol":"HTTP/1.1","requestId":"id=","requestTime":"04/Mar/2020:19:15:17+0000","requestTimeEpoch":1583349317135,"resourceId":null,"resourcePath":"/my/path","stage":"$default"},"pathParameters":null,"stageVariables":null,"body":"HellofromLambda!","isBase64Encoded":false}'
api_gateway_lambda_input_body = json.loads(input_json_string)

@app.route("/2018-06-01/runtime/invocation/next")
def next_invocation():
    return make_response((
        api_gateway_lambda_input_body,
        200,
        {
            "Lambda-Runtime-Aws-Request-Id": "8476a536-e9f4-11e8-9739-2dfe598c3fcd",
            "Lambda-Runtime-Deadline-Ms": "2542409706888",
            "Lambda-Runtime-Invoked-Function-Arn": "arn:aws:lambda:us-east-2:123456789012:function:custom-runtime",
            "Lambda-Runtime-Trace-Id": "Root=1-5bef4de7-ad49b0e87f6ef6c87fc2e700;Parent=9a9197af755a6419;Sampled=1",
        }
    ))

@app.route("/2018-06-01/runtime/invocation/<aws_request_id>/response", methods=['POST'])
def response(aws_request_id):
    print(request.json)
    return make_response(('SUCCESS'), 202)

