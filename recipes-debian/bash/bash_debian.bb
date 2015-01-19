require recipes-extended/bash/${BPN}_4.3.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-extended/bash/${BPN}:"

inherit debian-package
DEBIAN_SECTION = "base"
DPR = "0"

LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI += " \
file://execute_cmd.patch;striplevel=0 \
file://mkbuiltins_have_stringize.patch \
file://build-tests.patch \
file://test-output.patch \
file://cve-2014-6271.patch;striplevel=0 \
file://cve-2014-7169.patch \
file://Fix-for-bash-exported-function-namespace-change.patch \
file://cve-2014-7186_cve-2014-7187.patch \
file://cve-2014-6277.patch \
file://cve-2014-6278.patch \
file://run-ptest \
"
