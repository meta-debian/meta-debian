PR = "r0"

inherit debian-package
PV = "1.27.1"

LICENSE = "GPLv2+ & GPLv3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://debian/copyright;md5=6cc9239a1f4b61c5ea9db23f92b2d20b \
"

PACKAGECONFIG[acl] = "--with-posix-acls, --without-posix-acls, acl,"

inherit autotools gettext update-alternatives

# Configure follow debian/rules
# --dissable-gcc-warnings to avoid all warnings being treated as errors.
EXTRA_OECONF += " \
	--bindir=${base_bindir} \
	--enable-backup-scripts \
	--with-lzma=xz \
	--disable-gcc-warnings \
"

# Follow debian/rules
do_install_append(){
	install -d ${D}${sysconfdir}
	install -m 755 ${S}/debian/rmt.sh ${D}${sysconfdir}/rmt
	mv ${D}${libexecdir}/rmt ${D}${sbindir}/rmt-tar
	install -m 755 ${S}/debian/tarcat ${D}${sbindir}/tarcat

	mv ${D}${sbindir}/backup ${D}${sbindir}/tar-backup
	mv ${D}${sbindir}/restore ${D}${sbindir}/tar-restore

	mkdir -p ${D}${libdir}/mime/packages
	install -m 0644 ${S}/debian/tar.mime ${D}${libdir}/mime/packages/tar
}

PACKAGES =+ "${PN}-scripts"

FILES_${PN}-scripts = " \
	${libexecdir}/backup.sh \
	${libexecdir}/dump-remind \
	${sbindir}/tar-backup \
	${sbindir}/tar-restore \
"
FILES_${PN} += "${libdir}/mime/packages"

# Add update-alternatives definitions
ALTERNATIVE_PRIORITY="100"
ALTERNATIVE_${PN} = "tar"
ALTERNATIVE_LINK_NAME[tar] = "${base_bindir}/tar"

BBCLASSEXTEND = "nativesdk"
