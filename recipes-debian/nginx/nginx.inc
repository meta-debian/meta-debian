# base recipe: https://github.com/openembedded/meta-openembedded/tree/master/meta-webserver/recipes-httpd/nginx
# base branch: master
# base commit: 9d40291916136ed3c4480102708e75d81cc77ef1
SUMMARY = "nginx web/proxy server"
DESCRIPTION = "Nginx ("engine X") is a high-performance web and reverse proxy server \
 created by Igor Sysoev. It can be used both as a standalone web server \
 and as a proxy to reduce the load on back-end HTTP or mail servers."
HOMEPAGE = "http://nginx.net"

DPN = "nginx"
INC_PR = "r0"
inherit debian-package
PV = "1.6.2"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9d3b27bad611f5204a84ba6a572698e1"

# We do not have capability to run binaries when cross compiling
SRC_URI += "file://nginx-cross.patch"

inherit autotools-brokensep
DEPENDS += "libpcre openssl"

common_configure_flags = "\
	--with-int=4 \
	--with-long=${PTRSIZE} \
	--with-long-long=8 \
	--with-ptr-size=${PTRSIZE} \
	--with-sig-atomic-t=${PTRSIZE} \
	--with-size-t=${PTRSIZE} \
	--with-off-t=${PTRSIZE} \
	--with-time-t=${PTRSIZE} \
	--with-sys-nerr=132 \
	--crossbuild=Linux:${TUNE_ARCH} \
	--with-cc-opt="${CFLAGS} ${CPPFLAGS}" \
	--with-ld-opt="${LDFLAGS}" \
	--prefix=${datadir}/nginx \
	--conf-path=${sysconfdir}/nginx/nginx.conf \
	--http-log-path=${localstatedir}/log/nginx/access.log \
	--error-log-path=${localstatedir}/log/nginx/error.log \
	--lock-path=${localstatedir}/lock/nginx.lock \
	--pid-path=/run/nginx.pid \
	--http-client-body-temp-path=${localstatedir}/lib/nginx/body \
	--http-fastcgi-temp-path=${localstatedir}/lib/nginx/fastcgi \
	--http-proxy-temp-path=${localstatedir}/lib/nginx/proxy \
	--http-scgi-temp-path=${localstatedir}/lib/nginx/scgi \
	--http-uwsgi-temp-path=${localstatedir}/lib/nginx/uwsgi \
	--with-debug \
	--with-pcre-jit \
	--with-ipv6 \
	--with-http_ssl_module \
	--with-http_stub_status_module \
	--with-http_realip_module \
	--with-http_auth_request_module \
"
do_configure_prepend() {
	if [ "${SITEINFO_BITS}" = "64" ]; then
		PTRSIZE=8
	else
		PTRSIZE=4
	fi
	sed -i -e 's|R/usr/local/lib|R${STAGING_LIBDIR}|g' \
	       -e 's|L/usr/local/lib|L${STAGING_LIBDIR}|g' \
	       -e 's|/usr/local/include|${STAGING_INCDIR}|g' \
	       -e 's|\"/usr/include|\"${STAGING_INCDIR}|g' \
		${S}/auto/lib/*/conf ${S}/debian/modules/*/config
}
# Avoid a parallel build problem
PARALLEL_MAKE = ""
