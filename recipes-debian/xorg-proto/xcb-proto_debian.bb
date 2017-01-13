#
# Base recipe: meta/recipes-graphics/xorg-proto/xcb-proto_1.10.bb
# Base branch: daisy
#

SUMMARY = "X C Binding - protocol descriptions"
DESCRIPTION = "xcb-proto contains descriptions of the X Window System core protocol and\n\
selected extensions in an XML-based data format. The X C Binding (XCB)\n\
library uses these descriptions to generate much of its code. You only need\n\
this package if you want to compile XCB or otherwise make use of these\n\
protocol descriptions.\n\
.\n\
The XCB library provides an interface to the X Window System protocol,\n\
designed to replace the Xlib interface.  XCB provides several advantages over\n\
Xlib:\n\
.\n\
 * Size: small library and lower memory footprint\n\
 * Latency hiding: batch several requests and wait for the replies later\n\
 * Direct protocol access: one-to-one mapping between interface and protocol\n\
 * Thread support: access XCB from multiple threads, with no explicit locking\n\
 * Easy creation of new extensions: automatically generates interface from\n\
   machine-parsable protocol descriptions"
HOMEPAGE = "http://xcb.freedesktop.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=XCB"

PR = "r1"

inherit debian-package
PV = "1.10"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=d763b081cb10c223435b01e00dc0aba7 \
file://src/dri2.xml;beginline=2;endline=28;md5=f8763b13ff432e8597e0d610cf598e65 \
"

# xcb-proto has no debian patches
DEBIAN_PATCH_TYPE = "nopatch"

inherit autotools pkgconfig pythonnative

PACKAGES += "python-xcbgen"

FILES_${PN}-dev += "${datadir}/xcb/*.xml ${datadir}/xcb/*.xsd"
FILES_python-xcbgen = "${libdir}/python*"

RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

DEPENDS_append_class-native = " python-native"
BBCLASSEXTEND = "native nativesdk"
PACKAGE_ARCH = "all"
