SUMMARY = "Support for extra binary formats"
DESCRIPTION = "The binfmt_misc kernel module, contained in versions 2.1.43 and later of the \
Linux kernel, allows system administrators to register interpreters for \
various binary formats based on a magic number or their file extension, and \
cause the appropriate interpreter to be invoked whenever a matching file is \
executed. Think of it as a more flexible version of the #! executable \
interpreter mechanism."
HOMEPAGE = "http://binfmt-support.nongnu.org/"

inherit debian-package
PV = "2.1.5"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "libpipeline"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig

EXTRA_OECONF = " \
    --libexecdir=${libdir} \
    --with-systemdsystemunitdir=${systemd_system_unitdir} \
"

do_install_append() {
	install -D -m 0755 ${S}/debian/init ${D}${sysconfdir}/init.d/binfmt-support
}

FILES_${PN} += "${systemd_system_unitdir} ${datadir}/binfmts"
