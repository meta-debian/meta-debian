SUMMARY = "Fuse implementation of unionfs"
DESCRIPTION = "\
This is another unionfs implementation using filesystem in userspace (fuse)"

PR = "r0"
inherit debian-package
PV = "5.2.0.dfsg.1"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI += "file://dont-depend-on-help2man.patch"
inherit autotools-brokensep gettext

EXTRA_OECONF += "--with-external-Text-Unidecode=yes \
	--with-external-libintl-perl=yes \
	"
do_configure() {
	sed -i -e "s:\${confdir}/configure:LDFLAGS=\"${BUILD_LDFLAGS}\" \${confdir}/configure:g" ${S}/configure
	oe_runconf
}
do_compile() {
	oe_runmake am__append_7='install-info po po_document tp Pod-Simple-Texinfo util'
}

do_install () {
	oe_runmake install am__append_7='install-info po po_document tp Pod-Simple-Texinfo util' DESTDIR=${D}
	oe_runmake -C ${S}/po install-data-yes DESTDIR=${D}
	oe_runmake -C ${S}/po_document install-data-yes DESTDIR=${D}
}

do_install_append() {
	install -D debian/conf/50cyrtexinfo.cnf ${D}/${sysconfdir}/texmf/fmt.d/50cyrtexinfo.cnf
	ln -sf install-info ${D}${bindir}/ginstall-info
	install -m 0755 ${S}/util/txixml2texi ${D}/${bindir}/txixml2texi
	install -D ${S}/debian/info.mime ${D}/${libdir}/mime/packages/info
	install -D -m 0755 ${S}/debian/update-info-dir ${D}/${sbindir}/update-info-dir
	install -D ${S}/debian/info.menu ${D}/${datadir}/menu/info
	mkdir -p ${D}/${datadir}/texmf/tex/texinfo
	cp ${S}/doc/*.tex ${D}/${datadir}/texmf/tex/texinfo
	mkdir -p ${D}/${localstatedir}/lib/tex-common/fmtutil-cnf
	echo "50cyrtexinfo" > ${D}/${localstatedir}/lib/tex-common/fmtutil-cnf/texinfo.list
}

PACKAGES =+ "info install-info"

FILES_install-info += "${sbindir} ${bindir}/install-info ${bindir}/ginstall-info"

FILES_info += "${bindir}/info ${bindir}/infokey ${libdir}/mime ${datadir}/menu"

FILES_${PN} += "${datadir}/texmf ${datadir}/texinfo ${datadir}/doc ${datadir}/locale"
