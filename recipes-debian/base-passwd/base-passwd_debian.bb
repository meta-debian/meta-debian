#
# base recipe: meta/recipes-core/base-passwd/base-passwd_3.5.29.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "3.5.37"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

# Patch nobash.patch, noshadow.patch and disable-docs.patch
# can not apply, so nobash_edited.patch, noshadow_edited.patch
# and disable-docs_edited.patch were created with same purpose.
#
# Patch remove_cdebconf.patch remove all related part of libdebconf
# in base-passwd because libdebconf was not supported and the function
# related to libdebconf is not important.
SRC_URI += "\
	file://add_shutdown.patch\
	file://nobash_edited.patch\
	file://noshadow_edited.patch\
	file://input.patch\
	file://disable-docs_edited.patch\
	file://remove_cdebconf.patch\
"

inherit autotools

SSTATEPOSTINSTFUNCS += "base_passwd_sstate_postinst"

ROOT_HOME = "/root"

do_install () {
	install -d -m 755 ${D}${sbindir}
	install -o root -g root -p -m 755 ${B}/update-passwd ${D}${sbindir}/
	install -d -m 755 ${D}${mandir}/man8 ${D}${mandir}/pl/man8
	install -p -m 644 ${S}/man/update-passwd.8 ${D}${mandir}/man8/
	install -p -m 644 ${S}/man/update-passwd.pl.8 \
		${D}${mandir}/pl/man8/update-passwd.8
	gzip -9 ${D}${mandir}/man8/* ${D}${mandir}/pl/man8/*
	install -d -m 755 ${D}${datadir}/base-passwd
	install -o root -g root -p -m 644 ${S}/passwd.master ${D}${datadir}/base-passwd/
	sed -i 's#:/root:#:${ROOT_HOME}:#' ${D}${datadir}/base-passwd/passwd.master
	install -o root -g root -p -m 644 ${S}/group.master ${D}${datadir}/base-passwd/

	install -d -m 755 ${D}${docdir}/${DPN}
	install -p -m 644 ${S}/debian/changelog ${D}${docdir}/${DPN}/
	gzip -9 ${D}${docdir}/${DPN}/*
	install -p -m 644 ${S}/README ${D}${docdir}/${DPN}/
	install -p -m 644 ${S}/debian/copyright ${D}${docdir}/${DPN}/
}

base_passwd_sstate_postinst() {
	if [ "${BB_CURRENTTASK}" = "populate_sysroot" -o "${BB_CURRENTTASK}" = "populate_sysroot_setscene" ]
	then
		# Staging does not copy ${sysconfdir} files into the
		# target sysroot, so we need to do so manually. We
		# put these files in the target sysroot so they can
		# be used by recipes which use custom user/group
		# permissions.
		install -d -m 755 ${STAGING_DIR_TARGET}${sysconfdir}
		install -p -m 644 ${STAGING_DIR_TARGET}${datadir}/base-passwd/passwd.master \
					${STAGING_DIR_TARGET}${sysconfdir}/passwd
		install -p -m 644 ${STAGING_DIR_TARGET}${datadir}/base-passwd/group.master \
					${STAGING_DIR_TARGET}${sysconfdir}/group
	fi
}

python populate_packages_prepend() {
    # Add in the preinst function for ${PN}
    # We have to do this here as prior to this, passwd/group.master
    # would be unavailable. We need to create these files at preinst
    # time before the files from the package may be available, hence
    # storing the data from the files in the preinst directly.

    f = open(d.expand("${STAGING_DATADIR}/base-passwd/passwd.master"), 'r')
    passwd = "".join(f.readlines())
    f.close()
    f = open(d.expand("${STAGING_DATADIR}/base-passwd/group.master"), 'r')
    group = "".join(f.readlines())
    f.close()

    preinst = """#!/bin/sh
mkdir -p $D${sysconfdir}
if [ ! -e $D${sysconfdir}/passwd ]; then
\tcat << EOF > $D${sysconfdir}/passwd
""" + passwd + """EOF
fi
if [ ! -e $D${sysconfdir}/group ]; then
\tcat << EOF > $D${sysconfdir}/group
""" + group + """EOF
fi
"""
    d.setVar(d.expand('pkg_preinst_${PN}'), preinst)
}

addtask do_package after do_populate_sysroot

pkg_postinst_${PN} () {
#!/bin/sh
if [ -n "$D" ]; then
	exit 0
fi
${sbindir}/update-passwd
}
