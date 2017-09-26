#
# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree/meta-oe/recipes-connectivity/krb5/krb5_1.12.2.bb?h=dizzy
#

SUMMARY = "A network authentication protocol"
DESCRIPTION = "Kerberos is a system for authenticating users and services on a network. \
 Kerberos is a trusted third-party service.  That means that there is a \
 third party (the Kerberos server) that is trusted by all the entities on \
 the network (users and services, usually called "principals"). \
 . \
 This is the MIT reference implementation of Kerberos V5. \
 . \
 This package contains the Kerberos key server (KDC).  The KDC manages all \
 authentication credentials for a Kerberos realm, holds the master keys \
 for the realm, and responds to authentication requests.  This package \
 should be installed on both master and slave KDCs."

HOMEPAGE = "http://web.mit.edu/Kerberos/"
SECTION = "console/network"

PR = "r5"

inherit debian-package
PV = "1.12.1+dfsg"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://../NOTICE;md5=450c80c6258ce03387bd09df37638ebc"

DEPENDS = "ncurses util-linux e2fsprogs e2fsprogs-native keyutils"

inherit autotools-brokensep binconfig perlnative systemd

SYSTEMD_PACKAGES = "${PN}-admin-server ${PN}-kdc"
SYSTEMD_SERVICE_${PN}-admin-server = "krb5-admin-server.service"
SYSTEMD_SERVICE_${PN}-kdc = "krb5-kdc.service"

S = "${DEBIAN_UNPACK_DIR}/src/"

PACKAGECONFIG ??= "openssl ldap verto"
PACKAGECONFIG[libedit] = "--with-libedit,--without-libedit,libedit"
PACKAGECONFIG[openssl] = "--with-pkinit-crypto-impl=openssl,,openssl"
PACKAGECONFIG[ldap] = "--with-ldap,--without-ldap,libldap"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
PACKAGECONFIG[verto] = "--with-system-verto,--without-system-verto,libverto"

EXTRA_OECONF += " \
    --localstatedir=${sysconfdir} \
    --with-system-et --with-system-ss \
    --disable-rpath --enable-shared --without-tcl \
"

CACHED_CONFIGUREVARS += " \
	krb5_cv_attr_constructor_destructor=yes ac_cv_func_regcomp=yes \
	ac_cv_printf_positional=yes ac_cv_file__etc_environment=yes \
	ac_cv_file__etc_TIMEZONE=no \
    "

CFLAGS_append = " -fPIC -DDESTRUCTOR_ATTR_WORKS=1 -I${STAGING_INCDIR}/et "
LDFLAGS_append = " -lpthread "

PACKAGES =+ " \
	${PN}-admin-server ${PN}-gss-samples ${PN}-kdc ${PN}-kdc-ldap \
	${PN}-locales ${PN}-multidev ${PN}-otp ${PN}-pkinit ${PN}-user \
	libgssapi-krb5 libgssrpc libk5crypto libkadm5clnt-mit \
	libkadm5srv-mit libkdb5 libkrad-dev libkrad libkrb5 libkrb5support \
    "

FILES_${PN}-admin-server = " \
	${sysconfdir}/init.d/krb5-admin-server \
	${base_libdir}/systemd/system/krb5-admin-server.service \
	${sbindir}/kadmin.local \
	${sbindir}/kadmind \
	${sbindir}/kprop \
	${sbindir}/krb5_newrealm \
    "

FILES_${PN}-gss-samples = " \
	${bindir}/gss-client \
	${bindir}/gss-server \
    "

FILES_${PN}-kdc = " \
	${sbindir}/kdb5_util \
	${sbindir}/kpropd \
	${sbindir}/kproplog \
	${sbindir}/krb5kdc \
	${sysconfdir}/krb5kdc \
	${sysconfdir}/init.d/krb5-kdc \
	${systemd_system_unitdir}/krb5-kdc.service \
	${libdir}/krb5/plugins/kdb/db2.so \
	${datadir}/krb5-kdc \
	${localstatedir}/lib/krb5kdc \
    "

FILES_${PN}-kdc-ldap = " \
	${sysconfdir}/insserv/overrides/krb5-kdc \
	${systemd_system_unitdir}/krb5-admin-server.service.d/slapd-before-kdc.conf \
	${systemd_system_unitdir}/krb5-kdc.service.d/slapd-before-kdc.conf \
	${libdir}/krb5/libkdb_ldap.so.1* \
	${libdir}/krb5/plugins/kdb/kldap.so \
	${sbindir}/kdb5_ldap_util \
    "

FILES_${PN}-multidev = " \
	${bindir}/krb5-config.mit \
	${includedir}/mit-krb5/* \
	${libdir}/mit-krb5/* \
	${libdir}/pkgconfig/mit-krb5*.pc \
	${libdir}/pkgconfig/mit-krb5/* \
    "

FILES_${PN}-otp = "${libdir}/krb5/plugins/preauth/otp.so"
FILES_${PN}-pkinit = "${libdir}/krb5/plugins/preauth/pkinit.so"
FILES_${PN}-user = " \
	${bindir}/k5srvutil \
	${bindir}/kadmin \
	${bindir}/kdestroy \
	${bindir}/kinit \
	${bindir}/klist \
	${bindir}/kpasswd \
	${bindir}/ksu \
	${bindir}/kswitch \
	${bindir}/ktutil \
	${bindir}/kvno \
    "

FILES_libgssapi-krb5 = " \
	${libdir}/libgssapi_krb5${SOLIBS} \
	${sysconfdir}/gss/mech.d \
"
FILES_libgssrpc = "${libdir}/libgssrpc${SOLIBS}"
FILES_libk5crypto = "${libdir}/libk5crypto${SOLIBS}"
FILES_libkadm5clnt-mit = "${libdir}/libkadm5clnt_mit${SOLIBS}"
FILES_libkadm5srv-mit = "${libdir}/libkadm5srv_mit${SOLIBS}"
FILES_libkdb5 = "${libdir}/libkdb5${SOLIBS}"
FILES_libkrb5 = " \
	${libdir}/libkrb5${SOLIBS} \
	${libdir}/krb5/plugins/krb5 \
"
FILES_libkrb5support = "${libdir}/libkrb5support${SOLIBS}"
FILES_libkrad = "${libdir}/libkrad${SOLIBS}"

FILES_libkrad-dev = " \
	${includedir}/krad.h \
	${libdir}/libkrad.so \
    "

FILES_${PN} += "${datadir}/gnats"
FILES_${PN}-doc += "${datadir}/examples"
FILES_${PN}-dbg += " \
	${libdir}/krb5/plugins/*/.debug \
	${libdir}/mit-krb5/.debug \
    "

DEBIANNAME_${PN}-dev = "libkrb5-dev"
DEBIANNAME_${PN}-dbg = "libkrb5-dbg"

do_configure() {
	gnu-configize --force
	autoreconf
	oe_runconf
}

do_install_append() {
	install -d ${D}${sysconfdir}/init.d/
	install -d ${D}${base_libdir}/systemd/system/
	install -m 0755 ${S}/../debian/krb5-admin-server.init ${D}${sysconfdir}/init.d/krb5-admin-server
	install -m 0644 ${S}/../debian/krb5-admin-server.service ${D}${base_libdir}/systemd/system/
	install -m 0755 ${S}/../debian/krb5_newrealm ${D}${sbindir}/

	mv ${D}${sbindir}/gss-server ${D}${bindir}/gss-server

	install -m 0755 ${S}/../debian/krb5-kdc.init ${D}${sysconfdir}/init.d/krb5-kdc
	install -m 0644 ${S}/../debian/krb5-kdc.service ${D}${systemd_system_unitdir}/

	install -d ${D}${datadir}/krb5-kdc ${D}${docdir}/krb5-kdc/examples
	install -m 0644 ${DEBIAN_UNPACK_DIR}/debian/kdc.conf \
	        ${D}${datadir}/krb5-kdc/kdc.conf.template
	ln -sf ${datadir}/krb5-kdc/kdc.conf.template \
	        ${D}${docdir}/krb5-kdc/examples/kdc.conf

	# install for krb5-kdc-ldap package
	install -d ${D}${sysconfdir}/insserv/overrides/
	install -m 0644 ${S}/../debian/krb5-kdc-ldap.insserv-override ${D}${sysconfdir}/insserv/overrides/krb5-kdc

	install -d ${D}${systemd_system_unitdir}/krb5-admin-server.service.d/
	install -m 0644 ${S}/../debian/slapd-before-kdc.conf ${D}${systemd_system_unitdir}/krb5-admin-server.service.d/

	install -d ${D}${systemd_system_unitdir}/krb5-kdc.service.d/
	install -m 0644 ${S}/../debian/slapd-before-kdc.conf ${D}${systemd_system_unitdir}/krb5-kdc.service.d/

	cp ${D}${libdir}/libkdb_ldap.so.1* ${D}${libdir}/krb5/

	# install for krb5-multidev package
	cp ${D}${bindir}/krb5-config ${D}${bindir}/krb5-config.mit
	install -d ${D}${includedir}/mit-krb5/
	install -d ${D}${includedir}/mit-krb5/gssapi/
	install -d ${D}${includedir}/mit-krb5/gssrpc/
	install -d ${D}${includedir}/mit-krb5/kadm5/
	install -d ${D}${includedir}/mit-krb5/krb5/
	install -d ${D}${libdir}/mit-krb5/
	install -d ${D}${libdir}/pkgconfig/mit-krb5/

	mv ${D}${includedir}/gssapi.h ${D}${includedir}/mit-krb5/
	mv ${D}${includedir}/krb5.h ${D}${includedir}/mit-krb5/
	mv ${D}${includedir}/gssapi/* ${D}${includedir}/mit-krb5/gssapi/
	mv ${D}${includedir}/gssrpc/* ${D}${includedir}/mit-krb5/gssrpc/
	mv ${D}${includedir}/kadm5/* ${D}${includedir}/mit-krb5/kadm5/
	mv ${D}${includedir}/krb5/* ${D}${includedir}/mit-krb5/krb5/

	mv ${D}${libdir}/libgssapi_krb5.so ${D}${libdir}/mit-krb5/
	mv ${D}${libdir}/libgssrpc.so ${D}${libdir}/mit-krb5/
	mv ${D}${libdir}/libk5crypto.so ${D}${libdir}/mit-krb5/
	mv ${D}${libdir}/libkadm5clnt.so ${D}${libdir}/mit-krb5/
	mv ${D}${libdir}/libkadm5clnt_mit.so ${D}${libdir}/mit-krb5/
	mv ${D}${libdir}/libkadm5srv.so ${D}${libdir}/mit-krb5/
	mv ${D}${libdir}/libkadm5srv_mit.so ${D}${libdir}/mit-krb5/
	mv ${D}${libdir}/libkdb5.so ${D}${libdir}/mit-krb5/
	mv ${D}${libdir}/libkrb5.so ${D}${libdir}/mit-krb5/
	mv ${D}${libdir}/libkrb5support.so ${D}${libdir}/mit-krb5/
	mv ${D}${libdir}/pkgconfig/gssrpc.pc ${D}${libdir}/pkgconfig/mit-krb5/
	mv ${D}${libdir}/pkgconfig/kadm-client.pc ${D}${libdir}/pkgconfig/mit-krb5/
	mv ${D}${libdir}/pkgconfig/kadm-server.pc ${D}${libdir}/pkgconfig/mit-krb5/
	mv ${D}${libdir}/pkgconfig/kdb.pc ${D}${libdir}/pkgconfig/mit-krb5/
	mv ${D}${libdir}/pkgconfig/krb5-gssapi.pc ${D}${libdir}/pkgconfig/mit-krb5/
	mv ${D}${libdir}/pkgconfig/krb5.pc ${D}${libdir}/pkgconfig/mit-krb5/

	rm ${D}${libdir}/mit-krb5/libkadm5clnt.so
	rm ${D}${libdir}/mit-krb5/libkadm5srv.so

	for f in ${D}${libdir}/mit-krb5/*.so; do
		LINKLIB=$(basename $(readlink $f))
		rm $f
		ln -s ../$LINKLIB $f
	done

	ln -s mit-krb5/gssapi.h ${D}${includedir}/gssapi.h
	ln -s mit-krb5/krb5.h ${D}${includedir}/krb5.h

	for f in ${D}${includedir}/mit-krb5/gssapi/*.h; do
		LINKLIB=$(basename $f)
		ln -s ../mit-krb5/gssapi/$LINKLIB ${D}${includedir}/gssapi/$LINKLIB
	done

	for f in ${D}${includedir}/mit-krb5/gssrpc/*.h; do
		LINKLIB=$(basename $f)
		ln -s ../mit-krb5/gssrpc/$LINKLIB ${D}${includedir}/gssrpc/$LINKLIB
	done

	for f in ${D}${includedir}/mit-krb5/kadm5/*.h; do
		LINKLIB=$(basename $f)
		ln -s ../mit-krb5/kadm5/$LINKLIB ${D}${includedir}/kadm5/$LINKLIB
	done

	for f in ${D}${includedir}/mit-krb5/krb5/*.h; do
		LINKLIB=$(basename $f)
		ln -s ../mit-krb5/krb5/$LINKLIB ${D}${includedir}/krb5/$LINKLIB
	done

	ln -s mit-krb5/libgssapi_krb5.so ${D}${libdir}/libgssapi_krb5.so
	ln -s mit-krb5/libgssrpc.so ${D}${libdir}/libgssrpc.so
	ln -s mit-krb5/libk5crypto.so ${D}${libdir}/libk5crypto.so
	ln -s mit-krb5/libkadm5clnt.so ${D}${libdir}/libkadm5clnt.so
	ln -s mit-krb5/libkadm5clnt_mit.so ${D}${libdir}/libkadm5clnt_mit.so
	ln -s mit-krb5/libkadm5srv.so ${D}${libdir}/libkadm5srv.so
	ln -s mit-krb5/libkadm5srv_mit.so ${D}${libdir}/libkadm5srv_mit.so
	ln -s mit-krb5/libkdb5.so ${D}${libdir}/libkdb5.so
	ln -s mit-krb5/libkrb5.so ${D}${libdir}/libkrb5.so
	ln -s mit-krb5/libkrb5support.so  ${D}${libdir}/libkrb5support.so
	ln -s mit-krb5/gssrpc.pc ${D}${libdir}/pkgconfig/gssrpc.pc
	ln -s mit-krb5/kadm-client.pc ${D}${libdir}/pkgconfig/kadm-client.pc
	ln -s mit-krb5/kadm-server.pc ${D}${libdir}/pkgconfig/kadm-server.pc
	ln -s mit-krb5/kdb.pc ${D}${libdir}/pkgconfig/kdb.pc
	ln -s mit-krb5/krb5-gssapi.pc ${D}${libdir}/pkgconfig/krb5-gssapi.pc
	ln -s mit-krb5/krb5.pc ${D}${libdir}/pkgconfig/krb5.pc

	ln -s libkadm5clnt_mit.so ${D}${libdir}/mit-krb5/libkadm5clnt.so
	ln -s libkadm5srv_mit.so ${D}${libdir}/mit-krb5/libkadm5srv.so

	# According to debian/krb5-kdc.dirs.in
	install -d ${D}${localstatedir}/lib/krb5kdc \
	           ${D}${sysconfdir}/krb5kdc
	# According to debian/libgssapi-krb5-2.dirs
	install -d ${D}${sysconfdir}/gss/mech.d
	# According to debian/libkrb5-3.dirs.in
	install -d ${D}${libdir}/krb5/plugins/krb5

	chmod 700 ${D}${localstatedir}/lib/krb5kdc
	chmod 700 ${D}${sysconfdir}/krb5kdc
}

# Base on debian/krb5-kdc.postinst
pkg_postinst_${PN}-kdc () {
    DEFAULT_REALM="EXAMPLE.COM"
    # Try to get default realm from /etc/krb5.conf
    if [ -f $D${sysconfdir}/krb5.conf ]; then
        DEFAULT_REALM=$(grep "^\s*default_realm\s*=" $D${sysconfdir}/krb5.conf | cut -d= -f2 | xargs)
    fi

    if [ ! -f "$D${sysconfdir}/krb5kdc/kdc.conf" ]; then
        sed -e "s/@MYREALM/$DEFAULT_REALM/" \
            $D${datadir}/krb5-kdc/kdc.conf.template > $D${sysconfdir}/krb5kdc/kdc.conf
    fi

    if [ ! -d "$D${sysconfdir}/default" ]; then
        mkdir $D${sysconfdir}/default
    fi

    if [ -f "$D${sysconfdir}/default/krb5-kdc" ] ; then
            . $D${sysconfdir}/default/krb5-kdc
    fi
    cat <<'EOF' > $D${sysconfdir}/default/krb5-kdc

# Automatically generated.  Only the value of DAEMON_ARGS will be preserved.
# If you change anything in this file other than DAEMON_ARGS, first run
# dpkg-reconfigure krb5-kdc and disable managing the KDC configuration with
# debconf.  Otherwise, changes will be overwritten.

EOF
    if [ -n "$DAEMON_ARGS" ] ; then
        echo "DAEMON_ARGS=\"$DAEMON_ARGS\"" >> $D${sysconfdir}/default/krb5-kdc
    fi
}

# Base on debian/krb5-admin-server.postinst
pkg_postinst_${PN}-admin-server () {
    if [ -f "$D${sysconfdir}/default/krb5-admin-server" ] ; then
        . $D${sysconfdir}/default/krb5-admin-server
    fi
    cat <<'EOF' > $D${sysconfdir}/default/krb5-admin-server
# Automatically generated.  If you change anything in this file other than the
# values of  DAEMON_ARGS, first run dpkg-reconfigure
# krb5-admin-server and disable managing the kadmin configuration with
# debconf.  Otherwise, changes will be overwritten.

EOF
    if [ -n "$DAEMON_ARGS" ] ; then
        echo "DAEMON_ARGS=\"$DAEMON_ARGS\"" \
            >> $D${sysconfdir}/default/krb5-admin-server
    fi
}

RCONFLICTS_${PN}-dev = "hemidal-dev"
RREPLACES_${PN}-dev = "hemidal-dev"

RDEPENDS_${PN}-user += "libkrb5 krb5-config"
RDEPENDS_${PN}-kdc += "libkrb5 libkadm5srv-mit ${PN}-user krb5-config"
RDEPENDS_${PN}-kdc-ldap += "${PN}-kdc"
RDEPENDS_${PN}-admin-server += "libkrb5 ${PN}-kdc"
RDEPENDS_${PN}-pkinit += "libkrb5"
RDEPENDS_${PN}-otp += "libkrad"
RDEPENDS_libkrb5 += "libkrb5support"
RDEPENDS_libgssapi-krb5 += "libkrb5"
RDEPENDS_libkrad += "libkrb5"
RDEPENDS_libkrb5support += "libkeyutils"

# lsb-base is required for init scripts
RDEPENDS_${PN}-admin-server += "lsb-base"
RDEPENDS_${PN}-kdc += "lsb-base"

INSANE_SKIP_${PN}-multidev = "dev-so"

PARALLEL_MAKE = ""
