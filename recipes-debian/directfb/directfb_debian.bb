SUMMARY = "direct frame buffer graphics"
DESCRIPTION = "DirectFB is a graphics library which was designed with embedded systems \
in mind. It offers maximum hardware accelerated performance at a minimum \
of resource usage and overhead."
HOMEPAGE = "http://www.directfb.org/"

inherit debian-package
PV = "1.2.10.0"

LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=dcf3c825659e82539645da41a7908589"

DEPENDS_class-target = "${PN}-native"

SRC_URI += "file://configure-remove-host-path.patch"

inherit autotools pkgconfig lib_package

EXTRA_OECONF = " \
    --with-gfxdrivers=all \
    --enable-video4linux2 \
    --enable-static \
    --enable-unique \
    --disable-sdl \
    --disable-vnc \
"
EXTRA_OECONF_class-native = " \
    --with-gfxdrivers=none \
    --disable-unique \
    --disable-sdl \
    --disable-vnc \
"

PACKAGECONFIG ??= "freetype jpeg png zlib \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
                   "
PACKAGECONFIG[freetype] = "--enable-freetype,--disable-freetype,freetype"
PACKAGECONFIG[jpeg] = "--enable-jpeg,--disable-jpeg,libjpeg-turbo"
PACKAGECONFIG[png] = "--enable-png,--disable-png,libpng"
PACKAGECONFIG[x11] = "--enable-x11,--disable-x11,virtual/libx11 libxext xproto"
PACKAGECONFIG[zlib] = "--enable-zlib,--disable-zlib,zlib"

do_install_append(){
	# Remove unneeded files
	find ${D} -type f -name "*.la" -exec rm -f {} \;
	find ${D} -type f -name "*.o" -exec rm -f {} \;

	# Remove useless rpaths
	# ERROR: QA Issue: directfb: ... contains probably-redundant RPATH /usr/lib [useless-rpaths]
	# ERROR: QA run found fatal errors. Please consider fixing them.
	for elf_file in $(find ${D} -exec file {} \; | grep ELF | cut -d: -f1); do
		chrpath -d $elf_file
	done
}

PACKAGES =+ "lib${PN}-extra"

FILES_${PN} += " \
    ${libdir}/directfb-*/*/*.so \
    ${libdir}/directfb-*/*/*/*.so \
    ${datadir}/directfb-*/cursor.dat \
"
FILES_${PN}-dev += " \
    ${bindir}/directfb-config \
    ${bindir}/directfb-csource \
"
FILES_${PN}-staticdev += " \
    ${libdir}/directfb-*/*/*.a \
    ${libdir}/directfb-*/*/*/*.a \
"
FILES_${PN}-dbg += " \
    ${libdir}/*/*/.debug \
    ${libdir}/*/*/*/.debug \
"
FILES_lib${PN}-extra = " \
    ${libdir}/directfb-*/interfaces/IDirectFBFont/libidirectfbfont_ft2.so \
    ${libdir}/directfb-*/interfaces/IDirectFBImageProvider/libidirectfbimageprovider_jpeg.so \
    ${libdir}/directfb-*/interfaces/IDirectFBImageProvider/libidirectfbimageprovider_png.so \
    ${libdir}/directfb-*/systems/libdirectfb_x11.so \
"

PKG_${PN} = "lib${PN}-1.2-9"
PKG_${PN}-bin = "lib${PN}-bin"
PKG_${PN}-dev = "lib${PN}-dev"
RPROVIDES_${PN} += "lib${PN}-1.2-9"
RPROVIDES_${PN}-bin += "lib${PN}-bin"
RPROVIDES_${PN}-dev += "lib${PN}-dev"

BBCLASSEXTEND = "native"
