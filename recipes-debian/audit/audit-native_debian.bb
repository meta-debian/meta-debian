#
# base recipe http://git.yoctoproject.org/cgit/cgit.cgi/meta-selinux/tree/
# recipes-security/audit/audit_2.4.3.bb?h=master
# base branch: master
#

inherit debian-package
PV = "2.4"

PR = "r0"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
	file://lib/libaudit.h;beginline=1;endline=22;md5=26d304bf07003c3bbdabf21836cfd3c1"

BASE_SRC_URI = "\
        file://audit-python.patch \
        file://audit-python-configure.patch \
        file://fix-swig-host-contamination.patch \
"
SRC_URI = " \
        ${DEBIAN_SRC_URI} \
	${BASE_SRC_URI} \
	file://cross-compile-native_debian.patch \
"

inherit autotools-brokensep native

DEPENDS = "python libldap libcap-ng"
PARALLEL_MAKE = ""
EXTRA_OECONF += " \
        --libdir=${base_libdir} \
        --sbindir=${base_sbindir} \
        "

EXTRA_OECONF_append_arm = " --with-arm=yes"

EXTRA_OEMAKE += "PYLIBVER='python${PYTHON_BASEVERSION}' \
	PYINC='${STAGING_INCDIR}/$(PYLIBVER)' \
	pyexecdir=${libdir}/python${PYTHON_BASEVERSION}/dist-packages \
	STDINC='${STAGING_INCDIR}' \
	"
do_compile() {
	oe_runmake -C ${S}/lib all
	oe_runmake -C ${S}/auparse all
}
do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/lib/*_h ${D}${bindir}
	install -m 0755 ${S}/auparse/*_h ${D}${bindir}
}
