SUMMARY = "X11 Athena Widget library"

inherit debian-package autotools pkgconfig
PV = "1.0.12"

PR = "r0"
DEPENDS += "libx11 libxext libxmu libxt libxpm"

LICENSE = "XFree86-1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=1c65719d42900bb81b83e8293c20a364"

DEBIAN_PATCH_TYPE = "quilt"

# Follow configure options in Debian rules.
# Since xmlto is not existed so option with-xmlto
# is disabled.
EXTRA_OECONF = " \
	--disable-xaw6 \
	--without-fop \
	--disable-silent-rules \
	"
