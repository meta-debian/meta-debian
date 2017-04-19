SUMMARY = "GNU troff text-formatting system"
DESCRIPTION = "\
This package contains optional components of the GNU troff text-formatting \
system. The core package, groff-base, contains the traditional tools like  \
troff, nroff, tbl, eqn, and pic. This package contains additional devices  \
and drivers for output to DVI, HTML (when recommended packages are         \
installed - see below), PDF, HP LaserJet printers, and Canon CAPSL LBP-4   \
and LBP-8 printers. \
The X75, X75-12, X100, and X100-12 devices, which allow groff output to be \
conveniently viewed on an X display using the standard X11 fonts, are now  \
included here. They were previously in a separate package, groff-x11       \
"
HOMEPAGE = "https://www.gnu.org/software/groff/"

PR = "r0"
inherit debian-package
PV = "1.22.2"

DEPENDS += "texinfo groff-native"
DEPENDS_class-native = ""

LICENSE = "GPLv3+ & GFDL-1.3 & MIT & BSD-4-Clause"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://FDL;md5=10b9de612d532fdeeb7fe8fcd1435cc6 \
	file://src/devices/xditview/README;md5=b5638dfaf6c432022c8a2fbca76eb41d \
	file://tmac/doc.tmac;beginline=1;endline=28;md5=6ea1a309490948cb2b294971734b30b1"

inherit autotools-brokensep

EXTRA_OECONF += "--with-appresdir=${sysconfdir}/X11/app-defaults"

PACKAGECONFIG_class-target ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[x11] = "--with-x, --without-x, libx11 libxmu libxt libxaw"

PACKAGES =+ "${PN}-base"

do_configure_append() {
    # generate gnulib configure script
    olddir=`pwd`
    cd ${S}/src/libs/gnulib/
    ACLOCAL="$ACLOCAL" autoreconf -Wcross --verbose --install --force \
	${EXTRA_AUTORECONF} $acpaths || die "autoreconf execution failed."
    cd ${olddir}
}

do_configure_prepend() {
        if [ "${BUILD_SYS}" != "${HOST_SYS}" ]; then
                sed -i \
                    -e '/^GROFFBIN=/s:=.*:=${STAGING_BINDIR_NATIVE}/groff:' \
                    -e '/^TROFFBIN=/s:=.*:=${STAGING_BINDIR_NATIVE}/troff:' \
                    -e '/^GROFF_BIN_PATH=/s:=.*:=${STAGING_BINDIR_NATIVE}:' \
                    -e '/^GROFF_BIN_DIR=/s:=.*:=${STAGING_BINDIR_NATIVE}:' \
                    ${S}/contrib/*/Makefile.sub \
                    ${S}/doc/Makefile.in \
                    ${S}/doc/Makefile.sub
        fi
}

do_install_append() {
	install -d ${D}${sysconfdir}/${DPN}
	install -d ${D}${libdir}/mime/packages

	cat ${S}/debian/mandoc.local >> ${D}${sysconfdir}/${DPN}/man.local
	cat ${S}/debian/mandoc.local >> ${D}${sysconfdir}/${DPN}/mdoc.local

	rm -r ${D}${datadir}/${DPN}/site-tmac
	ln -s ../../..${sysconfdir}/${DPN} ${D}${datadir}/${DPN}/site-tmac

	install -m 0644 ${S}/debian/groff-base.mime \
		${D}${libdir}/mime/packages/groff-base

	ln -s eqn ${D}${bindir}/geqn
	ln -s pic ${D}${bindir}/gpic
	ln -s tbl ${D}${bindir}/gtbl
}

#shipment files for groff-base package:
FILES_${PN}-base = " \
    ${sysconfdir}/groff \
    ${libdir}/mime/packages/groff-base \
    ${datadir}/groff/${PV}/eign \
    ${datadir}/groff/site-tmac \
    ${datadir}/groff/current \
"
bindir_progs = "\
	geqn gpic gtbl eqn groff grog grops grotty neqn nroff pic preconv \
	soelim tbl troff"
datadir_progs_tmac = "\
	an*.tmac composite.tmac cp1047.tmac cs.tmac de.tmac den.tmac devtag.tmac \
	doc*.tmac eqnrc europs.tmac fallbacks.tmac fr.tmac hyphen.cs hyphen.den \
	hyphen* ja.tmac latin*.tmac man.tmac mandoc.tmac mdoc.tmac mdoc \
	papersize.tmac pic.tmac ps.tmac psatk.tmac psold.tmac pspic.tmac \
	safer.tmac sv.tmac trans.tmac troffrc troffrc-end tty-char.tmac \
	tty.tmac unicode.tmac www.tmac"
datadir_progs_font = "devascii devlatin1 devps devutf8"
python __anonymous() {
    val = str(d.getVar('FILES_groff-base', True))
    bindir = d.getVar('bindir', True)
    datadir = d.getVar('datadir', True)

    for prog in d.getVar('bindir_progs', True).split():
        val += " " + bindir + "/" + prog
    for prog in d.getVar('datadir_progs_tmac',True).split():
        val += " " + datadir + "/groff/${PV}/tmac/" + prog
    for prog in d.getVar('datadir_progs_font',True).split():
        val += " " + datadir + "/groff/${PV}/font/" + prog

    d.setVar('FILES_groff-base', val)
}

PARALLEL_MAKE = ""
BBCLASSEXTEND = "native"
