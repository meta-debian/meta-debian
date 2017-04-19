require libpam-krb5.inc

DPN = "libpam-krb5"
EXTRA_OECONF += "\
	--with-krb5-include=${STAGING_INCDIR}/heimdal \
	--with-krb5-lib=${STAGING_LIBDIR}/heimdal \
	--with-kadm-client-include=${STAGING_INCDIR}/heimdal \
	--with-kadm-client-lib=${STAGING_LIBDIR}/heimdal"
DEPENDS += "heimdal"
RCONFLICTS_${DPN} += "libpam-krb5"
