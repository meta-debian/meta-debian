#
# Base recipe: meta/recipes-graphics/xorg-lib/xkeyboard-config_2.11.bb
# Base branch: daisy
#

SUMMARY = "Keyboard configuration database for X Window"

DESCRIPTION = "The non-arch keyboard configuration database for X \
Window.  The goal is to provide the consistent, well-structured, \
frequently released open source of X keyboard configuration data for X \
Window System implementations.  The project is targeted to XKB-based \
systems."

HOMEPAGE = "http://freedesktop.org/wiki/Software/XKeyboardConfig"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xkeyboard-config"

PR = "r1"

inherit debian-package
PV = "2.12"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=0e7f21ca7db975c63467d2e7624a12f9"

DEPENDS = "intltool-native virtual/gettext util-macros libxslt-native"

#Follow configuration in debian/rules
EXTRA_OECONF = "\
		--with-xkb-rules-symlink=xfree86,xorg \
		--with-xkb-base=${datadir}/X11/xkb \
"
# There are dependencies which are run-time dependencies only and not required for building.
# Skip this check with --disable-runtime-deps.
EXTRA_OECONF += "--disable-runtime-deps"

inherit autotools pkgconfig

do_install_append () {
    install -d ${D}${datadir}/X11/xkb/compiled
    cd ${D}${datadir}/X11/xkb/rules && ln -sf base xorg
}

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

PACKAGES = "xkb-data"

FILES_xkb-data = "${datadir}" 

