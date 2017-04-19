#
# base recipe: meta-security/recipes-security/samhain/samhain.inc
# base branch: jethro
#

SUMMARY = "Data integrity and host intrusion alert system"
DESCRIPTION = "Samhain is an integrity checker and host intrusion detection system that\n\
can be used on single hosts as well as large, UNIX-based networks.\n\
It supports central monitoring as well as powerful (and new) stealth\n\
features to run undetected on memory using steganography.\n\
.\n\
Main features\n\
    * Complete integrity check\n\
         + uses cryptographic checksums of files to detect\n\
           modifications,\n\
         + can find rogue SUID executables anywhere on disk, and\n\
    * Centralized monitoring\n\
         + native support for logging to a central server via encrypted\n\
           and authenticated connections\n\
    * Tamper resistance\n\
         + database and configuration files can be signed\n\
         + logfile entries and e-mail reports are signed\n\
         + support for stealth operation"
HOMEPAGE = "http://la-samhna.de/samhain/index.html"

PR = "r1"

inherit debian-package
PV = "3.1.0"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=8ca43cbc842c2336e835926c2166c28b \
    file://COPYING;md5=6209be2b92e9660443776c548395c319 \
"

DEPENDS = "gmp"

DEBIAN_PATCH_TYPE = "nopatch"

inherit autotools-brokensep pkgconfig

# Follow debian/rules
EXTRA_OECONF += "--with-config-file=${sysconfdir}/samhain/samhainrc \
                 --with-state-dir=${localstatedir}/lib/samhain  \
                 ${DNMALLOC} \
                 ${DISABLE_ASM} \
                 --enable-network=no  \
                 --with-pid-file=${localstatedir}/run/samhain/samhain.pid \
                 --with-log-file=${localstatedir}/log/samhain/samhain.log \
                 "
DNMALLOC = "--disable-dnmalloc"
DNMALLOC_x86 = "--enable-dnmalloc"
DNMALLOC_x86-64 = "--enable-dnmalloc"
DISABLE_ASM = ""
DISABLE_ASM_x86-64 = "--disable-asm"

PACKAGECONFIG ??= "prelude"
PACKAGECONFIG[prelude] = "--with-prelude,--without-prelude,libprelude"

do_configure_prepend_arm() {
	export sh_cv___va_copy=yes
}

do_configure_prepend_aarch64() {
	export sh_cv___va_copy=yes
}

# If we use oe_runconf in do_configure() it will by default
# use the prefix --oldincludedir=/usr/include which is not
# recognized by Samhain's configure script and would invariably
# throw back the error "unrecognized option: --oldincludedir=/usr/include"
do_configure () {
	cd ${S}
	cat << EOF > ./config-site.${BP}
ssp_cv_lib=no
sh_cv_va_copy=yes
EOF
	export CONFIG_SITE=./config-site.${BP}
	./configure \
	    --build=${BUILD_SYS} \
	    --host=${HOST_SYS} \
	    --target=${TARGET_SYS} \
	    --prefix=${prefix} \
	    --exec_prefix=${exec_prefix} \
	    --bindir=${bindir} \
	    --sbindir=${sbindir} \
	    --libexecdir=${libexecdir} \
	    --datadir=${datadir} \
	    --sysconfdir=${sysconfdir} \
	    --sharedstatedir=${sharedstatedir} \
	    --localstatedir=${localstatedir} \
	    --libdir=${libdir} \
	    --includedir=${includedir} \
	    --infodir=${infodir} \
	    --mandir=${mandir} \
	    ${EXTRA_OECONF}
}

do_install_append() {
	# Follow debian/rules

	# Fix the permissions
	chmod o-rX ${D}${localstatedir}/log/samhain \
	           ${D}${localstatedir}/lib/samhain \
	           ${D}${sysconfdir}/samhain

	oe_runmake install install-boot DESTDIR="${D}"
	install -m 640 ${S}/debian/samhainrc ${D}${sysconfdir}/samhain/samhainrc
	install -m 644 ${S}/debian/samhain.logrotate.d ${D}${sysconfdir}/logrotate.d/samhain

	install -m 0755 ${S}/debian/samhain.init ${D}${sysconfdir}/init.d/samhain
}

FILES_${PN} += "/run"
