#
# Base recipe: meta/recipes-graphics/xorg-app/xinit_1.3.3.bb
# Base branch: daisy
#

SUMMARY = "X Window System initializer"

DESCRIPTION = "The xinit program is used to start the X Window System \
server and a first client program on systems that cannot start X \
directly from /etc/init or in environments that use multiple window \
systems. When this first client exits, xinit will kill the X server and \
then terminate."

require xorg-app-common.inc
PV = "1.3.4"

PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=18f01e7b39807bebe2b8df101a039b68"

EXTRA_OECONF = "ac_cv_path_MCOOKIE=${bindir}/mcookie"

RDEPENDS_${PN} += "util-linux"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

# Install package and set permission follow debian/rules
do_install_append () {
	install -d ${D}${sysconfdir}/X11/xinit
	install -m 755 ${S}/debian/local/xserverrc ${D}${sysconfdir}/X11/xinit
	chmod 755 ${D}${sysconfdir}/X11/xinit/xinitrc
}
