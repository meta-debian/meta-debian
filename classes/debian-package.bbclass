#
# debian-package.bbclass
#

# debian-source.bbclass will generate DEBIAN_SRC_URI information
# in recipes-debian/sources/<source name>.inc

DEBIAN_SRC_URI ?= ""
SRC_URI = "${DEBIAN_SRC_URI}"

DEBIAN_UNPACK_DIR ?= "${WORKDIR}/${BP}"
S = "${DEBIAN_UNPACK_DIR}"
DPV ?= "${PV}"


###############################################################################
# do_debian_unpack_extra
###############################################################################

# Make "debian" sub folder be inside source code folder
addtask debian_unpack_extra after do_unpack before do_debian_patch
do_debian_unpack_extra() {
	if [ -d ${WORKDIR}/debian ]; then
		rm -rf ${DEBIAN_UNPACK_DIR}/debian
		mv ${WORKDIR}/debian ${DEBIAN_UNPACK_DIR}/
	elif [ -f ${WORKDIR}/${BPN}_${DPV}.diff ]; then
		rm -rf ${DEBIAN_UNPACK_DIR}/debian
		cd ${DEBIAN_UNPACK_DIR}
		patch -p1 < ${WORKDIR}/${BPN}_${DPV}.diff
	fi
}

EXPORT_FUNCTIONS do_debian_unpack_extra


###############################################################################
# do_debian_patch
###############################################################################

# Check Debian source format and then decide the action.
# The meanings of return values are the follows.
#   0: native package, there is no patch
#   1: 1.0 format or custom format, need to apply patches
#   3: 3.0 quilt format, need to apply patches by quilt
debian_check_source_format() {
	FORMAT_FILE=${DEBIAN_UNPACK_DIR}/debian/source/format
	if [ ! -f ${FORMAT_FILE} ]; then
		bbnote "Debian source format is not defined, assume '1.0'"
		return 1
	fi
	FORMAT_VAL=$(cat ${FORMAT_FILE})
	bbnote "Debian source format is '${FORMAT_VAL}'"
	case "${FORMAT_VAL}" in
	"3.0 (native)")
		bbnote "nothing to do"
		return 0
		;;
	"3.0 (quilt)")
		return 3
		;;
	"3.0"*|"2.0"*)
		# FIXME: no information about how to handle
		bbfatal "unsupported source format"
		;;
	esac
	return 1
}

# Some 3.0 formatted source packages have no patch.
# Please set DEBIAN_QUILT_PATCHES = "" for such packages.
DEBIAN_QUILT_PATCHES ?= "${DEBIAN_UNPACK_DIR}/debian/patches"

DEBIAN_QUILT_DIR ?= "${DEBIAN_UNPACK_DIR}/.pc"
DEBIAN_QUILT_DIR_ESC ?= "${DEBIAN_UNPACK_DIR}/.pc.debian"

# apply patches by quilt
debian_patch_quilt() {
	# confirm that other patches didn't applied
	if [ -d ${DEBIAN_QUILT_DIR} -o -d ${DEBIAN_QUILT_DIR_ESC} ]; then
		bbfatal "unknown quilt patches already applied"
	fi

	# some source packages don't have patch
	if [ -z "${DEBIAN_QUILT_PATCHES}" ]; then
		if [ -f ${DEBIAN_UNPACK_DIR}/debian/patches/series \
			-s ${DEBIAN_UNPACK_DIR}/debian/patches/series ]; then
			bbfatal "DEBIAN_QUILT_PATCHES is null, but ${DEBIAN_UNPACK_DIR}/debian/patches/series exists"
		fi
		bbnote "no debian patch exists in the source tree, nothing to do"
		return
	fi

	if [ ! -d ${DEBIAN_QUILT_PATCHES} ]; then
		bbfatal "${DEBIAN_QUILT_PATCHES} not found"
	elif [ ! -f ${DEBIAN_QUILT_PATCHES}/series ]; then
		bbfatal "${DEBIAN_QUILT_PATCHES}/series not found"
	# sometimes series is empty, it's too scary
	elif [ -z "$(sed '/^#/d' ${DEBIAN_QUILT_PATCHES}/series)" ]; then
		FOUND_PATCHES="$(debian_find_patches)"
		if [ -z "${FOUND_PATCHES}" ]; then
			bbnote "series is empty, nothing to do"
			return
		else
			bbfatal "series is empty, but some patches found"
		fi
	fi

	# apply patches
	QUILT_PATCHES=${DEBIAN_QUILT_PATCHES} quilt --quiltrc /dev/null push -a

	# avoid conflict with "do_patch"
	if [ -d ${DEBIAN_QUILT_DIR} ]; then
		mv ${DEBIAN_QUILT_DIR} ${DEBIAN_QUILT_DIR_ESC}
	fi
}

DEBIAN_DPATCH_PATCHES ?= "${DEBIAN_UNPACK_DIR}/debian/patches"
# apply patches by dpatch
debian_patch_dpatch() {
	# Replace hardcode path in patch files
	find ${DEBIAN_DPATCH_PATCHES} -name "*.dpatch" -type f -exec sed -i \
	    -e "s@^#! /bin/sh /usr/share/dpatch/dpatch-run@#! /usr/bin/env dpatch-run@g" {} \;

	export PATH="${STAGING_DATADIR_NATIVE}/dpatch:$PATH"
	dpatch apply-all
}

DEBIAN_FIND_PATCHES_DIR ?= "${DEBIAN_UNPACK_DIR}/debian"

debian_find_patches() {
	find ${DEBIAN_FIND_PATCHES_DIR} \
		-name "*.patch" -o \
		-name "*.diff" \
		-type f
}

# used only when DEBIAN_PATCH_TYPE is "abnormal"
# this is very rare case; should not be used except
# the cases that all other types cannot be used
# this function must be overwritten by each recipe
debian_patch_abnormal() {
	bbfatal "debian_patch_abnormal not defined"
}

# decide an action to apply patches for the source package
# candidates: quilt, dpatch, nopatch, abnormal
DEBIAN_PATCH_TYPE ?= ""

addtask debian_patch after do_unpack before do_patch
do_debian_patch[dirs] = "${DEBIAN_UNPACK_DIR}"
do_debian_patch[depends] += "${@oe.utils.conditional(\
    'PN', 'quilt-native', '', 'quilt-native:do_populate_sysroot', d)}"
do_debian_patch[depends] += "${@oe.utils.conditional(\
    'DEBIAN_PATCH_TYPE', 'dpatch', 'dpatch-native:do_populate_sysroot', '', d)}"
do_debian_patch() {
	if debian_check_source_format; then
		return 0
	else
		FORMAT=$?
	fi
	# apply patches according to the source format
	case ${FORMAT} in
	1)
		# DEBIAN_PATCH_TYPE must be set manually to decide
		# an action when Debian source format is not 3.0
		if [ -z "${DEBIAN_PATCH_TYPE}" ]; then
			bbfatal "DEBIAN_PATCH_TYPE not set"
		fi

		bbnote "DEBIAN_PATCH_TYPE: ${DEBIAN_PATCH_TYPE}"
		if [ "${DEBIAN_PATCH_TYPE}" = "quilt" ]; then
			debian_patch_quilt
		elif [ "${DEBIAN_PATCH_TYPE}" = "dpatch" ]; then
			debian_patch_dpatch
		elif [ "${DEBIAN_PATCH_TYPE}" = "nopatch" ]; then
			# No patch and no function to apply patches in
			# some source packages. In such cases, confirm
			# that really no patch-related file is included
			FOUND_PATCHES=$(debian_find_patches)
			if [ -n "${FOUND_PATCHES}" ]; then
				bberror "the following patches found:"
				for p in ${FOUND_PATCHES}; do
					bberror ${p}
				done
				bbfatal "please re-consider DEBIAN_PATCH_TYPE"
			fi
		elif [ "${DEBIAN_PATCH_TYPE}" = "abnormal" ]; then
			debian_patch_abnormal
		else
			bbfatal "invalid DEBIAN_PATCH_TYPE: ${DEBIAN_PATCH_TYPE}"
		fi
		;;
	3)
		debian_patch_quilt
		;;
	esac
}
EXPORT_FUNCTIONS do_debian_patch
