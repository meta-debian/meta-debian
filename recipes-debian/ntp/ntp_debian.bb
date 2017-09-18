#
# base recipe:
#	http://cgit.openembedded.org/meta-openembedded/tree/meta-networking/recipes-support/ntp/ntp_4.2.8p3.bb
#

PR = "r2"

inherit debian-package
PV = "4.2.6.p5+dfsg"

LICENSE = "NTP"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=fea4b50c33b18c2194b4b1c9ca512670"

inherit autotools pkgconfig useradd

USERADD_PACKAGES = "${PN}"
NTP_USER_HOME ?= "/var/lib/ntp"
USERADD_PARAM_${PN} = "--system --home-dir ${NTP_USER_HOME} \
                       --no-create-home \
                       --shell /bin/false --user-group ntp"

# Configure follow debian/rules
EXTRA_OECONF += "--enable-all-clocks --enable-parse-clocks --enable-SHM \
                --disable-debugging --sysconfdir=/var/lib/ntp \
                --with-sntp=no \
                --with-lineeditlibs=edit \
                --without-ntpsnmpd \
                --disable-local-libopts \
                --enable-ntp-signd \
                --disable-dependency-tracking \
                lo_cv_test_autoopts=no \
"

PACKAGECONFIG ??= "openssl cap"
PACKAGECONFIG[openssl] = "--with-openssl-libdir=${STAGING_LIBDIR} \
                          --with-openssl-incdir=${STAGING_INCDIR} \
                          --with-crypto, \
                          --without-openssl --without-crypto, \
                          openssl"
PACKAGECONFIG[cap] = "--enable-linuxcaps,--disable-linuxcaps,libcap"
PACKAGECONFIG[readline] = "--with-lineeditlibs,--without-lineeditlibs,readline"

do_install_append(){
	# Follow debian/rules
	#
	# move the administrator programs from /usr/bin to /usr/sbin
	test -d ${D}${sbindir} || install -d ${D}${sbindir}
	for file in ntpdate ntp-wait ntpd ntptime ntp-keygen; do
		mv ${D}${bindir}/$file ${D}${sbindir}/$file || exit
	done

	# don't install tickadj
	rm ${D}${bindir}/tickadj

	install -D -m 0755 ${B}/scripts/ntpsweep ${D}${bindir}/ntpsweep

	test -d ${D}${sysconfdir}/dhcp/dhclient-exit-hooks.d || \
		install -d ${D}${sysconfdir}/dhcp/dhclient-exit-hooks.d
	install -D -m 0644 ${S}/debian/ntp.dhcp ${D}${sysconfdir}/dhcp/dhclient-exit-hooks.d/ntp
	install -D -m 0644 ${S}/debian/ntpdate.dhcp ${D}${sysconfdir}/dhcp/dhclient-exit-hooks.d/ntpdate
	install -D -m 0755 ${S}/debian/ntpdate-debian ${D}${sbindir}/ntpdate-debian

	install -D -m 0644 ${S}/debian/ntp.conf ${D}${sysconfdir}/ntp.conf

	# remove upstream man pages, which are currently not as nice as Debian's / ntpsnmpd we don't want
	( cd ${D}${mandir}/man1/; rm ntpd.1 ntpdc.1 ntp-keygen.1 ntpq.1 ntpsnmpd.1 )
	MANS="1 5 8"
	for i in $MANS; do
		test -d ${D}${mandir}/man$i || install -d ${D}${mandir}/man$i
		install -m 0644 ${S}/debian/man/*.$i ${D}${mandir}/man$i/
	done

	# Install files from ${S}/debian
	#
	test -d ${D}${sysconfdir}/cron.daily || install -d ${D}${sysconfdir}/cron.daily
	test -d ${D}${sysconfdir}/default || install -d ${D}${sysconfdir}/default
	test -d ${D}${sysconfdir}/init.d || install -d ${D}${sysconfdir}/init.d
	test -d ${D}${sysconfdir}/logcheck/ignore.d.server || \
			install -d ${D}${sysconfdir}/logcheck/ignore.d.server
	test -d ${D}${sysconfdir}/network/if-up.d || \
			install -d ${D}${sysconfdir}/network/if-up.d

	install -m 0755 ${S}/debian/ntp.cron.daily	${D}${sysconfdir}/cron.daily/ntp
	install -m 0644 ${S}/debian/ntp.default		${D}${sysconfdir}/default/ntp
	install -m 0644 ${S}/debian/ntpdate.default	${D}${sysconfdir}/default/ntpdate
	install -m 0755 ${S}/debian/ntp.init		${D}${sysconfdir}/init.d/ntp
	install -m 0644 ${S}/debian/ntpdate.logcheck.ignore.server \
			${D}${sysconfdir}/logcheck/ignore.d.server/ntpdate
	install -m 0755 ${S}/debian/ntpdate.if-up	${D}${sysconfdir}/network/if-up.d/ntpdate
}

PACKAGES =+ "ntpdate"

FILES_ntpdate = " \
	${sysconfdir}/*/ntpdate \
	${sysconfdir}/*/*/ntpdate \
	${sbindir}/ntpdate* \
"
