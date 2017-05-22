#
# base recipe: meta/recipes-graphics/fontconfig/fontconfig_2.11.0.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "2.11.0"

SUMMARY = "Generic font configuration library"
DESCRIPTION = "Fontconfig is a font configuration and customization library, \
which does not depend on the X Window System. It is designed to locate \
fonts within the system and select them according to requirements \
specified by applications. \
Fontconfig is not a rasterization library, nor does it impose a \
particular rasterization library on the application. The X-specific \
library 'Xft' uses fontconfig along with freetype to specify and \
rasterize fonts."

LICENSE = "MIT-style & MIT & PD"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=7a0449e9bc5370402a94c00204beca3d \
file://src/fcfreetype.c;endline=45;md5=5d9513e3196a1fbfdfa94051c09dfc84 \
file://src/fccache.c;beginline=1209;endline=1217;md5=adb322c092298953d6bc608f47ffa239 \
"

DEPENDS = "expat freetype zlib"

inherit autotools pkgconfig

EXTRA_OECONF = " --disable-docs --with-default-fonts=${datadir}/fonts"

BBCLASSEXTEND = "native"

SRC_URI += " \
	file://sysroot-arg.patch \
"

# Debian does not provide fontconfig-utils,
# but fontconfig-utils is required by fontcache.bbclass,
# so we need provide it too.
PACKAGES =+ "${PN}-utils ${PN}-config lib${PN}"
RDEPENDS_${PN}_class-target += "${PN}-utils ${PN}-config"

do_install_append_class-target() {
	sed -i -e "s|${STAGING_DIR_HOST}||" \
		${D}${libdir}/pkgconfig/fontconfig.pc
}
FILES_${PN}-utils = "${bindir}/*"
FILES_${PN}-config = " \
    ${sysconfdir} \
    ${datadir}/fontconfig/conf.avail \
    ${datadir}/xml/fontconfig/fonts.dtd \
"
FILES_lib${PN} = "${libdir}/libfontconfig${SOLIBS}"

# Work around past breakage in debian.bbclass
RPROVIDES_${PN}-utils = "lib${PN}-utils"
RREPLACES_${PN}-utils = "lib${PN}-utils"
RCONFLICTS_${PN}-utils = "lib${PN}-utils"
DEBIAN_NOAUTONAME_${PN}-utils = "1"

DEBIANNAME_${PN}-dev = "lib${PN}1-dev"
DEBIANNAME_${PN}-dbg = "lib${PN}1-dbg"
RPROVIDES_${PN}-dev += "lib${PN}-dev"
RPROVIDES_${PN}-dbg += "lib${PN}-dbg"
