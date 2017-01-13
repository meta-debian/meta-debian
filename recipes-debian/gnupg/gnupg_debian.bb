SUMMARY = "GNU Privacy Guard - encryption and signing tools"
DESCRIPTION = "\
 GnuPG is GNU's tool for secure communication and data storage.\
 It can be used to encrypt data and to create digital signatures. \
 It includes an advanced key management facility and is compliant \
 with the proposed OpenPGP Internet standard as described in RFC 4880. \
"
HOMEPAGE = "http://www.gnupg.org/"

PR = "r0"
inherit debian-package
PV = "1.4.18"

LICENSE = "GPLv3+ & bzip2"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
                    file://bzlib/LICENSE;md5=98ec24b7285bf09b448bc0df6c8a29ae"

inherit autotools gettext

DEPENDS = "zlib bzip2 readline"

EXTRA_OECONF = "--libexecdir=${libdir} \
                --enable-mailto \
                --with-mailprog=${sbindir}sendmail \
                --enable-large-secmem \
                --with-zlib=${STAGING_LIBDIR}/.. \
                --with-bzip2=${STAGING_LIBDIR}/.. \
                --disable-selinux-support \
                --with-readline=${STAGING_LIBDIR}/.. \
                ac_cv_sys_symbol_underscore=no"

# Force gcc's traditional handling of inline to avoid issues with gcc 5
CFLAGS += "-fgnu89-inline"

do_install_append () {
	install -d ${D}${docdir}/${PN}
	install -d ${D}${base_libdir}/udev/rules.d

	mv ${D}${datadir}/${PN}/* ${D}/${docdir}/${PN}/ || :
	mv ${D}${prefix}/doc/* ${D}/${docdir}/${PN}/ || :
	install -m 0755 ${S}/tools/lspgpot ${D}${bindir}
	install -m 0644 ${S}/debian/gnupg.udev \
		${D}/${base_libdir}/udev/rules.d/60-gnupg.rules
}

# split out gpgv from main package
RDEPENDS_${PN} = "gpgv"
PACKAGES =+ "gpgv"
FILES_gpgv = "${bindir}/gpgv"

PACKAGECONFIG ??= ""
PACKAGECONFIG[curl] = "--with-libcurl=${STAGING_LIBDIR},--without-libcurl,curl"
PACKAGECONFIG[libusb] = "--with-libusb=${STAGING_LIBDIR},--without-libusb,libusb"
