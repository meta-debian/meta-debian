SUMMARY = "Library for using InfiniBand/RDMA "verbs" directly."
DESCRIPTION = "libibverbs is a library that allows userspace processes to use RDMA "verbs" as \
described in the InfiniBand Architecture Specification and the RDMA Protocol Verbs Specification."
HOMEPAGE = "https://www.openfabrics.org/"

inherit debian-package autotools
PV = "1.1.8"

# source format is 3.0 but there is no patch
DEBIAN_QUILT_PATCHES = ""

PR = "r0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=7c557f27dd795ba77cc419dddc656b51"

# Re-ship packages and files according to Debian package list
PACKAGES += "ibverbs-utils"

FILES_ibverbs-utils = " \
  ${bindir} \
"

FILES_${PN} = " \
  ${libdir}/ \
"
