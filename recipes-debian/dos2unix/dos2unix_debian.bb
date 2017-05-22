SUMMARY = "Convert text file line endings between CRLF and LF"
HOMEPAGE = "http://freshmeat.net/projects/dos2unix"
PR = "r0"

inherit debian-package
PV = "6.0.4"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=37ef5b0498ecd2c4c7f2e2be47a75d5e"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

# Follow debian/rules
EXTRA_OEMAKE = ' \
    LDFLAGS_USER="${LDFLAGS}" \
    CFLAGS_USER="${CPPFLAGS} ${CFLAGS}" \
'

do_install() {
	oe_runmake DESTDIR="${D}" install

	# Follow debian/rules
	# dos2unix-N.N/ => dos2unix/
	mv ${D}${docdir}/$(cd ${D}${docdir} && ls) ${D}${docdir}/${DPN}/

	# Remove files that are not needed.
	rm ${D}${docdir}/${DPN}/COPYING.txt \
		${D}${docdir}/${DPN}/INSTALL.txt \
		${D}${docdir}/${DPN}/ChangeLog.txt
}
