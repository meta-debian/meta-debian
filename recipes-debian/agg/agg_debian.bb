SUMMARY = "AntiGrain Geometry graphical toolkit (development files)"
DESCRIPTION = "Anti-Grain Geometry (AGG) is a general purpose graphical toolkit written \
completely in standard and platform independent C++. It can be used in many \
areas of computer programming where high quality 2D graphics is an essential \
part of the project."
HOMEPAGE = "http://www.antigrain.com"

PR = "r0"

inherit debian-package
PV = "2.5+dfsg1"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://copying;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "virtual/libx11 virtual/libsdl freetype"

# agg_fix_for_automake-1.12.patch:
# 	Fix error: automatic de-ANSI-fication support has been removed
SRC_URI += "file://agg_fix_for_automake-1.12.patch"

inherit autotools pkgconfig

B_pic = "${WORKDIR}/build-pic"
D_pic = "${WORKDIR}/image-pic"

# Configure follow debian/rules
EXTRA_OECONF = " \
    --disable-examples \
    --x-includes=${STAGING_INCDIR} --x-libraries=${STAGING_LIBDIR} \
"
NONPIC_CONF = "--disable-gpc"
PIC_CONF = "--with-pic"

# We don't have sdl-config crossscript.
# However, we still set sdl-config path to sysroots
# to avoid sdl-config from host system.
export SDL_CONFIG = "${STAGING_BINDIR_CROSS}/sdl-config"
CXXFLAGS_prepend = " -I${STAGING_INCDIR}/SDL "
LDFLAGS_prepend = " -L${STAGING_LIBDIR} -lSDL "

# Run configure for NONPIC_CONF and PIC_CONF separately follow debian/rules
do_configure_append() {
	test -d ${B_pic} || mkdir ${B_pic}

	( cd ${B} && oe_runconf ${NONPIC_CONF} )
	( cd ${B_pic} && oe_runconf ${PIC_CONF} )
}

# Build libraries for PIC_CONF option
do_compile_append() {
	( cd ${B_pic} && oe_runmake )
}

do_install_append() {
	test -d ${D_pic} || mkdir ${D_pic}
	( cd ${B_pic} && oe_runmake install DESTDIR="${D_pic}" )

	# Follow debian/rules, copy libraries from pic to nonpic and rename
	install -m 0644 ${D_pic}${libdir}/libaggfontfreetype.a \
			${D}${libdir}/libaggfontfreetype_pic.a
	install -m 0644 ${D_pic}${libdir}/libaggplatformsdl.a \
			${D}${libdir}/libaggplatformsdl_pic.a
	install -m 0644 ${D_pic}${libdir}/libaggplatformX11.a \
			${D}${libdir}/libaggplatformX11_pic.a
	install -m 0644 ${D_pic}${libdir}/libagg.a \
			${D}${libdir}/libagg_pic.a

	# Remove .so / .la files
	find ${D}${libdir}/ -name "*.so*" | xargs rm
	find ${D}${libdir}/ -name "*.la" | xargs rm
}

# Debian only provides libagg-dev from agg's source code.
# ${PN} is empty, so don't depend on it.
PKG_${PN}-dev = "libagg-dev"
RDEPENDS_${PN}-dev = ""
