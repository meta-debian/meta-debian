#
# base recipe: meta/recipes-core/dbus/dbus.inc
# base branch: daisy
#

SUMMARY = "D-Bus message bus"
DESCRIPTION = "D-Bus is a message bus system, a simple way for applications to talk to one another. In addition to interprocess communication, D-Bus helps coordinate process lifecycle; it makes it simple and reliable to code a \"single instance\" application or daemon, and to launch applications and daemons on demand when their services are needed."
HOMEPAGE = "http://dbus.freedesktop.org"

PR = "r1"
inherit debian-package
PV = "1.8.22"

LICENSE = "AFL-2.1 | GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=10dded3b58148f3f1fd804b26354af3e \
                    file://dbus/dbus.h;beginline=6;endline=20;md5=7755c9d7abccd5dbd25a6a974538bb3c"
DEPENDS = "expat virtual/libintl"

# init.d/dbus require lsb-base
RDEPENDS_${PN}_class-target += "lsb-base"

RDEPENDS_dbus_class-native = ""
RDEPENDS_dbus_class-nativesdk = ""
PACKAGES += "${@bb.utils.contains('PTEST_ENABLED', '1', 'dbus-ptest', '', d)}"
ALLOW_EMPTY_dbus-ptest = "1"
RDEPENDS_dbus-ptest_class-target = "dbus-test-ptest"

SRC_URI += " \
           file://dbus/tmpdir.patch \
           file://dbus/os-test.patch \
           file://dbus/clear-guid_from_server-if-send_negotiate_unix_f.patch \
"

inherit useradd autotools pkgconfig gettext

python __anonymous() {
    if not bb.utils.contains('DISTRO_FEATURES', 'sysvinit', True, False, d):
        d.setVar("INHIBIT_UPDATERCD_BBCLASS", "1")
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r netdev"
USERADD_PARAM_${PN} = "--system --home ${localstatedir}/lib/dbus \
                       --no-create-home --shell /bin/false \
                       --user-group messagebus"

CONFFILES_${PN} = "${sysconfdir}/dbus-1/system.conf ${sysconfdir}/dbus-1/session.conf"

DEBIANNAME_${PN}-dbg = "${PN}-1-dbg"
DEBIANNAME_${PN}-doc = "${PN}-1-doc"

PACKAGES =+ "${PN}-lib"

OLDPKGNAME = "dbus-x11"
OLDPKGNAME_class-nativesdk = ""

# for compatibility
RPROVIDES_${PN} = "${OLDPKGNAME}"
RREPLACES_${PN} += "${OLDPKGNAME}"

FILES_${PN} = "${bindir}/dbus-daemon* \
               ${bindir}/dbus-uuidgen \
               ${bindir}/dbus-cleanup-sockets \
               ${bindir}/dbus-send \
               ${bindir}/dbus-monitor \
               ${bindir}/dbus-launch \
               ${bindir}/dbus-run-session \
               ${libexecdir}/dbus* \
               ${sysconfdir} \
               ${localstatedir} \
               ${datadir}/dbus-1/services \
               ${datadir}/dbus-1/system-services \
               ${systemd_unitdir}/system/"
FILES_${PN}-lib = "${base_libdir}/lib*.so.*"
RRECOMMENDS_${PN}-lib = "${PN}"
FILES_${PN}-dev += "${libdir}/dbus-1.0/include ${bindir}/dbus-glib-tool"

pkg_postinst_dbus() {
	# If both systemd and sysvinit are enabled, mask the dbus-1 init script
        if ${@bb.utils.contains('DISTRO_FEATURES','systemd sysvinit','true','false',d)}; then
		if [ -n "$D" ]; then
			OPTS="--root=$D"
		fi
		systemctl $OPTS mask dbus-1.service
	fi
}

# --disable-selinux: Don't use selinux support
EXTRA_OECONF = " \
		--disable-tests \
		--disable-xml-docs \
		--disable-doxygen-docs \
		--disable-libaudit \
		--disable-systemd \
		--without-dbus-glib \
		--disable-selinux \
"

# Follow debian/rules, libexecdir is ${prefix}/lib/dbus-1.0
libexecdir = "${libdir}/dbus-1.0"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG_class-native = ""
PACKAGECONFIG_class-nativesdk = ""

# Would like to --enable-systemd but that's a circular build-dependency between
# systemd<->dbus
PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_unitdir}/system/,--without-systemdsystemunitdir"
PACKAGECONFIG[x11] = "--with-x --enable-x11-autolaunch,--without-x --disable-x11-autolaunch, virtual/libx11 libsm"

do_install() {
	autotools_do_install

	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
		install -d ${D}${sysconfdir}/init.d
		install -m 0755 ${S}/debian/dbus.init ${D}${sysconfdir}/init.d/dbus
	fi

	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		for i in dbus.target.wants sockets.target.wants multi-user.target.wants; do \
			install -d ${D}${systemd_unitdir}/system/$i; done
		install -m 0644 ${B}/bus/dbus.service ${B}/bus/dbus.socket ${D}${systemd_unitdir}/system/
		cd ${D}${systemd_unitdir}/system/dbus.target.wants/
		ln -fs ../dbus.socket ${D}${systemd_unitdir}/system/dbus.target.wants/dbus.socket
		ln -fs ../dbus.socket ${D}${systemd_unitdir}/system/sockets.target.wants/dbus.socket
		ln -fs ../dbus.service ${D}${systemd_unitdir}/system/multi-user.target.wants/dbus.service
	fi

	if ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'true', 'false', d)}; then
		install -m 644 -D ${S}/debian/dbus-Xsession ${D}${sysconfdir}/X11/Xsession.d/75dbus_dbus-launch
	fi

	# On Debian, libdbus is installed in /lib instead of /usr/lib
	if [ ${libdir} != ${base_libdir} ]; then
		if [ ! -d ${D}${base_libdir} ]; then
			install -d ${D}${base_libdir}
		fi
		mv ${D}${libdir}/lib*.so.* ${D}${base_libdir}
		LINKLIB=$(basename $(readlink ${D}${libdir}/libdbus-1.so))
		rm ${D}${libdir}/libdbus-1.so
		ln -s ../../lib/${LINKLIB} ${D}${libdir}/libdbus-1.so
	fi

	install -d ${D}${sysconfdir}/default
	install -m 0644 ${S}/debian/dbus.default ${D}${sysconfdir}/default/dbus
	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${sysconfdir}/tmpfiles.d
		echo "d ${localstatedir}/run/dbus 0755 messagebus messagebus - none" \
		     > ${D}${sysconfdir}/tmpfiles.d/99_dbus.conf
	else
		install -d ${D}${sysconfdir}/default/volatiles
		echo "d messagebus messagebus 0755 ${localstatedir}/run/dbus none" \
		     > ${D}${sysconfdir}/default/volatiles/99_dbus
	fi

	mkdir -p ${D}${localstatedir}/lib/dbus

	chown messagebus:messagebus ${D}${localstatedir}/lib/dbus

	chown root:messagebus ${D}${libexecdir}/dbus-daemon-launch-helper
	chmod 4755 ${D}${libexecdir}/dbus-daemon-launch-helper

	# Remove Red Hat initscript
	rm -rf ${D}${sysconfdir}/rc.d

	# Remove empty testexec directory as we don't build tests
	rm -rf ${D}${libdir}/dbus-1.0/test

	# Remove /var/run as it is created on startup
	rm -rf ${D}${localstatedir}/run
}

do_install_class-native() {
	autotools_do_install

	# for dbus-glib-native introspection generation
	install -d ${STAGING_DATADIR_NATIVE}/dbus/
	# N.B. is below install actually required?
	install -m 0644 bus/session.conf ${STAGING_DATADIR_NATIVE}/dbus/session.conf

	# dbus-glib-native and dbus-glib need this xml file
	./bus/dbus-daemon --introspect > ${STAGING_DATADIR_NATIVE}/dbus/dbus-bus-introspect.xml

	# dbus-launch has no X support so lets not install it in case the host
	# has a more featured and useful version
	rm -f ${D}${bindir}/dbus-launch
}

do_install_class-nativesdk() {
	autotools_do_install

	# dbus-launch has no X support so lets not install it in case the host
	# has a more featured and useful version
	rm -f ${D}${bindir}/dbus-launch

	# Remove /var/run to avoid QA error
	rm -rf ${D}${localstatedir}/run

	# On Debian, libdbus is installed in /lib instead of /usr/lib
	if [ ${libdir} != ${base_libdir} ]; then
		if [ ! -d ${D}${base_libdir} ]; then
			install -d ${D}${base_libdir}
		fi
		mv ${D}${libdir}/lib*.so.* ${D}${base_libdir}
		LINKLIB=$(basename $(readlink ${D}${libdir}/libdbus-1.so))
		rm ${D}${libdir}/libdbus-1.so
		ln -s ../../lib/${LINKLIB} ${D}${libdir}/libdbus-1.so
	fi
}
BBCLASSEXTEND = "native nativesdk"
