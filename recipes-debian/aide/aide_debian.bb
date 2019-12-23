LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"

DEPENDS = "bison-native libmhash libpcre audit acl libselinux e2fsprogs"

inherit debian-package
require recipes-debian/sources/aide.inc

inherit autotools pkgconfig

EXTRA_OECONF += "--with-zlib --with-xattr --with-posix-acl --with-e2fsattrs --with-selinux --with-audit"

do_configure_append () {
	test "$ac_cv_ino_type" = "cross" && \
		test "$BASE_LIB" == "lib64" && \
			sed -i 's/cross/ino64_t/g' ${WORKDIR}/build/config.h || \
			sed -i 's/cross/ino_t/g' ${WORKDIR}/build/config.h
}

do_install_append () {
	# Base on debian/aide-common.install
	install -d 755 ${D}${bindir}/
	install -c -m 755 ${S}/contrib/aide-attributes.sh ${D}${bindir}/aide-attributes

	install -d 755 ${D}${datadir}/aide/config/aide
	install -c -m 644 ${S}/debian/aide.conf ${D}${datadir}/aide/config/aide
	install -c -m 644 ${S}/debian/aide.conf.d/* ${D}${datadir}/aide/config/aide
	install -c -m 644 ${S}/debian/aide.settings.d/* ${D}${datadir}/aide/config/aide
	
	install -d 755 ${D}${datadir}/aide/config/default
	install -c -m 644 ${S}/debian/default/aide ${D}${datadir}/aide/config/default

	install -c -m 644 ${S}/debian/ucf-helper/ucf-helper-functions.sh ${D}${datadir}/aide

	install -d 755 ${D}${datadir}/aide/config/cron.daily
	install -c -m 644 ${S}/debian/cron.daily/aide ${D}${datadir}/aide/config/cron.daily

	install -d 755 ${D}${sbindir}
	install -c -m 755 ${S}/debian/aideinit ${D}${sbindir}
	install -c -m 755 ${S}/debian/update-aide.conf ${D}${sbindir}
	install -c -m 755 ${S}/debian/wrapper/aide.wrapper ${D}${bindir}
	
	install -d 755 ${D}${datadir}/lintian/overrides
	install -c -m 644 ${S}/debian/lintian/overrides/aide-common ${D}${datadir}/lintian/overrides
	install -c -m 644 ${S}/debian/lintian/overrides/${PN} ${D}${datadir}/lintian/overrides
}

PACKAGES =+ "aide-common"
FILES_${PN} += "${bindir}/aide ${datadir}/lintian/overrides/${PN}"
FILES_aide-common += " \
		${bindir}/aide-* \
		${bindir}/aide.* \
		${datadir}/aide/* \
		${sbindir}/* \
		${datadir}/lintian/overrides/aide-common \
		"

RDEPENDS_aide-common += "bash"
