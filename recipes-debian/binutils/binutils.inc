#
# base recipe: meta/recipes-devtools/binutils/binutils.inc
# base branch: master
# base commit: 3b7c38458856805588d552508de10944ed38d9f2
#

INC_PR = "r0"

inherit debian-package
PV = "2.25"

DPN = "binutils"

LICENSE = "GPLv2+ & GPLv3+ & LGPLv2 & LGPLv3"
LIC_FILES_CHKSUM=" \
	file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552\
	file://COPYING.LIB;md5=9f604d8a4f8e74f4f5140845a21b6674\
	file://COPYING3;md5=d32239bcb673463ab874e80d47fae504\
	file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6\
	file://gas/COPYING;md5=d32239bcb673463ab874e80d47fae504\
	file://include/COPYING;md5=59530bdf33659b29e73d4adb9f9f6552\
	file://include/COPYING3;md5=d32239bcb673463ab874e80d47fae504\
	file://libiberty/COPYING.LIB;md5=a916467b91076e631dd8edb7424769c7\
	file://bfd/COPYING;md5=d32239bcb673463ab874e80d47fae504\
"

DEPENDS = "flex-native bison-native zlib-native gnu-config-native autoconf-native"

# Some patch files have already applied in Debian source code, so ignore them:
# 	0003-Point-scripts-location-to-libdir.patch
#	0004-Only-generate-an-RPATH-entry-if-LD_RUN_PATH-is-not-e.patch
#	0011-Change-default-emulation-for-mips64-linux.patch
SRC_URI += " \
	file://0002-configure-widen-the-regexp-for-SH-architectures.patch \
	file://0005-Explicitly-link-with-libm-on-uclibc.patch \
	file://0006-Use-libtool-2.4.patch \
	file://0007-Add-the-armv5e-architecture-to-binutils.patch \
	file://0008-don-t-let-the-distro-compiler-point-to-the-wrong-ins.patch \
	file://0009-Upstream-Status-Inappropriate-distribution-codesourc.patch \
	file://0010-Fix-rpath-in-libtool-when-sysroot-is-enabled.patch \
	file://0012-Add-XLP-instructions-support.patch \
	file://0013-Fix-an-internal-error-in-do_print_to_mapfile-seen-wi.patch \
	file://0014-gold-arm-Skip-pic-check-for-R_ARM_REL32.patch \
"

inherit autotools gettext multilib_header

DEBIAN_PATCH_TYPE = "quilt"

FILES_${PN} = " \
	${bindir}/${TARGET_PREFIX}* \
	${libdir}/lib*-*.so \
	${prefix}/${TARGET_SYS}/bin/* \
	${bindir}/embedspu"

RPROVIDES_${PN} += "${PN}-symlinks"

FILES_${PN}-dev = " \
	${includedir} \
	${libdir}/*.la \
	${libdir}/libbfd.so \
	${libdir}/libopcodes.so"

# Rather than duplicating multiple entries for these, make one
# list and reuse it.

USE_ALTERNATIVES_FOR = " \
	addr2line \
	ar \
	as \
	c++filt \
	elfedit \
	gprof \
	ld \
	ld.bfd \
	${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', 'ld.gold dwp', '', d)} \
	nm \
	objcopy \
	objdump \
	ranlib \
	readelf \
	size \
	strings \
	strip \
"

python do_package_prepend() {
    make_alts = d.getVar("USE_ALTERNATIVES_FOR", True) or ""
    prefix = d.getVar("TARGET_PREFIX", True)
    bindir = d.getVar("bindir", True)
    for alt in make_alts.split():
        d.setVarFlag('ALTERNATIVE_TARGET', alt, bindir + "/" + prefix + alt)
        d.setVarFlag('ALTERNATIVE_LINK_NAME', alt, bindir + "/" + alt)
}

B = "${S}/build.${HOST_SYS}.${TARGET_SYS}"

EXTRA_OECONF = "--program-prefix=${TARGET_PREFIX} \
		--disable-werror \
		--enable-plugins \
		--enable-threads \
                ${LDGOLD} \
                ${@bb.utils.contains('DISTRO_FEATURES', 'multiarch', '--enable-64-bit-bfd', '', d)}"

LDGOLD_class-native = ""
LDGOLD_class-crosssdk = ""
LDGOLD ?= "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', '--enable-gold=default --enable-threads', '', d)}"

# This is necessary due to a bug in the binutils Makefiles
# EXTRA_OEMAKE = "configure-build-libiberty all"

export AR = "${HOST_PREFIX}ar"
export AS = "${HOST_PREFIX}as"
export LD = "${HOST_PREFIX}ld"
export NM = "${HOST_PREFIX}nm"
export RANLIB = "${HOST_PREFIX}ranlib"
export OBJCOPY = "${HOST_PREFIX}objcopy"
export OBJDUMP = "${HOST_PREFIX}objdump"

export AR_FOR_TARGET = "${TARGET_PREFIX}ar"
export AS_FOR_TARGET = "${TARGET_PREFIX}as"
export LD_FOR_TARGET = "${TARGET_PREFIX}ld"
export NM_FOR_TARGET = "${TARGET_PREFIX}nm"
export RANLIB_FOR_TARGET = "${TARGET_PREFIX}ranlib"

export CC_FOR_HOST = "${CCACHE}${HOST_PREFIX}gcc ${HOST_CC_ARCH}"
export CXX_FOR_HOST = "${CCACHE}${HOST_PREFIX}gcc ${HOST_CC_ARCH}"

# autotools.bbclass sets the _FOR_BUILD variables, but for some reason we need
# to unset LD_LIBRARY_PATH.
export CC_FOR_BUILD = "LD_LIBRARY_PATH= ${BUILD_CC}"

MULTIARCH := "${@bb.utils.contains("DISTRO_FEATURES", "multiarch", "yes", "no", d)}"
do_configure[vardeps] += "MULTIARCH"
do_configure () {
	(cd ${S}; gnu-configize) || die "Failed to run gnu-configize"
	oe_runconf
#
# must prime config.cache to ensure the build of libiberty
#
	mkdir -p ${B}/build-${BUILD_SYS}
	for i in ${CONFIG_SITE}; do
		cat $i >> ${B}/build-${BUILD_SYS}/config.cache || true
	done
}

do_install () {
	autotools_do_install

	# We don't really need these, so we'll remove them...
	rm -rf ${D}${libdir}/ldscripts

	# Fix the /usr/${TARGET_SYS}/bin/* links
	for l in ${D}${prefix}/${TARGET_SYS}/bin/*; do
		rm -f $l
		ln -sf `echo ${prefix}/${TARGET_SYS}/bin \
			| tr -s / \
			| sed -e 's,^/,,' -e 's,[^/]*,..,g'`${bindir}/${TARGET_PREFIX}`basename $l` $l
	done

	# Install the libiberty header
	install -d ${D}${includedir}
	install -m 644 ${S}/include/ansidecl.h ${D}${includedir}
	install -m 644 ${S}/include/libiberty.h ${D}${includedir}

	cd ${D}${bindir}

	# Symlinks for ease of running these on the native target
	for p in ${TARGET_PREFIX}* ; do
		ln -sf $p `echo $p | sed -e s,${TARGET_PREFIX},,`
	done

	for alt in ${USE_ALTERNATIVES_FOR}; do
		rm -f ${D}${bindir}/$alt
	done

	oe_multilib_header bfd.h
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN}_class-target = "${USE_ALTERNATIVES_FOR}"
