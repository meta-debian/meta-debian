require curl.inc

PR = "${INC_PR}.0"

EXTRA_OECONF += " \
	--without-ssl --with-nss \
"

# Remove unnecessary files which have already been in curl
do_install_append(){
	rm -r ${D}${bindir}
	rm -r ${D}${datadir}
	rm -r ${D}${includedir}
	rm -r ${D}${libdir}/pkgconfig/
}

DEBIANNAME_${PN} = "libcurl3-nss"
RPROVIDES_${PN} = "libcurl3-nss"
DEBIANNAME_${PN}-dev = "libcurl4-nss-dev"
RPROVIDES_${PN}-dev = "libcurl4-nss-dev"
RCONFLICTS_${PN}-dev = "libcurl4-openssl-dev libcurl4-gnutls-dev"

RDEPENDS_${PN}-dev += "curl-dev"
