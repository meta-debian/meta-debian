#
# Base recipe: recipes-support/icu/icu_55.1.bb
# Base branch: jethro
#

SUMMARY = "International Component for Unicode libraries"
DESCRIPTION = "The International Component for Unicode (ICU) is a mature, \
portable set of C/C++ and Java libraries for Unicode support, software \
internationalization (I18N) and globalization (G11N), giving applications the \
same results on all platforms."
HOMEPAGE = "http://site.icu-project.org/"

PR = "r0"

inherit debian-package
PV = "52.1"

LICENSE = "ICU"
LIC_FILES_CHKSUM = "file://../license.html;md5=3a0605ebb7852070592fbd57e8967f3f"

S = "${DEBIAN_UNPACK_DIR}/source"

DEPENDS = "icu-native"
DEPENDS_class-native = ""

CPPFLAGS_append_libc-uclibc = " -DU_TIMEZONE=0"

inherit autotools pkgconfig binconfig

STAGING_ICU_DIR_NATIVE = "${STAGING_DATADIR_NATIVE}/${BPN}/${PV}"

# ICU needs the native build directory as an argument to its --with-cross-build option when
# cross-compiling. Taken the situation that different builds may share a common sstate-cache
# into consideration, the native build directory needs to be staged.
EXTRA_OECONF = "--enable-static --with-cross-build=${STAGING_ICU_DIR_NATIVE}"
EXTRA_OECONF_class-native = ""
EXTRA_OECONF_class-nativesdk = "--with-cross-build=${STAGING_ICU_DIR_NATIVE}"

do_install_append_class-native() {
	mkdir -p ${D}/${STAGING_ICU_DIR_NATIVE}/config
	cp -r ${B}/config/icucross.mk ${D}/${STAGING_ICU_DIR_NATIVE}/config
	cp -r ${B}/config/icucross.inc ${D}/${STAGING_ICU_DIR_NATIVE}/config
	cp -r ${B}/lib ${D}/${STAGING_ICU_DIR_NATIVE}
	cp -r ${B}/bin ${D}/${STAGING_ICU_DIR_NATIVE}
	cp -r ${B}/tools ${D}/${STAGING_ICU_DIR_NATIVE}
}

# Add packages follow debian
PACKAGES =+ "icu-devtools libicu"

# Rename packages follow debian
PKG_${PN}-dev = "libicu-dev"
PKG_libicu = "libicu52"
PKG_${PN}-dbg = "libicu52-dbg"
RPROVIDES_${PN}-dev += "libicu-dev"
RPROVIDES_${PN}-dbg += "libicu-dbg"

FILES_icu-devtools += "${bindir}/* ${sbindir}/*"
FILES_libicu += "${libdir}/*.so.*"

BBCLASSEXTEND = "native nativesdk"
