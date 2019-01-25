require gcc-8.inc
require recipes-devtools/gcc/gcc-source.inc

EXCLUDE_FROM_WORLD = "1"

do_unpack[depends] += "xz-native:do_populate_sysroot"
do_unpack_append() {
    bb.build.exec_func('unpack_extra', d)
}

unpack_extra() {
	rm -rf ${S} ${DEBIAN_UNPACK_DIR}/.pc.debian
	tar xf ${DEBIAN_UNPACK_DIR}/gcc-${PV}-dfsg.tar.xz -C ${DEBIAN_UNPACK_DIR}/
	mv ${DEBIAN_UNPACK_DIR}/gcc-${PV} ${S}
	ln -sf libsanitizer ${S}/libasan

	rm -rf ${S}/gcc/d ${S}/gcc/testsuite/gdc.test \
	       ${S}/gcc/testsuite/lib/gdc*.exp ${S}/libphobos
	tar -x -C ${S}/ --strip-components=1 -f ${DEBIAN_UNPACK_DIR}/gdc-*.tar.xz
}

# Generate debian/patches/series
do_debian_patch_prepend() {
	# Base on debian/rules.defs, set required variables for
	# using debian/rules.patch to generate debian/patches/series.
	export distrelease="${DISTRO_CODENAME}"
	export derivative="${DISTRO_NAME}"
	export distribution="${DISTRO_NAME}"
	export GFDL_INVARIANT_FREE=yes
	export PKGSOURCE=${DPN}
	export DEB_TARGET_ARCH=${DPKG_ARCH}
	export DEB_TARGET_ARCH_OS=${TARGET_OS}
	if [ "${HOST_SYS}" != "${BUILD_SYS}" ]; then
		if [ "${HOST_SYS}" != "${TARGET_SYS}" ]; then
			export DEB_CROSS=yes
			export build_type=cross-build-cross
		else
			export build_type=cross-build-native
		fi
	else
		if [ "${HOST_SYS}" != "${TARGET_SYS}" ]; then
			export DEB_CROSS=yes
			export build_type=build-cross
		else
			export build_type=build-native
		fi
	fi

	if [ $distribution-$DEB_TARGET_ARCH = Debian-arm64 ]; then
		export with_linaro_branch=yes
	fi
	export with_ssp=yes
	ssp_no_archs="alpha hppa ia64 m68k"
	if echo $ssp_no_archs | grep -q "$DEB_TARGET_ARCH"; then
		export with_ssp="not available on $DEB_TARGET_ARCH"
	fi
	# with_ssp_default is only set if $derivative is not Debian. Ignore it.
	pie_archs="amd64 arm64 armel armhf i386 \
	    mips mipsel mips64 mips64el mipsn32 mipsn32el \
	    mipsr6 mipsr6el mips64r6 mips64r6el mipsn32r6 mipsn32r6el \
	    ppc64el s390x sparc sparc64 kfreebsd-amd64 kfreebsd-i386 \
	    hurd-i386 riscv64"
	if echo $pie_archs | grep -q $DEB_TARGET_ARCH; then
		export with_pie=yes
	fi

	export biarch64=no
	export separate_lang=no
	export with_ada=no
	if [ x$separate_lang != xyes ]; then
		export with_d=yes
	fi
	export with_libphobos=no
	export single_package=no

	# Poky has a different way to support multilib,
	# using Debian multilib patches will cause conflict.
	export multilib=no
	export with_multiarch_lib=no
	if [ x$with_multiarch_lib = xyes ] && [ x$single_package != xyes ] && [ x$DEB_CROSS != xyes ]; then
		export with_multiarch_cxxheaders=yes
	fi

	cd ${DEBIAN_UNPACK_DIR}
	export stampdir=.
	rm -f debian/patches/series $stampdir/02-series-stamp
	make series -f debian/rules.patch
	cd -

	sed -i -e '/gcc-multiarch.*.diff/d' \
               -e '/config-ml.*.diff/d' \
               -e '/gcc-multilib-multiarch.diff/d' \
               -e '/cross-install-location.diff/d' \
               -e '/arm-multilib-.*.diff/d' \
            ${DEBIAN_UNPACK_DIR}/debian/patches/series
}
