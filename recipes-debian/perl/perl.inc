INC_PR = "r1"

inherit debian-package

DEBIAN_SECTION = "perl"
PV = "5.20.2"
PERL_PV = "${PV}"

LICENSE = "Artistic-1.0 | GPL-1.0"
LIC_FILES_CHKSUM = " \
file://Copying;md5=5b122a36d0f6dc55279a0ebc69f3c60b \
file://Artistic;md5=2e6fd2475335af892494fe1f7327baf3 \
"

EXTRA_OEMAKE = "-e MAKEFLAGS="

# Base on debian/rules, create file patchlevel-debian.h
# which is required by perl when build with option -DDEBIAN.
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
