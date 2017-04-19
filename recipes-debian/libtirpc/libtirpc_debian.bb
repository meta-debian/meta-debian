PR = "r0"

inherit debian-package
PV = "0.2.5"

LICENSE = "BSD"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=f835cce8852481e4b2bbbdd23b5e47f3 \
	file://src/netname.c;beginline=1;endline=27;md5=f8a8cd2cb25ac5aa16767364fb0e3c24 \
"

DEPENDS += "xz-utils-native"
PROVIDES = "virtual/librpc"

# Get patch file from meta/recipes-extended/libtirpc/,
# branch: daisy
SRC_URI_append_libc-uclibc = " file://remove-des-uclibc.patch"

inherit autotools pkgconfig

EXTRA_OECONF = "--disable-gssapi"

do_install_append (){
	# Move libraries from /usr/lib to /lib
	test -d ${D}${base_libdir} || install -d ${D}${base_libdir}
	mv ${D}${libdir}/libtirpc${SOLIBS} ${D}${base_libdir}

	# Relink library
	rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
	libname=`readlink ${D}${libdir}/libtirpc.so | xargs basename`
	ln -sf ${rel_lib_prefix}${base_libdir}/${libname} ${D}${libdir}/libtirpc.so
}
