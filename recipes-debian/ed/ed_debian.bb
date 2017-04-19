SUMMARY = "Classic UNIX line editor"
HOMEPAGE = "http://www.gnu.org/software/ed/"

PR = "r0"

inherit debian-package
PV = "1.10"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
    file://ed.h;endline=20;md5=2b62ce887e37f828a04aa32f1ec23787 \
    file://main.c;endline=17;md5=002b306d8e804a6fceff144b26554253 \
"

DEBIAN_PATCH_TYPE = "dpatch"

inherit texinfo update-alternatives

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_configure() {
	${S}/configure
}

do_install() {
	oe_runmake DESTDIR="${D}" install

	# Follow debian/rules
	install -d ${D}${base_bindir}
	mv ${D}${bindir}/ed ${D}${base_bindir}/
	mv ${D}${bindir}/red ${D}${base_bindir}/

	# Remove if directory empty
	if [ ! $(ls -A ${D}${bindir}) ]; then
		rm -r ${D}${bindir}
	fi
}

# Add update-alternatives definitions to avoid confict with busybox
ALTERNATIVE_${PN} = "ed"
ALTERNATIVE_PRIORITY[ed] = "100"
ALTERNATIVE_LINK_NAME[ed] = "${base_bindir}/ed"

# Follow debian/postinst
ALTERNATIVE_${PN} += "editor"
ALTERNATIVE_PRIORITY[editor] = "-100"
ALTERNATIVE_LINK_NAME[editor] = "${bindir}/editor"
ALTERNATIVE_TARGET[editor] = "${base_bindir}/ed.${DPN}"
