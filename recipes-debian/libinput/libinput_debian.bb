SUMMARY = "input device management and event handling library"
DESCRIPTION = "libinput is a library that handles input devices for display servers and\n\
other applications that need to directly deal with input devices.\n\
.\n\
It provides device detection, device handling, input device event\n\
processing and abstraction so minimize the amount of custom input\n\
code the user of libinput need to provide the common set of\n\
functionality that users expect."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/libinput/"

inherit debian-package
PV = "0.6.0+dfsg"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=673e626420c7f859fbe2be3a9c13632d"

DEPENDS = "libevdev mtdev systemd"

# source format is 3.0 (quilt) but there is no debian/patches
DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig
