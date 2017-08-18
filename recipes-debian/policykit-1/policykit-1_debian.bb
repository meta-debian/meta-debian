SUMMARY = "framework for managing administrative policies and privileges"
DESCRIPTION = "PolicyKit is an application-level toolkit for defining and handling the policy \
 that allows unprivileged processes to speak to privileged processes."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/polkit/"

inherit debian-package
PV = "0.105"

LICENSE = "LGPLv2+ & Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=155db86cdbafa7532b41f390409283eb \
                    file://test/mocklibc/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit autotools pkgconfig

SRC_URI += "file://disable-gtk-doc-and-introspection_debian.patch"

EXTRA_OECONF += "--disable-examples \
                 --libexecdir=${libdir}/${DPN} \
                 --with-os-type=debian \
                 ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--enable-systemd', '--disable-systemd', d)}"

DEPENDS += "glib-2.0 intltool-native expat libpam dbus libxslt libselinux \
            ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

do_install_append() {
	# Follow debian/policykit-1.install
	install -D -m 0644 ${S}/debian/polkitd.service \
		${D}${systemd_system_unitdir}/polkitd.service

	# Follow debian/rules
	echo "[Configuration]\nAdminIdentities=unix-group:sudo" > \
		${D}${sysconfdir}/polkit-1/localauthority.conf.d/51-debian-sudo.conf

	# Remove unwanted files
	rm -rf ${D}${libdir}/polkit-1/extensions/libnullbackend.a \
	       ${D}${libdir}/polkit-1/extensions/libnullbackend.la \
	       ${D}${libdir}/*.la
}
PACKAGES =+ "libpolkit-agent-1 libpolkit-agent-1-dev \
             libpolkit-backend-1 libpolkit-backend-1-dev"

FILES_libpolkit-agent-1 = "${libdir}/libpolkit-agent-1${SOLIBS}"
FILES_libpolkit-agent-1-dev = "${includedir}/polkit-1/polkitagent/*.h \
                               ${libdir}/libpolkit-agent-1.so \
                               ${libdir}/pkgconfig/polkit-agent-1.pc"
FILES_libpolkit-backend-1 = "${libdir}/libpolkit-backend-1${SOLIBS}"
FILES_libpolkit-backend-1-dev = "${includedir}/polkit-1/polkitbackend/*.h \
                                 ${libdir}/libpolkit-backend-1.so \
                                 ${libdir}/pkgconfig/polkit-backend-1.pc"

FILES_${PN} += "${datadir}/dbus-1/* \
                ${datadir}/polkit-1/* \
                ${libdir}/polkit-1/extensions/libnullbackend.so \
                ${systemd_system_unitdir}/*"

RDEPENDS_${PN} += "dbus libpam-systemd"
