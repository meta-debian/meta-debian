require bind.inc

PR = "${INC_PR}.1"
inherit native

EXTRA_OECONF = " \
	--disable-epoll \
	--disable-kqueue \
	--disable-devpoll \
	--disable-threads \
	--disable-linux-caps \
	--without-openssl \
	--without-libxml2 \
	--enable-ipv6 \
	--enable-shared \
	--enable-exportlib \
	--with-libtool \
	--with-gssapi=no \
"

do_install_append () {
	install -d ${D}${base_bindir}
	install -m 0755 ${B}/lib/export/dns/gen ${D}${base_bindir}/gen-lib-export-dns
	install -m 0755 ${B}/lib/dns/gen ${D}${base_bindir}/gen-lib-dns
	install -m 0755 ${B}/bin/tools/genrandom ${D}${base_bindir}
}
