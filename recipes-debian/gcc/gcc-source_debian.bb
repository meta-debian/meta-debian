# This recipe takes responsibility on preparing full-patched source code
# for another gcc's target, based on idea of gcc-4.9 recipes from openembedded.
# Refer:
#
# http://cgit.openembedded.org/openembedded-core/tree/meta/recipes-devtools/
# gcc/gcc-source.inc
require gcc-4.9.inc

deltask do_populate_lic
deltask do_configure
deltask do_compile
deltask do_install
deltask do_populate_sysroot
deltask do_package
deltask do_package_write_rpm
deltask do_package_write_ipk
deltask do_package_write_deb
deltask do_package_qa
deltask do_packagedata
deltask do_rm_work

# .orig.tar.gz is doubly-compressed
do_unpack_append() {
    bb.build.exec_func('do_uncompress', d)
}

do_uncompress() {
	cd ${S}
	PV_SRCPKG=$(head -n 1 ${S}/debian/changelog | \
					sed "s|.*(\([^()]*\)).*|\1|")
	PV_ORIG=$(echo $PV_SRCPKG | sed "s|-.*||")

	tar xvf ${S}/gcc-$PV_ORIG-dfsg.tar.xz -C ${S}
	mv ${S}/gcc-$PV_ORIG/* ${S}
	rm -r ${S}/gcc-$PV_ORIG
}

# To prevent applying patch again
do_debian_patch_prepend() {
	if test -f ${S}/.patched ; then
		exit 0
	fi
}

do_debian_patch_append() {
	touch ${S}/.patched
}
