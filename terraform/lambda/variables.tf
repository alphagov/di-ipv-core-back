variable "environment" {
  type        = string
  description = "identifier for the environment to be added to tags and resource names"
}

locals {
  default_tags = {
    Environment = var.environment
    Source      = "github.com/alphagov/di-ipv-core-back/terraform/lambda"
  }
}
