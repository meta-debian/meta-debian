SUMMARY_telnetd = "The telnet server"
SUMMARY_telnet = "The telnet client"
DESCRIPTION_telnetd = "The in.telnetd program is a server which supports the DARPA telnet interactive \
communication protocol."
DESCRIPTION_telnet = "The telnet command is used for interactive communication with another host \
using the TELNET protocol."

inherit debian-package
PV = "0.17"

LICENSE = "BSD"
LIC_FILES_CHKSUM = " \
    file://telnetd/telnetd.h;endline=35;md5=3035c3809d14ee7f30535a630d9a12fd \
    file://telnetd/setproctitle.c;beginline=6;endline=40;md5=3a6dfb03c6fb3e7ebef1056d810527a5 \
    file://telnet/array.h;beginline=6;endline=33;md5=fd682374ef36a754753afa1d0498f572 \
"

# testcode's binaries cannot run if we build with cross compiler
# so don't run them
SRC_URI += "file://configure-disable-run-testcode.patch"

DEPENDS = "ncurses update-inetd-native"

DEBIAN_PATCH_TYPE = "nopatch"

inherit useradd

USERADD_PACKAGES = "telnetd"
USERADD_PARAM_telnetd = "--system --no-create-home --user-group \
                         --groups utmp --home-dir /nonexistent telnetd"

do_configure() {
	./configure
	sed -i -e 's/^CFLAGS=\(.*\)$/CFLAGS= -Ddebian -D_GNU_SOURCE -g \1/' \
	       -e 's/^CXXFLAGS=\(.*\)$/CXXFLAGS= -Ddebian -D_GNU_SOURCE -g \1/' \
	    MCONFIG
}

do_compile() {
	oe_runmake
}

do_install() {
	install -d ${D}${sbindir} ${D}${bindir} ${D}${libdir} \
	           ${D}${docdir}/telnet \
	           ${D}${mandir}/man1 ${D}${mandir}/man5 ${D}${mandir}/man8

	# Base on debian/rules
	oe_runmake -C telnet 'INSTALLROOT=${D}' 'MANDIR=${mandir}' install
	mv ${D}${bindir}/telnet ${D}${bindir}/telnet.netkit
	mv ${D}${mandir}/man1/telnet.1 \
	   ${D}${mandir}/man1/telnet.netkit.1
	cp ${S}/telnet/README ${D}${docdir}/telnet/README.telnet
	cp ${S}/telnet/README.old ${D}${docdir}/telnet/README.telnet.old

	oe_runmake -C telnetd 'INSTALLROOT=${D}' 'MANDIR=${mandir}' install
	cp ${S}/telnetlogin/telnetlogin.8 ${D}${mandir}/man8/
	cp ${S}/telnetlogin/telnetlogin ${D}${libdir}/

	install -d ${D}${datadir}/menu
	cp ${S}/debian/menu ${D}${datadir}/menu/telnet
}

PACKAGES =+ "telnet telnetd"
FILES_telnet = "${bindir}/telnet.netkit ${datadir}/menu/telnet"
FILES_telnetd = "${libdir}/telnetlogin ${sbindir}/in.telnetd"

inherit update-alternatives
ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_telnet = "telnet"
ALTERNATIVE_LINK_NAME[telnet] = "${bindir}/telnet"
ALTERNATIVE_TARGET[telnet] = "${bindir}/telnet.netkit"

# Base on debian/telnetd.postinst
pkg_postinst_telnetd() {
  chown root:telnetd $D${libdir}/telnetlogin
  chmod 4754 $D${libdir}/telnetlogin

  # Remove old entry telnet and add new entry for in.telnetd
  ent="telnet	stream	tcp	nowait	root	/usr/sbin/tcpd  /usr/sbin/in.telnetd"
  update-inetd --file $D${sysconfdir}/inetd.conf --remove ".*telnet"
  update-inetd --file $D${sysconfdir}/inetd.conf --group STANDARD --add "$ent"
}

RDEPENDS_telnet += "netbase"
RDEPENDS_telnetd += "openbsd-inetd"
RPROVIDES_telnet += "telnet-client"
RPROVIDES_telnetd += "telnet-server"
