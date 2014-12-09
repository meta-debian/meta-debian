#
# debian-package.bbclass
#

# Debian source package name
DPN ?= "${BPN}"

# should be defined in each recipe
DPR ?= ""

# TODO:
# * remove SECTION (need to change git repo paths)
# * consider other branches like backport
# * consider fetching by a common tag
DEBIAN_BRANCH ?= "${DISTRO_CODENAME}-master"
DEBIAN_SECTION ?= "SECTION"
SRC_URI = "${DEBIAN_GIT_URI}/${DEBIAN_SECTION}/${BPN}.git;protocol=git;branch=${DEBIAN_BRANCH}"
# By default, always use latest version of the default branch
# TODO: consider fetching by a common tag
SRCREV = "${AUTOREV}"

# TODO: we want to include "Package version number" in PV
PV = "git${SRCPV}"

DEBIAN_UNPACK_DIR ?= "${WORKDIR}/git"

# sometimes need to be overwritten by a sub directory
S = "${DEBIAN_UNPACK_DIR}"

###############################################################################
# parse
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

# do_debian_patch depends on quilt
# FIXME: also depends on "dpatch"
DEPENDS += "${@base_conditional('PN', 'quilt-native', '', 'quilt-native', d)}"

DEBIAN_QUILT_DIR ?= "${DEBIAN_UNPACK_DIR}/.pc"
DEBIAN_QUILT_DIR_ESC ?= "${DEBIAN_UNPACK_DIR}/.pc.debian"

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

# apply patches by quilt
debian_patch_quilt() {
	if [ ! -s ${DEBIAN_UNPACK_DIR}/debian/patches/series ]; then
		bbfatal "no patch in series"
	fi
	QUILT_PATCHES=${DEBIAN_UNPACK_DIR}/debian/patches \
		quilt --quiltrc /dev/null push -a
}

# apply patches by dpatch
debian_patch_dpatch() {
	dpatch apply-all
}

addtask debian_patch after do_unpack before do_patch
do_debian_patch[dirs] = "${DEBIAN_UNPACK_DIR}"
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
	if [ ! -d ${DEBIAN_UNPACK_DIR}/debian/patches ]; then
		bbfatal "debian/patches not found in non-native package"
	fi

	# apply patches according to the source format
	case ${FORMAT} in
	1)
		if [ -f ${DEBIAN_UNPACK_DIR}/debian/patches/series ]; then
			bbnote "patch type: quilt"
			debian_patch_quilt
		elif [ -f ${DEBIAN_UNPACK_DIR}/debian/patches/00list ]; then
			bbnote "patch type: dpatch"
			debian_patch_dpatch
		else
			bbfatal "unsupported patch type"
		fi
		;;
	3)
		debian_patch_quilt
		;;
	esac

	# avoid conflict with "do_patch"
	if [ -d ${DEBIAN_QUILT_DIR} ]; then
		mv ${DEBIAN_QUILT_DIR} ${DEBIAN_QUILT_DIR_ESC}
	fi
}
EXPORT_FUNCTIONS do_debian_patch
