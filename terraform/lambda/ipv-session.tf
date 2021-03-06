module "ipv-session" {
  source      = "../modules/endpoint"
  environment = var.environment

  rest_api_id            = aws_api_gateway_rest_api.ipv_internal.id
  rest_api_execution_arn = aws_api_gateway_rest_api.ipv_internal.execution_arn
  root_resource_id       = aws_api_gateway_rest_api.ipv_internal.root_resource_id
  http_method            = "POST"
  path_part              = "ipv-session"
  handler                = "uk.gov.di.ipv.core.session.IpvSessionHandler::handleRequest"
  function_name          = "${var.environment}-create-ipv-session"
  role_name              = "${var.environment}-ipv-session-role"

  allow_access_to_ipv_sessions_table = true
  ipv_sessions_table_policy_arn      = aws_iam_policy.policy-ipv-sessions-table.arn
  ipv_sessions_table_name            = aws_dynamodb_table.ipv-sessions.name
  credential_issuers_iam_policy_arn  = var.credential_issuers_iam_policy_arn
}
