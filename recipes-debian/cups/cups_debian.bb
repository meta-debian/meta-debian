#
# Base recipes: meta/recipes-extended/cups/cups.inc
#               meta/recipes-extended/cups/cups_1.7.1.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "An Internet printing system for Unix"
SECTION = "console/utils"

inherit debian-package autotools-brokensep binconfig
PV = "1.7.5"

PR = "r1"
DEPENDS = "gnutls libpng libjpeg-turbo dbus dbus-glib zlib libusb1"

LICENSE = "GPLv2 & LGPLv2"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c5e50cb4b8f24b04636b719683a9102d"

PROVIDES = "cups14"

SRC_URI += "\
	file://0001-don-t-try-to-run-generated-binaries_debian.patch \
"

PACKAGECONFIG ??= "avahi \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"

PACKAGECONFIG[avahi] = "--enable-avahi --enable-dnssd,--disable-avahi --disable-dnssd,avahi"
PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl"
PACKAGECONFIG[pam] = "--enable-pam, --disable-pam, libpam"

EXTRA_OECONF = " \
               --enable-gnutls \
               --enable-dbus \
               --enable-browsing \
               --disable-openssl \
               --disable-gssapi \
               --enable-debug \
               --disable-relro \
               --enable-libusb \
               --without-php \
               --without-perl \
               --without-python \
               --without-java \
               "

# Add more conf options according to Debian rules
EXTRA_OECONF += " \
	--with-docdir=${datadir}/cups/doc-root \
	--localedir=${datadir}/cups/locale \
	--enable-libpaper \
	--enable-ssl \
	--enable-threads \
	--enable-static \
	--with-dbusdir=${sysconfdir}/dbus-1 \
	--disable-launchd \
	--with-cups-group=lp \
	--with-system-groups=lpadmin \
	--with-printcap=${localstatedir}/run/cups/printcap \
	--with-log-file-perm=0640 \
	--with-local_protocols='dnssd' \
	--with-systemdsystemunitdir=${systemd_system_unitdir} \
	"

do_configure() {
	gnu-configize
	libtoolize --force
	autoconf
	DSOFLAGS="${LDFLAGS}" oe_runconf
}

do_compile () {
	sed -i s:STRIP:NOSTRIP: Makedefs
	sed -i s:serial:: backend/Makefile

	echo "all:"    >  man/Makefile
	echo "libs:" >> man/Makefile
	echo "install:" >> man/Makefile
	echo "install-data:" >> man/Makefile
	echo "install-exec:" >> man/Makefile
	echo "install-headers:" >> man/Makefile
	echo "install-libs:" >> man/Makefile

	oe_runmake "SSLLIBS=-lgnutls -L${STAGING_LIBDIR}" \
		   "LIBPNG=-lpng -lm -L${STAGING_LIBDIR}" \
		   "LIBJPEG=-ljpeg -L${STAGING_LIBDIR}" \
		   "LIBZ=-lz -L${STAGING_LIBDIR}" \
		   "-I."
}

# This variable is set base on default value of CUPS_SERVERBIN
# in config-scripts/cups-directories.m4.
# It just helps to maintain do_install and files shipping easier.
# Change this variable does not make installation path be changed.
CUPS_SERVERBIN = "${exec_prefix}/lib/cups"

do_install () {
	oe_runmake "DSTROOT=${D}" install

	# Remove /var/run from package as cupsd will populate it on startup
	rm -fr ${D}/${localstatedir}/run
	rmdir ${D}/${CUPS_SERVERBIN}/driver

	# Correct locations, path according to Debian rules
	install -d ${D}${CUPS_SERVERBIN}/backend-available
	mv ${D}${CUPS_SERVERBIN}/backend/lpd ${D}${CUPS_SERVERBIN}/backend-available/
	mv ${D}${CUPS_SERVERBIN}/backend/socket ${D}${CUPS_SERVERBIN}/backend-available/
	mv ${D}${CUPS_SERVERBIN}/backend/usb ${D}${CUPS_SERVERBIN}/backend-available/
	mv ${D}${CUPS_SERVERBIN}/backend/snmp ${D}${CUPS_SERVERBIN}/backend-available/
	mv ${D}${CUPS_SERVERBIN}/backend/dnssd ${D}${CUPS_SERVERBIN}/backend-available/

	install -D -m 644 ${S}/debian/local/apparmor-profile ${D}${sysconfdir}/apparmor.d/usr.sbin.cupsd
	install -D -m 644 ${S}/debian/local/cups.ufw.profile ${D}${sysconfdir}/ufw/applications.d/cups
	install -D -m 644 ${S}/debian/local/apport-hook.py ${D}${datadir}/apport/package-hooks/source_cups.py

	# Manual install pam file
	install -D -m 644 ${S}/debian/cups-daemon.cups.pam ${D}${sysconfdir}/pam.d/cups

	rm -rf ${D}${datadir}/cups/banners ${D}${datadir}/cups/data \
	       ${D}${datadir}/cups/model ${D}${datadir}/cups/profiles
}

python do_package_append() {
    import subprocess
    # Change permissions back the way they were, they probably had a reason...
    workdir = d.getVar('WORKDIR', True)
    subprocess.call('chmod 0511 %s/install/cups/var/run/cups/certs' % workdir, shell=True)
}

# Re-arrange list of packages build from source
PACKAGES = "${PN} libcups2 ${PN}-dev ${PN}-staticdev ${PN}-dbg ${PN}-libimage \
	${PN}-bsd ${PN}-client ${PN}-common ${PN}-core-drivers ${PN}-daemon \
	${PN}-ppdc ${PN}-server-common libcupscgi1 libcupscgi-dev \
	libcupsimage2 libcupsimage2-dev libcupsmime1 libcupsmime-dev \
	libcupsppdc1 libcupsppdc-dev"

FILES_${PN} = " \
	${CUPS_SERVERBIN}/backend-available/dnssd \
	${CUPS_SERVERBIN}/backend-available/lpd \
	${CUPS_SERVERBIN}/backend-available/snmp \
	${CUPS_SERVERBIN}/backend-available/socket \
	${CUPS_SERVERBIN}/backend-available/usb \
	${CUPS_SERVERBIN}/cgi-bin/*.cgi \
	${CUPS_SERVERBIN}/filter/rastert* \
	${CUPS_SERVERBIN}/monitor/* \
	${CUPS_SERVERBIN}/daemon/* \
	${sysconfdir}/cups/snmp.conf \
	${sysconfdir}/cups/ppd \
	${sysconfdir}/cups/interfaces \
	${sbindir}/cupsfilter \
	${sysconfdir}/cups/cupsd.conf \
	${datadir}/applications \
	"

FILES_${PN}-bsd = " \
	${bindir}/lpq \
	${bindir}/lpr \
	${bindir}/lprm \
	${sbindir}/lpc \
	"

# ippfind will not appear if avahi package not existed
FILES_${PN}-client = " \
	${bindir}/cancel \
	${bindir}/cupstestdsc \
	${bindir}/cupstestppd \
	${bindir}/ippfind \
	${bindir}/ipptool \
	${bindir}/lp \
	${bindir}/lpoptions \
	${bindir}/lppasswd \
	${bindir}/lpstat \
	${sbindir}/accept \
	${sbindir}/cupsaccept \
	${sbindir}/cupsaddsmb \
	${sbindir}/cupsctl \
	${sbindir}/cupsdisable \
	${sbindir}/cupsenable \
	${sbindir}/cupsreject \
	${sbindir}/lpadmin \
	${sbindir}/lpinfo \
	${sbindir}/lpmove \
	${sbindir}/reject \
	${datadir}/cups/ipptool/* \
	"

FILES_${PN}-common = " \
	${datadir}/cups/drv/sample.drv \
	${datadir}/cups/locale \
	${datadir}/cups/ppdc/*.defs \
	${datadir}/doc/cups-common \
	${datadir}/lintian/overrides \
	"

FILES_${PN}-core-drivers = " \
	${CUPS_SERVERBIN}/filter/commandtops \
	${CUPS_SERVERBIN}/filter/gziptoany \
	${CUPS_SERVERBIN}/filter/pstops \
	"

#Missing cups-daemon file because it requires apparmor_parser command
#to create.
FILES_${PN}-daemon = " \
	${sysconfdir}/apparmor.d/usr.sbin.cupsd \
	${sysconfdir}/cups/cups-files.conf \
	${sysconfdir}/init.d/cups \
	${sysconfdir}/pam.d/cups \
	${sysconfdir}/ufw \
	${sysconfdir}/cups/ssl \
	${systemd_system_unitdir} \
	${CUPS_SERVERBIN}/backend/http* \
	${CUPS_SERVERBIN}/backend/ipp* \
	${CUPS_SERVERBIN}/backend/https \
	${CUPS_SERVERBIN}/backend/https \
	${CUPS_SERVERBIN}/notifier/* \
	${sbindir}/cupsd \
	${datadir}/apport \
	${datadir}/cups/cupsd.conf.default \
	"

FILES_${PN}-ppdc = " \
	${bindir}/ppdc \
	${bindir}/ppdhtml \
	${bindir}/ppdi \
	${bindir}/ppdmerge \
	${bindir}/ppdpo \
	${datadir}/cups/examples \
	${datadir}/cups/ppdc/*.h \
	"

FILES_${PN}-server-common = " \
	${datadir}/cups/doc-root \
	${datadir}/cups/mime \
	${datadir}/cups/templates \
	${datadir}/cups/usb \
	"

FILES_libcups2 = " \
	${libdir}/libcups.so.* \
	"

FILES_${PN}-dev = " \
	${bindir}/cups-config \
	${includedir}/cups/adminutil.h \
	${includedir}/cups/array.h \
	${includedir}/cups/backend.h \
	${includedir}/cups/cups.h \
	${includedir}/cups/dir.h \
	${includedir}/cups/file.h \
	${includedir}/cups/http.h \
	${includedir}/cups/i18n.h \
	${includedir}/cups/ipp.h \
	${includedir}/cups/language.h \
	${includedir}/cups/ppd.h \
	${includedir}/cups/pwg.h \
	${includedir}/cups/sidechannel.h \
	${includedir}/cups/transcode.h \
	${includedir}/cups/versioning.h \
	${libdir}/libcups.so \
	"

FILES_${PN}-staticdev = " \
	${libdir}/libcups.a \
	${libdir}/libcupscgi.a \
	${libdir}/libcupsimage.a \
	${libdir}/libcupsmime.a \
	${libdir}/libcupsppdc.a \
	"

FILES_libcupscgi1 = " \
	${libdir}/libcupscgi.so.1 \
	"

FILES_libcupscgi-dev = " \
	${includedir}/cups/cgi.h \
	${includedir}/cups/help-index.h \
	${libdir}/libcupscgi.so \
	"

FILES_libcupsimage2 = " \
	${libdir}/libcupsimage.so.2 \
	"

FILES_libcupsimage2-dev = " \
	${includedir}/cups/raster.h \
	${libdir}/libcupsimage.so \
	"

FILES_libcupsmime1 = " \
	${libdir}/libcupsmime.so.1 \
	"

FILES_libcupsmime-dev = " \
	${includedir}/cups/mime.h \
	${libdir}/libcupsmime.so \
	"

FILES_libcupsppdc1 = " \
	${libdir}/libcupsppdc.so.1 \
	"
FILES_libcupsppdc-dev = " \
	${includedir}/cups/ppdc.h \
	${libdir}/libcupsppdc.so \
	"

FILES_${PN}-libimage = " \
	${libdir}/libcupsimage.so.* \
	"

DOTDEBUG-dbg = "${bindir}/.debug ${sbindir}/.debug ${libexecdir}/.debug ${libdir}/.debug \
            ${base_bindir}/.debug ${base_sbindir}/.debug ${base_libdir}/.debug ${libdir}/${BPN}/.debug \
            ${libdir}/matchbox-panel/.debug /usr/src/debug"

DEBUGFILEDIRECTORY-dbg = "/usr/lib/debug /usr/src/debug"

FILES_${PN}-dbg = "${@d.getVar(['DOTDEBUG-dbg', 'DEBUGFILEDIRECTORY-dbg'][d.getVar('PACKAGE_DEBUG_SPLIT_STYLE', True) == 'debug-file-directory'], True)}"

FILES_${PN}-dbg += " \
	${CUPS_SERVERBIN}/backend-available/.debug \
	${CUPS_SERVERBIN}/backend/.debug \
	${CUPS_SERVERBIN}/cgi-bin/.debug \
	${CUPS_SERVERBIN}/filter/.debug \
	${CUPS_SERVERBIN}/monitor/.debug \
	${CUPS_SERVERBIN}/notifier/.debug \
	${CUPS_SERVERBIN}/daemon/.debug \
	${localstatedir} \
	${sysconfdir}/rc* \
	${sysconfdir}/dbus-1 \
	"

#package the html for the webgui inside the main packages (~1MB uncompressed)
FILES_${PN} += "${datadir}/doc/cups/images \
                ${datadir}/doc/cups/*html \
                ${datadir}/doc/cups/*.css \
                ${datadir}/icons/ \
               "

# Correct name of Debian package
DEBIANNAME_${PN}-dev = "libcups2-dev"
DEBIANNAME_libcupscgi-dev = "libcupscgi1-dev"
DEBIANNAME_libcupsmime-dev = "libcupsmime1-dev"
DEBIANNAME_libcupsppdc-dev = "libcupsppdc1-dev"

CONFFILES_${PN} += "${sysconfdir}/cups/cupsd.conf"

SYSROOT_PREPROCESS_FUNCS += "cups_sysroot_preprocess"
cups_sysroot_preprocess () {
	sed -i ${SYSROOT_DESTDIR}${bindir_crossscripts}/cups-config -e 's:cups_datadir=.*:cups_datadir=${datadir}/cups:' -e 's:cups_serverbin=.*:cups_serverbin=${libdir}/cups:'
}

# Base on debian/control
RDEPENDS_libcup2 += "libavahi-client libavahi-common zlib"

PARALLEL_MAKE = ""
