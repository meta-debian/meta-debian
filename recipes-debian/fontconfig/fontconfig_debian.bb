#
# base recipe: meta/recipes-graphics/fontconfig/fontconfig_2.11.0.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package

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
file://src/fccache.c;beginline=1131;endline=1146;md5=754c7b855210ee746e5f0b840fad9a9f \
"

DEPENDS = "expat freetype zlib"

PACKAGES =+ "fontconfig-utils"
FILES_${PN} =+ "${datadir}/xml/*"
FILES_fontconfig-utils = "${bindir}/*"

# Work around past breakage in debian.bbclass
RPROVIDES_fontconfig-utils = "libfontconfig-utils"
RREPLACES_fontconfig-utils = "libfontconfig-utils"
RCONFLICTS_fontconfig-utils = "libfontconfig-utils"
DEBIAN_NOAUTONAME_fontconfig-utils = "1"

inherit autotools pkgconfig

EXTRA_OECONF = " --disable-docs --with-default-fonts=${datadir}/fonts"

BBCLASSEXTEND = "native"

SRC_URI += " \
	file://sysroot-arg.patch \
"
