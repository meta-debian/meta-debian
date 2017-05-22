#
# base recipe: meta/recipes-extended/slang/slang_2.2.4.bb
# base branch: jethro
# base commit: 1c914a844b35ff57b1c528251a9eaa19cedbaa10
#

SUMMARY = "S-Lang programming library"
DESCRIPTION = "S-Lang is a C programmer's library that includes routines for the rapid\n\
development of sophisticated, user friendly, multi-platform applications.\n\
.\n\
This package contains only the shared library libslang.so.* and copyright\n\
information. It is only necessary for programs that use this library (such\n\
as jed and slrn). If you plan on doing development with S-Lang, you will\n\
need the companion -dev package as well."
HOMEPAGE = "http://www.jedsoft.org/slang/"

PR = "r0"

inherit debian-package
PV = "2.3.0"
DPN = "slang2"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=a52a18a472d4f7e45479b06563717c02"

DEPENDS = "pcre libpng libonig zlib chrpath-native"

inherit autotools-brokensep

CLEANBROKEN = "1"
PARALLEL_MAKE = ""

EXTRA_OECONF = " \
    --with-pcre=${STAGING_LIBDIR}/.. --with-png=${STAGING_LIBDIR}/.. \
    --with-z=${STAGING_LIBDIR}/.. --with-onig=${STAGING_LIBDIR}/.. \
    --x-includes=${STAGING_INCDIR} --x-libraries=${STAGING_LIBDIR}"

do_configure_prepend() {
	# slang keeps configure.ac and rest of autoconf files in autoconf/ directory
	# we have to go there to be able to run gnu-configize cause it expects configure.{in,ac}
	# to be present. Resulting files land in autoconf/autoconf/ so we need to move them.
	( cd ${S}/autoconf && \
	gnu-configize --force && \
	mv autoconf/config.* . )
}

do_compile_append() {
	# Follow debian/rules
	oe_runmake -C src static
	ar cqv libslang_pic.a src/elfobjs/*.o
}

do_install() {
	oe_runmake install DESTDIR=${D} -e 'INST_LIB_DIR=${STAGING_LIBDIR}'

	# Follow debian/rules
	cp src/slang.ver ${D}${libdir}/libslang_pic.map
	mkdir -p ${D}${base_libdir}
	cp -a src/objs/libslang.a ${D}${libdir}/
	mv ${D}${libdir}/libslang.so.* ${D}${base_libdir}/

	# Relink libslang.so
	libname=`readlink ${D}${libdir}/libslang.so | xargs basename`
	rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
	ln -sf ${rel_lib_prefix}${base_libdir}/libslang.so.2 ${D}${libdir}/libslang.so

	cp libslang_pic.a ${D}${libdir}/
	chrpath -d ${D}${libdir}/slang/v2/modules/*.so
	chrpath -d ${D}${bindir}/slsh
}

PACKAGES =+ "libslang2-modules libslang2-pic slsh"

FILES_libslang2-modules = "${libexecdir}/v2/modules/*"
FILES_libslang2-pic = "${libdir}/libslang_pic.*"
FILES_slsh = "${sysconfdir}/* ${bindir}/* ${datadir}/slsh/*"
FILES_${PN}-dbg += "${libexecdir}/v2/modules/.debug"

DEBIANNAME_${PN} = "libslang2"
DEBIANNAME_${PN}-dev = "libslang2-dev"

BBCLASSEXTEND = "native"
