SUMMARY = "An easy to use logging library"
DESCRIPTION = " \
liblogging (the upstream project) is a collection of several components. \
Namely: stdlog, journalemu, rfc3195. \
The stdlog component of liblogging can be viewed as an enhanced version of \
the syslog(3) API. It retains the easy semantics, but makes the API more \
sophisticated "behind the scenes" with better support for multiple threads \
and flexibility for different log destinations (e.g. syslog and systemd \
journal)."
SECTION = "libs"

PR = "r0"
inherit debian-package
PV = "1.0.4"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=63fe03535d83726f5655072502bef1bc"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig

EXTRA_OECONF = " --disable-journal --disable-rfc3195 "

# fix-error-when-cross-compile.patch
#	AC_CHECK_FILES only works when not cross compiling
SRC_URI += "file://fix-error-when-cross-compile.patch"

PKG_${PN} = "liblogging-stdlog0"
PKG_${PN}-dev = "liblogging-stdlog-dev"
