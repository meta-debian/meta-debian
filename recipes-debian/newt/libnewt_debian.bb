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

inherit debian-package

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5f30f0716dfdd0d91eb439ebec522ec2"

# slang needs to be >= 2.2
DEPENDS = "slang popt tcl"

SRC_URI += " \
	file://fix_SHAREDDIR.patch \
	file://cross_ar.patch \
"

DPN = "newt"

EXTRA_OECONF = " \
	--without-tcl \
	--with-gpm-support \
	--with-colorsfile=/etc/newt/palette \
	CFLAGS="-I${STAGING_INCDIR_NATIVE}/tcl8.6 $(CFLAGS)" \
	"

inherit autotools-brokensep python-dir

CLEANBROKEN = "1"

export STAGING_INCDIR
export STAGING_LIBDIR

export BUILD_SYS
export HOST_SYS

PACKAGES_prepend = "whiptail "

do_configure_prepend() {
    sh autogen.sh
}

do_compile_prepend() {
    # Make sure the recompile is OK
    rm -f ${B}/.depend
}

do_install_append() {
	install -D ${S}/whiptcl.so ${D}${libdir}/whiptcl/whiptcl.so
	install -D ${S}/snack.py ${D}${libdir}/${PYTHON_DIR}/dist-packages/snack.py
	install -D ${S}/_snack.so ${D}${libdir}/${PYTHON_DIR}/dist-packages/_snack.so
	install -D ${S}/_snack.so ${D}${libdir}/${PYTHON_DIR}/dist-packages/_snack_d.so
}

PACKAGES += "python-newt python-newt-dbg newt-tcl"

FILES_python-newt-dbg = "${libdir}/${PYTHON_DIR}/dist-packages/_snack_d.so"
FILES_python-newt = "${libdir}/${PYTHON_DIR}/dist-packages"
FILES_whiptail = "${bindir}/whiptail"
FILES_libnewt0.52 += "${datadir}/locale*"
FILES_newt-tcl = "${libdir}/whiptcl/whiptcl.so"

BBCLASSEXTEND = "native"
