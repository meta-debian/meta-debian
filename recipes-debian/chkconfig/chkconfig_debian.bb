SUMMARY = "A system tool for maintaining the /etc/rc*.d hierarchy"
DESCRIPTION = "\
	Chkconfig is a basic system utility.  It updates and queries runlevel \
	information for system services.  Chkconfig manipulates the numerous \
	symbolic links in /etc/rc.d, to relieve system administrators of some \
	of the drudgery of manually editing the symbolic links."
HOMEPAGE = "http://fedorahosted.org/releases/c/h/chkconfig"

PR = "r2"
inherit debian-package
PV = "11.4.54.60.1debian1"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://t/libtap.sh;md5=6c36428024c005158a85a2881975599c"

# rcd-path.diff:
#     This patch is based on debian/patches/rcd-path.diff.
#     The patches in debian/patches are ignored because debian/source/format is "3.0 (native)"
#     However, we need this patch to correct path to "rc*.d" directory
SRC_URI += "file://rcd-path.diff"

#install follow Debian jessie
do_install() {
	install -d ${D}${base_sbindir}
	install -m 0755 ${S}/chkconfig ${D}${base_sbindir}/
	
}

FILES_${PN} += "${base_sbindir}/*"

# chkconfig requires insserv to configure service
RRECOMMENDS_${PN} += "insserv"
