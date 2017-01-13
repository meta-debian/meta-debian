#
# Base recipe: meta/recipes-graphics/xorg-lib/libxt_1.1.4.bb
# Base branch: daisy
#

SUMMARY = "Xt: X Toolkit Intrinsics library"

DESCRIPTION = "The Intrinsics are a programming library tailored to the \
special requirements of user interface construction within a network \
window system, specifically the X Window System. The Intrinsics and a \
widget set make up an X Toolkit. The Intrinsics provide the base \
mechanism necessary to build a wide variety of interoperating widget \
sets and application environments. The Intrinsics are a layer on top of \
Xlib, the C Library X Interface. They extend the fundamental \
abstractions provided by the X Window System while still remaining \
independent of any particular user interface policy or style."

require xorg-lib-common.inc
PV = "1.1.4"

PR = "${INC_PR}.0"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=6565b1e0094ea1caae0971cc4035f343"

# Option --enable-unit-tests require glib-2.0
DEPENDS += "util-linux libxcb libsm virtual/libx11 kbproto libxdmcp glib-2.0"
PROVIDES = "xt"

BBCLASSEXTEND = "native"

# Follow configure in debian/rules
EXTRA_OECONF += "--with-appdefaultdir=/etc/X11/app-defaults \
		--with-xfile-search-path="/usr/lib/X11/%L/%T/%N%S:/usr/lib/X11/%l/%T/%N%S:/usr/lib/X11/%T/%N%S:/etc/X11/%L/%T/%N%C%S:/etc/X11/%l/%T/%N%C%S:/etc/X11/%T/%N%C%S:/etc/X11/%L/%T/%N%S:/etc/X11/%l/%T/%N%S:/etc/X11/%T/%N%S" \
		--enable-unit-tests \
		--disable-silent-rules \
		--disable-specs \
		"

do_compile() {
	(
		unset CC LD CXX CCLD CFLAGS
		oe_runmake -C util 'XT_CFLAGS=' 'CC=${BUILD_CC}' 'LD=${BUILD_LD}' 'CXX=${BUILD_CXX}' 'CCLD=${BUILD_CCLD}' 'CFLAGS=-D_GNU_SOURCE -I${STAGING_INCDIR_NATIVE} ${BUILD_CFLAGS}' 'LDFLAGS=${BUILD_LDFLAGS}' 'CXXFLAGS=${BUILD_CXXFLAGS}' 'CPPFLAGS=${BUILD_CPPFLAGS}' makestrs
	)
	if [ "$?" != "0" ]; then
		exit 1
	fi
	oe_runmake
}

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
