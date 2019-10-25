require quilt.inc
inherit native

do_debian_patch() {
	cd ${DEBIAN_UNPACK_DIR}
	patches=`cat debian/patches/series | sed -e 's/#.*//g'`
	for p in $patch; do
		patch -p1 < $p
	done
}
