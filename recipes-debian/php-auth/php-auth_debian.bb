DESCRIPTION = "PHP PEAR modules for creating an authentication system"

PR = "r0"

inherit debian-package
PV = "1.6.4"

LICENSE = "PHP-3.0 & BSD-3-Clause"
LIC_FILES_CHKSUM = " \
    file://Auth-1.6.4/Auth.php;beginline=9;endline=13;md5=6863d257d87ab97c7e821ca3fe49a316 \
    file://Auth-1.6.4/Auth/Frontend/md5.js;endline=8;md5=915fa4679c019c47741472ce7ce6afd1 \
"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

do_install() {
	install -d ${D}${datadir}/php/Auth/Container
	install -d ${D}${datadir}/php/Auth/Frontend
	install -m 644 ${S}/Auth-*/Auth.php ${D}${datadir}/php/
	install -m 644 ${S}/Auth-*/Auth/*.php ${D}${datadir}/php/Auth
	install -m 644 ${S}/Auth-*/Auth/Container/* ${D}${datadir}/php/Auth/Container
	install -m 644 ${S}/Auth-*/Auth/Frontend/* ${D}${datadir}/php/Auth/Frontend
}

FILES_${PN}="${datadir}"

