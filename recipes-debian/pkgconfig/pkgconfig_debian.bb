#
# base recipe: meta/recipes-devtools/pkgconfig/pkgconfig_git.bb
# base branch: master
# base commit: d886fa118c930d0e551f2a0ed02b35d08617f746
#

SUMMARY = "manage compile and link flags for libraries"
DESCRIPTION = "pkg-config is a system for managing library compile and link flags that \
works with automake and autoconf."
HOMEPAGE = "http://pkg-config.freedesktop.org"

inherit debian-package
require recipes-debian/sources/pkg-config.inc
BPN = "pkg-config"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEBIAN_PATCH_TYPE = "nopatch"

SRC_URI += " \
	file://pkg-config-native.in \
	file://fix-glib-configure-libtool-usage.patch \
	file://gcc-format-nonliteral-1.patch;patchdir=${S}/glib \
	file://gcc-format-nonliteral-2.patch;patchdir=${S}/glib \
"

inherit autotools

PACKAGECONFIG ??= "glib"
PACKAGECONFIG_class-native = ""
PACKAGECONFIG_class-nativesdk = ""

PACKAGECONFIG[glib] = "--without-internal-glib,--with-internal-glib,glib-2.0 pkgconfig-native"

acpaths = "-I ."

# Set an empty dev package to ensure the base PN package gets
# the pkg.m4 macros, pkgconfig does not deliver any other -dev
# files.
FILES_${PN}-dev = ""
FILES_${PN} += "${datadir}/aclocal/pkg.m4"

# Install a pkg-config-native wrapper that will use the native sysroot instead
# of the MACHINE sysroot, for using pkg-config when building native tools.
do_install_append_class-native () {
	sed -e "s|@PATH_NATIVE@|${PKG_CONFIG_PATH}|" \
		-e "s|@LIBDIR_NATIVE@|${PKG_CONFIG_LIBDIR}|" \
		< ${WORKDIR}/pkg-config-native.in > ${B}/pkg-config-native
	install -m755 ${B}/pkg-config-native ${D}${bindir}/pkg-config-native
}

RPROVIDES_${PN} += "pkg-config"
PKG_${PN} = "pkg-config"

BBCLASSEXTEND = "native nativesdk"
