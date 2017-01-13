#
# base recipe:http://cgit.openembedded.org/meta-openembedded/\
#  tree/meta-oe/recipes-support/openldap/openldap_2.4.43.bb
# base branch: master
#

SUMMARY = "OpenLDAP Directory Service"
DESCRIPTION = "\
	OpenLDAP Software is an open source implementation of \
	the Lightweight Directory Access Protocol."
HOMEPAGE = "http://www.OpenLDAP.org/license.html"

INC_PR = "r0"
inherit debian-package
PV = "2.4.40+dfsg"
LICENSE = "OLDAP-2.8"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=f2bdbaa4f50199a00b6de2ca7ec1db05 \
                    file://LICENSE;md5=153d07ef052c4a37a8fac23bc6031972 \
"
DEPENDS += " util-linux groff-native"

#Declare Debian patch type
DEBIAN_PATCH_TYPE = "quilt"

inherit autotools-brokensep systemd toolchain-scripts

# CV SETTINGS
# Required to work round AC_FUNC_MEMCMP which gets the wrong answer
# when cross compiling (should be in site?)
EXTRA_OECONF += "ac_cv_func_memcmp_working=yes"

# CONFIG DEFINITIONS
# The following is necessary because it cannot be determined for a
# cross compile automagically.  Select should yield fine on all OE
# systems...
EXTRA_OECONF += "--with-yielding-select=yes"
# Shared libraries are nice...
EXTRA_OECONF += "--enable-dynamic"

# UNIX crypt(3) passwd support:
EXTRA_OECONF += "--enable-crypt"

# Append URANDOM_DEVICE='/dev/urandom' to CPPFLAGS:
# This allows tls to obtain random bits from /dev/urandom, by default
# it was disabled for cross-compiling.
CPPFLAGS_append = " -D_GNU_SOURCE -DURANDOM_DEVICE=\'/dev/urandom\'"

do_configure_prepend() {
	sed -i -e "s:##STAGING_INCDIR##:${STAGING_INCDIR}:g" ${S}/contrib/slapd-modules/lastbind/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/lastbind/Makefile
	sed -i -e "s:##STAGING_INCDIR##:${STAGING_INCDIR}:g" ${S}/contrib/slapd-modules/smbk5pwd/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/smbk5pwd/Makefile
	sed -i -e "s:##STAGING_INCDIR##:${STAGING_INCDIR}:g" ${S}/contrib/slapd-modules/autogroup/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/autogroup/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/acl/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/dupent/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/passwd/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/passwd/sha2/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/passwd/pbkdf2/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/kinit/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/allowed/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/denyop/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/dsaschema/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/trace/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/nssov/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/samba4/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/noopsrch/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/addpartial/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/smbk5pwd/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/allop/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/autogroup/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/proxyOld/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/comp_match/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/lastmod/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/cloak/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/nops/Makefile
}
do_configure() {
	cp ${STAGING_DATADIR_NATIVE}/libtool/config/ltmain.sh ${S}/build
	aclocal
	libtoolize --force --copy
	gnu-configize
	autoconf
	oe_runconf
}
