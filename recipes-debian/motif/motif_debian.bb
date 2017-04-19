#
# base recipe: meta-openembedded/meta-oe/recipes-support/openmotif/openmotif_2.3.3.bb
# base branch: jethro
#

DESCRIPTION = "Motif is the industry standard GUI component toolkit for *NIX."
HOMEPAGE = "http://motif.ics.com/"

inherit debian-package
PV = "2.3.4"
PR = "r2"

LICENSE = "LGPLv2+ & MIT-X"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=4fbd65380cdd255951079008b364516c \
    file://lib/Xm/Xmfuncs.h;beginline=5;endline=26;md5=fedd8fcbb39f275b0768ef743a3e4026 \
    file://lib/Xm/Xpms_popen.c;endline=27;md5=41f741c482cf11af42b581d5c01f683a \
"

# Ignore looking for headers on host system
SRC_URI += "file://configure.patch"

DEPENDS = "freetype virtual/libx11 libxft xbitmaps"

inherit autotools-brokensep

# Configure base on debian/rules
EXTRA_OECONF = " \
    --disable-printing \
    --disable-demos \
    --enable-xft \
    --with-mwmrcdir="${sysconfdir}/X11/mwm" \
    --with-xmbinddir="${datadir}/X11/bindings" \
    --with-x11rgbdir="${sysconfdir}/X11" \
"
EXTRA_OECONF += "--x-includes=${STAGING_INCDIR} --x-libraries=${STAGING_LIBDIR}"

PACKAGECONFIG ??= "jpeg png"
PACKAGECONFIG[jpeg] = "--enable-jpeg,--disable-jpeg,libjpeg-turbo"
PACKAGECONFIG[png] = "--enable-png,--disable-png,libpng"

do_compile() {
	(
		# HACK: build a native binaries need during the build
		oe_runmake -C config/util \
		    CC="${BUILD_CC}" LD="${BUILD_LD}" CXX="${BUILD_CXX}" \
		    CPPFLAGS="${BUILD_CPPFLAGS}" CFLAGS="${BUILD_CFLAGS}" \
		    X_CFLAGS="" LIBS="" makestrs
	)
	if [ "$?" != "0" ]; then
		exit 1
	fi
	oe_runmake -C lib
	oe_runmake -C include
}

do_install() {
	oe_runmake DESTDIR=${D} -C lib install
	oe_runmake DESTDIR=${D} -C include install

	# According to debian/libmrm4.links
	ln -sf libMrm.so.4.0.4 ${D}${libdir}/libMrm.so.3

	# According to debian/libxm4.links
	ln -sf libXm.so.4.0.4 ${D}${libdir}/libXm.so.3
}

PACKAGES =+ "libmrm libxm"

FILES_libmrm = "${libdir}/libMrm${SOLIBS}"
FILES_libxm = "${libdir}/libXm${SOLIBS}"

# package motif is empty, so don't let motif-dev depend on it
RDEPENDS_${PN}-dev = "libmrm libxm"

DEBIANNAME_${PN}-dev = "lib${PN}-dev"
RPROVIDES_${PN}-dev = "lib${PN}-dev"
