DESCRIPTION = "PHP PEAR modules for creating an authentication system"

PR = "r0"

inherit debian-package

LICENSE = "PHP-3.1"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=02f173e28791fe9054ea54631abf4dc3"

do_install() {
	install -d ${D}${datadir}/php/Auth/Container
	install -d ${D}${datadir}/php/Auth/Frontend
	install -m 644 ${S}/Auth-*/Auth.php ${D}${datadir}/php/
	install -m 644 ${S}/Auth-*/Auth/*.php ${D}${datadir}/php/Auth
	install -m 644 ${S}/Auth-*/Auth/Container/* ${D}${datadir}/php/Auth/Container
	install -m 644 ${S}/Auth-*/Auth/Frontend/* ${D}${datadir}/php/Auth/Frontend
}

FILES_${PN}="${datadir}"

