SUMMARY = "PNG image format decoding library"
HOMEPAGE = "http://www.libpng.org/"
SECTION = "libs"
LICENSE = "Libpng"
LIC_FILES_CHKSUM = "file://LICENSE;md5=12b4ec50384c800bc568f519671b120c \
                    file://png.h;endline=144;md5=15ae15f53376306868259924a9db4e05 \
"
DEPENDS = "zlib"

inherit debian-package
require recipes-debian/sources/libpng1.6.inc
BPN = "libpng"

UPSTREAM_CHECK_URI = "http://libpng.org/pub/png/libpng.html"

BINCONFIG = "${bindir}/libpng-config ${bindir}/libpng16-config"

inherit autotools binconfig-disabled pkgconfig

#DPN = "libpng1.6"
# override 'S' set by debian-package-ng
#S = "${WORKDIR}/${BPN}-${DEB_SRC_VERSION}"
#SRC_URI[dsc.md5sum] = "840c5a4648c85655c3f4d89c038581fa"
#SRC_URI[dsc.sha256sum] = "b4d875fa27ce7a682ec0a5b078d71d1353b745e8b12a79af21e7478538ffbb87"

# Work around missing symbols
EXTRA_OECONF_append_class-target = " ${@bb.utils.contains("TUNE_FEATURES", "neon", "--enable-arm-neon=on", "--enable-arm-neon=off" ,d)}"

PACKAGES =+ "${PN}-tools"

FILES_${PN}-tools = "${bindir}/png-fix-itxt ${bindir}/pngfix ${bindir}/pngcp"

BBCLASSEXTEND = "native nativesdk"
