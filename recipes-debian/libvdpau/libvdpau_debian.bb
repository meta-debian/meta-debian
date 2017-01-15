SUMMARY = "Video Decode and Presentation API for Unix"
DESCRIPTION = "\
VDPAU (Video Decode and Presentation API for Unix) is an open source \
library (libvdpau) and API designed by NVIDIA originally for its GeForce \
8 series and later GPU hardware, targeted at the X Window System on Unix \
operating-systems (including Linux, FreeBSD, and Solaris). This VDPAU API \
allows video programs to offload portions of the video decoding process \
and video post-processing to the GPU video-hardware. \
"
HOMEPAGE = "http://cgit.freedesktop.org/~aplattner/libvdpau"
PR = "r0"
inherit debian-package pkgconfig
PV = "0.8"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=83af8811a28727a13f04132cc33b7f58"
inherit autotools

DEPENDS += "libx11 libxext dri2proto"
#follow debian/rules
EXTRA_OECONF += "--enable-dri2"
do_install_append() {
	rm ${D}${libdir}/vdpau/libvdpau_trace.so \
		${D}${libdir}/vdpau/libvdpau_trace.la \
		${D}${libdir}/*.la
}
FILES_${PN} += "${libdir}/vdpau/libvdpau_trace.so.*"
FILES_${PN}-dbg += "${libdir}/vdpau/.debug"
