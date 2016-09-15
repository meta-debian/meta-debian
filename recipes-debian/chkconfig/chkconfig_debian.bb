SUMMARY = "A system tool for maintaining the /etc/rc*.d hierarchy"
DESCRIPTION = "\
	Chkconfig is a basic system utility.  It updates and queries runlevel \
	information for system services.  Chkconfig manipulates the numerous \
	symbolic links in /etc/rc.d, to relieve system administrators of some \
	of the drudgery of manually editing the symbolic links."
HOMEPAGE = "http://fedorahosted.org/releases/c/h/chkconfig"

PR = "r1"
inherit debian-package

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://t/libtap.sh;md5=6c36428024c005158a85a2881975599c"

#install follow Debian jessie
do_install() {
	install -d ${D}${base_sbindir}
	install -m 0755 ${S}/chkconfig ${D}${base_sbindir}/
	
}

FILES_${PN} += "${base_sbindir}/*"

# chkconfig requires insserv to configure service
RRECOMMENDS_${PN} += "insserv"
