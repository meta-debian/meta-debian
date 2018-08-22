require recipes-devtools/perl/perl.inc

inherit debian-package
require recipes-debian/sources/perl.inc

do_debian_unpack_extra_append() {
	if [ -d ${WORKDIR}/metaconfig-debian-${BPN}-5.26.1 ]; then
		rm -rf ${DEBIAN_UNPACK_DIR}/regen-configure
		mv ${WORKDIR}/metaconfig-debian-${BPN}-5.26.1 ${DEBIAN_UNPACK_DIR}/regen-configure
	fi
}

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/perl/perl"

# debian/patches/debian/patchlevel.diff makes a change that requires patchlevel-debian.h.
# Generate this header to satisfy build.
do_compile_prepend(){
	cd ${S}

	export patches="debian/patches/series"
	export patchlevel="patchlevel-debian.h"
	export package_version=$(head -1 debian/changelog | cut -d'(' -f2 | cut -d')' -f1)
	# this gets prepended to the patch names in patchlevel.h
	export patchprefix="DEBPKG:"

	touch $patchlevel
	test -d ${S}/debian
	test -f $patches      # maintainer sanity check
	debian/gen-patchlevel -p $patchprefix -v $package_version \
	        $patches > $patchlevel

	cd -
}
