require recipes-graphics/xorg-proto/xextproto_7.3.0.bb

BPN = "x11proto-xext"
inherit debian-package
DEBIAN_SECTION = "x11"
DPR = "0"

S = "${WORKDIR}/git" 

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=86f273291759d0ba2a22585cd1c06c53"

# There are no debian/patches
do_debian_patch() {
	if debian_check_source_format; then
		return 0
	else
		FORMAT=$?
	fi

	if [ -d ${DEBIAN_QUILT_DIR} -o -d ${DEBIAN_QUILT_DIR_ESC} ]; then
		bbfatal "unknown quilt patches already applied"
	fi

	# debian/patches must exist in non-native source package.
	# Some old packages have ignore this rule. For such packages,
	# user needs to overwrite this function by their hands.
	#if [ ! -d ${DEBIAN_UNPACK_DIR}/debian/patches ]; then
	#	bbfatal "debian/patches not found in non-native package"
	#fi

	# apply patches according to the source format
	#case ${FORMAT} in
	#1)
	#	if [ -f ${DEBIAN_UNPACK_DIR}/debian/patches/series ]; then
	#		bbnote "patch type: quilt"
	#		debian_patch_quilt
	#	elif [ -f ${DEBIAN_UNPACK_DIR}/debian/patches/00list ]; then
	#		bbnote "patch type: dpatch"
	#		debian_patch_dpatch
	#	else
	#		bbfatal "unsupported patch type"
	#	fi
	#	;;
	#3)
	#	debian_patch_quilt
	#	;;
	#esac

	# avoid conflict with "do_patch"
	if [ -d ${DEBIAN_QUILT_DIR} ]; then
		mv ${DEBIAN_QUILT_DIR} ${DEBIAN_QUILT_DIR_ESC}
	fi
}
