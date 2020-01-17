# base recipe: meta-security/recipes-security/suricata/suricata_4.1.3.bb
# base branch: warrior

SUMMARY = "The Suricata Engine is an Open Source Next Generation Intrusion Detection and Prevention Engine"
HOMEPAGE = "http://suricata-ids.org/"
SECTION = "security Monitor/Admin"
LICENSE = "GPLv2"

inherit debian-package
require recipes-debian/sources/suricata.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI += " \
	   file://volatiles.03_suricata \
	   file://suricata.yaml \
	   file://suricata.service \
	   file://run-ptest \
	   "

inherit autotools-brokensep pkgconfig python3-dir systemd ptest

CFLAGS += "-D_DEFAULT_SOURCE"

CACHED_CONFIGUREVARS = "ac_cv_header_htp_htp_h=yes ac_cv_lib_htp_htp_conn_create=yes \
			ac_cv_path_HAVE_WGET=no ac_cv_path_HAVE_CURL=no "

EXTRA_OECONF += " --disable-debug \
		--enable-non-bundled-htp \
		--disable-gccmarch-native \
		--disable-suricata-update \
		"

PACKAGECONFIG ??= "htp jansson file pcre yaml pcap cap-ng net nfnetlink nss nspr"
PACKAGECONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'ptest', 'unittests', '', d)}"

PACKAGECONFIG[htp] = "--with-libhtp-includes=${STAGING_INCDIR} --with-libhtp-libraries=${STAGING_LIBDIR}, ,libhtp,"
PACKAGECONFIG[pcre] = "--with-libpcre-includes=${STAGING_INCDIR} --with-libpcre-libraries=${STAGING_LIBDIR}, ,libpcre ," 
PACKAGECONFIG[yaml] = "--with-libyaml-includes=${STAGING_INCDIR} --with-libyaml-libraries=${STAGING_LIBDIR}, ,libyaml ,"
PACKAGECONFIG[pcap] = "--with-libpcap-includes=${STAGING_INCDIR} --with-libpcap-libraries=${STAGING_LIBDIR}, ,libpcap ," 
PACKAGECONFIG[cap-ng] = "--with-libcap_ng-includes=${STAGING_INCDIR} --with-libcap_ng-libraries=${STAGING_LIBDIR}, ,libcap-ng , "
PACKAGECONFIG[net] = "--with-libnet-includes=${STAGING_INCDIR} --with-libnet-libraries=${STAGING_LIBDIR}, , libnet," 
PACKAGECONFIG[nfnetlink] = "--with-libnfnetlink-includes=${STAGING_INCDIR} --with-libnfnetlink-libraries=${STAGING_LIBDIR}, ,libnfnetlink ,"
PACKAGECONFIG[nfq] = "--enable-nfqueue, --disable-nfqueue,libnetfilter-queue,"

PACKAGECONFIG[jansson] = "--with-libjansson-includes=${STAGING_INCDIR} --with-libjansson-libraries=${STAGING_LIBDIR},,jansson"
PACKAGECONFIG[file] = ",,file"
PACKAGECONFIG[nss] = "--with-libnss-includes=${STAGING_INCDIR} --with-libnss-libraries=${STAGING_LIBDIR},, nss," 
PACKAGECONFIG[nspr] = "--with-libnspr-includes=${STAGING_INCDIR} --with-libnspr-libraries=${STAGING_LIBDIR},, nspr," 
PACKAGECONFIG[python] = "--enable-python, --disable-python, python3" 
PACKAGECONFIG[unittests] = "--enable-unittests, --disable-unittests," 

do_install_append () {
	install -d ${D}${sysconfdir}/suricata

	oe_runmake install-conf DESTDIR=${D}

	install -d ${D}${sysconfdir}/suricata/rules
	install -m 0644 ${S}/rules/*.rules ${D}${sysconfdir}/suricata/rules

	oe_runmake install-rules DESTDIR=${D}

	install -d ${D}${sysconfdir}/suricata ${D}${sysconfdir}/default/volatiles
	install -m 0644 ${WORKDIR}/volatiles.03_suricata  ${D}${sysconfdir}/default/volatiles/volatiles.03_suricata

	install -m 0644 ${S}/threshold.config ${D}${sysconfdir}/suricata

	install -d ${D}${systemd_unitdir}/system
	sed  -e s:/etc:${sysconfdir}:g \
	     -e s:/var/run:/run:g \
	     -e s:/var:${localstatedir}:g \
	     -e s:/usr/bin:${bindir}:g \
	     -e s:/bin/kill:${base_bindir}/kill:g \
	     -e s:/usr/lib:${libdir}:g \
	     ${WORKDIR}/suricata.service > ${D}${systemd_system_unitdir}/suricata.service

	# Remove /var/run as it is created on startup
	rm -rf ${D}${localstatedir}/run

}

pkg_postinst_ontarget_${PN} () {
	if [ -e ${sysconfdir}/init.d/populate-volatile.sh ] ; then
		${sysconfdir}/init.d/populate-volatile.sh update
	fi
}

SYSTEMD_PACKAGES = "${PN}"

PACKAGES =+ "${PN}-socketcontrol"
FILES_${PN} += "${systemd_unitdir}"
FILES_${PN}-socketcontrol = "${bindir}/suricatasc ${PYTHON_SITEPACKAGES_DIR}"

CONFFILES_${PN} = "${sysconfdir}/suricata/suricata.yaml"

RDEPENDS_${PN} = "lsb-base"
RDEPENDS_${PN}-python = "python"
