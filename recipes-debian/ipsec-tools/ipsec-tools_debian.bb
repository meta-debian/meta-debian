# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree/meta-networking/recipes-support/ipsec-tools/ipsec-tools_0.8.1.bb?h=dora
# base branch: dora

DESCRIPTION = "IPsec-Tools is a port of KAME's IPsec utilities to the \
Linux-2.6 IPsec implementation."
HOMEPAGE = "http://ipsec-tools.sourceforge.net/"
SECTION = "console/network"

PR = "r0"
inherit debian-package
PV = "0.8.2+20140711"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://src/libipsec/pfkey.c;beginline=6;endline=31;md5=bc9b7ff40beff19fe6bc6aef26bd2b24"

DEPENDS = "virtual/kernel openssl readline flex bison-native libldap krb5 libpam"

# fix bug undefined reference to 'yylex'
SRC_URI += "file://0001-Don-t-link-against-libfl.patch"

inherit autotools

# Configure follow debian/rules
EXTRA_OECONF = " \
	--verbose \
	--prefix=/usr \
	--sysconfdir=/etc/racoon \
	--libdir=/usr/lib/ipsec-tools \
	--localstatedir=/var/run \
	--enable-shared \
	--disable-static \
	--enable-frag \
	--enable-hybrid \
	--with-libldap=${STAGING_LIBDIR}/.. \
	--enable-gssapi \
	--enable-dpd \
	--enable-adminport \
	--with-kernel-headers=${STAGING_INCDIR} \
	--with-libpam \
	--without-readline \
	--disable-security-context \
	--enable-natt \
    "

# install file follow Debian's package
do_install_append() {
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/racoon
	install -d ${D}${base_libdir}/systemd/system
	install -m 755 ${S}/debian/ipsec-tools.setkey.default ${D}${sysconfdir}/default/setkey
	install -m 755 ${S}/debian/ipsec-tools.setkey.init ${D}${sysconfdir}/init.d/setkey
	install -m 755 ${S}/debian/ipsec-tools.conf ${D}${sysconfdir}/
	rm -r ${D}${localstatedir}
	
	install -m 755 ${S}/debian/racoon.init ${D}${sysconfdir}/init.d/racoon
	install -m 755 ${S}/src/racoon/samples/psk.txt.sample ${D}${sysconfdir}/racoon/psk.txt
	install -m 755 ${S}/debian/racoon.conf ${D}${sysconfdir}/racoon/racoon.conf
	install -m 755 ${S}/debian/racoon-tool.conf ${D}${sysconfdir}/racoon/racoon-tool.conf
	install -m 755 ${S}/debian/racoon.service ${D}${base_libdir}/systemd/system/
	install -m 755 ${S}/debian/racoon-tool.pl ${D}${sbindir}/racoon-tool
	rm -rf ${D}${libdir}/ipsec-tools/*.so  
}

PACKAGES =+ "racoon "

# ship file to package ipsec-tools
FILES_${PN} += " \
	${sysconfdir}/default/setkey \
	${sysconfdir}/init.d/setkey \
	${sysconfdir}/ipsec-tools.conf \
	${sbindir}/setkey \
	${libdir}/ipsec-tools/libipsec.so.0* \
    "

# ship file to package racoon
FILES_racoon = " \
	${sysconfdir}/init.d/racoon \
	${sysconfdir}/racoon/* \
	${sbindir}/plainrsa-gen \
	${sbindir}/racoonctl \
	${sbindir}/racoon-tool \
	${sbindir}/racoon \
	${base_libdir}/systemd/system/* \
	${includedir}/ \
	${libdir}/${PN}/libracoon.so* \	
    "

FILES_${PN}-dbg += "${libdir}/${PN}/.debug"
