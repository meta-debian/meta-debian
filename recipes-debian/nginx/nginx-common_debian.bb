require nginx.inc
PR = "${INC_PR}.0"

inherit useradd

do_configure() {
	./configure \
		${common_configure_flags}
}
do_install_append() {
	#follow debian/nginx-common.dirs
	install -d ${D}${sysconfdir}/nginx/sites-enabled \
	           ${D}${localstatedir}/lib/nginx \
	           ${D}${localstatedir}/www/html

	# Base on debian/nginx-common.install
	cp -r ${S}/debian/conf/* ${D}${sysconfdir}/nginx
	install -D -m 0644 ${S}/debian/ufw/nginx \
		${D}${sysconfdir}/ufw/applications.d/nginx
	
	install -D -m 0755 ${S}/debian/nginx-common.nginx.init \
		${D}${sysconfdir}/init.d/nginx
	install -D -m 0644 ${S}/debian/nginx-common.nginx.default \
		${D}${sysconfdir}/default/nginx
	install -D -m 0644 ${S}/debian/nginx-common.nginx.logrotate \
		${D}${sysconfdir}/logrotate.d/nginx
	# remove unwanted files
	rm -rf ${D}${sysconfdir}/nginx/*.default \
	       ${D}${datadir}/nginx/sbin \
	       ${D}${datadir}/nginx/html/50x.html \
	       ${D}${localstatedir}/run ${D}/run

	install -D -m644 ${S}/debian/nginx-common.nginx.service \
	                 ${D}${systemd_system_unitdir}/nginx.service
}
pkg_postinst_${PN} () {
	. ${STAGING_DATADIR}/debconf/confmodule
	logdir="$D${localstatedir}/log/nginx"
	chown root:adm $logdir
	chmod 0755 $logdir
	
	access_log="$logdir/access.log"
	error_log="$logdir/error.log"

	if [ ! -e "$access_log" ]; then
		touch "$access_log"
		chmod 640 "$access_log"
		chown www-data:adm "$access_log"
	fi

	if [ ! -e "$error_log" ]; then
		touch "$error_log"
		chmod 640 "$error_log"
		chown www-data:adm "$error_log"
	fi

	# If a symlink doesn't exist and can be created, then create it.
	if [ ! -e $D${sysconfdir}/nginx/sites-enabled/default ] &&
	   [ -d $D${sysconfdir}/nginx/sites-enabled ] && [ -d $D${sysconfdir}/nginx/sites-available ]; then
		ln -s ${sysconfdir}/nginx/sites-available/default $D${sysconfdir}/nginx/sites-enabled/default
	fi

	# Create a default index page when not already present.
	if [ ! -e $D${localstatedir}/www/html/index.nginx-debian.html ]; then
		cp $D${datadir}/nginx/html/index.html $D${localstatedir}/www/html/index.nginx-debian.html
	fi
}
USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r adm"
USERADD_PARAM_${PN} = "-r --no-create-home www-data"

FILES_${PN} += "\
	${systemd_system_unitdir} \
	${datadir}/nginx/html/*"
RDEPENDS_${PN} += "lsb-base"
