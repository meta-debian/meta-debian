require postfix.inc
PR = "${INC_PR}.0"
inherit native
DEPENDS += "db-native tinycdb-native mysql-native \
        postgresql-native libldap-native libpcre-native"

export CCARGS = "-DDEBIAN -DMAX_DYNAMIC_MAPS -DHAS_PCRE \ 
                 -DHAS_SQLITE -DMYORIGIN_FROM_FILE \
                 -DHAS_CDB \
                 -DHAS_MYSQL -I${STAGING_INCDIR}/mysql \
                 -DHAS_SQLITE -I${STAGING_INCDIR} \
                 -DHAS_SSL -I${STAGING_INCDIR}/openssl \
                 -DUSE_TLS"

export AUXLIBS = "-L${STAGING_LIBDIR_NATIVE} -lssl -lcrypto -lpthread"
export BUILD_SYSROOT = "${STAGING_DIR_NATIVE}"
do_configure_prepend() {
	sed -i -e "s:f \/usr\/include:f ${STAGING_INCDIR}:g" ${S}/makedefs
}
do_install_prepend() {
	export LD_LIBRARY_PATH=${S}/lib:$LD_LIBRARY_PATH
}
