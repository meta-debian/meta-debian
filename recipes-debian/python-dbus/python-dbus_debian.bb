#
# base recipe: meta/recipes-devtools/python/python-dbus_1.2.0.bb
# base branch: jethro
#

SUMMARY = "simple interprocess messaging system"
DESCRIPTION = "D-Bus is a message bus, used for sending messages between applications. \
Conceptually, it fits somewhere in between raw sockets and CORBA in \
terms of complexity."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/DBusBindings#Python"

PR = "r0"

inherit debian-package
PV = "1.2.0"
DPN = "dbus-python"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=0b83047ce9e948b67c0facc5f233476a"

DEPENDS = "dbus dbus-glib"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit distutils-base autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[docs] = "--enable-html-docs,--disable-html-docs,python-docutils-native"
PACKAGECONFIG[api-docs] = "--enable-api-docs,--disable-api-docs,python-docutils-native python-epydoc-native"

export BUILD_SYS
export HOST_SYS

do_install_append() {
	# Follow debian/rules
	find ${D} -name '*.py[co]' -print0 | xargs -0 rm -f
	find ${D} -name '*.la' -print0 | xargs -0 rm -f
}

RDEPENDS_${PN}_class-target = "python-io python-logging python-stringold python-threading python-xml"

FILES_${PN}-dbg += "${libdir}/${PYTHON_DIR}/dist-packages/.debug"

BBCLASSEXTEND = "native"
