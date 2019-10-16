SUMMARY = "Fully featured PAM module for using PKCS#11 smart cards"
DESCRIPTION = "This Linux-PAM login module allows a X.509 certificate based user login."
HOMEPAGE = "https://github.com/OpenSC/pam_pkcs11"
LICENSE = "LGPL-2.1+"

DEBIAN_UNPACK_DIR = "${WORKDIR}/pam_pkcs11-${PV}"

LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34 \
                    file://src/pam_pkcs11/pam_pkcs11.c;beginline=1;endline=17;md5=c072526b15fdd965a8696dbda85c66ee"

inherit debian-package
require recipes-debian/sources/pam-pkcs11.inc

DEPENDS = "openssl pcsc-lite libpam curl"

inherit autotools gettext pkgconfig

EXTRA_OECONF += "--libdir=${base_libdir} --with-curl --with-ldap=false"

FILES_${PN} += "${base_libdir}"

RDEPENDS_${PN} += "bash"
