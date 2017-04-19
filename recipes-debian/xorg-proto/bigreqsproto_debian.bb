#
# Base recipe: meta/recipes-graphics/xorg-proto/bigreqsproto_1.1.2.bb
# Base branch: daisy
#

require xorg-proto-common.inc
PV = "1.1.2"

SUMMARY = "BigReqs: X Big Requests extension headers"

DESCRIPTION = "This package provides the wire protocol for the \
BIG-REQUESTS extension, used to send larger requests that usual in order \
to avoid fragmentation."

PR = "${INC_PR}.0"

BBCLASSEXTEND = "native nativesdk"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b12715630da6f268d0d3712ee1a504f4"

DPN = "x11proto-bigreqs"

# there is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
