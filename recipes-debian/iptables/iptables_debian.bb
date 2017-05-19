#
# base recipe: meta/recipes-extended/iptables/iptables_1.4.21.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "1.4.21"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263\
	file://iptables/iptables.c;beginline=13;endline=25;md5=c5cffd09974558cf27d0f763df2a12dc \
"

RRECOMMENDS_${PN} = " \
	kernel-module-x-tables \
	kernel-module-ip-tables \
	kernel-module-iptable-filter \
	kernel-module-iptable-nat \
	kernel-module-nf-defrag-ipv4 \
	kernel-module-nf-conntrack \
	kernel-module-nf-conntrack-ipv4 \
	kernel-module-nf-nat \
	kernel-module-ipt-masquerade \
"

inherit autotools pkgconfig

EXTRA_OECONF = "--with-kernel=${STAGING_INCDIR}"

# Configure follow Debian
EXTRA_OECONF += " \
	--enable-libipq \
	--enable-devel \
	--libdir=${base_libdir} \
	--with-xtlibdir=${base_libdir}/xtables \
	--with-pkgconfigdir=${libdir}/pkgconfig \
	--sbindir=${base_sbindir} \
"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ipv6', '', d)}"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

do_install_append(){
	# Correct installation follow Debian
	install -d ${D}${sbindir}
	( cd ${D}${base_sbindir} &&
	  mv $(ls *apply nf*) ${D}${sbindir}/ )
}

PACKAGES =+ "libxtables"

FILES_libxtables = "${base_libdir}/libxtables${SOLIBS}"
FILES_${PN} += "${base_libdir}/xtables/*"
FILES_${PN}-dbg += "${base_libdir}/xtables/.debug"

DEBIANNAME_libxtables = "libxtables10"
