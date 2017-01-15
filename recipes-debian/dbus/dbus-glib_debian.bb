#
# base recipe: meta/recipes-core/dbus/dbus-glib_0.100.2.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "0.102"

SUMMARY = "High level language (GLib) binding for D-Bus"
DESCRIPTION = "GLib bindings for the D-Bus message bus that integrate \
the D-Bus library with the GLib thread abstraction and main loop."
HOMEPAGE = "http://www.freedesktop.org/Software/dbus"
LICENSE = "AFL-2.1 | GPLv2+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=cf5b3a2f7083750d504333114e738656 \
	file://dbus/dbus-glib.h;beginline=7;endline=21;md5=7755c9d7abccd5dbd25a6a974538bb3c \
"
SECTION = "base"

DEPENDS = "expat glib-2.0 virtual/libintl dbus-glib-native dbus"
DEPENDS_class-native = "glib-2.0-native dbus-native"

SRC_URI += " \
	file://dbus-glib/no-examples.patch \
	file://dbus-glib/test-install-makefile.patch \
"

inherit autotools pkgconfig gettext

#default disable regression tests, some unit test code in non testing code
#PACKAGECONFIG_pn-${PN} = "tests" enable regression tests local.conf
PACKAGECONFIG ??= ""
PACKAGECONFIG[tests] = "--enable-tests,,,"

EXTRA_OECONF = "--with-introspect-xml=${STAGING_DATADIR_NATIVE}/dbus/dbus-bus-introspect.xml \
                --with-dbus-binding-tool=${STAGING_BINDIR_NATIVE}/dbus-binding-tool"
EXTRA_OECONF_class-native = "--with-introspect-xml=${STAGING_DATADIR_NATIVE}/dbus/dbus-bus-introspect.xml"

PACKAGES += "${PN}-bash-completion ${PN}-tests-dbg ${PN}-tests"

FILES_${PN} = "${libdir}/lib*${SOLIBS}"
FILES_${PN}-bash-completion = "${sysconfdir}/bash_completion.d/dbus-bash-completion.sh \
   ${libexecdir}/dbus-bash-completion-helper"
FILES_${PN}-dev += "${libdir}/dbus-1.0/include ${bindir}/dbus-glib-tool"
FILES_${PN}-dev += "${bindir}/dbus-binding-tool"

RDEPENDS_${PN}-tests += "dbus-x11"
FILES_${PN}-tests = "${datadir}/${BPN}/tests"
FILES_${PN}-tests-dbg = "${datadir}/${BPN}/tests/.debug/* \
                         ${datadir}/${BPN}/tests/core/.debug/* \
                         ${datadir}/${BPN}/tests/interfaces/.debug/*"

# Change the package name follow Debian
DEBIANNAME_${PN}-dbg = "libdbus-glib-1-2-dbg"
DEBIANNAME_${PN}-dev = "libdbus-glib-1-dev"
DEBIANNAME_${PN}-doc = "libdbus-glib-1-doc"
DEBIANNAME_${PN} = "libdbus-glib-1-2"

BBCLASSEXTEND = "native"
