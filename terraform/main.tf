terraform {
  required_providers {
    yandex = {
      source = "yandex-cloud/yandex"
    }
  }
}

provider "yandex" {
  folder_id = var.folder_id
}

variable "folder_id" {
  type        = string
  description = "ID каталога Yandex Cloud, в котором создаются ресурсы"
}

variable "bucket_name" {
  type        = string
  description = "Имя бакета Yandex Object Storage"
}

variable "domain" {
  type        = string
  description = "Доменное имя сайта"
}

variable "cert_id" {
  type        = string
  description = "ID сертификата в Yandex Certificate Manager"
}

variable "dns_zone_id" {
  type        = string
  description = "ID DNS зоны в Yandex Cloud DNS"
}

resource "yandex_iam_service_account" "cloudphoto" {
  name      = "svs-cloudphoto"
  folder_id = var.folder_id
}

resource "yandex_resourcemanager_folder_iam_member" "cloudphoto_storage_admin" {
  folder_id = var.folder_id
  role      = "storage.admin"
  member    = "serviceAccount:${yandex_iam_service_account.cloudphoto.id}"
}

resource "yandex_iam_service_account_static_access_key" "cloudphoto" {
  service_account_id = yandex_iam_service_account.cloudphoto.id
}

resource "yandex_storage_bucket" "photos" {
  bucket    = var.bucket_name
  folder_id = var.folder_id

  https {
    certificate_id = var.cert_id
  }
}

resource "yandex_dns_recordset" "photos" {
  zone_id = var.dns_zone_id
  name    = "${var.bucket_name}."
  type    = "CNAME"
  ttl     = 600
  data    = ["${var.bucket_name}.website.yandexcloud.net."]
}

output "access_key" {
  value = yandex_iam_service_account_static_access_key.cloudphoto.access_key
}

output "secret_key" {
  value     = yandex_iam_service_account_static_access_key.cloudphoto.secret_key
  sensitive = true
}
