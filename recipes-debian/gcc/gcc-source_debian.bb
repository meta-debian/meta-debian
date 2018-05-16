# This recipe takes responsibility on preparing full-patched source code
# for another gcc's target, based on idea of gcc-4.9 recipes from openembedded.
# Refer:
#
# http://cgit.openembedded.org/openembedded-core/tree/meta/recipes-devtools/
# gcc/gcc-source.inc
require gcc-4.9.inc

deltask do_debian_patch
deltask do_patch
deltask do_populate_lic
deltask do_configure
deltask do_compile
deltask do_install
deltask do_populate_sysroot
deltask do_populate_lic
deltask do_package
deltask do_package_write_rpm
deltask do_package_write_ipk
deltask do_package_write_deb
deltask do_package_qa
deltask do_packagedata
deltask do_rm_work

WORKDIR = "${SW}"
SSTATE_SWSPEC = "sstate:gcc::${PV}:${PR}::${SSTATE_VERSION}:"

STAMP = "${STAMPS_DIR}/work-shared/gcc-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/gcc-[0-9]*-*"

DEPENDS = ""
