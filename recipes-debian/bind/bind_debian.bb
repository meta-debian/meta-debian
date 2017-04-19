require bind.inc

PR = "${INC_PR}.2"
DEPENDS += " bind-native"

# correct-path-to-gen-file.patch
#	use gen file in sysroots when cross-compile, not build directory because gen file is a binary file	

# correct-path-to-genrandom-file.patch
#       use genrandom file in sysroots when cross-compile, not build directory because genrandom file is a binary file

SRC_URI += " \
	file://correct-path-to-gen-file.patch \
	file://correct-path-to-genrandom-file.patch \
"

EXTRA_OECONF = " \
	--sysconfdir=${sysconfdir}/bind \
	--enable-threads \
	--enable-largefile \
	--with-libtool \
	--enable-shared \
	--enable-static \
	--with-openssl=${STAGING_LIBDIR}/.. \
	--with-gssapi=${STAGING_LIBDIR}/../ \
	--with-gnu-ld \
	--with-dlz-postgres=no \
	--with-dlz-mysql=no \
	--with-dlz-bdb=no \
	--with-dlz-filesystem=yes \
	--with-dlz-stub=yes \
	--with-geoip=no \
	--enable-ipv6 \
	--disable-linux-caps \
	--disable-threads \
	--with-ecdsa=yes \
	--with-gost=no \
	--with-randomdev=/dev/random \
	--with-dlz-odbc=no \
	--with-dlz-ldap=${STAGING_LIBDIR}/../ \
"

# Split building exportable library base on debian/rules
do_configure_append() {
	# Base on debian/rules
	olddir=`pwd`
	test -e ${B}/export || mkdir ${B}/export && cp -r ${S}/* ${B}/export || true
	cd ${B}/export
	oe_runconf --disable-epoll \
	           --disable-kqueue \
	           --disable-devpoll \
	           --disable-threads \
	           --disable-linux-caps \
	           --without-openssl \
	           --without-libxml2 \
	           --enable-ipv6 \
	           --enable-shared \
	           --enable-exportlib \
	           --with-libtool \
	           --with-gssapi=no \
	           --with-export-libdir=${libdir} \
	           --with-export-includedir=${includedir}/bind-export
	cd $olddir
}

do_compile_prepend() {
	oe_runmake -C ${B}/export
}

do_install_prepend() {
	oe_runmake -C ${B}/export install DESTDIR=${D}
}

# Install files base on debian/rules
do_install_append () {
	find ${D} -name *.la -execdir rm -f {} \;

	if [ "${libdir}" != "${base_libdir}" ]; then
		test -d ${D}${base_libdir} || install -d ${D}${base_libdir}
		mv ${D}${libdir}/*-export.so.* ${D}${base_libdir}/

		rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
		cd ${D}${libdir} && for link in *-export.so; do
			file=$(basename $(readlink $link))
			ln -sf $rel_lib_prefix${base_libdir}/$file $link
		done
	fi

	# Base on debian/bind9.dirs
	install -d ${D}${sysconfdir}/ufw/applications.d \
	           ${D}${sysconfdir}/apparmor.d/force-complain \
	           ${D}${sysconfdir}/apparmor.d/local \
	           ${D}${localstatedir}/cache/bind \
	           ${D}${datadir}/${DPN} \
	           ${D}${sysconfdir}/ppp/ip-up.d \
	           ${D}${sysconfdir}/ppp/ip-down.d \
	           ${D}${sysconfdir}/network/if-up.d \
	           ${D}${sysconfdir}/network/if-down.d

	ETCBIND=${D}${sysconfdir}/bind
	ETCAPP=${D}${sysconfdir}/apparmor.d

	install -c -o bin -g bin -m 444 ${S}/debian/db.0 ${ETCBIND}/db.0
	install -c -o bin -g bin -m 444 ${S}/debian/db.0 ${ETCBIND}/db.255
	install -c -o bin -g bin -m 444 ${S}/debian/db.empty ${ETCBIND}
	install -c -o bin -g bin -m 444 ${S}/debian/zones.rfc1918 ${ETCBIND}
	install -c -o bin -g bin -m 444 ${S}/debian/db.127 ${ETCBIND}
	install -c -o bin -g bin -m 444 ${S}/debian/db.local ${ETCBIND}
	install -c -o bin -g bin -m 444 ${S}/debian/db.root ${ETCBIND}
	install -c -o bin -g bin -m 440 ${S}/debian/named.conf ${ETCBIND}
	install -c -o bin -g bin -m 440 ${S}/debian/named.conf.local ${ETCBIND}
	install -c -o bin -g bin -m 440 ${S}/debian/named.conf.default-zones ${ETCBIND}
	install -c -o bin -g bin -m 440 ${S}/bind.keys ${ETCBIND}

	install -c -o bin -g bin -m 440 ${S}/debian/named.conf.options ${D}${datadir}/${DPN}/

	install -m 644 -o root -g root ${S}/debian/apparmor-profile ${ETCAPP}/usr.sbin.named
	install -m 644 -o root -g root ${S}/debian/apparmor-profile.local ${ETCAPP}/local/usr.sbin.named

	install ${S}/debian/ip-up.d ${D}${sysconfdir}/ppp/ip-up.d/bind9
	install ${S}/debian/ip-down.d ${D}${sysconfdir}/ppp/ip-down.d/bind9
	install ${S}/debian/ip-up.d ${D}${sysconfdir}/network/if-up.d/bind9
	install ${S}/debian/ip-down.d ${D}${sysconfdir}/network/if-down.d/bind9
	install -m644 ${S}/debian/bind9.ufw.profile ${D}${sysconfdir}/ufw/applications.d/bind9

	install -D -m 644 ${S}/debian/bind9.tmpfile ${D}${libdir}/tmpfiles.d/bind9.conf
	install -D -m 644 ${S}/debian/lwresd.tmpfile ${D}${libdir}/tmpfiles.d/lwresd.conf

	# Base on debian/libbind-dev.install
	install -m 0644 ${S}/lib/dns/include/dns/dlz_dlopen.h ${D}${includedir}/dns/

	# Install systemd service
	install -D -m 644 ${S}/bind9.service ${D}${systemd_system_unitdir}/bind9.service
	install -D -m 644 ${S}/bind9-resolvconf.service ${D}${systemd_system_unitdir}/bind9-resolvconf.service
	install -D -m 644 ${S}/debian/lwresd.service ${D}${systemd_system_unitdir}/lwresd.service

	# Install sysvinit init script
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/bind9.init ${D}${sysconfdir}/init.d/bind9
	install -m 0755 ${S}/debian/lwresd.init ${D}${sysconfdir}/init.d/lwresd		
}

# Base on debian/bind9.postinst
pkg_postinst_${PN}() {
	mkdir -p $D${localstatedir}/lib/bind
	chown root:bind $D${localstatedir}/lib/bind
	chmod 755 $D${localstatedir}/lib/bind

	if [ ! -s $D${sysconfdir}/bind/rndc.key ] && [ ! -s $D${sysconfdir}/bind/rndc.conf ]; then
		rndc-confgen -r /dev/urandom -a -c $D${sysconfdir}/bind/rndc.key
	fi

	if [ ! -f $D${sysconfdir}/bind/named.conf.options ]; then
		cp $D${datadir}/${DPN}/named.conf.options $D${sysconfdir}/bind/named.conf.options
		chmod 644 $D${sysconfdir}/bind/named.conf.options
	fi

	uid=$(ls -ln $D${sysconfdir}/bind/rndc.key | awk '{print $3}')
	if [ "$uid" = "0" ]; then
		chown bind $D${sysconfdir}/bind/rndc.key
		chgrp bind $D${sysconfdir}/bind
		chmod g+s $D${sysconfdir}/bind
		chgrp bind $D${sysconfdir}/bind/rndc.key $D${localstatedir}/cache/bind
		chgrp bind $D${sysconfdir}/bind/named.conf* || true
		chmod g+r $D${sysconfdir}/bind/rndc.key $D${sysconfdir}/bind/named.conf* || true
		chmod g+rwx $D${localstatedir}/cache/bind
	fi
}

CONFFILES_${PN} = " \
	${sysconfdir}/bind/named.conf \
	${sysconfdir}/bind/named.conf.local \
	${sysconfdir}/bind/named.conf.options \
	${sysconfdir}/bind/db.0 \
	${sysconfdir}/bind/db.127 \
	${sysconfdir}/bind/db.empty \
	${sysconfdir}/bind/db.local \
	${sysconfdir}/bind/db.root \
"

PACKAGES =+ "${DPN}-host ${DPN}utils dnsutils \
             lib${PN}-export-dev lib${DPN} libdns-export libdns \
             libirs-export libisc-export libisc libisccc \
             libisccfg-export libisccfg liblwres lwresd \
             "

FILES_${DPN}-host = "${bindir}/host"

FILES_${DPN}utils = " \
	${sbindir}/dnssec-checkds \
	${sbindir}/dnssec-coverage \
	${sbindir}/dnssec-dsfromkey \
	${sbindir}/dnssec-keyfromlabel \
	${sbindir}/dnssec-keygen \
	${sbindir}/dnssec-revoke \
	${sbindir}/dnssec-settime \
	${sbindir}/dnssec-signzone \
	${sbindir}/dnssec-verify \
	${sbindir}/named-checkconf \
	${sbindir}/named-checkzone \
	${sbindir}/named-compilezone \
	${sbindir}/rndc* \
"

FILES_dnsutils = " \
	${bindir}/dig \
	${bindir}/nslookup \
	${bindir}/nsupdate \
"

FILES_lib${PN}-export-dev = "${libdir}/lib*-export${SOLIBSDEV}"
FILES_lib${DPN} = "${libdir}/libbind9${SOLIBS}"
FILES_libdns-export = "${base_libdir}/libdns-export${SOLIBS}"
FILES_libdns = "${libdir}/libdns${SOLIBS}"
FILES_libirs-export = "${base_libdir}/libirs-export${SOLIBS}"
FILES_libisc-export = "${base_libdir}/libisc-export${SOLIBS}"
FILES_libisc = "${libdir}/libisc${SOLIBS}"
FILES_libisccc = "${libdir}/libisccc${SOLIBS}"
FILES_libisccfg-export = "${base_libdir}/libisccfg-export${SOLIBS}"
FILES_libisccfg = "${libdir}/libisccfg${SOLIBS}"
FILES_liblwres = "${libdir}/liblwres${SOLIBS}"

FILES_lwresd = " \
	${sysconfdir}/init.d/lwresd \
	${systemd_system_unitdir}/lwresd.service \
	${libdir}/tmpfiles.d/lwresd.conf \
	${sbindir}/lwresd \
"

FILES_${PN} += " \
	/run \
	${libdir}/tmpfiles.d/bind9.conf \
	${datadir}/${DPN} \
	${localstatedir} \
"
FILES_${PN}-dev += " \
	${bindir}/isc-config.sh \
"

# Package "bind" and "bind-dev" on Poky are equal to "bind9" and "libbind-dev" on Debian
DEBIANNAME_${PN} = "${DPN}"
DEBIANNAME_${PN}-dev = "lib${PN}-dev"
RPROVIDES_${PN} += "${DPN}"
RPROVIDES_${PN}-dev += "lib${PN}-dev"

# Package "host" is provided by "bind9-host" according to debian/control
RPROVIDES_${DPN}-host += "host"

# Dependencies between bind's packages base on debian/control
RDEPENDS_${PN} += "libdns libisccfg libisc libisccc ${DPN}utils liblwres lib${DPN}"
RDEPENDS_${DPN}-host += "libdns libisccfg libisc liblwres lib${DPN}"
RDEPENDS_${PN}-dev += "libdns libisccfg libisc liblwres lib${DPN}"
RDEPENDS_lib${DPN} += "libdns libisccfg libisc"
RDEPENDS_libdns += "libisc"
RDEPENDS_libisccc += "libisc"
RDEPENDS_libisccfg += "libdns libisccc libisc"
RDEPENDS_dnsutils += "libdns libisccfg libisc liblwres lib${DPN}"
RDEPENDS_lwresd += "libdns libisccfg libisccc libisc liblwres lib${DPN}"
RDEPENDS_lib${PN}-export-dev += "${DPN}-host libdns-export libisccfg-export libisc-export libirs-export"
