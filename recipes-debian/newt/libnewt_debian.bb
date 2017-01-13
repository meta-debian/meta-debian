#
# base recipe: meta/recipes-extended/newt/libnewt_0.52.18.bb
# base branch: master
#
SUMMARY = "A library for text mode user interfaces"

DESCRIPTION = "Newt is a programming library for color text mode, widget \
based user interfaces.  Newt can be used to add stacked windows, entry \
widgets, checkboxes, radio buttons, labels, plain text fields, scrollbars, \
etc., to text mode user interfaces.  This package also contains the \
shared library needed by programs built with newt, as well as a \
/usr/bin/dialog replacement called whiptail.  Newt is based on the \
slang library."

HOMEPAGE = "https://fedorahosted.org/newt/"

PR = "r3"
inherit debian-package
PV = "0.52.17"

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

# slang needs to be >= 2.2
DEPENDS = "slang popt tcl"

# remove_slang_include.patch:
#     Remove host include path /usr/include/slang
# fix_SHAREDDIR.patch:
#     Ensure the directory ${SHAREDDIR} exists to avoid parallel-make issue
# cross_ar.patch:
#     Replace host's ar by cross ar from sysroot
# disable_python_dbg.patch:
#     Currently, we don't provide package python*-dbg so disable support for python debug.
SRC_URI += " \
	file://remove_slang_include.patch \
	file://fix_SHAREDDIR.patch \
	file://cross_ar.patch \
	file://disable_python_dbg.patch \
"

DPN = "newt"

# base on debian/rules
EXTRA_OECONF = " \
	--with-gpm-support \
	--with-colorsfile=${sysconfdir}/newt/palette \
	CFLAGS="-I${STAGING_INCDIR}/tcl8.6 ${CFLAGS}" \
"
CACHED_CONFIGUREVARS_append_class-target = " ac_cv_c_tclconfig=${STAGING_BINDIR_CROSS}"
CACHED_CONFIGUREVARS_append_class-native = " ac_cv_c_tclconfig=${STAGING_LIBDIR}/tcl8.6"

inherit autotools-brokensep pythonnative

# Prevent Makefile searching for python from host system
EXTRA_OEMAKE += "PYTHONVERS=${PYTHON_DIR}"

CLEANBROKEN = "1"

export STAGING_INCDIR
export STAGING_LIBDIR

export BUILD_SYS
export HOST_SYS

do_configure_prepend() {
	sh autogen.sh
}

do_compile_prepend() {
	# Make sure the recompile is OK
	rm -f ${B}/.depend
}

do_install_append() {
	install -d ${D}${libdir}/whiptcl \
	           ${D}${sysconfdir}/newt \
	           ${D}${sysconfdir}/bash_completion.d
	install -m 0644 ${S}/newt*.ver ${D}${libdir}/libnewt_pic.map
	install -m 0644 ${S}/debian/palette.original ${D}${sysconfdir}/newt/
	install -m 0644 ${S}/debian/bash_completion.d/* ${D}${sysconfdir}/bash_completion.d/
	mv ${D}${libdir}/whiptcl.so ${D}${libdir}/whiptcl/
}

PACKAGE_BEFORE_PN = "python-${DPN} ${DPN}-tcl ${PN}-pic whiptail"

FILES_python-${DPN} = "${libdir}/${PYTHON_DIR}/*-packages/*"
FILES_${DPN}-tcl = "${libdir}/whiptcl/whiptcl.so"
FILES_${PN}-pic = "${libdir}/libnewt_pic.map"
FILES_whiptail = " \
    ${bindir}/whiptail \
    ${sysconfdir}/bash_completion.d/whiptail \
"
FILES_${PN}-dbg += " \
    ${libdir}/*/.debug \
    ${libdir}/${PYTHON_DIR}/*-packages/.debug \
"

RDEPENDS_${PN}-pic += "${PN}-dev"
RDEPENDS_${DPN}-tcl += "${PN}"
RDEPENDS_python-${DPN} += "${PN}"

# According debian/control, libnewt-pic depends on libnewt-dev.
# Skip 'QA Issue: libnewt-pic rdepends on libnewt-dev [dev-deps]'.
INSANE_SKIP_${PN}-pic += "dev-deps"

BBCLASSEXTEND = "native"
