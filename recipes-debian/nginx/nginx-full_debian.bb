require nginx.inc
PR = "${INC_PR}.0"

DEPENDS += "libxslt libxml2 geoip gzip expat libpam"
do_configure() {
	./configure \
		${common_configure_flags} \
		--with-http_addition_module \
		--with-http_dav_module \
		--with-http_geoip_module \
		--with-http_gzip_static_module \
		--with-http_spdy_module \
		--with-http_sub_module \
		--with-http_xslt_module \
		--with-mail \
		--with-mail_ssl_module \
		--add-module=${S}/debian/modules/nginx-auth-pam \
		--add-module=${S}/debian/modules/nginx-dav-ext-module \
		--add-module=${S}/debian/modules/nginx-echo \
		--add-module=${S}/debian/modules/nginx-upstream-fair \
		--add-module=${S}/debian/modules/ngx_http_substitutions_filter_module
}
do_install_append() {
	install -D -m 0755 ${S}/objs/nginx ${D}${sbindir}/nginx
	# remove the conflict files with nginx-common package
	rm -rf ${D}${sysconfdir} \
	       ${D}${base_libdir} \
	       ${D}${datadir} \
	       ${D}${localstatedir}/run
}
RDEPENDS_${PN} += "nginx-common"
RPROVIDES_${PN} += "httpd httpd-cgi nginx"
RCONFLICTS_${PN} += "nginx-extras nginx-light"
FILES_${PN} += "/run"
