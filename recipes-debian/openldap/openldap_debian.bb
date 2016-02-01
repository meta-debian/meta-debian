require openldap.inc
PR = "${INC_PR}.0"

#
# Makefile_debian.patch:
#	This patch is correct the path of $LIBTOOL, $CC, $LDAP_LIB, $LDAP_INC 
#	in the Makefiles (including the Makefiles in subfolders)
# install-strip.patch:
#	The original top.mk used INSTALL, not INSTALL_STRIP_PROGRAM when
#	installing .so and executables, this fails in cross compilation
#	environments
# Makefile-native_debian.patch:
#       This patch is correct the path of $LIBTOOL, $LDAP_LIB, $LDAP_INC
#       in the Makefiles (including the Makefiles in subfolders)

SRC_URI_class-target = "\
		${DEBIAN_SRC_URI} \
		file://Makefile_debian.patch \
		file://install-strip.patch"
SRC_URI_class-native = "\
		${DEBIAN_SRC_URI} \
		file://Makefile-native_debian.patch \
		file://install-strip.patch"

DEPENDS += "heimdal libldap unixodbc"
#correct the patch to libperl.so
LDFLAGS_prepend = " -L${STAGING_LIBDIR}/perl/${PERLVERSION}/CORE "

inherit perlnative cpan-base

PACKAGECONFIG ??= "\
	gnutls modules ldap meta monitor null passwd shell proxycache dnssrv sock \
	bdb mdb hdb relay collect constraint dds deref dyngroup dynlist memberof \
	ppolicy perl refint retcode rwm seqmod sssvlv syncprov translucent unique \
	valsort auditlog accesslog sasl slapi sql \
"
#--with-tls with TLS/SSL support auto|openssl|gnutls [auto]
PACKAGECONFIG[gnutls] = "--with-tls=gnutls,,gnutls libgcrypt"
PACKAGECONFIG[openssl] = "--with-tls=openssl,,openssl"

PACKAGECONFIG[sasl] = "--with-cyrus-sasl,--without-cyrus-sasl,libsasl2"
PACKAGECONFIG[modules] = "lt_cv_dlopen_self=yes --enable-modules,--disable-modules,libtool"

#--enable-bdb          enable Berkeley DB backend no|yes|mod yes
# The Berkely DB is the standard choice.  This version of OpenLDAP requires
# the version 4 implementation or better.
PACKAGECONFIG[bdb] = "--enable-bdb=mod,--enable-bdb=no,db"

#--enable-dnssrv       enable dnssrv backend no|yes|mod no
PACKAGECONFIG[dnssrv] = "--enable-dnssrv=mod,--enable-dnssrv=no"

#--enable-hdb          enable Hierarchical DB backend no|yes|mod no
PACKAGECONFIG[hdb] = "--enable-hdb=mod,--enable-hdb=no,db"

#--enable-ldap         enable ldap backend no|yes|mod no
PACKAGECONFIG[ldap] = "--enable-ldap=mod,--enable-ldap=no,"

#--enable-mdb          enable mdb database backend no|yes|mod [yes]
PACKAGECONFIG[mdb] = "--enable-mdb=mod,--enable-mdb=no,"

#--enable-meta         enable metadirectory backend no|yes|mod no
PACKAGECONFIG[meta] = "--enable-meta=mod,--enable-meta=no,"

#--enable-monitor      enable monitor backend no|yes|mod yes
PACKAGECONFIG[monitor] = "--enable-monitor=mod,--enable-monitor=no,"

#--enable-ndb          enable MySQL NDB Cluster backend no|yes|mod [no]
PACKAGECONFIG[ndb] = "--enable-ndb=mod,--enable-ndb=no,"

#--enable-null         enable null backend no|yes|mod no
PACKAGECONFIG[null] = "--enable-null=mod,--enable-null=no,"

#--enable-passwd       enable passwd backend no|yes|mod no
PACKAGECONFIG[passwd] = "--enable-passwd=mod,--enable-passwd=no,"

#--enable-perl         enable perl backend no|yes|mod no
#  This requires a loadable perl dynamic library, if enabled without
#  doing something appropriate (building perl?) the build will pick
#  up the build machine perl - not good (inherit perlnative?)
PACKAGECONFIG[perl] = "--enable-perl=mod,--enable-perl=no,perl"

#--enable-relay        enable relay backend no|yes|mod [yes]
PACKAGECONFIG[relay] = "--enable-relay=mod,--enable-relay=no,"

#--enable-shell        enable shell backend no|yes|mod no
# configure: WARNING: Use of --without-threads is recommended with back-shell
PACKAGECONFIG[shell] = "--enable-shell=mod --without-threads,--enable-shell=no,"

#--enable-sock         enable sock backend no|yes|mod [no]
PACKAGECONFIG[sock] = "--enable-sock=mod,--enable-sock=no,"

#--enable-sql          enable sql backend no|yes|mod no
# sql requires some sql backend which provides sql.h, sqlite* provides
# sqlite.h (which may be compatible but hasn't been tried.)
PACKAGECONFIG[sql] = "--enable-sql=mod,--enable-sql=no,sqlite3"

#--enable-dyngroup     Dynamic Group overlay no|yes|mod no
#  This is a demo, Proxy Cache defines init_module which conflicts with the
#  same symbol in dyngroup
PACKAGECONFIG[dyngroup] = "--enable-dyngroup=mod,--enable-dyngroup=no,"

#--enable-proxycache   Proxy Cache overlay no|yes|mod no
PACKAGECONFIG[proxycache] = "--enable-proxycache=mod,--enable-proxycache=no,"

#--enable-collect
PACKAGECONFIG[collect] = "--enable-collect=mod,--enable-collect=no,"

#--enable-constraint 
PACKAGECONFIG[constraint] = "--enable-constraint=mod,--enable-constraint=no,"

#--enable-dds  
PACKAGECONFIG[dds] = "--enable-dds=mod,--enable-dds=no,"

#--enable-deref
PACKAGECONFIG[deref] = "--enable-deref=mod,--enable-deref=no,"

#--enable-dynlist
PACKAGECONFIG[dynlist] = "--enable-dynlist=mod,--enable-dynlist=no,"

#--enable-memberof
PACKAGECONFIG[memberof] = "--enable-memberof=mod,--enable-memberof=no,"

#--enable-ppolicy
PACKAGECONFIG[ppolicy] = "--enable-ppolicy=mod,--enable-ppolicy=no,"

#--enable-refint
PACKAGECONFIG[refint] = "--enable-refint=mod,--enable-refint=no,"

#--enable-retcode  
PACKAGECONFIG[retcode] = "--enable-retcode=mod,--enable-retcode=no,"

#--enable-rwm 
PACKAGECONFIG[rwm] = "--enable-rwm=mod,--enable-rwm=no,"

#--enable-seqmod  
PACKAGECONFIG[seqmod] = "--enable-seqmod=mod,--enable-seqmod=no,"

#--enable-sssvlv  
PACKAGECONFIG[sssvlv] = "--enable-sssvlv=mod,--enable-sssvlv=no,"

#--enable-accesslog
PACKAGECONFIG[accesslog] = "--enable-accesslog=mod,--enable-accesslog=no,"

#--enable-auditlog
PACKAGECONFIG[auditlog] = "--enable-auditlog=mod,--enable-auditlog=no,"

#--enable-syncprov
PACKAGECONFIG[syncprov] = "--enable-syncprov=mod,--enable-syncprov=no,"

#--enable-translucent  
PACKAGECONFIG[translucent] = "--enable-translucent=mod,--enable-translucent=no,"

#--enable-unique
PACKAGECONFIG[unique] = "--enable-unique=mod,--enable-unique=no,"

#--enable-valsort
PACKAGECONFIG[valsort] = "--enable-valsort=mod,--enable-valsort=no,"

#--enable-slapi
PACKAGECONFIG[slapi] = "--enable-slapi=yes,--enable-slapi=no,"

#remove host include and library paths
do_compile_prepend() {
	sed -i -e "s:-L\/usr\/local\/lib::g" ${S}/servers/slapd/back-perl/Makefile
	sed -i -e "s:-I\/usr\/local\/include::g" ${S}/servers/slapd/back-perl/Makefile
}

do_compile_append () {
	oe_runmake -C ${S}/contrib/slapd-modules/lastbind
	oe_runmake 'LDAP_INC=-I${S}/include -I${STAGING_INCDIR} -I${S}/servers/slapd' \
		-C ${S}/contrib/slapd-modules/smbk5pwd
	oe_runmake -C ${S}/contrib/slapd-modules/autogroup
	oe_runmake -C ${S}/contrib/slapd-modules/passwd/sha2
}

do_install_append_class-target() {
	oe_runmake -C ${S}/contrib/slapd-modules/lastbind install DESTDIR=${D}
	oe_runmake -C ${S}/contrib/slapd-modules/smbk5pwd install DESTDIR=${D}
	oe_runmake -C ${S}/contrib/slapd-modules/autogroup install DESTDIR=${D}
	oe_runmake -C ${S}/contrib/slapd-modules/passwd/sha2 install DESTDIR=${D}
	rm ${D}${libdir}/ldap/*.a
}

do_install_append_class-native() {
	oe_runmake -C ${S}/contrib/slapd-modules/lastbind install DESTDIR=${D}${STAGING_DIR_NATIVE}
	oe_runmake -C ${S}/contrib/slapd-modules/smbk5pwd install DESTDIR=${D}${STAGING_DIR_NATIVE}
	oe_runmake -C ${S}/contrib/slapd-modules/autogroup install DESTDIR=${D}${STAGING_DIR_NATIVE}
	oe_runmake -C ${S}/contrib/slapd-modules/passwd/sha2 install DESTDIR=${D}${STAGING_DIR_NATIVE}
	rm ${D}${libdir}/ldap/*.a
}
#install follow debian jessie
do_install_append() {
	install -d ${D}${libdir}/ldap
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/default
	install -d ${D}${datadir}/slapd
	mv ${D}${sysconfdir}/openldap ${D}${sysconfdir}/ldap	
	
	#remove the files conflict with libldap and unwanted files
	rm ${D}${libdir}/liblber*.so* ${D}${libdir}/libldap*.so* \
		${D}${sysconfdir}/ldap/ldap.conf* ${D}${sysconfdir}/ldap/slapd.* 
		
	rm -r ${D}${includedir}
	rm ${D}${localstatedir}/lib/ldap/*
	rm ${D}${libdir}/libslapi.la ${D}${libdir}/libslapi.a ${D}${libdir}/libslapi.so
	rm ${D}${sysconfdir}/ldap/DB_CONFIG.example
	rm ${D}${libdir}/libldap_r.la ${D}${libdir}/liblber.la ${D}${libdir}/libldap.la
	
	install -m 0644 ${S}/debian/schema/*.schema ${D}${sysconfdir}/ldap/schema/
	install -m 0644 ${S}/debian/schema/*.ldif ${D}${sysconfdir}/ldap/schema/

	install -m 0755 ${S}/debian/slapd.init ${D}${sysconfdir}/init.d/slapd
	install -m 0644 ${S}/debian/slapd.default ${D}${sysconfdir}/default/slapd
	
	install -m 0644 ${S}/debian/DB_CONFIG ${D}${datadir}/slapd/
	install -m 0644 ${S}/debian/slapd.conf ${D}${datadir}/slapd/
	install -m 0644 ${S}/debian/slapd.init.ldif ${D}${datadir}/slapd/
	install -m 0755 ${S}/debian/ldiftopasswd ${D}${datadir}/slapd/

	mv ${D}${libexecdir}/slapd ${D}/${sbindir}/slapd
	
	SLAPTOOLS="slapadd slapcat slapdn slapindex slappasswd slaptest slapauth slapacl slapschema"
	cd ${D}/${sbindir}/
	rm -f ${SLAPTOOLS}
	for i in ${SLAPTOOLS}; do ln -sf slapd $i; done
	
	rmdir "${D}${localstatedir}/run"
	mv ${D}${libdir}/openldap/openldap/* ${D}${libdir}/ldap
	rm -rf ${D}${libdir}/openldap
	
	#Correct the permission of files follow Debian jessie
	chmod 0644 ${D}${libdir}/ldap/*.la
	for f in ${D}${libdir}/ldap/*.so; do
		LINKLIB=$(basename $(readlink $f))
		chmod 0644 ${D}${libdir}/ldap/${LINKLIB}
	done
}

INITSCRIPT_PACKAGES = "slapd"
INITSCRIPT_NAME_slapd = "openldap"
INITSCRIPT_PARAMS_slapd = "defaults"
SYSTEMD_SERVICE_slapd = "hostapd.service"
SYSTEMD_AUTO_ENABLE_slapd ?= "disable"

# The executables go in a separate package.  This allows the
# installation of the libraries with no daemon support.
# Each module also has its own package - see above.
PACKAGES =+ "slapd-smbk5pwd slapd"

# Package contents - shift most standard contents to -bin
#FILES_${PN} = "${localstatedir}"
FILES_slapd = "\
	${sysconfdir}/init.d ${libexecdir}/slapd ${sbindir}/* \
	${sysconfdir}/ldap/schema \
	${systemd_unitdir}/system/* \
	${libdir}/ldap/*.so.* \
	${libdir}/ldap/*.la \
	${libdir}/libslapi-2.4.so.* \
	${datadir}/slapd/* \
	${sysconfdir}/default \
"
PKG_${PN}-dbg = "slapd-dbg"
PKG_${PN}-dev = "libldap2-dev"
PKG_${PN} = "ldap-utils"

#FILES_ldap-utils = "${bindir}"

FILES_${PN}-dev = "\
	${libdir}/lib*.so                               \
	${libdir}/*.la          ${libdir}/*.a           \
	${libexecdir}/ldap/*.a  ${libexecdir}/ldap/*.la \
	${libexecdir}/ldap/*.so ${libdir}/ldap/*.so     \
        "
FILES_slapd-smbk5pwd = "${libdir}/ldap/smbk5pwd.so.* ${libdir}/ldap/smbk5pwd.la"

FILES_${PN}-dbg += "${libdir}/ldap/.debug/* ${sbindir}/.debug/*"

BBCLASSEXTEND = "native"
