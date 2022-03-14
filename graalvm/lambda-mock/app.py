import flask
from flask import Flask, make_response, request

app = Flask(__name__)

@app.route("/2018-06-01/runtime/invocation/next")
def next_invocation():
    return make_response((
        {"sausage": "yum"},
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

