# base recipe: meta-openembedded/meta-oe/recipes-support/syslog-ng/syslog-ng_3.19.1.bb
# base branch: warrior

SUMMARY = "Enhanced system logging daemon"
DESCRIPTION = "syslog-ng is an enhanced log daemon, supporting a wide range of input \
and output methods: syslog, unstructured text, message queues, \
databases (SQL and NoSQL alike) and more."
HOMEPAGE = "https://www.syslog-ng.com/"

inherit debian-package
require recipes-debian/sources/syslog-ng.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/syslog-ng-syslog-ng-${PV}"

LICENSE = "GPL-2.0-with-OpenSSL-exception & LGPL-2.1-with-OpenSSL-exception"
LIC_FILES_CHKSUM = "file://COPYING;md5=24c0c5cb2c83d9f2ab725481e4df5240"

SRC_URI += " \
    file://configure.ac-add-option-enable-thread-tls-to-manage-.patch \
    file://fix-config-libnet.patch \
    file://fix-invalid-ownership.patch \
"

DEPENDS = " \
    libpcre glib-2.0 openssl ivykis \
    bison-native autoconf-archive-native \
"

inherit autotools gettext systemd pkgconfig update-rc.d

EXTRA_OECONF = " \
    --enable-dynamic-linking \
    --disable-sub-streams \
    --disable-pacct \
    --localstatedir=${localstatedir}/lib/${BPN} \
    --sysconfdir=${sysconfdir}/${BPN} \
    --with-module-dir=${libdir}/${BPN} \
    --with-sysroot=${STAGING_DIR_HOST} \
    --without-mongoc --disable-mongodb \
    --with-librabbitmq-client=no \
    --disable-python \
    --disable-java --disable-java-modules \
    --with-ivykis=system \
    ${CONFIG_TLS} \
"

CONFIG_TLS = "--enable-thread-tls"
CONFIG_TLS_arm = "${@oe.utils.conditional( "DEBUG_BUILD", "1", " --disable-thread-tls", " --enable-thread-tls", d )}"

PACKAGECONFIG ??= " \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6 systemd', d)} \
"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,,"
PACKAGECONFIG[systemd] = "--enable-systemd --with-systemdsystemunitdir=${systemd_system_unitdir},\
                          --disable-systemd --without-systemdsystemunitdir,systemd,"
PACKAGECONFIG[linux-caps] = "--enable-linux-caps,--disable-linux-caps,libcap,"
PACKAGECONFIG[dbi] = "--enable-sql,--disable-sql,libdbi,"
PACKAGECONFIG[libnet] = "--enable-libnet --with-libnet=${STAGING_BINDIR_CROSS},--disable-libnet,libnet,"
PACKAGECONFIG[http] = "--enable-http,--disable-http,curl,"
PACKAGECONFIG[smtp] = "--enable-smtp --with-libesmtp=${STAGING_LIBDIR},--disable-smtp,libesmtp,"
PACKAGECONFIG[json] = "--enable-json --with-jsonc=system,--disable-json,json-c,"
PACKAGECONFIG[tcp-wrapper] = "--enable-tcp-wrapper,--disable-tcp-wrapper,tcp-wrappers,"
PACKAGECONFIG[geoip] = "--enable-geoip,--disable-geoip,geoip,"
PACKAGECONFIG[native] = "--enable-native,--disable-native,,"

do_configure_prepend() {
	# In Debian source, these folders are empty
	for i in ${S}/lib/ivykis ${S}/lib/jsonc; do
		test -d $i && rmdir --ignore-fail-on-non-empty $i
	done
}

do_install_append() {
	install -d ${D}${sysconfdir}/${BPN}/conf.d \
	           ${D}${sysconfdir}/default \
	           ${D}${sysconfdir}/init.d \
	           ${D}${sysconfdir}/logcheck/ignore.d.paranoid \
	           ${D}${sysconfdir}/logcheck/ignore.d.server \
	           ${D}${sysconfdir}/logcheck/violations.ignore.d \
	           ${D}${sysconfdir}/logrotate.d \
	           ${D}${systemd_system_unitdir} \
	           ${D}${localstatedir}/lib/${BPN}

	install -m 0644 ${S}/debian/syslog-ng-core.syslog-ng.default \
	                ${D}${sysconfdir}/default/syslog-ng
	install -m 0755 ${S}/debian/syslog-ng-core.syslog-ng.init \
	                ${D}${sysconfdir}/init.d/syslog-ng
	install -m 0644 ${S}/debian/syslog-ng-core.syslog-ng.logcheck.ignore.paranoid \
	                ${D}${sysconfdir}/logcheck/ignore.d.paranoid/syslog-ng
	install -m 0644 ${S}/debian/syslog-ng-core.syslog-ng.logcheck.ignore.server \
	                ${D}${sysconfdir}/logcheck/ignore.d.server/syslog-ng
	install -m 0644 ${S}/debian/syslog-ng-core.syslog-ng.logcheck.violations.ignore \
	                ${D}${sysconfdir}/logcheck/violations.ignore.d/syslog-ng
	install -m 0644 ${S}/debian/syslog-ng-core.syslog-ng.logrotate \
	                ${D}${sysconfdir}/logrotate.d/syslog-ng

	install -m 0644 ${S}/debian/syslog-ng.systemd ${D}${systemd_system_unitdir}/syslog-ng.service
	install -m 0644 ${S}/debian/syslog-ng.conf ${D}${sysconfdir}/${BPN}/
	install -m 0644 ${S}/debian/scl.conf ${D}${sysconfdir}/${BPN}/
	install -m 0644 ${S}/debian/tty10.linux.conf \
		        ${D}${datadir}/${BPN}/include/scl/system/tty10.conf

	rm -f ${D}${systemd_system_unitdir}/syslog-ng@.service
}

RDEPENDS_${PN} += "gawk lsb"
RCONFLICTS_${PN} = "busybox-syslog sysklogd rsyslog"

CONFFILES_${PN} = " \
    ${sysconfdir}/${BPN}/syslog-ng.conf \
    ${sysconfdir}/${BPN}/scl.conf"
SYSTEMD_SERVICE_${PN} = "syslog-ng.service"

INITSCRIPT_NAME = "syslog-ng"
INITSCRIPT_PARAMS = "start 20 2 3 4 5 . stop 90 0 1 6 ."
