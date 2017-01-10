require nginx.inc
PR = "${INC_PR}.0"

do_configure() {
	./configure \
		${common_configure_flags} \
		--with-http_gzip_static_module \
		--without-http_browser_module \
		--without-http_geo_module \
		--without-http_limit_req_module \
		--without-http_limit_zone_module \
		--without-http_memcached_module \
		--without-http_referer_module \
		--without-http_scgi_module \
		--without-http_split_clients_module \
		--without-http_ssi_module \
		--without-http_userid_module \
		--without-http_uwsgi_module \
		--add-module=${S}/debian/modules/nginx-echo
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
RCONFLICTS_${PN} += "nginx-extras nginx-full"
FILES_${PN} += "/run"
