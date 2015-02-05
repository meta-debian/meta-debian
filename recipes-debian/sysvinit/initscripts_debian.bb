require recipes-core/sysvinit/sysvinit_2.88dsf.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-core/sysvinit/sysvinit:\
"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "0"

BPN = "sysvinit"
RDEPENDS_${PN} = "sysvinit-inittab"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI += "\
	file://rcS-default\
	file://rc\
	file://rcS\
	file://bootlogd.init\
"

# Fix LIBDIR so libcrypt.a can be seen
do_compile_prepend() {
        sed -i -e "s:^LIBDIR=.*:LIBDIR=${STAGING_LIBDIR}:" ${S}/src/Makefile
}

# Implement initscripts package which includes additional init scripts
PACKAGES =+ "${PN}-reboot ${PN}-halt"
FILES_${PN}-reboot = "${sysconfdir}/init.d/reboot"
FILES_${PN}-halt = "${sysconfdir}/init.d/halt"

# Install initscripts
do_install() {
	oe_runmake 'ROOT=${D}' install
        install -d ${D}${sysconfdir} \
                   ${D}${sysconfdir}/default \
                   ${D}${sysconfdir}/init.d
        install -m 0644    ${WORKDIR}/rcS-default       ${D}${sysconfdir}/default/rcS
        install -m 0755    ${WORKDIR}/rc                ${D}${sysconfdir}/init.d
        install -m 0755    ${WORKDIR}/rcS               ${D}${sysconfdir}/init.d
        install -m 0755    ${WORKDIR}/bootlogd.init     ${D}${sysconfdir}/init.d/bootlogd
        ln -sf bootlogd ${D}${sysconfdir}/init.d/stop-bootlogd
        install -d ${D}${sysconfdir}/rcS.d

        # NOTE: use variables to define rc number
        ln -sf ../init.d/bootlogd ${D}${sysconfdir}/rcS.d/S${SYSVINIT_BOOTLOGD_RC}bootlogd
        for level in 2 3 4 5; do
                install -d ${D}${sysconfdir}/rc$level.d
                ln -s ../init.d/stop-bootlogd ${D}${sysconfdir}/rc$level.d/S${SYSVINIT_STOP_BOOTLOGD_RC}stop-bootlogd
        done

        mv                 ${D}${base_sbindir}/init               ${D}${base_sbindir}/init.${BPN}
        mv ${D}${base_bindir}/pidof ${D}${base_bindir}/pidof.${BPN}
        mv ${D}${base_sbindir}/halt ${D}${base_sbindir}/halt.${BPN}
        mv ${D}${base_sbindir}/reboot ${D}${base_sbindir}/reboot.${BPN}
        mv ${D}${base_sbindir}/shutdown ${D}${base_sbindir}/shutdown.${BPN}
        mv ${D}${base_sbindir}/poweroff ${D}${base_sbindir}/poweroff.${BPN}
        mv ${D}${bindir}/last ${D}${bindir}/last.${BPN}
        mv ${D}${bindir}/mesg ${D}${bindir}/mesg.${BPN}
        mv ${D}${bindir}/wall ${D}${bindir}/wall.${BPN}

	# install additional init scripts
        install -m 0755 ${S}/debian/src/initscripts/etc/init.d/halt ${D}${sysconfdir}/init.d
        install -m 0755 ${S}/debian/src/initscripts/etc/init.d/reboot ${D}${sysconfdir}/init.d
}
