SUMMARY = "High-performance mail transport agent"
DESCRIPTION = "\
Postfix is Wietse Venema's mail transport agent that started life as an \
alternative to the widely-used Sendmail program. Postfix attempts to be fast, \
easy to administer, and secure, while at the same time being sendmail compatible \
enough to not upset existing users. Thus, the outside has a sendmail-ish flavor, \
but the inside is completely different. \
"
HOMEPAGE = "http://www.postfix.org"

INC_PR = "r0"
inherit debian-package autotools-brokensep
PV = "2.11.3"

LICENSE = "IPL-1.0"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=64375f37431336ea1b1b3005fe3fa354"
DEBIAN_PATCH_TYPE = "nopatch"

# Correct-cross-compiler_debian.patch:
#       using cross_compile $(CC), instead of 'gcc' in Makefiles
#
# Add-library-to-LIBS_debian.patch:
#       Add the library ${B}/lib/libdns.a to LIBS variable to avoid error \
#       "undefined reference" to some variable
#
# fix-postfix-files-install_debian.patch:
#       error install when run postfix-install script, \
#       the dict_*.so files are not in ${B}/libexec, \
#       these files will install by command in do_install function.
SRC_URI += "\
	file://Correct-cross-compiler_debian.patch \
	file://Add-library-to-LIBS_debian.patch \
	file://fix-postfix-files-install_debian.patch"

export LIB_VER = "1.0.1"

EXTRA_OEMAKE += "OPT= DEBUG= OPTS= "
do_compile() {
	oe_runmake makefiles 
	cd lib && for i in dns global master tls util ; do \
		ln -sf lib${i}.a libpostfix-${i}.so.${LIB_VER}; \
		ln -sf libpostfix-${i}.so.${LIB_VER} libpostfix-${i}.so.${LIB_VER%.*.*}; \
	done
	cd -
	oe_runmake
}
do_install() {
	sh ./postfix-install 'install_root=${D}' \
                'config_directory=${sysconfdir}/${DPN}' \
                'daemon_directory=${libexecdir}' \
                'command_directory=${sbindir}' \
                'queue_directory=${localstatedir}/spool/${DPN}' \
                'sendmail_path=${sbindir}/sendmail' \
                'newaliases_path=${bindir}/newaliases' \
                'mailq_path=${bindir}/mailq' \
                'manpage_directory=${mandir}' \
                'readme_directory=${docdir}/${DPN}' \
                'data_directory=${localstatedir}/lib/${DPN}' \
                -non-interactive

	#follow debian/rules
	install -d ${D}${includedir}/${DPN}
	install ${S}/lib/*.so.* ${D}${libdir}
	install -D ${S}/lib/dict_*.so ${D}${libdir}/${DPN}
	for i in ${D}${libdir}/*.${LIB_VER}; do
		ln -sf ${i##*/} ${i%so.*}so
	done
	install ${S}/debian/postfix-add-filter ${D}${sbindir}
	install ${S}/debian/postfix-add-policy ${D}${sbindir}
	install ${S}/auxiliary/qshape/qshape.pl ${D}${sbindir}/qshape
	install bin/[a-z]* ${D}${sbindir}
	install libexec/[a-z]* ${D}${libdir}/${DPN}
	ln -s ../sbin/rmail ${D}${bindir}/rmail
	ln -s ../sbin/sendmail ${D}${libdir}/sendmail

	install -D -m 0644 ${S}/conf/master.cf ${D}${datadir}/${DPN}/master.cf.dist
	install -m 0644 ${S}/conf/main.cf ${D}${datadir}/${DPN}/main.cf.dist
	install -m 0644 ${S}/debian/main.cf.in ${D}${datadir}/${DPN}/main.cf.debian
	sed -i -e "s:@@DISTRO@@:${DISTRO}:g" ${D}${datadir}/${DPN}/main.cf.debian
	sed -i -e "s:@@DISTRO@@:${DISTRO}:g" ${D}${datadir}/${DPN}/main.cf.dist
	install -m 0644 ${S}/conf/main.cf.tls ${D}${datadir}/${DPN}/main.cf.tls
	install -m 0644 ${S}/debian/functions \
		${D}${datadir}/${DPN}/postinst.functions
	install -m 0644 ${S}/include/* ${D}${includedir}/${DPN}

	install ${S}/debian/postfix_groups.pl ${D}${libdir}

	rm ${D}${sysconfdir}/${DPN}/* ${D}${libdir}/${DPN}/nqmgr
	install -m 0755 ${S}/conf/postfix-script conf/post-install \
		${D}${sysconfdir}/${DPN}
	install -m 0644 ${S}/conf/postfix-files ${D}${sysconfdir}/${DPN}

	install -D -m 0644 ${S}/debian/postfix.ufw.profile \
		${D}${sysconfdir}/ufw/applications.d/postfix
	install -D -m 0644 ${S}/debian/rsyslog.conf \
		${D}${sysconfdir}/rsyslog.d/postfix.conf
	install -D -m 0644 ${S}/debian/postfix.insserv.conf \
		${D}${sysconfdir}/insserv.conf.d/postfix

	install -D ${S}/debian/init.d ${D}${sysconfdir}/init.d/postfix
	install -D ${S}/debian/ip-up.d ${D}${sysconfdir}/ppp/ip-up.d/postfix
	install -D ${S}/debian/ip-down.d ${D}${sysconfdir}/ppp/ip-down.d/postfix
	install -D ${S}/debian/ip-up.d ${D}${sysconfdir}/network/if-up.d/postfix
	install -D ${S}/debian/ip-down.d \
		${D}${sysconfdir}/network/if-down.d/postfix
	install -D ${S}/debian/update-libc.d \
		${D}${sysconfdir}/resolvconf/update-libc.d/postfix
}
PARALLEL_MAKE = ""
