#
# base recipe: /meta/recipes-graphics/libsdl/libsdl-1.2.15
# base branch: master
# base commit: 0e5a9114f58828058595d773e5b97771c88f7be8
#

SUMMARY = "Simple DirectMedia Layer"
DESCRIPTION = "Simple DirectMedia Layer is a cross-platform multimedia \
library designed to provide low level access to audio, keyboard, mouse, \
joystick, 3D hardware via OpenGL, and 2D video framebuffer."
HOMEPAGE = "http://www.libsdl.org"
BUGTRACKER = "http://bugzilla.libsdl.org/"

PR = "r1"
inherit debian-package
PV = "1.2.15"

#Correct the debian package name
DPN ="libsdl1.2"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=27818cd7fd83877a8e3ef82b82798ef4"

DEPENDS = "${@bb.utils.contains('DISTRO_FEATURES', 'directfb', 'directfb', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'virtual/libgl', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'virtual/libx11 \
	    libxext libxrandr libxrender', '', d)} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'libglu', '', d)} \
           "
DEPENDS_class-nativesdk = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', \
			   'virtual/nativesdk-libx11 nativesdk-libxrandr \
			   nativesdk-libxrender nativesdk-libxext', '', d)}"

PROVIDES = "virtual/libsdl"

inherit autotools lib_package binconfig-disabled

#Correct follow debian/rules
EXTRA_OECONF = "--disable-rpath --enable-sdl-dlopen --disable-loadso \
		--disable-video-ggi --disable-video-svga --disable-video-aalib \
		--disable-nas --disable-esd --disable-arts \
		--disable-alsa-shared --disable-pulseaudio-shared \
		--disable-x11-shared \
		--enable-video-directfb --enable-video-caca\
"
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa', '', d)}"
PACKAGECONFIG[alsa] = "--enable-alsa --disable-alsatest,--disable-alsa,alsa-lib,"

EXTRA_AUTORECONF += "--include=acinclude --exclude=autoheader"

do_configure_prepend() {
	# Remove old libtool macros.
	MACROS="libtool.m4 lt~obsolete.m4 ltoptions.m4 ltsugar.m4 ltversion.m4"
	for i in ${MACROS}; do
		rm -f ${S}/acinclude/$i
	done
	export SYSROOT=$PKG_CONFIG_SYSROOT_DIR
}

#install follow Debian jessie
do_install_append () {
	LINKLIB=$(basename $(readlink ${D}${libdir}/libSDL.so))
	chmod 0644 ${D}${libdir}/${LINKLIB}
	rm ${D}${libdir}/*.la
}
#Corect the packages name
DEBIANNAME_${PN} = "libsdl1.2debian"
DEBIANNAME_${PN}-dev = "libsdl1.2-dev"
DEBIANNAME_${PN}-dbg = "libsdl1.2-dbg"

BBCLASSEXTEND = "nativesdk"
