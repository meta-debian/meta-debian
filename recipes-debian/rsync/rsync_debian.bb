#
# base recipe: /meta/recipes-devtools/rsync/rsync_3.1.1.bb
# base branch: master
# base commit: 933aaf7d3c3e5a6825274512ff2f607e2a947db2
#

SUMMARY = "File synchronization tool"
DESCRIPTION = "rsync is a fast and versatile file-copying tool which can \
copy locally and to/from a remote host. It offers many options to control \
its behavior, and its remote-update protocol can minimize network traffic to \
make transferring updates between machines fast and efficient"
HOMEPAGE = "http://rsync.samba.org/"
BUGTRACKER = "http://rsync.samba.org/bugzilla.html"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PR = "r0"
inherit debian-package
PV = "3.1.1"

DEPENDS = "popt"

#acinclude.m4:
#	Need to run autoconf
SRC_URI +=  "file://acinclude.m4"

do_debian_patch_prepend () {
	# debian/patches/series does not exist,
	# so we need generate it before apply patch.
	olddir=$(pwd)
	cd ${DEBIAN_QUILT_PATCHES}
	for i in $(ls *.diff *.patch); do
		echo $i >> ${DEBIAN_QUILT_PATCHES}/series
	done
	cd $olddir
}

inherit autotools-brokensep

PACKAGECONFIG ??= "acl attr"
PACKAGECONFIG[acl] = "--enable-acl-support,--disable-acl-support,acl,"
PACKAGECONFIG[attr] = "--enable-xattr-support,--disable-xattr-support,attr,"

# rsync 3.0 uses configure.sh instead of configure, and
# makefile checks the existence of configure.sh
do_configure_prepend () {
	rm -f ${S}/configure ${S}/configure.sh
	cp -f ${WORKDIR}/acinclude.m4 ${S}/

	# By default, if crosscompiling, rsync disables a number of
	# capabilities, hardlinking symlinks and special files (i.e. devices)
	export rsync_cv_can_hardlink_special=yes
	export rsync_cv_can_hardlink_symlink=yes
}

do_configure_append () {
	cp -f ${S}/configure ${S}/configure.sh
}

do_install_append() {
	#Create /etc/default and /etc/init.d lib/systemd/system folders
	install -d ${D}${sysconfdir}
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${base_libdir}
	install -d ${D}${base_libdir}/systemd
	install -d ${D}${base_libdir}/systemd/system
	
	#Install /etc/default/rsync	
	install -m 0644 ${S}/debian/default ${D}${sysconfdir}/default/rsync

	#Install /etc/init.d/rsync
	install -m 0755 ${S}/debian/init.d ${D}${sysconfdir}/init.d/rsync

	#Install /lib/systemd/system/rsync.service
	install -m 0644 ${S}/packaging/systemd/rsync.service \
			${D}${base_libdir}/systemd/system
}

FILES_${PN} += " ${base_libdir}/systemd/system/rsync.service"
