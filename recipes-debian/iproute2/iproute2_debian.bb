#
# base recipe: meta/recipes-connectivity/iproute2/iproute2_4.19.0.bb
# base branch: warrior
#

require recipes-connectivity/iproute2/iproute2.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
    file://ip/ip.c;beginline=3;endline=8;md5=689d691d0410a4b64d3899f8d6e31817 \
"

inherit debian-package
require recipes-debian/sources/iproute2.inc

# CFLAGS are computed in Makefile and reference CCOPTS
EXTRA_OEMAKE_append = " CCOPTS='${CFLAGS}'"
