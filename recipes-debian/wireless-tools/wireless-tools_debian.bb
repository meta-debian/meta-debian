#
# Base recipe: meta/recipes-connectivity/wireless-tools/wireless-tools_30.pre9.bb
# Base branch: daisy
#

SUMMARY = "Tools for the Linux Standard Wireless Extension Subsystem"
HOMEPAGE = "http://www.hpl.hp.com/personal/Jean_Tourrilhes/Linux/Tools.html"

PR = "r0"

inherit debian-package
PV = "30~pre9"

LICENSE = "GPLv2 & (LGPLv2.1 | MPL-1.1 | BSD)"
LIC_FILES_CHKSUM = "\
file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
file://iwconfig.c;beginline=1;endline=12;md5=cf710eb1795c376eb10ea4ff04649caf \
file://iwevent.c;beginline=59;endline=72;md5=d66a10026d4394f0a5b1c5587bce4537 \
file://sample_enc.c;beginline=1;endline=4;md5=838372be07874260b566bae2f6ed33b6"

# remove.ldconfig.call.patch: prevent make install-libs from creating invalid cache
SRC_URI += " \
file://remove.ldconfig.call.patch \
"
EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile() {
	oe_runmake all libiw.a
}

# Follow debian/rules
do_install() {
	oe_runmake install install-static PREFIX=${D}
	install -d ${D}${libdir}
	install -d ${D}${base_libdir}/udev
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/network/if-pre-up.d
	install -d ${D}${sysconfdir}/network/if-post-down.d
	install -m 0755 19-udev-ifrename.rules ${D}${base_libdir}/udev/19-ifrename.rules
	mv ${D}${base_libdir}/libiw.a ${D}${libdir}
	unlink ${D}${base_libdir}/libiw.so
	ln -sf ${base_libdir}/libiw.so.30 ${D}${libdir}/libiw.so
	install -m 0755 ${S}/debian/ifrename.init ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/wireless-tools.if-pre-up  ${D}${sysconfdir}/network/if-pre-up.d/wireless-tools
	install -m 0755 ${S}/debian/wireless-tools.if-post-down ${D}${sysconfdir}/network/if-post-down.d/wireless-tools
}

#Ship paackges follow Debian
PACKAGES =+ "ifrename libiw-dev libiw"

FILES_ifrename = "${sysconfdir}/init.d ${base_libdir}/udev ${base_sbin}/ifrename"
FILES_libiw-dev = "${includedir} ${libdir}/*.so"
FILES_libiw = "${base_libdir}/libiw.so.30"
