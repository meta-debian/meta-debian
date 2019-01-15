require recipes-devtools/perl/perl.inc

inherit debian-package
require recipes-debian/sources/perl.inc

LICENSE = "GPL-1.0+ | Artistic-1.0"
LIC_FILES_CHKSUM = " \
    file://Copying;md5=5b122a36d0f6dc55279a0ebc69f3c60b \
    file://Artistic;md5=71a4d5d9acc18c0952a6df2218bb68da \
"

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/perl/perl"

do_debian_unpack_extra_append() {
	mv ${WORKDIR}/metaconfig-* ${DEBIAN_UNPACK_DIR}/regen-configure
}

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
