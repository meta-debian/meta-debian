SUMMARY = "Multitouch Protocol Translation Library"
DESCRIPTION = "libmtdev is a library for translating evdev multitouch events using the legacy \
protocol to the new multitouch slots protocol. This is necessary for kernel \
drivers that have not been updated to use the newer protocol."
HOMEPAGE = "http://bitmath.org/code/mtdev/"

inherit debian-package
PV = "1.1.5"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea6bd0268bb0fcd6b27698616ceee5d6"

# source format is 3.0 (quilt) but there is no debian/patches
DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig

PACKAGES =+ "${PN}-tools"

FILES_${PN}-tools = "${bindir}/*"

# Provide packages as Debian
RPROVIDES_${PN} += "lib${PN}"
RPROVIDES_${PN}-dev += "lib${PN}-dev"

# Prevent mtdev-tools auto name to libmtdev-tools
DEBIAN_NOAUTONAME_${PN}-tools = "1"
