require quilt.inc
require recipes-devtools/quilt/quilt-native.inc

# quilt-native also depends on native quilt command.
# This is a special overwritten to apply all patches in
# the quilt source without native quilt command.
debian_patch_quilt() {
	bbnote "applying all patches without quilt command"

	PATCH_DIR=${DEBIAN_UNPACK_DIR}/debian/patches
	if [ ! -s ${PATCH_DIR}/series ]; then
		bbfatal "no patch in series"
	fi
	for patch in $(sed "s@#.*@@" ${PATCH_DIR}/series); do
		bbnote "applying $patch"
		patch -p1 < ${PATCH_DIR}/${patch}
	done
}
