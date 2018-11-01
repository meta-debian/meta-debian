#
# Base recipe: meta/recipes-extended/sysstat/sysstat_10.2.1.bb
# Base branch: daisy
#

SUMMARY = "System performance tools"
DESCRIPTION = "The sysstat utilities are a collection of performance monitoring tools for Linux."
HOMEPAGE = "http://sebastien.godard.pagesperso-orange.fr/"

PR = "r0"

inherit debian-package autotools-brokensep gettext
PV = "11.0.1"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

KEEP_NONARCH_BASELIB = "1"

# --disable-yesterday:
# sa2 fails to use "-d yesterday", which is not supported by busybox date
EXTRA_OECONF += " \
		--disable-yesterday \
		--disable-man-group \
		--disable-compress-manpg \
		--enable-copy-only \
		sa_lib_dir=${nonarch_libdir}/${DPN} \
		sa_dir=${localstatedir}/log/${DPN} \
		conf_dir=${sysconfdir}/${DPN}"

# Install files follow deian/rules
do_install_append() {
	mkdir -p -m 755 ${D}${libdir}/sysstat
	install -m 0755 ${S}/contrib/isag/isag ${D}${bindir}/isag
	install -m 0644 ${S}/contrib/isag/isag.1 ${D}${mandir}/man1
	install -d ${D}${datadir}/applications
	install -m 0644 ${S}/debian/isag.desktop ${D}${datadir}/applications
	install -m 0755 ${S}/debian/debian-sa1 ${D}${nonarch_libdir}/${DPN}
	rm -rf ${D}${docdir}
	install -d ${D}${sysconfdir}/cron.d
	install -m 0644 ${S}/debian/sysstat.cron.d ${D}${sysconfdir}/cron.d/sysstat
	install -d ${D}${sysconfdir}/cron.daily
	install -m 0755 ${S}/debian/sysstat.cron.daily ${D}${sysconfdir}/cron.daily/sysstat
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/sysstat.init.d ${D}${sysconfdir}/init.d/sysstat
}

# According to debian/sysstat.postinst
inherit update-alternatives
ALTERNATIVE_PRIORITY = "0"
ALTERNATIVE_${PN} = "sar"
ALTERNATIVE_LINK_NAME[sar] = "${bindir}/sar"

pkg_postinst_${PN}() {
	cat > $D${sysconfdir}/default/sysstat << EOF
#
# Default settings for /etc/init.d/sysstat, /etc/cron.d/sysstat
# and /etc/cron.daily/sysstat files
#

# Should sadc collect system activity informations? Valid values
# are "true" and "false". Please do not put other values, they
# will be overwritten by debconf!
ENABLED="false"

EOF
}

# Ship packages follow debian
PACKAGES =+ "isag"
FILES_isag += "${bindir}/isag \
               ${datadir}/applications/isag.desktop \
               "
