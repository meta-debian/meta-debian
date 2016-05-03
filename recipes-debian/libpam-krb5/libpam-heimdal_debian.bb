require libpam-krb5.inc

DPN = "libpam-krb5"
DEPENDS += "heimdal"
RCONFLICTS_${DPN} += "libpam-krb5"
