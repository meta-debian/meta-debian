SUMMARY = "rsync remote-delta algorithm library"
DESCRIPTION = "librsync implements the rsync remote-delta algorithm, which allows for \
efficient remote updates of a file, without requiring the old and new versions \
to both be present at the transmitter. The library uses a stream-based designed \
so that it can be easily embedded into network applications. \
"
inherit debian-package
require recipes-debian/sources/librsync.inc

LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

EXTRA_OECONF += "--enable-shared --disable-static"

PACKAGES =+ "rdiff"

FILES_rdiff += "${bindir}/rdiff"

inherit autotools pkgconfig
