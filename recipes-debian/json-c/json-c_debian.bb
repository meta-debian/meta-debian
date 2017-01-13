#
# base recipe: meta/recipes-devtools/json-c/json-c_0.12.bb
# base branch: master
# base commit: 646a29c3dab7f18d03da8c966ce55d8a09483c0e
#

SUMMARY = "C bindings for apps which will manipulate JSON data"
DESCRIPTION = "\
	JSON-C implements a reference counting object model that allows \
	you to easily construct JSON objects in C."
HOMEPAGE = "https://github.com/json-c/json-c/wiki"

PR = "r0"
inherit debian-package
PV = "0.11"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=de54b60fbbc35123ba193fea8ee216f2"

#The Makefile is lame, no parallel build
PARALLEL_MAKE = ""

RPROVIDES_${PN} = "libjson"

inherit autotools

do_configure_prepend() {
    # Clean up autoconf cruft that should not be in the tarball
    rm -f ${S}/config.status
}

#install follow Debian jessies
do_install_append () {
	install -d ${D}${base_libdir}
	mv ${D}${libdir}/*.so.2* ${D}${base_libdir}/
	
	chmod 0644 ${D}${base_libdir}/libjson-c.so.2.0.0

	rm ${D}${libdir}/libjson.so.0
	rm ${D}${libdir}/*.la
	
	rm ${D}${libdir}/libjson.a
	ln -s libjson-c.a ${D}${libdir}/libjson.a
	
	#Remove usr/lib/libjson.so.0.1.0 file
	rm ${D}${libdir}/*.so.*
	rm ${D}${libdir}/libjson.so
	ln -s libjson-c.so ${D}${libdir}/libjson.so
	
	chmod 0644 ${D}${base_libdir}/libjson-c.so.2.0.0

	ln -s libjson-c.so.2 ${D}${base_libdir}/libjson.so.0
	
	#Correct the softlink: libjson-c.so -> /lib/libjson-c.so.2.0.0
	LINKLIB=$(basename $(readlink ${D}${libdir}/libjson-c.so))
	rm ${D}${libdir}/libjson-c.so
	ln -s ../../lib/${LINKLIB} ${D}${libdir}/libjson-c.so
	
	#Correct the softlink json.pc -> json-c.pc
	rm ${D}${libdir}/pkgconfig/json.pc
	ln -s json-c.pc ${D}${libdir}/pkgconfig/json.pc
}
#Create new packages
PACKAGES =+ "libjson0 libjson0-dev libjson-c2"

DEBIANNAME_${PN}-doc = "libjson-c-doc"
DEBIANNAME_${PN}-dbg = "libjson-c2-dbg"
DEBIANNAME_${PN}-dev = "libjson-c-dev"

FILES_libjson0 = "${base_libdir}/libjson.so.0"

FILES_libjson0-dev =  " ${includedir}/json ${libdir}/pkgconfig/json.pc \
			${libdir}/libjson.so"

FILES_libjson-c2 = "${base_libdir}/*.so.2*"

FILES_libjson-c-dev =  "${includedir}/json-c/* ${libdir}/libjson-c.so \
			${libdir}/pkgconfig/json-c.pc"
