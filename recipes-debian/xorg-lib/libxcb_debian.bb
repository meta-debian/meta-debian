#
# Base recipe: meta/recipes-graphics/xorg-lib/libxcb_1.10.bb
# Base branch: daisy
#

SUMMARY = "XCB: The X protocol C binding library"
DESCRIPTION = "The X protocol C-language Binding (XCB) is a replacement \
for Xlib featuring a small footprint, latency hiding, direct access to \
the protocol, improved threading support, and extensibility."
HOMEPAGE = "http://xcb.freedesktop.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=XCB"

PR = "r0"

inherit debian-package
PV = "1.10"

BBCLASSEXTEND = "native nativesdk"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d763b081cb10c223435b01e00dc0aba7"

DEPENDS = "xcb-proto xproto libxau libxslt-native xcb-proto-native \
	   libpthread-stubs libxdmcp"

PACKAGES_DYNAMIC = "^libxcb-.*"

FILES_${PN} = "${libdir}/libxcb.so.*"

inherit autotools pkgconfig pythonnative

python populate_packages_prepend () {
    do_split_packages(d, '${libdir}', '^libxcb-(.*)\.so\..*$', 'libxcb-%s', 'XCB library module for %s', allow_links=True)
    do_split_packages(d, '${includedir}/xcb', '^(.*)\.h$', 'libxcb-%s-dev', 'X C Binding, %s extension, development files', allow_links=True)
    do_split_packages(d, '${libdir}/pkgconfig', '^xcb-(.*)\.pc$', 'libxcb-%s-dev', 'X C Binding, %s extension, development files', allow_links=True)
    do_split_packages(d, '${libdir}', '^libxcb-(.*)\.so$', 'libxcb-%s-dev', 'X C Binding, %s extension, development files', allow_links=True)

}

SRC_URI += " \
	file://xcbincludedir.patch \
"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

FILES_${PN}-dev = " \
    ${libdir}/libxcb.so \
    ${libdir}/pkgconfig/xcb.pc \
    ${libdir}/*.la \
"
