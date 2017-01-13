SUMMARY = "fast, self-healing application container server"
DESCRIPTION = "uWSGI presents a complete stack for networked/clustered web applications,\n\
 implementing message/object passing, caching, RPC and process management.\n\
 It uses the uwsgi protocol for all the networking/interprocess communications.\n\
 .\n\
 uWSGI can be run in preforking, threaded, asynchronous/evented modes and\n\
 supports various forms of green threads/coroutines (such as uGreen, Greenlet,\n\
 Fiber). uWSGI provides several methods of configuration: via command line,\n\
 via environment variables, via XML, INI, YAML configuration files, via LDAP\n\
 and more.\n\
 .\n\
 On top of all this, it is designed to be fully modular. This means that\n\
 different plugins can be used in order to add compatibility with tons of\n\
 different technology on top of the same core."
HOMEPAGE = "http://projects.unbit.it/uwsgi/"

PR = "r1"
inherit debian-package
PV = "2.0.7"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=33ab1ce13e2312dddfad07f97f66321f"
inherit setuptools pkgconfig update-alternatives

# Required by python command
export HOST_SYS
export BUILD_SYS
# prevent host contamination and add ${STAGING_INCDIR} \
# using UWSGI_INCLUDES environment variable to detect include paths:
export UWSGI_INCLUDES = "${STAGING_INCDIR}"

DEPENDS += "zeromq3 apache2 libpam openssl libyaml curl geoip tcp-wrappers \
            lua5.1 libmatheval jansson postgresql"
UWSGI_SRCPLUGINS_CORE="\
 cache carbon cgi cheaper_backlog2 cheaper_busyness clock_monotonic clock_realtime \
 corerouter echo emperor_amqp emperor_zeromq fastrouter http logfile logsocket \
 nagios notfound pam ping rawrouter redislog router_basicauth router_cache \
 router_http router_memcached router_redirect router_rewrite router_static \
 router_uwsgi rpc rrdtool rsyslog signal spooler ssi sslrouter stats_pusher_statsd \
 symcall syslog transformation_gzip transformation_tofile transformation_toupper \
 ugreen zergpool matheval"

UWSGI_SRCPLUGINS_ADDON_MISC="\
 alarm_curl curl_cron geoip graylog2 ldap router_access sqlite3 xslt"

do_compile_append() {
	PYTHON_SUFFIX=`echo ${PYTHON_BASEVERSION} | sed "s:\.::"`
	${PYTHON} uwsgiconfig.py -v --build debian/buildconf/uwsgi-core.ini
	for PLUGIN_NAME in ${UWSGI_SRCPLUGINS_CORE} ${UWSGI_SRCPLUGINS_ADDON_MISC}; do
		${PYTHON} uwsgiconfig.py -v \
		--plugin plugins/${PLUGIN_NAME} \
		debian/buildconf/uwsgi-plugin.ini \
		${PLUGIN_NAME}
	done

	# Compile python plugin
	PYTHON_SUFFIX=`echo ${PYTHON_BASEVERSION} | sed "s:\.::"`
	${PYTHON} uwsgiconfig.py -v \
		--plugin plugins/python \
		debian/buildconf/uwsgi-plugin.ini \
		python${PYTHON_SUFFIX}

	# Compile lua51 plugin
	${PYTHON} uwsgiconfig.py -v \
		--plugin plugins/lua \
		debian/buildconf/uwsgi-plugin.ini \
		lua51
}
do_install_append() {
	install -d ${D}${libdir}/${PN}/plugins
	install -m 0755 ${S}/*.so \
		${D}${libdir}/${PN}/plugins

	install -m 0755 ${S}/uwsgi-core ${D}${bindir}
	# remove unwanted file
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/__pycache__ \
	       ${D}${PYTHON_SITEPACKAGES_DIR}/uWSGI-* \
	       ${D}${bindir}/uwsgi
	
	# follow debian/uwsgi.dirs and debian/uwsgi.install
	install -d ${D}${sysconfdir} \
	           ${D}${datadir}/${PN} \
	           ${D}${localstatedir}/log/${PN}/app
	cp -r ${S}/debian/uwsgi-files/etc/* ${D}${sysconfdir}
	cp -r ${S}/debian/uwsgi-files/conf ${D}${datadir}/${PN}
	cp -r ${S}/debian/uwsgi-files/init ${D}${datadir}/${PN}

	# fllow debian/uwsgi-emperor.install
	cp -r ${S}/debian/uwsgi-emperor-files/etc/* ${D}${sysconfdir}

	install -D -m 0644 ${S}/debian/uwsgi.default \
	                   ${D}${sysconfdir}/default/uwsgi
	install -m 0644 ${S}/debian/uwsgi-emperor.default \
	                ${D}${sysconfdir}/default/uwsgi-emperor
	install -D -m 0755 ${S}/debian/uwsgi.init.d \
	                   ${D}${sysconfdir}/init.d/uwsgi
	install -m 0755 ${S}/debian/uwsgi-emperor.init.d \
	                ${D}${sysconfdir}/init.d/uwsgi-emperor
	install -D -m 0644 ${S}/debian/uwsgi.logrotate \
	                   ${D}${sysconfdir}/logrotate.d/uwsgi
	install -D -m 0644 ${S}/debian/uwsgi-emperor.logrotate \
	                   ${D}${sysconfdir}/logrotate.d/uwsgi-emperor

	PYTHON_SUFFIX=`echo ${PYTHON_BASEVERSION} | sed "s:\.::"`
	UWSGI_PLUGIN_INFILES="python${PYTHON_SUFFIX} alarm_curl curl_cron \
                              geoip graylog2 ldap router_access sqlite3 xslt lua51"
	for file in ${UWSGI_PLUGIN_INFILES}; do
		ln -sf uwsgi-core ${D}${bindir}/uwsgi_${file}
	done
}
PACKAGES =+ "\
 ${PN}-plugin-alarm-curl ${PN}-plugin-curl-cron ${PN}-plugin-lua5.1 \
 ${PN}-plugin-geoip ${PN}-plugin-graylog2 ${PN}-plugin-ldap \
 ${PN}-plugin-router-access ${PN}-plugin-sqlite3 ${PN}-plugin-xslt \
 ${PN}-emperor ${PN}-plugin-python ${PN}-core python-uwsgidecorators \
 "
FILES_${PN}-plugin-alarm-curl    = "${libdir}/${PN}/plugins/alarm_curl_plugin.so \
                                    ${bindir}/uwsgi_alarm_curl \
                                   "
FILES_${PN}-plugin-curl-cron     = "${libdir}/${PN}/plugins/curl_cron_plugin.so \
                                    ${bindir}/uwsgi_curl_cron \
                                   "
FILES_${PN}-plugin-lua5.1         = "${libdir}/${PN}/plugins/lua51_plugin.so \
                                   ${bindir}/uwsgi_lua51 \
                                   "
FILES_${PN}-plugin-geoip         = "${libdir}/${PN}/plugins/geoip_plugin.so \
                                    ${bindir}/uwsgi_geoip \
                                   "
FILES_${PN}-plugin-graylog2      = "${libdir}/${PN}/plugins/graylog2_plugin.so \
                                    ${bindir}/uwsgi_graylog2 \
                                   "
FILES_${PN}-plugin-ldap          = "${libdir}/${PN}/plugins/ldap_plugin.so \
                                    ${bindir}/uwsgi_ldap \
                                   "
FILES_${PN}-plugin-router-access = "${libdir}/${PN}/plugins/router_access_plugin.so \
                                    ${bindir}/uwsgi_router_access \
                                   "
FILES_${PN}-plugin-sqlite3       = "${libdir}/${PN}/plugins/sqlite3_plugin.so \
                                    ${bindir}/uwsgi_sqlite3 \
                                   "
FILES_${PN}-plugin-xslt          = "${libdir}/${PN}/plugins/xslt_plugin.so \
                                    ${bindir}/uwsgi_xslt \
                                   "
FILES_${PN}-core                 = "${bindir}/uwsgi-core \
                                    ${libdir}/${PN}/plugins/*.so \
                                   "
FILES_${PN}-emperor              = "${sysconfdir}/default/uwsgi-emperor \
                                    ${sysconfdir}/init.d/uwsgi-emperor \
                                    ${sysconfdir}/logrotate.d/uwsgi-emperor \
                                    ${sysconfdir}/uwsgi-emperor/* \
                                   "
FILES_${PN}-plugin-python       = "${libdir}/${PN}/plugins/python*.so \
                                    ${bindir}/uwsgi_python* \
                                   "
FILES_python-uwsgidecorators    = "${PYTHON_SITEPACKAGES_DIR}"
FILES_${PN}-dbg                 += "${libdir}/${PN}/plugins/.debug/*"

# follow debian/control
RDEPENDS_${PN}                      += "${PN}-core lsb-base sysvinit-initscripts"
RDEPENDS_${PN}-plugin-python        += "${PN}-core"
RPROVIDES_${PN}-plugin-python       += "httpd-wsgi"
RDEPENDS_${PN}-emperor              += "${PN}-core lsb-base sysvinit-initscripts"
RDEPENDS_${PN}-plugin-alarm-curl    += "${PN}-core"
RDEPENDS_${PN}-plugin-curl-cron     += "${PN}-core"
RDEPENDS_${PN}-plugin-emperor-pg    += "${PN}-core"
RDEPENDS_${PN}-plugin-lua51         += "${PN}-core"
RDEPENDS_${PN}-plugin-geoip         += "${PN}-core"
RDEPENDS_${PN}-plugin-graylog2      += "${PN}-core"
RDEPENDS_${PN}-plugin-ldap          += "${PN}-core"
RDEPENDS_${PN}-plugin-router-access += "${PN}-core"
RDEPENDS_${PN}-plugin-sqlite3       += "${PN}-core"
RDEPENDS_${PN}-plugin-xslt          += "${PN}-core"
RDEPENDS_python-uwsgidecorators     += "${PN}-core ${PN}-plugin-python"

# Base on debian/uwsgi-core.postinst
ALTERNATIVE_${PN}-core = "uwsgi"
ALTERNATIVE_PRIORITY[uwsgi] = "75"
ALTERNATIVE_LINK_NAME[uwsgi] = "${bindir}/uwsgi"
ALTERNATIVE_TARGET[uwsgi] = "${bindir}/uwsgi-core"
