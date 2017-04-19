#
# Base recipe: meta/recipes-graphics/xorg-driver/xf86-input-keyboard_1.8.0.bb
# Base branch: daisy
#

require xorg-driver-input.inc
PV = "1.8.0"

SUMMARY = "X.Org X server -- keyboard input driver"

DESCRIPTION = "keyboard is an Xorg input driver for keyboards. The \
driver supports the standard OS-provided keyboard interface.  The driver \
functions as a keyboard input device, and may be used as the X server's \
core keyboard."

PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=ea2099d24ac9e316a6d4b9f20b3d4e10"

DPN = "xserver-xorg-input-keyboard"

# There is no debian patch, but debian/rules keep using quilt.
# debian/rules - line26:
#     dh $@ --with quilt,autoreconf,xsf --builddirectory=build/
# so we do the same thing with DEBIAN_PATCH_TYPE is "quilt".
DEBIAN_PATCH_TYPE = "quilt"
# Empty DEBIAN_QUILT_PATCHES to avoid error "debian/patches not found"
DEBIAN_QUILT_PATCHES = ""
