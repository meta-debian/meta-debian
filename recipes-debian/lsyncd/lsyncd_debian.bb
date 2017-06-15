SUMMARY = "daemon to synchronize local directories using rsync"
DESCRIPTION = " Lsyncd (Live syncing mirror daemon) uses rsync to synchronize local \
 directories with a remote machine running rsyncd. Lsyncd watches \
 multiple directories trees through inotify. The first step after \
 adding the watches is to rsync all directories with the remote host, \
 and then sync single file by collecting the inotify events. So lsyncd \
 is a light-weight live mirror solution that should be easy to install \
 and use while blending \
 well with your system."
HOMEPAGE = "https://github.com/axkibe/lsyncd"

DISTRO_CODENAME = "jessie"
inherit debian-package
PV = "2.1.5"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=335b31c435c9c061dfffc6fff1f52e89"

inherit autotools-brokensep pkgconfig

CACHED_CONFIGUREVARS += "ac_cv_path_LUA51=${STAGING_BINDIR_NATIVE}/lua5.1"
DEPENDS += "lua5.1 lua5.1-native"

do_install_append() {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/lsyncd.init \
		${D}${sysconfdir}/init.d/lsyncd
}

RDEPENDS_${PN} += "rsync lua5.1"
