# base recipe: meta-security/recipes-security/google-authenticator-libpam/google-authenticator-libpam_1.05.bb 
# base branch: warrior

SUMMARY = "Google Authenticator PAM module"
HOME_PAGE = "https://github.com/google/google-authenticator-libpam"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
LICENSE = "Apache-2.0"

inherit debian-package
require recipes-debian/sources/google-authenticator.inc

DEPENDS = "libpam"

DEBIAN_UNPACK_DIR = "${WORKDIR}/google-authenticator"
S = "${DEBIAN_UNPACK_DIR}/libpam"

inherit autotools distro_features_check

EXTRA_OECONF += "--libdir=${base_libdir}"

REQUIRED_DISTRO_FEATURES = "pam"

PACKAGES =+ "libpam-${PN}"
FILES_libpam-${PN} = "${base_libdir}/security/pam_google_authenticator.so \
		      ${bindir}/*"

RDEPENDS_libpam-${PN}  = "libpam"
