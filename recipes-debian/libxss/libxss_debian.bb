SUMMARY = "X11 Screen Saver extension library"
DESCRIPTION = "libXss provides an X Window System client interface to the MIT-SCREEN-SAVER\n\
extension to the X protocol.\n\
.\n\
The Screen Saver extension allows clients behaving as screen savers to\n\
register themselves with the X server, to better integrate themselves with\n\
the running session."
HOMEPAGE = "http://www.X.org"

inherit debian-package
PV = "1.2.2"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=21fd154ee757813632ada871a34113fb"

DEPENDS = "util-macros virtual/libx11 libxext xextproto scrnsaverproto"

DEBIAN_PATCH_TYPE = "nopatch"

inherit autotools pkgconfig
