#
# base recipe: http://cgit.openembedded.org/meta-openembedded/tree/meta-oe/\
# recipes-support/syslog-ng/syslog-ng_3.5.4.1.bb
# base branch: master
#

SUMMARY = "Alternative system logger daemon"
DESCRIPTION = "\
	syslog-ng, as the name shows, is a syslogd replacement, but with new \
	functionality for the new generation. The original syslogd allows \
	messages only to be sorted based on priority/facility pairs; syslog-ng\
	adds the possibility to filter based on message contents using regular\
	expressions. The new configuration scheme is intuitive and powerful. \
	Forwarding logs over TCP and remembering all forwarding hops makes it \
	ideal for firewalled environments. \
	"
HOMEPAGE = "\
	http://www.balabit.com/network-security/syslog-ng/opensource-logging-system"

LICENSE = "GPLv2 & LGPLv2.1+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=e0e8658d9be248f01b7933df24dc1408 \
	file://GPL.txt;md5=133409beb2c017d3b0f406f22c5439e7 \
	file://LGPL.txt;md5=4fbd65380cdd255951079008b364516c"

PR = "r1"
inherit debian-package
PV = "3.5.6"

#disable-build-doc_debian.patch:
#	this patch to disable build doc, \
#	to build doc folder need to docbook-xsl packages.
SRC_URI += "file://disable-build-doc_debian.patch"

DEPENDS = "flex glib-2.0 util-linux"

inherit autotools-brokensep systemd pkgconfig update-alternatives

# Empty DEBIAN_QUILT_PATCHES to avoid error "debian/patches not found"
DEBIAN_QUILT_PATCHES = ""

# Configure base on debian/rules
EXTRA_OECONF += " \
	--sysconfdir=${sysconfdir}/${DPN} \
	--localstatedir=${localstatedir}/lib/${DPN} \
	--datadir=${datadir}/${DPN} \
	--libdir=${libdir}/${DPN} \
	--enable-dynamic-linking 	\
	--with-libmongo-client=system 	\
	--with-librabbitmq-client=no 	\
	--enable-mongodb 		\
	--with-ivykis=system 		\
	--enable-amqp 			\
	--with-librabbitmq-client=internal \
	--disable-build-docs \
	--disable-doc \
	--disable-tools \
"

PACKAGES =+ "\
	${PN}-mod-amqp ${PN}-mod-geoip ${PN}-mod-json 	 \
	${PN}-mod-mongodb ${PN}-mod-redis ${PN}-mod-smtp \
	${PN}-mod-sql ${PN}-mod-stomp ${PN}-core 	 \
	"

PACKAGECONFIG ??= "\
	openssl json dbi smtp geoip tcp-wrapers libnet 			  \
	eventlog hiredis libmongo-client ivykis 			  \
	${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
	"
PACKAGECONFIG[openssl] = "--enable-ssl,--disable-ssl,openssl,"
PACKAGECONFIG[systemd] = "\
	--enable-systemd --with-systemdsystemunitdir=${systemd_unitdir}/system/,\
	--disable-systemd --without-systemdsystemunitdir, systemd, libsystemd-daemon-dev"
PACKAGECONFIG[linux-caps] = "--enable-linux-caps,--disable-linux-caps,libcap,"
PACKAGECONFIG[pcre] = "--enable-pcre,--disable-pcre,libpcre,"
PACKAGECONFIG[dbi] = "--enable-sql,--disable-sql,libdbi,"
PACKAGECONFIG[libnet] = "\
	--enable-libnet --with-libnet=${STAGING_BINDIR_CROSS},\
	--disable-libnet,libnet,"
PACKAGECONFIG[smtp] = "\
	--enable-smtp --with-libesmtp=${STAGING_LIBDIR},--disable-smtp,libesmtp,"
PACKAGECONFIG[json] = "--enable-json,--disable-json,json-c,"
PACKAGECONFIG[tcp-wrapper] = "\
	--enable-tcp-wrapper,--disable-tcp-wrapper,tcp-wrappers,"
PACKAGECONFIG[geoip] = "--enable-geoip,--disable-geoip,geoip,"
PACKAGECONFIG[ivykis] = "--enable-ivykis,--disable-ivykis,ivykis,"
PACKAGECONFIG[libmongo-client] = "\
	--enable-libmongo-client,--disable-libmongo-client,libmongo-client,"
PACKAGECONFIG[hiredis] = "--enable-hiredis,--disable-hiredis,hiredis,"
PACKAGECONFIG[eventlog] = "--enable-eventlog,--disable-eventlog,eventlog,"
do_configure_append() {
	# According to Debian, the directory to install modules contains package version,
	# so we need re-run configure with correct version from ${S}/VERSION
	VERSION=`cat ${S}/VERSION`
	oe_runconf --with-module-dir=${libdir}/${DPN}/$VERSION
}

#install follow debian jessie
do_install() {
	# Correct pkgconfigdir to /usr/lib/pkgconfig
	oe_runmake DESTDIR=${D} \
	           pkgconfigdir=${libdir}/pkgconfig \
	           install
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/logcheck/ignore.d.paranoid
	install -d ${D}${sysconfdir}/logcheck/ignore.d.server
	install -d ${D}${sysconfdir}/logcheck/violations.ignore.d
	install -d ${D}${sysconfdir}/logrotate.d

	# According to debian/syslog-ng-core.dirs
	install -d ${D}${sysconfdir}/${DPN}/conf.d

	install -m 0644 ${S}/debian/syslog-ng-core.syslog-ng.default \
			${D}${sysconfdir}/default/syslog-ng
	install -m 0644 ${S}/debian/syslog-ng-core.syslog-ng.upstart \
			${D}${sysconfdir}/init/syslog-ng.conf
	# NOTE: "inherit systemd" remove ${sysconfdir}/init.d if DISTRO_FEATURES
	#includes systemd but not sysvinit.
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
	install -m 0644 ${S}/debian/syslog-ng.conf \
			${D}${sysconfdir}/${DPN}/syslog-ng.conf
	install -m 0644 ${S}/debian/tty10.linux.conf \
			${D}${datadir}/${DPN}/include/scl/system/tty10.conf
}

FILES_${PN}-mod-amqp = "${libdir}/${DPN}/*/libafamqp.so"
FILES_${PN}-mod-geoip = "${libdir}/${DPN}/*/libtfgeoip.so"
FILES_${PN}-mod-json = "${libdir}/${DPN}/*/libjson-plugin.so"
FILES_${PN}-mod-mongodb = "${libdir}/${DPN}/*/libafmongodb.so"
FILES_${PN}-mod-redis = "${libdir}/${DPN}/*/libredis.so"
FILES_${PN}-mod-smtp = "${libdir}/${DPN}/*/libafsmtp.so"
FILES_${PN}-mod-sql = "${libdir}/${DPN}/*/libafsql.so"
FILES_${PN}-mod-stomp = "${libdir}/${DPN}/*/libafstomp.so"
FILES_${PN}-dev += "\
	${libdir}/pkgconfig/* \
	${libdir}/${DPN}/libsyslog-ng.so \
	${datadir}/${DPN}/tools/* \
"
FILES_${PN}-core = "\
	${sysconfdir}/* 				\
	${base_libdir}/systemd/system/syslog-ng.service \
	${bindir}/* 					\
	${sbindir}/* 					\
	${libdir}/${DPN}/*/*.so 			\
	${libdir}/${DPN}/libsyslog-ng-*.so 		\
	${datadir}/${DPN}/include/*			\
"
FILES_${PN} += "${datadir}/*"
FILES_${PN}-dbg += "${libdir}/${DPN}/*/.debug/*"
FILES_${PN}-staticdev += "${libdir}/${DPN}/syslog-ng/libtest/*.a"
INSANE_SKIP_${PN}-core = "dev-so"
# ignore to check dev-so because syslog-ng has dependency to libsystemd-daemon-dev
INSANE_SKIP_${PN} += "dev-deps"

#runtime depend, follow debian/control
RDEPENDS_${PN} += "${PN}-core"
RDEPENDS_${PN}-dev += "${PN}-core"
RDEPENDS_${PN}-mod-json += "${PN}-core"
RDEPENDS_${PN}-mod-mongodb += "${PN}-core"
RDEPENDS_${PN}-mod-sql += "${PN}-core"
RDEPENDS_${PN}-mod-smtp += "${PN}-core"
RDEPENDS_${PN}-mod-amqp += "${PN}-core"
RDEPENDS_${PN}-mod-geoip += "${PN}-core"
RDEPENDS_${PN}-mod-redis += "${PN}-core"
RDEPENDS_${PN}-mod-stomp += "${PN}-core"
