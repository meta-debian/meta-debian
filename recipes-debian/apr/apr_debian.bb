#
# base recipe: meta/recipes-support/apr/apr_1.4.8.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package autotools-brokensep

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4dfd4cd216828c8cae5de5a12f3844c8"

SRC_URI += " \
file://configure_fixes.patch \
"

BBCLASSEXTEND = "native"

# Fix QA issue file not shipped to any package
FILES_${PN} += "${datadir} ${libdir}"
