SUMMARY = "Sub Band CODEC library"
DESCRIPTION = "This provides the library and tool which operate SBC \
(sub band codec) in A2DP (the Advanced Audio Distribution Profile)."
HOMEPAGE = "http://www.bluez.org/"

inherit debian-package
PV = "1.2"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
                    file://COPYING.LIB;md5=fb504b67c50331fc78734fed90fb0e09"

inherit autotools pkgconfig

# Base on debian/rules
EXTRA_OECONF += "--disable-tester --disable-silent-rules"

#Empty DEBIAN_QUILT_PATCHES to avoid error :debian/patches not found
DEBIAN_QUILT_PATCHES = ""

PACKAGES =+ "${PN}-tools"

FILES_${PN}-tools = "${bindir}/*"
DEBIAN_NOAUTONAME_${PN}-tools = "1"
RPROVIDES_${PN} += "libsbc1"
RPROVIDES_${PN}-dev += "libsbc-dev"
