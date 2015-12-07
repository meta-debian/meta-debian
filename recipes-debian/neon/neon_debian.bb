# base recipe: meta/recipes-support/neon/neon_0.30.1.bb
# base branch: master 

include neon.inc

DEPENDS += "openssl"
DEPENDS_class-native += "openssl-native"

EXTRA_OECONF += " --with-ssl=openssl"

RCONFLICTS_${PN}-dev = "neon-gnutls-dev"
RREPLACES_${PN}-dev = "neon-gnutls-dev"
