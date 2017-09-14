SUMMARY = "inetd configuration file updater"
DESCRIPTION = "This package provides a program used by other packages to\n\
automatically update /etc/inetd.conf, the configuration file shared\n\
by all implementations of the Internet super-server.\n\
.\n\
Note that xinetd is not supported by this package."

inherit debian-package
PV = "4.43"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://update-inetd;beginline=5;endline=20;md5=3eac2c882cb20eab9f16557a10bbe19e"

# send SIGHUP signal to reset inetd service is useless when we bitbake
SRC_URI_append_class-native = " file://dont-wakeup-inetd.patch"

# runtime dependencies for update-inetd-native
DEPENDS_class-native = "libfile-copy-recursive-perl-native debconf-native"

inherit cpan-base perlnative

do_install() {
	install -d ${D}${datadir}/perl5 ${D}${sbindir}
	install -m 0644 ${S}/DebianNet.pm ${D}${datadir}/perl5/
	install -m 0755 ${S}/update-inetd ${D}${sbindir}/
}

do_install_append_class-native() {
	# Use perl-native to run update-inetd
	sed -i -e "s@/usr/bin/perl@/usr/bin/env nativeperl@g" ${D}${sbindir}/update-inetd
}

FILES_${PN} += "${datadir}/perl5/*"

RDEPENDS_${PN}_class-target += "perl-base libfile-copy-recursive-perl debconf"

BBCLASSEXTEND = "native"
