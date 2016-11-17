require libpam-krb5.inc

EXTRA_OECONF += "\
        --with-krb5-include=${STAGING_INCDIR}/mit-krb5 \
        --with-krb5-lib=${STAGING_LIBDIR}/mit-krb5 \
        --with-kadm-client-include=${STAGING_INCDIR}/mit-krb5 \
        --with-kadm-client-lib=${STAGING_LIBDIR}/mit-krb5"
DEPENDS += "krb5"
RCONFLICTS_${PN} += "libpam-heimdal"
