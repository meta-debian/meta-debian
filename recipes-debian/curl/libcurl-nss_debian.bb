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
