#
# base recipe: meta-selinux/recipes-security/selinux/libsemanage_2.4.bb
# base branch: jethro
#

SUMMARY = "SELinux policy management library"
DESCRIPTION = " Security-enhanced Linux is a patch of the Linux kernel and a \
number of utilities with enhanced security functionality designed to \
add mandatory access controls to Linux.  The Security-enhanced Linux \
kernel contains new architectural components originally developed to \
improve the security of the Flask operating system. These \
architectural components provide general support for the enforcement \
of many kinds of mandatory access control policies, including those \
based on the concepts of Type Enforcement, Role-based Access \
Control, and Multi-level Security."
HOMEPAGE = "http://userspace.selinuxproject.org/"

PR = "r1"

inherit debian-package
PV = "2.3"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343"

SRC_URI += " \
    file://libsemanage-allow-to-disable-audit-support.patch \
"

inherit lib_package pythonnative

DEPENDS += "ustr libsepol libselinux bison-native flex-native"
DEPENDS_append_class-target = " audit"

EXTRA_OEMAKE_class-native += "DISABLE_AUDIT=y"

do_compile() {
	oe_runmake all \
		INCLUDEDIR='${STAGING_INCDIR}' \
		LIBDIR='${STAGING_LIBDIR}'

	oe_runmake pywrap \
		INCLUDEDIR='${STAGING_INCDIR}' \
		LIBDIR='${STAGING_LIBDIR}' \
		PYLIBVER='python${PYTHON_BASEVERSION}' \
		PYINC='-I${STAGING_INCDIR}/$(PYLIBVER)' \
		PYLIB='-L${STAGING_LIBDIR}/$(PYLIBVER) -l$(PYLIBVER)' \
		PYTHONLIBDIR='${PYLIB}'
}

do_install() {
	oe_runmake install \
		DESTDIR="${D}" \
		PREFIX="${D}/${prefix}" \
		INCLUDEDIR="${D}/${includedir}" \
		LIBDIR="${D}/${libdir}" \
		SHLIBDIR="${D}/${libdir}"

	oe_runmake install-pywrap \
		DESTDIR=${D} \
		PYLIBVER='python${PYTHON_BASEVERSION}' \
		PYLIBDIR='${D}/${libdir}/$(PYLIBVER)'

	# Fix permission as debian/rules
	chmod -x ${D}${PYTHON_SITEPACKAGES_DIR}/semanage.py
}

do_install_append_class-target() {
	sed -i -e "s|${STAGING_DIR_HOST}||g" ${D}${libdir}/pkgconfig/*.pc
}

PACKAGES =+ "${PN}-common python-semanage"

FILES_${PN}-common = "${sysconfdir}"
FILES_python-semanage = "${PYTHON_SITEPACKAGES_DIR}/*"
FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/.debug"

RDEPENDS_${PN}_class-target += "${PN}-common"

DEBIANNAME_${PN}-dev = "${PN}1-dev"

BBCLASSEXTEND = "native"
