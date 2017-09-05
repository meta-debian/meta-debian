require curl.inc

PR = "${INC_PR}.0"

EXTRA_OECONF += " \
	--without-ssl --with-gnutls \
"

# Follow debian/rules.
# Remove patch of nss, we will build libcurl-gnutls
do_debian_patch_prepend(){
	sed -i '/^99_nss.patch/d' ${DEBIAN_QUILT_PATCHES}/series
	rm ${DEBIAN_QUILT_PATCHES}/99_nss.patch
}

# Remove unnecessary files which have already been in curl
do_install_append(){
	rm -r ${D}${bindir}
	rm -r ${D}${datadir}
	rm -r ${D}${includedir}
	rm -r ${D}${libdir}/pkgconfig/
}

DEBIANNAME_${PN} = "libcurl3-gnutls"
RPROVIDES_${PN} = "libcurl3-gnutls"
DEBIANNAME_${PN}-dev = "libcurl4-gnutls-dev"
RPROVIDES_${PN}-dev = "libcurl4-gnutls-dev"
RCONFLICTS_${PN}-dev = "libcurl4-openssl-dev libcurl4-nss-dev"
RDEPENDS_${PN}-dev += "curl-dev"
