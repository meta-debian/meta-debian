#
# Base recipe: meta/recipes-graphics/xorg-proto/fixesproto_5.0.bb
# Base branch: daisy
#

require xorg-proto-common.inc
PV = "5.0"

SUMMARY = "XFixes: X Fixes extension headers"

DESCRIPTION = "This package provides the wire protocol for the X Fixes \
extension.  This extension is designed to provide server-side support \
for application work arounds to shortcomings in the core X window \
system."

PR = "${INC_PR}.0"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=262a7a87da56e66dd639bf7334a110c6 \
file://xfixesproto.h;endline=43;md5=c3a9ee6db3532ed0d44dea266cfc97f4"

RCONFLICTS_${PN} = "fixesext"

BBCLASSEXTEND = "native nativesdk"

DPN = "x11proto-fixes"

# THere is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
