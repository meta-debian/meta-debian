SUMMARY = "Cluster Resource Agents"
DESCRIPTION = "The Cluster Resource Agents are a set of scripts to interface with \
 several services to operate in a High Availability environment \
 for both Pacemaker and rgmanager resource managers."
HOMEPAGE = "https://github.com/ClusterLabs/resource-agents"

PR = "r0"
inherit debian-package
PV = "3.9.3+git20121009"

LICENSE = "GPLv2+ & GPLv3 & LGPLv2+"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
    file://heartbeat/ocf-shellfuncs.in;beginline=6;endline=22;md5=59f0463ffb03c4e5a331f0a03106e440"

inherit autotools-brokensep pkgconfig
DEPENDS += "cluster-glue glib-2.0 libnet libxslt"

# Disable-build-doc_debian.patch:
# 	Don't build doc to reduce dependency,this depends on docbook-xml and docbook-xsl"
# set-OCF_ROOT_DIR-to-libdir-ocf.patch:
#	correct OCF_ROOT_DIR is ${libdir}/ocf
SRC_URI += "file://Disable-build-doc_debian.patch \
            file://set-OCF_ROOT_DIR-to-libdir-ocf.patch"

EXTRA_OECONF += "--with-ocf-root=${libdir}/ocf \
                 --with-initdir=${sysconfdir}/init.d \
                 --disable-fatal-warnings \
                "
do_install_append() {
	# follow debian/rules
	rm ${D}${datadir}/cluster/drbd.metadata \
	   ${D}${datadir}/cluster/drbd.sh \
	   ${D}${sbindir}/rhev-check.sh

	install -D -m 0644 ${S}/debian/ldirectord.default \
		${D}${sysconfdir}/default/ldirectord
	
	# install the Asterisk PBX ocf resource agent
	cp -ax ${S}/debian/patches/ocf-asterisk \
		${D}${libdir}/ocf/resource.d/heartbeat/asterisk

	ln -sf ../../../..${datadir}/cluster \
		${D}${libdir}/ocf/resource.d/redhat
	ln -sf ../../..${sbindir}/ldirectord \
		${D}${sysconfdir}/ha.d/resource.d/ldirectord
}
PACKAGES =+ "ldirectord"
FILES_ldirectord = "\
	${sysconfdir}/default/ldirectord \
	${sysconfdir}/ha.d/resource.d/ldirectord \
	${sysconfdir}/init.d/ldirectord \
	${sysconfdir}/logrotate.d/ldirectord \
	${sbindir}/ldirectord \
"
FILES_${PN} += "${libdir} ${datadir} /run"
FILES_${PN}-dbg += "\
	${libdir}/ocf/resource.d/heartbeat/.debug \
	${libdir}/heartbeat/.debug \	
"
# follow debian/control
RDEPENDS_${PN} += "cluster-glue python"
RDEPENDS_ldirectord += "ipvsadm libmailtools-perl libsocket6-perl libwww-perl perl"
RRECOMMENDS_ldirectord += "logrotate"

# avoid a parallel build problem
PARALLEL_MAKE = ""
