SUMMARY = "high-performance memory object caching system"
DESCRIPTION = "\
	Danga Interactive developed memcached to enhance the speed of LiveJournal.com, 	\
	a site which was already doing 20 million+ dynamic page views per day for 1 	\
	million users with a bunch of webservers and a bunch of database servers. 	\
	memcached dropped the database load to almost nothing, yielding faster page 	\
	load times for users, better resource utilization, and faster access to the 	\
	databases on a memcache miss \
	"
HOMEPAGE = "http://www.memcached.org/"
PR = "r0"
inherit debian-package
PV = "1.4.21"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=7e5ded7363d335e1bb18013ca08046ff"

inherit autotools-brokensep

#configure follow debian/rules
EXTRA_OECONF += "--enable-sasl"

DEPENDS += "libevent libsasl2"
RDEPENDS_${PN} += "perl lsb-base"

#install follow Debian jessie
do_install_append() {
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${base_libdir}/systemd/system
	install -d ${D}${datadir}/memcached

	install -m 0644 ${S}/debian/memcached.default ${D}${sysconfdir}/default/memcached
	install -m 0644 ${S}/scripts/memcached-init ${D}${sysconfdir}/init.d/memcached
	install -m 0644 ${S}/debian/memcached.service \
		${D}${base_libdir}/systemd/system/memcached.service
	install -m 0644 ${S}/debian/memcached.conf \
		${D}${datadir}/memcached/memcached.conf.default

	cp -a ${S}/scripts ${D}${datadir}/memcached
	install -m 0755 ${S}/debian/systemd-memcached-wrapper \
		${D}${datadir}/memcached/scripts
	rm ${D}${datadir}/memcached/scripts/memcached-init
	rm ${D}${datadir}/memcached/scripts/memcached.sysv
	rm ${D}${datadir}/memcached/scripts/memcached.service
}
FILES_${PN} += "${base_libdir}/*"
PARALLEL_MAKE = ""
