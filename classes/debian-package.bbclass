#
# debian-package.bbclass
#

# Debian source package name
DPN ?= "${BPN}"

# revision of Debian recipes
# should be statically defined in each recipe
DPR ?= ""

# default SRC_URI
DEBIAN_GIT_DIR ??= "debian-${DPN}.git"
DEBIAN_GIT_BRANCH ?= "${DISTRO_CODENAME}-master"
DEBIAN_SRC_URI ?= "${DEBIAN_GIT_URI}/${DEBIAN_GIT_DIR};\
protocol=${DEBIAN_GIT_PROTOCOL};branch=${DEBIAN_GIT_BRANCH}"

SRC_URI = "${DEBIAN_SRC_URI}"

# By default, always use latest version of the default branch
SRCREV = "${AUTOREV}"

PV = "git${SRCPV}"

DEBIAN_UNPACK_DIR ?= "${WORKDIR}/git"

# sometimes need to be set to a sub directory in DEBIAN_UNPACK_DIR
S = "${DEBIAN_UNPACK_DIR}"

###############################################################################
# during parsing recipe
###############################################################################

python __anonymous() {
    # append DPR to original PR
    pr = d.getVar("PR", True) or ""
    dpr = d.getVar("DPR", True) or ""
    d.setVar("PR", pr + "deb" + dpr)
}

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

DEBIAN_QUILT_PATCHES ?= "${DEBIAN_UNPACK_DIR}/debian/patches"
DEBIAN_QUILT_DIR ?= "${DEBIAN_UNPACK_DIR}/.pc"
DEBIAN_QUILT_DIR_ESC ?= "${DEBIAN_UNPACK_DIR}/.pc.debian"

# apply patches by quilt
debian_patch_quilt() {
	# confirm that other patches didn't applied
	if [ -d ${DEBIAN_QUILT_DIR} -o -d ${DEBIAN_QUILT_DIR_ESC} ]; then
		bbfatal "unknown quilt patches already applied"
	fi
	# Some packages which don't modify upstream sources have
	# empty series or don't have debian/packages. This seems
	# to be acceptable as an implementation of source package
	if [ ! -f ${DEBIAN_QUILT_PATCHES}/series ]; then
		bbwarn "${DEBIAN_QUILT_PATCHES}/series not found, nothing to do"
	else
		# sometimes series is empty, it's scary
		if [ -z "$(sed '/^#/d' ${DEBIAN_QUILT_PATCHES}/series)" ]; then
			bbwarn "no patch in series, nothing to do"
		else
			# apply patches
			QUILT_PATCHES=${DEBIAN_QUILT_PATCHES} \
				quilt --quiltrc /dev/null push -a
		fi
	fi

	# avoid conflict with "do_patch"
	if [ -d ${DEBIAN_QUILT_DIR} ]; then
		mv ${DEBIAN_QUILT_DIR} ${DEBIAN_QUILT_DIR_ESC}
	fi
}

# apply patches by dpatch
debian_patch_dpatch() {
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
do_debian_patch[depends] += "${@base_conditional(\
    'PN', 'quilt-native', '', 'quilt-native:do_populate_sysroot', d)}"
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

###############################################################################
# do_debian_fix_timestamp
###############################################################################

inherit debian-fix-timestamp
