#
# Base recipe: meta/recipes-graphics/xorg-app/xkbcomp_1.2.4.bb
# Base branch: daisy
#

require xorg-app-common.inc
PV = "7.7+1"

SUMMARY = "A program to compile XKB keyboard description"

DESCRIPTION = "The xkbcomp keymap compiler converts a description of an \
XKB keymap into one of several output formats. The most common use for \
xkbcomp is to create a compiled keymap file (.xkm extension) which can \
be read directly by XKB-capable X servers or utilities."

LIC_FILES_CHKSUM = "file://COPYING;md5=08436e4f4476964e2e2dd7e7e41e076a"

PR = "${INC_PR}.1"

DEPENDS += "libxkbfile"

BBCLASSEXTEND = "native"

DPN = "x11-xkb-utils"

# Apply patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

# 11_xkb_documentation_updates.diff is commented in debian/patches/series.
# Remove it to avoid error: "series is empty, but some patches found"
do_debian_patch_prepend() {
	if [ -f ${DEBIAN_QUILT_PATCHES}/11_xkb_documentation_updates.diff ]; then
		rm  ${DEBIAN_QUILT_PATCHES}/11_xkb_documentation_updates.diff
	fi
}

S = "${DEBIAN_UNPACK_DIR}/${PN}"
