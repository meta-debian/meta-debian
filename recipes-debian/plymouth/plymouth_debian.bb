SUMMARY = "Boot animation, logger and I/O multiplexer"
DESCIPTION = "Plymouth provides a boot-time I/O multiplexing framework - the \
most obvious use for which is to provide an attractive graphical animation in \
place of the text messages that normally get shown during boot. (The messages \
are instead redirected to a logfile for later viewing.) However, in \
event-driven boot systems Plymouth can also usefully handle user interaction \
such as password prompts for encrypted file systems. This package provides the \
basic framework, enabling a text-mode animation."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/Plymouth"

inherit debian-package
PV = "0.9.0"

PR = "r0"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit autotools pkgconfig
DEPENDS += "libpng udev pango libxslt"

EXTRA_OECONF += "--disable-silent-rules \
                 --enable-pango \
                 --enable-systemd-integration \
                 --enable-static \
                 --enable-tracing \
                 --disable-gdm-transition \
                 --with-background-color=0x005a8a \
                 --with-gdm-autostart-file \
                 --with-logo=${datadir}/plymouth/debian-logo.png \
                 --with-release-file=${sysconfdir}/os-release \
                 --with-system-root-install \
                 --without-rhgb-compat-link \
                 --with-boot-tty=/dev/tty7 \
                 --with-shutdown-tty=/dev/tty7 \
                 --libexecdir=${libdir} \
                 --enable-documentation=no \
                 "
PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'gtk drm', '', d)}"
PACKAGECONFIG[gtk] = "--enable-gtk --with-log-viewer,--disable-gtk --without-log-viewer,gtk+,"
PACKAGECONFIG[drm] = "--enable-drm,--disable-drm,libdrm libpciaccess,"

do_install_append(){
	# Base on debian/rules
	# Moving /usr/lib to /lib
	mv ${D}${libdir}/*${SOLIBS} ${D}${base_libdir}
	for f in ${D}${libdir}/*.so; do
		LINKLIB=$(basename $(readlink $f))
		ln -sf ../../lib/$LINKLIB $f
	done
	# Adding initramfs-tools integration
	install -D -m 0755 ${S}/debian/local/plymouth.hook \
	                   ${D}${datadir}/initramfs-tools/hooks/plymouth
	sed -i -e 's|@DEB_HOST_MULTIARCH@||g' ${D}${datadir}/initramfs-tools/hooks/plymouth
	install -D -m 0755 ${S}/debian/local/plymouth.init-premount \
	                   ${D}${datadir}/initramfs-tools/scripts/init-premount/plymouth
	install -D -m 0755 ${S}/debian/local/plymouth.init-bottom \
	                   ${D}${datadir}/initramfs-tools/scripts/init-bottom/plymouth

	# Adding other debian specific files
	install -D -m 0644 ${S}/debian/local/debian-logo.png \
	                   ${D}${datadir}/plymouth/debian-logo.png
	install -D -m 0755 ${S}/debian/local/plymouth-update-initrd \
	                   ${D}${libdir}/plymouth/plymouth-update-initrd
	install -D -m 0644 ${S}/debian/local/plymouth.lsb \
	                   ${D}${base_libdir}/lsb/init-functions.d/99-plymouth

	install -D -m 0755 ${S}/debian/plymouth.init \
	                   ${D}${sysconfdir}/init.d/plymouth
	install -m 0755 ${S}/debian/plymouth.plymouth-log.init \
	                ${D}${sysconfdir}/init.d/plymouth-log

	# Base on debian/plymouth.links
	ln -sf plymouth-read-write.service \
	       ${D}${systemd_system_unitdir}/plymouth-log.service
	ln -sf plymouth-quit.service \
	       ${D}${systemd_system_unitdir}/plymouth.service

	rm -rf ${D}${localstatedir}/run
}

PACKAGES =+ "${PN}-themes ${PN}-x11"
FILES_${PN}-themes = "\
	${libdir}/plymouth/fade-throbber.so \
	${libdir}/plymouth/label.so \
	${libdir}/plymouth/space-flares.so \
	${libdir}/plymouth/throbgress.so \
	${libdir}/plymouth/two-step.so \
	${datadir}/plymouth/themes/glow/* \
	${datadir}/plymouth/themes/fade-in/* \
	${datadir}/plymouth/themes/script/* \
	${datadir}/plymouth/themes/solar/* \
	${datadir}/plymouth/themes/spinfinity/* \
	${datadir}/plymouth/themes/spinner/* \
	"
FILES_${PN}-x11 = "\
	${bindir}/plymouth-log-viewer \
	${libdir}/plymouth/renderers/x11.so \
	${datadir}/gdm/autostart/LoginWindow/plymouth-log-viewer.desktop \
	"
FILES_${PN}-dbg += "${libdir}/plymouth/renderers/.debug"
FILES_${PN}-staticdev += "${libdir}/plymouth/renderers/*.a"
FILES_${PN} += "\
	${systemd_system_unitdir}/* \
	${datadir}/initramfs-tools/* \
	${base_libdir}/lsb/* \
	${datadir}/plymouth/themes/details/* \
	${datadir}/plymouth/themes/text/* \
	${datadir}/plymouth/themes/tribar/* \
	"
