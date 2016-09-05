require postfix.inc
PR = "${INC_PR}.0"
DEPENDS += "db tinycdb postgresql cpio libsasl2 sqlite3 \
	lsb mysql libpcre postfix-native"

export CCARGS = "-DDEBIAN -DMAX_DYNAMIC_MAPS -DHAS_PCRE -DHAS_LDAP \
                 -DUSE_LDAP_SASL -DHAS_SQLITE -DMYORIGIN_FROM_FILE \
                 -DHAS_CDB \
                 -DHAS_MYSQL -I${STAGING_INCDIR}/mysql \
                 -DHAS_PGSQL -I${STAGING_INCDIR}/postgresql \
                 -DHAS_SQLITE -I${STAGING_INCDIR} \
                 -DHAS_SSL -I${STAGING_INCDIR}/openssl \
                 -DUSE_SASL_AUTH -I${STAGING_INCDIR}/sasl \
                 -DUSE_CYRUS_SASL \
                 -DUSE_TLS"
export AUXLIBS = "-lssl -lcrypto -lsasl2 -lpthread"
export POSTCONF = "${STAGING_SBINDIR_NATIVE}/postconf"
export SYSLIBS = "${LDFLAGS}"

do_configure_prepend() {
	sed -i -e "s:f \/usr\/include:f ${STAGING_INCDIR}:g" ${S}/makedefs
	sed -i -e "s:bin\/postconf:${STAGING_SBINDIR_NATIVE}/postconf:g" \
		${S}/postfix-install
}
do_install_prepend() {
	export LD_LIBRARY_PATH=${STAGING_LIBDIR_NATIVE}:$LD_LIBRARY_PATH
}

PACKAGES =+ "${PN}-cdb ${PN}-ldap ${PN}-mysql ${PN}-pcre ${PN}-pgsql"
FILES_${PN}-cdb = "${libdir}/${DPN}/dict_cdb.so"
FILES_${PN}-ldap = "${libdir}/${DPN}/dict_ldap.so"
FILES_${PN}-mysql = "${libdir}/${DPN}/dict_mysql.so"
FILES_${PN}-pcre = "${libdir}/${DPN}/dict_pcre.so"
FILES_${PN}-pgsql = "${libdir}/${DPN}/dict_pgsql.so"
FILES_${PN} += "${libdir}/sendmail ${libdir}/postfix_groups.pl"

RDEPENDS_${PN} += "netbase lsb-base cpio"
RDEPENDS_${PN}-ldap += "${PN}"
RDEPENDS_${PN}-cdb += "${PN}"
RDEPENDS_${PN}-pcre += "${PN}"
RDEPENDS_${PN}-mysql += "${PN}"
RDEPENDS_${PN}-pgsql += "${PN}"
