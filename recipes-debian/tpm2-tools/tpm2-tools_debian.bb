# base recipe: meta-security/meta-tpm/recipes-tpm2/tpm2-tools/tpm2-tools_3.1.3.bb
# base branch: warrior

SUMMARY = "Tools for TPM2."
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=91b7c548d73ea16537799e8060cea819"
SECTION = "tpm"

inherit debian-package
require recipes-debian/sources/tpm2-tools.inc

DEPENDS = "pkgconfig tpm2-tss openssl curl autoconf-archive"

inherit autotools pkgconfig
