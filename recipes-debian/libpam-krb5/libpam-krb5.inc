SUMMARY = "PAM module for MIT Kerberos"
DESCRIPTION = "\
A Kerberos PAM module build against the MIT Kerberos libraries.  It \
supports authenticating against a Kerberos v5 KDC, obtaining tickets and \
populating an initial ticket cache, authorizing users via a ~/.k5login \
file, and changing Kerberos passwords. \
"
HOMEPAGE = "http://www.eyrie.org/~eagle/software/pam-krb5/"
PR = "r0"
inherit debian-package
PV = "4.6"

LICENSE = "BSD & FSF-Unlimited & GPLv2+"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=189b47c200abb728c50aa74cc6e5b0b8 \
	file://Makefile.in;beginline=1;endline=11;md5=c9cfb1cde6b401324181bf6c69886021 \
	file://build-aux/compile;beginline=6;endline=29;md5=631bad8734d56ad4dcf55723d73fc323 \
	"
inherit autotools-brokensep

#follow debian/rules
EXTRA_OECONF += "--enable-reduced-depends --libdir=${base_libdir}"
DEPENDS += "libpam"
do_install_append() {
	install -d ${D}${datadir}/pam-configs
	install -m 0644 ${S}/debian/pam-auth-update \
		${D}${datadir}/pam-configs/krb5
	rm ${D}${base_libdir}/security/*.la
}
FILES_${PN} += "${base_libdir} ${datadir}/pam-configs"
FILES_${PN}-dbg += "${base_libdir}/security/.debug"

RDEPENDS_${PN} += "libpam-runtime libpam0g"
