SUMMARY = "cluster monitoring toolkit"
DESCRIPTION = "Ganglia is a scalable, real-time cluster monitoring environment \
 that collects cluster statistics in an open and well-defined XML format."
HOMEPAGE = "http://ganglia.info/"

PR = "r1"
inherit debian-package
PV = "3.6.0"

LICENSE = "BSD & LGPLv2+ & Apache-1.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b3c74375e67e92756770b3ee9debc5f \
                    file://lib/getopt_init.c;endline=19;md5=765b59293ed2609f223df9e582636879 \
                    file://lib/readdir.c;endline=47;md5=302a4cfe73766648336760988ae36035"
DEPENDS = "apr confuse libpcre python rrdtool libtool"

# Follow debian/rules
EXTRA_OECONF += "--sysconfdir=${sysconfdir}/${DPN} --enable-shared --with-gmetad"

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
	install -d ${D}${libdir}/${DPN}/python_modules/
	install -m 0644 ${S}/debian/gmond.conf ${D}${sysconfdir}/${DPN}/
	install -m 0755 ${S}/debian/ganglia-monitor.init ${D}${sysconfdir}/init.d/ganglia-monitor
	install -m 0755 ${S}/debian/gmetad.init ${D}${sysconfdir}/init.d/gmetad

	# Empty out the dependency field in our .la files
	for file in ${D}${libdir}/*.la; do
		sed -i "/dependency_libs/ s/'.*'/''/" $file
	done

	install -d ${D}${sysconfdir}/${DPN}/conf.d
	cp ${S}/debian/modpython.conf            ${D}${sysconfdir}/${DPN}/conf.d/
	cp ${S}/gmond/python_modules/*/*.pyconf* ${D}${sysconfdir}/${DPN}/conf.d/
	cp ${S}/gmond/python_modules/*/*.py      ${D}${libdir}/${DPN}/python_modules/
	cp ${S}/gmetad/gmetad.conf               ${D}${sysconfdir}/${DPN}
}

PACKAGES =+ "${PN}-monitor gmetad ${PN}-monitor-python"

FILES_${PN} += "${systemd_system_unitdir}"

FILES_${PN}-monitor = " \
	${sysconfdir}/${DPN}/gmond.conf \
	${sysconfdir}/init.d/ganglia-monitor \
	${bindir}/gmetric \
	${bindir}/gstat \
	${sbindir}/gmond"

FILES_gmetad = " \
	${sysconfdir}/${DPN}/gmetad.conf \
	${sysconfdir}/init.d/gmetad \
	${sbindir}/gmetad"

FILES_${PN}-monitor-python = " \
	${sysconfdir}/${DPN}/conf.d/* \
	${libdir}/${DPN}/python_modules/*"

RDEPENDS_gmetad += "${PN} start-stop-daemon"
RDEPENDS_${PN}-monitor += "${PN} start-stop-daemon"
RDEPENDS_${PN}-monitor-python += "${PN}-monitor"

PKG_${PN} = "libganglia1"
PKG_${PN}-dev = "libganglia1-dev"
