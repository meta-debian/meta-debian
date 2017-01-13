DESCRIPTION = "OpenHPI is an implementation of the Service Availability Forum's Hardware \
Platform Interface (HPI) specification.  (See http://www.saforum.org) \
As such, OpenHPI facilitates the development of computer manageability \
applications which are not tied to a single hardware vendor's products."
HOMEPAGE = "http://openhpi.org"

PR = "r0"

inherit debian-package
PV = "2.14.1"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=e3c772a32386888ccb5ae1c0ba95f1a4"

DEPENDS = "libxml2"

inherit autotools gettext pkgconfig

# There is no debian/source or debian/patches
DEBIAN_PATCH_TYPE = "nopatch"

# Follow debian/rules.
# Remove unrecognised options:
# --enable-snmp_client --enable-remote_client
EXTRA_OECONF = " \
    --with-varpath=${localstatedir}/lib/openhpi \
    --enable-daemon --enable-clients --enable-cpp_wrappers \
    --enable-simulator --enable-watchdog CFLAGS='${CFLAGS}' \
"
PACKAGECONFIG ?= "ipmi sysfsutils snmp"
PACKAGECONFIG[ipmi] = "--enable-ipmi --enable-ipmidirect, --disable-ipmi --disable-ipmidirect, openipmi"
PACKAGECONFIG[sysfsutils] = "--enable-sysfs, --disable-sysfs, sysfsutils"
PACKAGECONFIG[snmp] = "--enable-snmp_bc, --disable-snmp_bc, net-snmp"

do_configure_prepend() {
	# Prevent using host headers
	sed -i -e "s:-I/usr/include:-I${STAGING_INCDIR}:g" ${S}/configure.ac
	find ${S} -name Makefile.am -exec sed -i -e "s:-I/usr/include:-I${STAGING_INCDIR}:g" {} \;
}

PACKAGES =+ " \
    ${PN}-plugin-ilo2-ribcl ${PN}-plugin-ipmi \
    ${PN}-plugin-ipmidirect ${PN}-plugin-oa-soap \
    ${PN}-plugin-snmp-bc ${PN}-plugin-sysfs \
    ${PN}-plugin-watchdog lib${PN} ${PN}d ${PN}-clients \
"

FILES_${PN}-plugin-ilo2-ribcl = "${libdir}/openhpi/libilo2_ribcl${SOLIBS}"
FILES_${PN}-plugin-ipmi = "${libdir}/openhpi/libipmi${SOLIBS}"
FILES_${PN}-plugin-ipmidirect = "${libdir}/openhpi/libipmidirect${SOLIBS}"
FILES_${PN}-plugin-oa-soap = "${libdir}/openhpi/liboa_soap${SOLIBS}"
FILES_${PN}-plugin-snmp-bc = " \
    ${libdir}/libopenhpi_snmp${SOLIBS} \
    ${libdir}/openhpi/libsnmp_bc${SOLIBS} \
"
FILES_${PN}-plugin-sysfs = "${libdir}/openhpi/libsysfs*${SOLIBS}"
FILES_${PN}-plugin-watchdog = "${libdir}/openhpi/libwatchdog${SOLIBS}"
FILES_lib${PN} = " \
    ${sysconfdir}/openhpi \
    ${localstatedir}/lib/openhpi \
    ${libdir}/*${SOLIBS} \
    ${libdir}/openhpi/libosahpi${SOLIBS} \
    ${libdir}/openhpi/libsimulator${SOLIBS} \
"
FILES_${PN}d = " \
    ${sbindir}/openhpid \
    ${sysconfdir}/init.d \
"
FILES_${PN}-clients = "${bindir}/hpi*"
FILES_${PN}-dev += "${libdir}/openhpi/*${SOLIBSDEV}"

RDEPENDS_${PN} += "lsb-base"

# Debian provides openhpi package with doc files only,
# but on Poky it becomes empty.
ALLOW_EMPTY_${PN} = "1"

# Follow debian/control
RDEPENDS_${PN} += " \
    ${PN}d ${PN}-clients ${PN}-plugin-ilo2-ribcl \
    ${PN}-plugin-ipmi ${PN}-plugin-ipmidirect \
    ${PN}-plugin-oa-soap ${PN}-plugin-snmp-bc \
"
RDEPENDS_${PN}-dev = "lib${PN} libopenipmi openipmi"
RDEPENDS_${PN}d += "lib${PN} lsb-base"
RDEPENDS_${PN}-clients += "lib${PN}"
RDEPENDS_${PN}-plugin-ipmi += "libopenipmi openipmi"

# All openhpi-plugin-* depend on libopenhpi
python populate_packages_prepend(){
    import re

    pn = d.getVar("PN", True)
    packages = d.getVar("PACKAGES", True)

    patern_plugin_package = pn + '-plugin-.*'

    for package in packages.split():
        if re.match(patern_plugin_package,package):
            d.appendVar("RDEPENDS_" + package, " lib" + pn)
}

DEBIANNAME_lib${PN} = "lib${PN}2"
DEBIANNAME_${PN}-dev = "lib${PN}-dev"
DEBIAN_NOAUTONAME_${PN}-plugin-snmp-bc = "1"
