#
# base recipe: http://git.yoctoproject.org/cgit/cgit.cgi/meta-intel-iot-\
#	middleware/tree/recipes-extended/hiredis/hiredis_0.13.1.bb?		
# base branch: master
# base commit: c3a68293df470b3b73963b7ce6ca0c6d87c45904
#

SUMMARY = "minimalistic C client library for Redis"
DESCRIPTION = "\
	Hiredis is a minimalistic C client library for the Redis database. It is \
	minimalistic because it just adds minimal support for the protocol, but	\
	at the same time it uses an high level printf-alike API in order to make\
	it much higher level than otherwise suggested by its minimal code base	\
	and the lack of explicit bindings for every Redis command\
	"
HOMEPAGE = "http://github.com/redis/hiredis"

PR = "r0"
inherit debian-package
PV = "0.11.0"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=d84d659a35c666d23233e54503aaea51"

#correct-path-prefix-Makefile_debian.patch:
#	this patch is corrected the PREFIX variable in Makefile is '/usr'
#	instead of '${D}/usr' and CMAKE_MODULES_PATH=share/cmake-2.8/Modules
#	same as debian/rules
SRC_URI+= "file://correct-prefix-and-cmake-path-Makefile_debian.patch"

inherit autotools-brokensep

do_compile() {
	oe_runmake 'CC=${CC}'
}

do_install() {
	#create new folders to prepare for runmake
	install -d ${D}${libdir}/pkgconfig
	install -d ${D}${datadir}/cmake-2.8/Modules
	install -d ${D}${datadir}/cmake-3.0/Modules
	oe_runmake install DESTDIR=${D}	
	ln -s ../../cmake-2.8/Modules/FindHiredis.cmake \
		${D}${datadir}/cmake-3.0/Modules/FindHiredis.cmake
	LINKLIB=$(basename $(readlink ${D}${libdir}/libhiredis.so))
	LINKLIB_2=$(basename $(readlink ${D}${libdir}/$LINKLIB))
	chmod 0644 ${D}${libdir}/${LINKLIB_2}
	
}

FILES_${PN}-dev += "${datadir}/*"
