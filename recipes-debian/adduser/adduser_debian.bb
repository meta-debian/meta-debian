SUMMARY = "add and remove users and groups"
DESCRIPTION = "This package includes the 'adduser' and 'deluser' commands for \
creating and removing users. \
- 'adduser' creates new users and groups and adds existing users to \
  existing groups; \
- 'deluser' removes users and groups and removes users from a given \
  group. \
"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://adduser;beginline=6;endline=25;md5=7d76e2c905de138038725c2b5cfd5d9e"

inherit debian-package
require recipes-debian/sources/adduser.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/adduser"

SRC_URI += "file://adduser-add-M-option-for-useradd.patch"

inherit cpan-base update-alternatives

do_install() {
	install -d ${D}${sbindir} ${D}${sysconfdir} \
	           ${D}${datadir}/${BPN} ${D}${docdir}/${BPN} ${D}${mandir} \
	           ${D}${libdir}/perl5/${@get_perl_version(d)}/Debian/
	sed -e "s/VERSION/${PV}/g" ${S}/adduser > ${D}${sbindir}/adduser
	sed -e "s/VERSION/${PV}/g" ${S}/deluser > ${D}${sbindir}/deluser
	sed -e "s/VERSION/${PV}/g" ${S}/AdduserCommon.pm \
	    > ${D}${libdir}/perl5/${@get_perl_version(d)}/Debian/AdduserCommon.pm

	chmod 0755 ${D}${sbindir}/*

	install -m 0644 ${S}/*.conf ${D}${sysconfdir}/
	install -m 0644 ${S}/adduser.conf ${D}${datadir}/${BPN}/

	${S}/debian/scripts/install-manpages.pl ${PV} ${S}/doc/ ${D}${mandir}
	cp -fr ${S}/examples ${D}${docdir}/${BPN}/
}

RDEPENDS_${PN} = " \
    shadow \
    perl-module-getopt-long \
    perl-module-overloading \
    perl-module-file-find \
    perl-module-file-temp \
"

ALTERNATIVE_${PN} = "adduser deluser addgroup delgroup"
ALTERNATIVE_PRIORITY = "60"
ALTERNATIVE_LINK_NAME[adduser] = "${sbindir}/adduser"
ALTERNATIVE_LINK_NAME[deluser] = "${sbindir}/deluser"
ALTERNATIVE_LINK_NAME[addgroup] = "${sbindir}/addgroup"
ALTERNATIVE_LINK_NAME[delgroup] = "${sbindir}/delgroup"
ALTERNATIVE_TARGET[addgroup] = "${sbindir}/adduser.${BPN}"
ALTERNATIVE_TARGET[delgroup] = "${sbindir}/deluser.${BPN}"
