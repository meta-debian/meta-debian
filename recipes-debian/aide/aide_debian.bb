LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"

DEPENDS = "bison-native libmhash libpcre audit acl libselinux e2fsprogs"

inherit debian-package
require recipes-debian/sources/aide.inc

inherit autotools pkgconfig

EXTRA_OECONF += "--with-zlib --with-xattr --with-posix-acl --with-e2fsattrs --with-selinux --with-audit"

do_configure () {
	mkdir -p aide && cd aide && ${S}/configure ${EXTRA_OECONF} ${CONFIGUREOPTS} && cd ..
	mkdir -p aide-xen && cd aide-xen && ${S}/configure ${EXTRA_OECONF} ${CONFIGUREOPTS} --with-extra-libs="-L${STAGING_BINDIR_NATIVE}${libdir}/xen/" && cd ..
	mkdir -p aide-dynamic && cd aide-dynamic && ${S}/configure ${EXTRA_OECONF} ${CONFIGUREOPTS} --disable-static && cd ..
}

do_configure_append () {
	test "$ac_cv_ino_type" = "cross" && \
		test "$BASE_LIB" == "lib64" && \
			sed -i 's/cross/ino64_t/g' ${WORKDIR}/build/*/config.h || \
			sed -i 's/cross/ino_t/g' ${WORKDIR}/build/*/config.h
}

do_compile () {
	make -C aide
	make -C aide-xen
	make -C aide-dynamic
}

do_install () {
	make -C aide DESTDIR="${D}/aide" install
	make -C aide-xen DESTDIR="${D}/aide-xen" install
	make -C aide-dynamic DESTDIR="${D}/aide-dynamic" install
	
	# Base on debian/aide-common.install
	mkdir -p ${D}/aide-common${bindir}/
	install -c -m 755 ${S}/contrib/aide-attributes.sh ${D}/aide-common${bindir}/aide-attributes

	mkdir -p ${D}/aide-common${datadir}aide/config/aide
	install -c -m 644 ${S}/debian/aide.conf ${D}/aide-common${datadir}aide/config/aide
	install -c -m 644 ${S}/debian/aide.conf.d/* ${D}/aide-common${datadir}aide/config/aide
	install -c -m 644 ${S}/debian/aide.settings.d/* ${D}/aide-common${datadir}aide/config/aide
	
	mkdir -p ${D}/aide-common${datadir}aide/config/default
	install -c -m 644 ${S}/debian/default/aide ${D}/aide-common${datadir}aide/config/default

	mkdir -p ${D}/aide-common${datadir}
	install -c -m 644 ${S}/debian/ucf-helper/ucf-helper-functions.sh ${D}/aide-common${datadir}aide

	mkdir -p ${D}/aide-common${datadir}aide/config/cron.daily
	install -c -m 644 ${S}/debian/cron.daily/aide ${D}/aide-common${datadir}aide/config/cron.daily

	mkdir -p ${D}/aide-common${sbindir}
	install -c -m 755 ${S}/debian/aideinit ${D}/aide-common${sbindir}
	install -c -m 755 ${S}/debian/update-aide.conf ${D}/aide-common${sbindir}
	install -c -m 755 ${S}/debian/wrapper/aide.wrapper ${D}/aide-common${bindir}
	
	mkdir -p ${D}/aide-common${datadir}lintian/overrides
	install -c -m 644 ${S}/debian/lintian/overrides/aide-common ${D}/aide-common${datadir}lintian/overrides
}

PACKAGES =+ "${PN}-xen ${PN}-dynamic ${PN}-common"
FILES_${PN} += "${PN}/*"
FILES_${PN}-xen += "${PN}-xen/*"
FILES_${PN}-dynamic += "${PN}-dynamic/*"
FILES_${PN}-common += "${PN}-common/*"

RDEPENDS_${PN}-common += "bash"
