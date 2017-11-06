SUMMARY = "GNOME configuration database system"
DESCRIPTION = "GConf is a configuration database system for storing application \
 preferences. It supports default or mandatory settings set by the \
 administrator, and changes to the database are instantly applied to all \
 running applications. It is written for the GNOME desktop but doesn't \
 require it."
HOMEPAGE = "http://projects.gnome.org/gconf/"

inherit debian-package
PV = "3.2.6"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605"

SRC_URI += "file://disable_HAVE_INTROSPECTION_and_disable_build_doc.patch"

# Add option to change root path for running postinst at do_rootfs
SRC_URI_append_class-native = " \
    file://add_option_change_root_path.patch \
"

inherit autotools-brokensep pkgconfig gettext

DEPENDS_class-target += "intltool-native glib-2.0 libxml2 dbus dbus-glib libldap \
                         ${PN}-native"
DEPENDS_class-native += "intltool-native glib-2.0-native libxml2-native dbus-native \
                          dbus-glib-native libldap-native"

EXTRA_OECONF += "--disable-gtk --disable-orbit --libexecdir=${libdir}/gconf"

PACKAGECONFIG ??= ""
PACKAGECONFIG[policykit] = "--enable-defaults-service,--disable-defaults-service,policykit-1"

EXTRA_OEMAKE += "pkglibdir=${libdir}/gconf"

do_install_append() {
	install -D -m 0755 ${S}/debian/gconf-schemas \
		${D}${sbindir}/gconf-schemas
	install -m 0755 ${S}/debian/update-gconf-defaults \
		${D}${bindir}/update-gconf-defaults

	# Remove unwanted files
	rm -rf ${D}${libdir}/*/*/*.a \
	       ${D}${libdir}/*/*/*.la

	# Base on debian/gconf2-common.install
	install -d ${D}${docdir}/gconf2-common
	mv ${D}${datadir}/GConf/schema/evoldap.schema \
		${D}${docdir}/gconf2-common/
	rm -rf ${D}${datadir}/GConf

	install -D -m 0644 ${S}/debian/default.path \
		${D}${datadir}/gconf/default.path

	# Base on debian/gconf2.links
	ln -sf gconftool-2 ${D}${bindir}/gconftool

	# Base on debian/gconf2-common.dirs
	install -d ${D}${datadir}/gconf/defaults \
	           ${D}${datadir}/gconf/mandatory \
	           ${D}${datadir}/gconf/schemas \
	           ${D}${localstatedir}/lib/gconf/defaults \
	           ${D}${localstatedir}/lib/gconf/debian.defaults \
	           ${D}${localstatedir}/lib/gconf/debian.mandatory
}

# Base on debian/gconf2.postinst
pkg_postinst_${PN}() {
	for GCONF_DIR in \
		$D${sysconfdir}/gconf/gconf.xml.mandatory \
		$D${sysconfdir}/gconf/gconf.xml.defaults ; do
		GCONF_TREE=$GCONF_DIR/%gconf-tree.xml
		if [ ! -f "$GCONF_TREE" ]; then
			gconf-merge-tree "$GCONF_DIR"
			chmod 644 "$GCONF_TREE"
			find "$GCONF_DIR" -mindepth 1 -maxdepth 1 -type d -exec rm -rf \{\} \;
			rm -f "$GCONF_DIR/%gconf.xml"
		fi
	done

	# Upon installation/upgrade, regenerate all databases, because in this case 
	# there will be no trigger run

	# When run do_rootfs, root path need to be changed.
	# Options --root are only available in gconf-native.
	change_root=""
	if [ -n $D ]; then
		change_root="--root $D"
	fi

	gconf-schemas --register-all --no-signal $change_root
	update-gconf-defaults --no-signal $change_root
	update-gconf-defaults --no-signal --mandatory $change_root

	if [ -z $D ]; then
		# Tell all running daemons to reload their databases
		kill -s HUP `pidof gconfd-2` >/dev/null 2>&1 || true
	fi
}

PACKAGES =+ "${PN}-gsettings-backend ${PN}-service ${PN}-common lib${PN}"

FILES_${PN}-gsettings-backend = "${libdir}/gio/modules/libgsettingsgconfbackend.so"
FILES_${PN}-service = "${libdir}/gconf/*/* \
                       ${datadir}/dbus-1/*"
FILES_${PN}-common = "${sysconfdir}/gconf/2/evoldap.conf \
                      ${sysconfdir}/gconf/2/path \
                      ${datadir}/gconf/default.path \
                      ${datadir}/sgml/*"
FILES_lib${PN} = "${libdir}/libgconf-2${SOLIBS}"
FILES_${PN}-dbg += "${libdir}/gio/modules/.debug"
FILES_${PN}-dbg += "${libdir}/gconf/*/.debug"

DEBIANNAME_${PN}-dev = "libgconf2-dev"
RPROVIDES_${PN}-dev = "libgconf2-dev"
DEBIANNAME_${PN} = "${PN}2"
RPROVIDES_${PN} = "${PN}2"
DEBIANNAME_${PN}-common = "${PN}2-common"
RPROVIDES_${PN}-common = "${PN}2-common"
RPROVIDES_lib${PN} = "lib${PN}-2-4 lib${PN}2-4"

# Base on debian/control
RDEPENDS_${PN}_class-target += "${PN}-service psmisc lib${PN} libxml2 python glib-2.0"
RDEPENDS_${PN}-service += "${PN}-common libldap libxml2 glib-2.0 lib${PN}"
RDEPENDS_${PN}-common += "ucf"
RDEPENDS_lib${PN} += "${PN}-common glib-2.0 ${PN}-service"
RDEPENDS_${PN}-dev += "lib${PN} glib-2.0-dev dbus-dev"
RDEPENDS_${PN}-gsettings-backend += "lib${PN} ${PN}-service glib-2.0"

BBCLASSEXTEND = "native"
