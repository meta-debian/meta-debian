require libpam-krb5.inc

DEPENDS += "krb5"
RCONFLICTS_${PN} += "libpam-heimdal"
