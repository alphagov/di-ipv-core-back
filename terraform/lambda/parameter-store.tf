resource "aws_ssm_parameter" "credential-issuers-config" {
  name      = "/${var.environment}/credential-issuers-config"
  type      = "String"
  value     = var.credential_issuers_config
  overwrite = true
}

resource "aws_iam_role_policy" "get-credential-issuers-config" {
  name = "get-credential-issuers-config"
  role = module.credential-issuer.iam_role_id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid = "GetCredentialIssuersConfig"
        Action = [
          "ssm:GetParameter",
          "ssm:GetParametersByPath"
        ]
        Effect   = "Allow"
        Resource = aws_ssm_parameter.credential-issuers-config.arn
      }
    ]
  })
}
