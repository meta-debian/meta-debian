SUMMARY = "cluster monitoring toolkit"
DESCRIPTION = "Ganglia is a scalable, real-time cluster monitoring environment \
 that collects cluster statistics in an open and well-defined XML format."
HOMEPAGE = "http://ganglia.info/"

PR = "r0"
inherit debian-package
PV = "3.6.1"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b3c74375e67e92756770b3ee9debc5f"
# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

#compile and install follow debian/rules
do_compile_prepend() {
	touch ChangeLog NEWS
}
do_install() {
	install -d ${D}${sysconfdir}/ganglia-webfrontend
	oe_runmake install APACHE_USER=www-data DESTDIR=${D} \
		GDESTDIR=${datadir}/ganglia-webfrontend  \
		GWEB_STATEDIR=${localstatedir}/lib/ganglia-web
	cp -f ${S}/debian/apache.conf \
		${D}${sysconfdir}/ganglia-webfrontend
	cp -f ${S}/debian/conf_debian.php \
		${D}${datadir}/ganglia-webfrontend/conf.php
	rm ${D}${datadir}/ganglia-webfrontend/COPYING
}

PKG_${PN} = "${PN}frontend"
FILES_${PN} += "${datadir}"
RDEPENDS_${PN} += "apache2 rrdtool"
