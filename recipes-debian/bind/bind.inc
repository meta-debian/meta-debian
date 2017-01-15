#
# base recipe: /meta/recipes-connectivity/bind/bind_9.10.2-P4.bb
# base branch: master
#

SUMMARY = "ISC Internet Domain Name Server"
HOMEPAGE = "http://www.isc.org/sw/bind/"

INC_PR = "r0"
DPN = "bind9"
inherit debian-package
PV = "9.9.5.dfsg"

LICENSE = "ISC & BSD"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=a3df5f651469919a0e6cb42f84fb6ff1"

DEPENDS = "openssl libcap krb5"
DEBIAN_PATCH_TYPE = "nopatch"

# fix-configure-for-gssapi.patch 
#	fix path to gssapi.h file

SRC_URI += " \
	file://fix-configure-for-gssapi.patch \
"

inherit autotools-brokensep systemd useradd pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[httpstats] = "--with-libxml2,--without-libxml2,libxml2"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --home /var/cache/bind --no-create-home \
                       --user-group bind"

INITSCRIPT_NAME = "bind"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE_${PN} = "bind9-resolvconf.service bind9.service lwresd.service"

PARALLEL_MAKE = ""

RDEPENDS_${PN} = "python-core"
RDEPENDS_${PN}-dev = ""

do_install_prepend() {
	# clean host path in isc-config.sh before the hardlink created
	# by "make install":
	#   bind9-config -> isc-config.sh
	sed -i -e "s,${STAGING_LIBDIR},${libdir}," ${B}/isc-config.sh
}
