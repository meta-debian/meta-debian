# base recipe: meta/recipes-support/neon/neon_0.30.1.bb
# base branch: master

include neon.inc

DEPENDS += "gnutls"
DEPENDS_class-native += "gnutls-native"

EXTRA_OECONF += " \
    --with-ca-bundle=${sysconfdir}/ssl/certs/ca-certificates.crt \
    --with-ssl=gnutls \
"

do_compile_prepend(){
	( cd ${B}
	sed -i "s/-lneon/-lneon-gnutls/g" \
		neon-config neon.pc Makefile src/Makefile test/Makefile
	sed -i "s/libneon\./libneon-gnutls\./g" \
                neon-config neon.pc Makefile src/Makefile test/Makefile
	)
}

RCONFLICTS_${PN}-dev = "neon-dev"
RREPLACES_${PN}-dev = "neon-dev"
