SUMMARY = "Administrative utilities for the XFS filesystem"
DESCRIPTION = "\
	The xfsdump package contains xfsdump, xfsrestore and a number of    \
	other administrative utilities for managing XFS filesystems.        \
	xfsdump examines files in a filesystem, determines which need to be \
	backed up, and copies those files to a specified disk, tape or other\
	torage medium.  It uses XFS-specific directives for optimizing the  \
	ump of an XFS filesystem, and also knows how to backup XFS extended \
	ttributes.  Backups created with xfsdump are 'endian safe' and can  \
	hus be transfered between Linux machines of different architectures \
	nd also between IRIX machines \
"
HOMEPAGE = "http://oss.sgi.com/projects/xfs/"
PR = "r0"
inherit debian-package
PV = "3.1.4"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://doc/COPYING;md5=15c832894d10ddd00dfcf57bee490ecc"

#Remove install as use
SRC_URI += "file://remove-install-as-use_debian.patch"

inherit autotools-brokensep
DEBIAN_PATCH_TYPE = "nopatch"

DEPENDS += "xfsprogs attr gettext"

do_configure_prepend () {
	#correct the path to binaries files, to use the binaries files 
	#in sysroot instead of host system
	sed -i -e "s: \/usr\/bin: ${STAGING_BINDIR_NATIVE}:g" \
		${S}/m4/package_utilies.m4
}

#install follow Debian jessie
do_install_append() {
	#correct the softlinks
	rm ${D}${sbindir}/xfsdump ${D}${sbindir}/xfsrestore
	ln -s ../../${base_sbindir}/xfsdump ${D}${sbindir}/xfsdump
	ln -s ../../${base_sbindir}/xfsrestore ${D}${sbindir}/xfsrestore
}
FILES_${PN} += "${datadir}/locale/*"
