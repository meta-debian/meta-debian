# 
# Base recipe: meta/recipes-support/libgcrypt/libgcrypt_1.5.3.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "General purpose cryptographic library based on the code from GnuPG"
HOMEPAGE = "http://directory.fsf.org/project/libgcrypt/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

inherit debian-package autotools-brokensep binconfig pkgconfig
PV = "1.6.3"

PR = "r3"
DPN = "libgcrypt20"
DEPENDS = "libgpg-error libcap"

SRC_URI += " \
           file://add-pkgconfig-support.patch \
           file://libgcrypt-fix-building-error-with-O2-in-sysroot-path.patch \
	"

# helper program gcryptrnd and getrandom are under GPL, rest LGPL
LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LIB;md5=bbb461211a33b134d42ed5ee802b37ff"

ARM_INSTRUCTION_SET = "arm"

# Follow debian/rules
EXTRA_OECONF = " \
	--enable-noexecstack \
	--enable-ld-version-script --enable-static \
	--libdir=${base_libdir} \
"
# Avoid error:
# 	| ./.libs/libgcrypt.so: undefined reference to `_gcry_mpih_lshift'
# 	| ./.libs/libgcrypt.so: undefined reference to `_gcry_mpih_add_n'
# 	| ./.libs/libgcrypt.so: undefined reference to `_gcry_mpih_addmul_1'
# 	| ./.libs/libgcrypt.so: undefined reference to `_gcry_mpih_submul_1'
# 	| ./.libs/libgcrypt.so: undefined reference to `_gcry_mpih_mul_1'
# 	| ./.libs/libgcrypt.so: undefined reference to `_gcry_mpih_sub_n'
# 	| ./.libs/libgcrypt.so: undefined reference to `_gcry_mpih_rshift'
EXTRA_OECONF += "--disable-asm"

# libgcrypt.pc is added locally and thus installed here
do_install_append() {
	install -d ${D}/${libdir}/pkgconfig
	install -m 0644 ${B}/src/libgcrypt.pc ${D}/${libdir}/pkgconfig/

	# Move libgcrypt.so and static library to ${libdir}
	rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
	libname=`readlink ${D}${base_libdir}/libgcrypt.so | xargs basename`
	ln -s $rel_lib_prefix${base_libdir}/$libname ${D}${libdir}/libgcrypt.so
	rm ${D}${base_libdir}/*.so

	mv ${D}${base_libdir}/*.a ${D}${libdir}/
	mv ${D}${base_libdir}/*.la ${D}${libdir}/
	#correct the path of libdir to match from libgrypt.so installed.
	sed -i -e "s:^libdir='/lib':libdir='/usr/lib':g" ${D}${libdir}/libgcrypt.la
}

# Specifies the lead (or primary) compiled library file (.so) 
# that the debian class applies its naming policy
LEAD_SONAME = "libgcrypt.so"

# Correct list of files in package
FILES_${PN}-dev += "${bindir}/*"

# Correct name of .deb file
DEBIANNAME_${PN} = "libgcrypt20"
DEBIANNAME_${PN}-dev = "libgcrypt20-dev"
DEBIANNAME_${PN}-dbg = "libgcrypt20-dbg"
DEBIANNAME_${PN}-doc = "libgcrypt20-doc"

BBCLASSEXTEND = "native"
