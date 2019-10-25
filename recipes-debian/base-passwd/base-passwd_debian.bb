#
# base recipe: meta/recipes-core/base-passwd/base-passwd_3.5.29.bb
# base branch: warrior
#

SUMMARY = "Debian base system master password and group files"
DESCRIPTION = "These are the canonical master copies of the user database files \
(/etc/passwd and /etc/group), containing the Debian-allocated user and \
group IDs. The update-passwd tool is provided to keep the system databases \
synchronized with these master files"

inherit debian-package
require recipes-debian/sources/base-passwd.inc

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

FILESPATH_append = ":${COREBASE}/meta/recipes-core/base-passwd/base-passwd"

# Patch editted base on meta-debian: morty
# Revise nobash.patch to nobash_edited.patch because the source change.
# Revise noshadow.patch to noshadow_edited.patch because it depends on nobash.patch
# Revise  disable-docs.patch to disable-docs_edited.patch because Makefile.in does not exist in source code.

# Patch remove_cdebconf.patch remove all related part of libdebconf
# in base-passwd because libdebconf was not supported and the function
# related to libdebconf is not important.

SRC_URI += "\
	file://add_shutdown.patch \
	file://nobash_edited.patch \
	file://noshadow_edited.patch \
	file://input.patch \
	file://disable-docs_edited.patch \
	file://remove_cdebconf_edited.patch \
"

DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}"
inherit autotools

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
	install -d -m 755 ${D}${docdir}/${BPN}
	install -p -m 644 ${S}/debian/changelog ${D}${docdir}/${BPN}/
	gzip -9 ${D}${docdir}/${BPN}/*
	install -p -m 644 ${S}/README ${D}${docdir}/${BPN}/
	install -p -m 644 ${S}/debian/copyright ${D}${docdir}/${BPN}/
}

basepasswd_sysroot_postinst() {
	#!/bin/sh
	# Install passwd.master and group.master to sysconfdir
	install -d -m 755 ${STAGING_DIR_TARGET}${sysconfdir}
	for i in passwd group; do
		install -p -m 644 ${STAGING_DIR_TARGET}${datadir}/base-passwd/\$i.master \
			${STAGING_DIR_TARGET}${sysconfdir}/\$i
	done

	# Run any useradd postinsts
	for script in ${STAGING_DIR_TARGET}${bindir}/postinst-useradd-*; do
		if [ -f \$script ]; then
			\$script
		fi
	done
}

SYSROOT_DIRS += "${sysconfdir}"
SYSROOT_PREPROCESS_FUNCS += "base_passwd_tweaksysroot"

base_passwd_tweaksysroot () {
	mkdir -p ${SYSROOT_DESTDIR}${bindir}
	dest=${SYSROOT_DESTDIR}${bindir}/postinst-${PN}
	echo "${basepasswd_sysroot_postinst}" > $dest
	chmod 0755 $dest
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
\tcat << 'EOF' > $D${sysconfdir}/passwd
""" + passwd + """EOF
fi
if [ ! -e $D${sysconfdir}/group ]; then
\tcat << 'EOF' > $D${sysconfdir}/group
""" + group + """EOF
fi
"""
    d.setVar(d.expand('pkg_preinst_${PN}'), preinst)
}

addtask do_package after do_populate_sysroot

ALLOW_EMPTY_${PN} = "1"

PACKAGES =+ "${PN}-update"
FILES_${PN}-update = "${sbindir}/* ${datadir}/${PN}"

pkg_postinst_${PN}-update () {
	#!/bin/sh
	if [ -n "$D" ]; then
		exit 0
	fi
	${sbindir}/update-passwd
}

