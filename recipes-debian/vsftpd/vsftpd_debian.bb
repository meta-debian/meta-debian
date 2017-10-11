SUMMARY = "Very Secure FTP server"
HOMEPAGE = "https://security.appspot.com/vsftpd.html"

PR = "r1"

inherit debian-package
PV = "3.0.2"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=a6067ad950b28336613aed9dd47b1271 \
file://COPYRIGHT;md5=04251b2eb0f298dae376d92454f6f72e \
file://LICENSE;md5=654df2042d44b8cac8a5654fc5be63eb"

SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'pam','','file://nopam.patch',d)}"

DEPENDS = "libcap openssl tcp-wrappers \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam','libpam','',d)} \
           "

LIBS="-lwrap -lcap -lssl -lcrypto -lcrypt \
      ${@bb.utils.contains('DISTRO_FEATURES', 'pam','-lpam','',d)} \
      "

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile () {
	oe_runmake CFLAGS="$(CFLAGS) -W -Wshadow" LIBS="${LIBS}" LINK=""
}

do_install () {
	install -d ${D}${sbindir}
	install -d ${D}${bindir}
	install -d ${D}${mandir}
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/logrotate.d
	install -d ${D}${base_libdir}/systemd/system
	install -m 0755 vsftpd ${D}${sbindir}/vsftpd
	install -m 0644 ${S}/vsftpd.conf ${D}${sysconfdir}
	install -m 0644 ${S}/debian/local/ftpusers ${D}${sysconfdir}
	install -m 0755 ${S}/debian/local/vsftpdwho ${D}${bindir}
	install -m 0755 ${S}/debian/vsftpd.init ${D}${sysconfdir}/init.d/vsftpd
	install -m 0755 ${S}/debian/vsftpd.logrotate  \
			${D}${sysconfdir}/logrotate.d/vsftpd
	install -m 0644 ${S}/debian/vsftpd.service \
			${D}${base_libdir}/systemd/system

	if ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'true', 'false', d)}; then
		install -d ${D}${sysconfdir}/pam.d
		install -m 0755 ${S}/debian/vsftpd.pam ${D}${sysconfdir}/pam.d/vsftpd
	fi
}

FILES_${PN} += "${sysconfdir} ${base_libdir}"

inherit systemd
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE_${PN} = "vsftpd.service"
