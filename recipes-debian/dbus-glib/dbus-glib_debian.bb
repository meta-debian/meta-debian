require recipes-core/dbus/${BPN}_0.100.2.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-core/dbus/dbus-glib-0.100.2:\
"

inherit debian-package
DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "AFL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=cf5b3a2f7083750d504333114e738656"

# Exclude inappropriate patch which for older version
# test-install-makefile.patch 
# obsolete_automake_macros.patch
SRC_URI += " \
file://no-examples.patch \
"
