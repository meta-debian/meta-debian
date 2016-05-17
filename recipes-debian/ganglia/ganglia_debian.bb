SUMMARY = "cluster monitoring toolkit"
DESCRIPTION = "Ganglia is a scalable, real-time cluster monitoring environment \
 that collects cluster statistics in an open and well-defined XML format."
HOMEPAGE = "http://ganglia.info/"

PR = "r0"
inherit debian-package

LICENSE = "BSD & LGPLv2+ & Apache-1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b3c74375e67e92756770b3ee9debc5f \
                    file://lib/getopt_init.c;endline=19;md5=765b59293ed2609f223df9e582636879 \
                    file://lib/readdir.c;endline=47;md5=302a4cfe73766648336760988ae36035"
DEPENDS = "apr confuse libpcre python rrdtool libtool"
EXTRA_OECONF += "--with-gmetad --enable-shared"

inherit autotools-brokensep pythonnative

# avoid error: #error "LONG_BIT definition appears wrong for platform (bad gcc/glibc config?)."
# use pyport.h file of target machine
do_configure_prepend () {
	sed -i -e "s:\$PyEXEC_INSTALLDIR:${STAGING_DIR_HOST}${exec_prefix}:g" ${S}/configure.ac
}

# The ganglia autoconf setup doesn't include libmetrics in its
# AC_OUTPUT list -- it reconfigures libmetrics using its own rules.
# Unfortunately this means an OE autoreconf will not regenerate
# ltmain.sh (and others) in libmetrics and as such the build will
# fail.  We explicitly force regeneration of that directory. 
do_configure_append() {
	(cd ${S} ; autoreconf -fvi )
	(cd ${S}/libmetrics ; autoreconf -fvi)
}

do_install_append() {
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/${PN}
	install -d ${D}${libdir}/${PN}/python_modules/
	install -m 0644 ${S}/debian/gmond.conf ${D}${sysconfdir}/${PN}/
	install -m 0755 ${S}/debian/ganglia-monitor.init ${D}${sysconfdir}/init.d/ganglia-monitor
	mv ${D}${sysconfdir}/gmetad.conf ${D}${sysconfdir}/${PN}/
	install -m 0755 ${S}/gmetad/gmetad.init ${D}${sysconfdir}/init.d/gmetad

	mv ${D}${sysconfdir}/conf.d ${D}${sysconfdir}/${PN}/	
	cp ${S}/gmond/python_modules/conf.d/* ${D}${sysconfdir}/${PN}/conf.d
	cp ${S}/gmond/python_modules/*/*.py ${D}${libdir}/${PN}/python_modules/	
}

PACKAGES =+ "ganglia-monitor gmetad ganglia-monitor-python"

FILES_ganglia-monitor = " \
	${sysconfdir}/${PN}/gmond.conf \
	${sysconfdir}/init.d/ganglia-monitor \
	${bindir}/gmetric \
	${bindir}/gstat \
	${sbindir}/gmond"

FILES_gmetad = " \
	${sysconfdir}/${PN}/gmetad.conf \
	${sysconfdir}/init.d/gmetad \
	${sbindir}/gmetad"

FILES_ganglia-monitor-python = " \
	${sysconfdir}/${PN}/conf.d/* \
	${libdir}/${PN}/python_modules/*"

PKG_${PN} = "libganglia1"
PKG_${PN}-dev = "libganglia1-dev"
