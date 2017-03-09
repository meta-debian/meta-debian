SUMMARY = "Configuration files for Kerberos Version 5"

inherit debian-package
PV = "2.3"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=b83a9a12562a4ee4582a554747a6c49a"

SRC_URI += " \
    ${@base_conditional('CONFIG_NEW_REALM','1','file://add-new-realm-to-krb5_conf.patch','',d)} \
"

CONFIG_NEW_REALM ??= "1"
DEFAULT_REALM ??= "EXAMPLE.COM"
KERBEROS_SERVER ??= "localhost"
ADMIN_SERVER ??= "localhost"

do_install() {
	install -d ${D}${datadir}/kerberos-configs/ ${D}${sysconfdir}
	install ${S}/krb5.conf.template ${D}${datadir}/kerberos-configs/
	cp ${D}${datadir}/kerberos-configs/krb5.conf.template ${D}${sysconfdir}/krb5.conf

	if [ -n ${DEFAULT_REALM} ]; then
		sed -i -e "s:^\(\s*default_realm\s*=\).*:\1 ${DEFAULT_REALM}:g" ${D}${sysconfdir}/krb5.conf
	fi
	if [ "${CONFIG_NEW_REALM}" = "1" ]; then
		sed -i -e \
		   "s:##NEW_REALM##:\t${DEFAULT_REALM} = {\n\t\tkdc = ${KERBEROS_SERVER}\n\t\tadmin_server = ${ADMIN_SERVER}\n\t}:g" \
		   ${D}${sysconfdir}/krb5.conf
	fi
}

PACKAGES = "krb5-config"
FILES_krb5-config = " \
    ${datadir}/kerberos-configs/* \
    ${sysconfdir}/* \
"

RDEPENDS_krb5-config += "bind9-host"
