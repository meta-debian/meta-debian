require recipes-connectivity/avahi/${PN}_0.6.31.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-connectivity/avahi/files:\
"

inherit debian-package
DEBIAN_SECTION = "net"
DPR = "0"

# Resolve conflict when applying reuseport-check.patch
SRC_URI += " \
file://00avahi-autoipd \
file://99avahi-autoipd \
file://initscript.patch \
file://avahi_fix_install_issue.patch \
file://fix_for_automake_1.12.x.patch \
file://out-of-tree.patch \
file://0001-avahi-fix-avahi-status-command-error-prompt.patch \
file://reuseport-check_debian.patch \
"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = " \
file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
file://avahi-common/address.h;endline=25;md5=b1d1d2cda1c07eb848ea7d6215712d9d \
file://avahi-daemon/introspect.xsl;md5=d594555a7c223ea294cc4dd3a4467dbf \
"
