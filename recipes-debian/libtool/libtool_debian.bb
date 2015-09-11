#
# base recipe: meta/recipes-devtools/libtool/libtool_2.4.2.bb
# base branch: daisy
#

require libtool.inc

PR = "${INC_PR}.1"

#                                                                               
# We want the results of libtool-cross preserved - don't stage anything ourselves.
#                                                                               
SYSROOT_PREPROCESS_FUNCS += "libtool_sysroot_preprocess"                        
                                                                                
libtool_sysroot_preprocess () {                                                 
        rm -rf ${SYSROOT_DESTDIR}${bindir}/*                                    
        rm -rf ${SYSROOT_DESTDIR}${datadir}/aclocal/*                           
        rm -rf ${SYSROOT_DESTDIR}${datadir}/libtool/config/*                    
} 
