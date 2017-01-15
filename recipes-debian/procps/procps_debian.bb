PR = "r0"

inherit debian-package
PV = "3.3.9"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM=" \
	file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
	file://COPYING.LIB;md5=4cf66a4984120007c9881cc871cf49db \
"

DEPENDS = "ncurses"

# init.d/procps require lsb-base
RDEPENDS_${PN} += "lsb-base"

inherit autotools gettext pkgconfig update-alternatives

# Configure options follow debian/rules.
# Reconfigure path of bindir, sbindir for correct install dirs
# follow Debian and procps Makefile:
# 	bindir is set to /bin, include: kill, ps
#	sbindir is set to /sbin, include: sysctl
#	usrbin_execdir is set to /usr/bin, include other commands.
EXTRA_OECONF = " \
	--enable-watch8bit \
	--enable-w-from \
	--enable-skill \
	--disable-pidof \
	--prefix=${prefix} \
	--exec_prefix=${base_prefix} \
	--bindir=${base_bindir} \
	--sbindir=${base_sbindir} \
"

CPPFLAGS += "-I${S}"

# Install and link file base on debian/rules
do_install_append () {
	# Rename w as there are two of them
	mv ${D}${bindir}/w ${D}${bindir}/w.procps
	mv ${D}${mandir}/man1/w.1 ${D}${mandir}/man1/w.procps.1

	install -d ${D}${sysconfdir}
	install -m 0644 ${S}/debian/sysctl.conf ${D}${sysconfdir}/sysctl.conf

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/procps.init.linux ${D}${sysconfdir}/init.d/procps

	# Correct library and link location follow Debian
	if [ ${base_libdir} != ${libdir} ]; then
		install -d ${D}${base_libdir}
		mv ${D}${libdir}/libprocps${SOLIBS} ${D}${base_libdir}/
		rm ${D}${libdir}/libprocps${SOLIBSDEV}
		ln -s ../../${base_libdir}/libprocps.so.3 ${D}${libdir}/libprocps${SOLIBSDEV}
	fi
}

CONFFILES_${PN} = "${sysconfdir}/sysctl.conf"

PACKAGES =+ " lib${PN}"
FILES_lib${PN} = "${base_libdir}/*"
DEBIANNAME_lib${PN} = "lib${PN}3"

ALTERNATIVE_${PN} = "w"
ALTERNATIVE_PRIORITY[w] = "50"
ALTERNATIVE_LINK_NAME[w] = "${bindir}/w"
ALTERNATIVE_TARGET[w] = "${bindir}/w.${DPN}"

# Add update-alternatives definitions to avoid conflict with Debian
ALTERNATIVE_${PN} += "kill ps sysctl"
ALTERNATIVE_PRIORITY[kill] = "100"
ALTERNATIVE_LINK_NAME[kill] = "${base_bindir}/kill"
ALTERNATIVE_PRIORITY[ps] = "100"
ALTERNATIVE_LINK_NAME[ps] = "${base_bindir}/ps"
ALTERNATIVE_PRIORITY[sysctl] = "100"
ALTERNATIVE_LINK_NAME[sysctl] = "${base_sbindir}/sysctl"
