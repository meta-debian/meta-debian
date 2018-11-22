#
# Generate debian/patches/series
#

# DPKG_ARCH is a mapping from TARGET_ARCH to Debian's architectures.
do_debian_patch_prepend() {
	# Base on debian/rules.defs, set required variables for
	# using debian/rules.patch to generate debian/patches/series.
	export distrelease=${DISTRO_DEB_CODENAME}
	export derivative=${DISTRO_DEB_NAME}
	export distribution=${DISTRO_DEB_NAME}
	export GFDL_INVARIANT_FREE=yes
	export PKGSOURCE=${DPN}
	export DEB_TARGET_ARCH=${DPKG_ARCH}
	export DEB_TARGET_ARCH_OS=${TARGET_OS}

	if [ "${HOST_SYS}" != "${TARGET_SYS}" ]; then
		export DEB_CROSS=yes
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

	export biarch64=no
	export with_dev=yes
	separate_lang=no
	export with_ada=no
	if [ x$with_ada = xyes ]; then
		export with_libgnat=yes
	fi

	if [ x$separate_lang != xyes ]; then
		export with_d=yes
	fi

	export with_libphobos=no
	export single_package=no
	export multilib=yes
	export with_multiarch_lib=yes
	if [ x$with_multiarch_lib = xyes ] && [ x$single_package != xyes ] && [ x$DEB_CROSS != xyes ]; then
		export with_multiarch_cxxheaders=yes
	fi

	cd ${DEBIAN_UNPACK_DIR}
	export stampdir=.
	rm -f debian/patches/series $stampdir/02-series-stamp
	make series -f debian/rules.patch
	cd -

	# Poky has its own way and configs to cross build.
	# It's hard to bitbake work with these patches because of
	# different way/stage/process in building.
	# The current implementation for recipe is still work fine,
	# so temporarily ignore them.
	sed -i -e '/gcc-multiarch.*.diff/d' \
	       -e '/g++-multiarch.*.diff/d' \
	       -e '/config-ml.*.diff/d' \
	       -e '/gcc-multilib-multiarch.diff/d' \
	       -e '/cross-install-location.diff/d' \
	    ${DEBIAN_UNPACK_DIR}/debian/patches/series
}
