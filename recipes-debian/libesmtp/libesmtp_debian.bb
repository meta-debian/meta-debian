#
# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree\
#		/meta-networking/recipes-support/libesmtp/libesmtp_1.0.6.bb?
# base branch: master
# base commit: 7ddf591eb625abad574eea6af82685c267252cdb
#

SUMMARY = "SMTP client library"
DESCRIPTION = "\
	LibESMTP is a library to manage posting (or submission of) \
	electronic mail using SMTP to a preconfigured Mail Transport Agent(MTA)\
	such as Exim or PostFix."
HOMEPAGE = "http://www.stafford.uklinux.net/libesmtp/"

PR = "r0"
inherit debian-package
PV = "1.0.6"

LICENSE = "GPL-2.0+ & LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://auth-client.h;md5=955622f1ac3009c083042cfd8c34a2b5"
inherit autotools binconfig
DEPENDS = "openssl"

#Configure follow debian/rules
EXTRA_OECONF += "--enable-etrn"

#prepare some header file for compile
do_compile_prepend (){
	cp ${S}/auth-client.h ${S}/login
	cp ${S}/auth-plugin.h ${S}/login
	cp ${S}/auth-client.h ${S}/plain
	cp ${S}/auth-plugin.h ${S}/plain
	cp ${S}/auth-client.h ${S}/crammd5
        cp ${S}/auth-plugin.h ${S}/crammd5
}

#install follow Debian jessies
do_install_append () {
	#Correct the permission of file
	LINKLIB=$(basename $(readlink ${D}${libdir}/libesmtp.so))
	chmod 0644 ${D}${libdir}/$LINKLIB
	
	#re-name /usr/lib/esmtp-plugins to /usr/lib/esmtp
	mv ${D}${libdir}/esmtp-plugins ${D}${libdir}/esmtp
	chmod 0644 ${D}${libdir}/esmtp/*.so	
	#remove .la files
	rm ${D}${libdir}/esmtp/*.la
	rm ${D}${libdir}/*la	
}
#Correct the package name
PKG_${PN} = "libesmtp6"

FILES_${PN}-dev += "${bindir}/libesmtp-config"
FILES_${PN} += "${libdir}/esmtp/*.so"
FILES_${PN}-staticdev += "${libdir}/esmtp/*.a"
FILES_${PN}-dbg += "${libdir}/esmtp/.debug/*"
