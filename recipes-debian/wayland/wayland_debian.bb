SUMMARY = "wayland compositor infrastructure"
DESCRIPTION = "\
Wayland is a protocol for a compositor to talk to its clients as well \
as a C library implementation of that protocol. The compositor can be \
a standalone display server running on Linux kernel modesetting and   \
evdev input devices, an X application, or a wayland client \
itself. The clients can be traditional applications, X servers \
(rootless or fullscreen) or other display servers. \
"
HOMEPAGE = "http://wayland.freedesktop.org/"
PR = "r0"
inherit debian-package
PV = "1.6.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=1d4476a7d98dd5691c53d4d43a510c72"
inherit autotools pkgconfig

DEPENDS_class-target += "${PN}-native libffi expat"
#building documentation depend on doxygen which is not yet in meta-debian
EXTRA_OECONF += "--disable-documentation"
EXTRA_OEMAKE_class-target += "wayland_scanner=${STAGING_BINDIR_NATIVE}/wayland-scanner"
# Debian's source code isn't contains patch file
DEBIAN_PATCH_TYPE = "nopatch"

# Avoid a parallel build problem
PARALLEL_MAKE = ""
do_install_append() {
	#remove the unwanted files
	rm ${D}${libdir}/*.la
}
PACKAGES =+ "lib${PN}-client lib${PN}-cursor lib${PN}-server"

FILES_lib${PN}-cursor += "${libdir}/libwayland-cursor.so.*"
FILES_lib${PN}-server += "${libdir}/libwayland-server.so.*"
FILES_lib${PN}-client += "${libdir}/libwayland-client.so.*"
FILES_${PN}-dev += "${datadir}/${PN} ${bindir}"
PKG_${PN}-dev = "lib${PN}-dev"

BBCLASSEXTEND = "native"
