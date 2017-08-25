#
# base recipe: meta/recipes-devtools/pkgconfig/pkgconfig_0.28.bb
# base branch: daisy
#

SUMMARY = "manage compile and link flags for libraries"
DESCRIPTION = "pkg-config is a system for managing library compile and link flags that \
works with automake and autoconf."
HOMEPAGE = "http://pkg-config.freedesktop.org"

PR = "r1"

inherit debian-package
PV = "0.28"

DPN = "pkg-config"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "glib-2.0"
DEPENDS_class-native = ""
DEPENDS_class-nativesdk = ""

DEBIAN_PATCH_TYPE = "nopatch"

SRC_URI += " \
	file://pkg-config-native.in \
	file://fix-glib-configure-libtool-usage.patch \
	file://obsolete_automake_macros.patch \
	file://0001-Strip-system-library-directories-reliably.patch \
"

inherit autotools

EXTRA_OECONF = "--without-internal-glib"
EXTRA_OECONF_class-native = "--with-internal-glib"
EXTRA_OECONF_class-nativesdk = "--with-internal-glib"

acpaths = "-I ."

# Set an empty dev package to ensure the base PN package gets
# the pkg.m4 macros, pkgconfig does not deliver any other -dev
# files.
FILES_${PN}-dev = ""
FILES_${PN} += "${datadir}/aclocal/pkg.m4 \
                ${datadir}/pkg-config-crosswrapper \
                "

do_install_append() {
	install -m 0755 ${S}/debian/pkg-config-crosswrapper ${D}${datadir}/
}

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

#
# Debian Native Test
#
inherit debian-test

SRC_URI_DEBIAN_TEST = "\
        file://pkg-config-native-test/run_native_test_pkg-config \
        file://pkg-config-native-test/run_modversion_command \
        file://pkg-config-native-test/run_libs_command \
        file://pkg-config-native-test/run_cflags_command \
        file://pkg-config-native-test/foo.pc \
        file://pkg-config-native-test/bar.pc \
"

DEBIAN_NATIVE_TESTS = "run_native_test_pkg-config"
TEST_DIR = "${B}/native-test"
