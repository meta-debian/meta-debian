#
# Base recipe: meta-oe/recipes-graphics/xorg-app/twm_1.0.9.bb
# Base commit: 41c804bb34bb36b3a863e4c60550f171a72c8dc1
#

require xorg-app-common.inc
PV = "1.0.8"

DESCRIPTION = "tiny window manager"

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=4c6d42ef60e8166aa26606524c0b9586"

DEPENDS += " libxext libxt libxmu"

# Apply patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

# Install package follow Debian
do_install_append() {
	install -d ${D}${sysconfdir}/X11/twm
	install -m 0644 ${S}/src/system.twmrc ${D}${sysconfdir}/X11/twm/system.twmrc-menu
	install -d ${D}${datadir}/xsessions
	install -m 644 ${S}/debian/twm.desktop ${D}${datadir}/xsessions/twm.desktop
	install -d ${D}${sysconfdir}/menu-methods
	install -m 0644 ${S}/debian/twm.menu-method ${D}${sysconfdir}/menu-methods
	install -d ${D}${libdir}/X11
	ln -s ${sysconfdir}/X11/twm ${D}${libdir}/X11/twm
}

FILES_${PN} = "${sysconfdir} ${bindir} ${libdir}"
FILES_${PN}-doc = "${datadir}"
