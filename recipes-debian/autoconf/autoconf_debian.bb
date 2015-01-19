require recipes-devtools/autoconf/${BPN}_2.69.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-devtools/autoconf/${BPN}:\
"

inherit debian-package
DEBIAN_SECTION = "devel"

DPR = "0"

LICENSE = "GPL-2.0 & GPL-3.0"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
file://COPYINGv3;md5=d32239bcb673463ab874e80d47fae504 \
"

SRC_URI += "file://autoreconf-include.patch \
            file://check-automake-cross-warning.patch \
            file://autoreconf-exclude.patch \
            file://autoreconf-foreign.patch \
            file://autoreconf-gnuconfigize.patch \
            file://autoheader-nonfatal-warnings.patch \
            file://config_site.patch \
            file://remove-usr-local-lib-from-m4.patch \
            file://preferbash.patch \
            file://autotest-automake-result-format.patch \
           "
